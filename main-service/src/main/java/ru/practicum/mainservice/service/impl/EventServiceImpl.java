package ru.practicum.mainservice.service.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.EventDtoComparator;
import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.GetEventsRequest;
import ru.practicum.mainservice.dto.NewEventDto;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.exception.AccessDeniedException;
import ru.practicum.mainservice.exception.DataValidationException;
import ru.practicum.mainservice.exception.EntityNotFoundException;
import ru.practicum.mainservice.exception.EventChangeDeniedException;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.ParticipationRequestMapper;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.EventState;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.QEvent;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.api.EventService;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsdto.ViewStatsRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final ParticipationRequestRepository requestRepository;

    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        return eventRepository.findAllByInitiator(user, getPageable(from, size)).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto addEvent(NewEventDto eventDto, long userId) {
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", eventDto.getCategory())));
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        event.setInitiator(initiator);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d does not have access to event id=%d", userId, eventId));
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.isPublished()) {
            throw new EntityNotFoundException("You cannot view an unpublished event.");
        }

        Long view = statsClient.getStatistics(
                ViewStatsRequest.builder()
                        .uris(Set.of("/events/" + eventId))
                        .unique(true)
                        .build()
                )
                .stream()
                .findAny()
                .map(ViewStats::getHits)
                .orElse(0L);

        int confirmedRequests = requestRepository.countParticipationRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(confirmedRequests);
        eventFullDto.setViews(view);

        return eventFullDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.getInitiator().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d does not have access to event id=%d", userId, eventId));
        }

        if (event.isPublished()) {
            throw new EventChangeDeniedException("Only pending or canceled events can be changed");
        }

        if (updateEvent.getEventDate() != null) {
            LocalDateTime twoHoursLimit = LocalDateTime.now().plusHours(2);
            if (updateEvent.getEventDate().isBefore(twoHoursLimit)) {
                throw new DataValidationException("The minimum time should be 2 hours from the current one.");
            }
            event.setEventDate(updateEvent.getEventDate());
        }

        Category category = event.getCategory();
        if (updateEvent.getCategory() != null) {
            category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", updateEvent.getCategory())));
        }

        event.setCategory(category);

        EventMapper.updateEvent(event, updateEvent);

        if (updateEvent.isStatesNeedUpdate()) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    return cancelEvent(event);
                case SEND_TO_REVIEW:
                    return sendEvent(event);
                default: throw new IllegalArgumentException("Unknown event status action");
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (event.isPublished()) {
            throw new EventChangeDeniedException("Only pending events can be changed");
        }

        if (updateEvent.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime newStartTime = updateEvent.getEventDate();
            if (ChronoUnit.HOURS.between(now, newStartTime) < 1) {
                throw new DataValidationException("The start date of the event to be modified must be no earlier than one hour from the date of publication");
            }
            event.setEventDate(updateEvent.getEventDate());
        }

        Category category = event.getCategory();
        if (updateEvent.getCategory() != null) {
            category = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Category with id=%d was not found", updateEvent.getCategory())));
        }

        event.setCategory(category);

        EventMapper.updateEvent(event, updateEvent);

        if (updateEvent.isStatesNeedUpdate()) {
            switch (updateEvent.getStateAction()) {
                case PUBLISH_EVENT:
                    return publishEvent(event);
                case REJECT_EVENT:
                    return rejectEvent(event);
                default: throw new IllegalArgumentException("Unknown event status action");
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> find(GetEventsRequest request) {
        QEvent qEvent = QEvent.event;
        List<BooleanExpression> predicates = new ArrayList<>();

        predicates.add(qEvent.state.eq(EventState.PUBLISHED));

        if (request.getCategories() != null) {
            predicates.add(qEvent.category.id.in(request.getCategories()));
        }

        if (request.getText() != null) {
            predicates
                    .add(qEvent.annotation.containsIgnoreCase(request.getText())
                    .or(qEvent.description.containsIgnoreCase(request.getText())));
        }

        if (request.getPaid() != null) {
            predicates.add(qEvent.paid.eq(request.getPaid()));
        }

        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            validateTime(request.getRangeStart(), request.getRangeEnd());
            predicates.add(qEvent.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
        } else {
            predicates.add(qEvent.eventDate.after(LocalDateTime.now()));
        }

        Predicate predicate = predicates.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);

        List<Event> events = eventRepository.findAll(predicate, getPageable(request.getFrom(), request.getSize())).getContent();

        //id события и кол-во просмотров
        Map<Long, Long> eventIdAndCountViews = getEventViews(events);

        Function<Event, EventShortDto> mapper = mapEventToDto(eventIdAndCountViews);

        return events.stream()
                .filter(event -> {
                    if (request.getOnlyAvailable() && event.getParticipantLimit() > 0) {
                        return event.isAvailable();
                    }
                    return true;
                })
                .map(mapper)
                .sorted(EventDtoComparator.of(request.getSort()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> findByAdmin(GetEventsRequest request) {
        QEvent qEvent = QEvent.event;
        List<BooleanExpression> predicates = new ArrayList<>();

        if (request.getUsers() != null) {
            predicates.add(qEvent.initiator.id.in(request.getUsers()));
        }

        if (request.getStates() != null) {
            predicates.add(qEvent.state.in(request.getStates()));
        }

        if (request.getCategories() != null) {
            predicates.add(qEvent.category.id.in(request.getCategories()));
        }

        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            predicates.add(qEvent.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
        }

        if (request.getRangeStart() != null) {
            predicates.add(qEvent.eventDate.after(request.getRangeStart()));
        }

        if (request.getRangeEnd() != null) {
            predicates.add(qEvent.eventDate.before(request.getRangeEnd()));
        }

        Predicate predicate = predicates.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);

        List<Event> events;

        if (predicate != null) {
            events = eventRepository.findAll(predicate, getPageable(request.getFrom(), request.getSize())).getContent();
        } else {
            events = eventRepository.findAll(getPageable(request.getFrom(), request.getSize())).getContent();
        }

        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        //проверка что владелец события == userId
        if (!event.getInitiator().equals(user)) {
            throw new AccessDeniedException(String.format("User with id=%d is not authorized to view participants of event with id=%d", userId, eventId));
        }

        return requestRepository.findAllByEvent(event).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdate) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        //проверка что владелец события == userId
        if (!event.getInitiator().equals(user)) {
            throw new AccessDeniedException(String.format("User with id=%d is not authorized to view participants of event with id=%d", userId, eventId));
        }
        //проверка что есть места
        if (!event.isAvailable()) {
            throw new IllegalStateException("The participant limit has been reached");
        }

        List<ParticipationRequest> participationRequests = requestRepository.findAllByIdIn(eventRequestStatusUpdate.getRequestIds());
        List<ParticipationRequest> updatedParticipationRequests;

        //if если пришел CONFIRMED и else если пришел REJECTED
        if (eventRequestStatusUpdate.getStatus() == RequestStatus.CONFIRMED) {
            updatedParticipationRequests = participationRequests.stream()
                    .filter(req -> req.getEvent().equals(event))
                    .peek(req -> {
                        if (event.isAvailable() && req.isPending()) {
                            req.setStatus(RequestStatus.CONFIRMED);
                            event.setConfirmedParticipants(event.getConfirmedParticipants() + 1);
                        } else if (req.isPending()) {
                            req.setStatus(RequestStatus.REJECTED);
                        }
                    }).collect(Collectors.toList());
        } else {
            updatedParticipationRequests = participationRequests.stream()
                    .filter(req -> req.getEvent().equals(event))
                    .filter(req -> {
                        if (req.isConfirmed() || req.isCanceled()) {
                            throw new EventChangeDeniedException("Only pending events can be changed");
                        }
                        return true;
                    })
                    .peek(req -> req.setStatus(RequestStatus.REJECTED))
                    .collect(Collectors.toList());
        }
        eventRepository.save(event);
        return ParticipationRequestMapper.toEventRequestStatusUpdateResult(requestRepository.saveAll(updatedParticipationRequests));
    }

    private Function<Event, EventShortDto> mapEventToDto(Map<Long, Long> eventIdAndCountViews) {
        return event -> {
            EventShortDto eventShortDto = EventMapper.toEventShortDto(event);

            Long views = eventIdAndCountViews.getOrDefault(event.getId(), 0L);
            eventShortDto.setViews(views);

            return eventShortDto;
        };
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        Map<String, Long> uriAndEventId = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(id -> "/event/" + id, Function.identity()));

        List<ViewStats> viewStats = statsClient.getStatistics(
                ViewStatsRequest.builder()
                        .uris(uriAndEventId.keySet())
                        .unique(true)
                        .build()
        );

        return viewStats.stream()
                .collect(Collectors.toMap(
                        stat -> uriAndEventId.get(stat.getUri()),
                        ViewStats::getHits));
    }

    private EventFullDto sendEvent(Event event) {
        event.setState(EventState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private EventFullDto cancelEvent(Event event) {
        event.setState(EventState.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private EventFullDto rejectEvent(Event event) {
        event.setState(EventState.CANCELED);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private EventFullDto publishEvent(Event event) {
        if (event.isCanceled()) {
            throw new EventChangeDeniedException("Only pending events can be changed");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new DataValidationException("Время завершения раньше начала");
        }
    }

    private Pageable getPageable(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
