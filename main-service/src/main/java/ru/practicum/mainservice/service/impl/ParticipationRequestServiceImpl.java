package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.exception.AccessDeniedException;
import ru.practicum.mainservice.exception.EntityNotFoundException;
import ru.practicum.mainservice.mapper.ParticipationRequestMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.ParticipationRequestRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.api.ParticipationRequestService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        return requestRepository.findAllByRequester(requester).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto save(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.isPending() || event.isCanceled()) {
            throw new IllegalStateException("Event not PUBLISHED");
        }

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        if (event.getInitiator().getId() == userId) {
            throw new IllegalStateException(String.format("A user with id=%d cannot send a request to his own event", userId));
        }

        if (event.getParticipantLimit() != 0 && !event.isAvailable()) {
            throw new IllegalStateException("Maximum number of participants");
        }

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .event(event)
                .requester(requester)
                .build();

        //если для события отключена пре-модерация, или лимит 0 тогда авто CONFIRMED
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedParticipants(event.getConfirmedParticipants() + 1);
            eventRepository.save(event);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
        ParticipationRequest participationRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Request with id=%d was not found", requestId)));
        if (!participationRequest.getRequester().equals(requester)) {
            throw new AccessDeniedException("User cannot change stranger request");
        }

        Event event = participationRequest.getEvent();
        if (participationRequest.isConfirmed()) {
            event.setConfirmedParticipants(event.getConfirmedParticipants() - 1);
        }
        participationRequest.setStatus(RequestStatus.CANCELED);

        eventRepository.save(event);

        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }
}
