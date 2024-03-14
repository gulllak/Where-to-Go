package ru.practicum.mainservice.mapper;

import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.NewEventDto;
import ru.practicum.mainservice.dto.UpdateEventBaseRequest;
import ru.practicum.mainservice.exception.UpdateValidationException;
import ru.practicum.mainservice.model.Event;

public class EventMapper {
    public static Event toEvent(NewEventDto eventDto) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .participantLimit(eventDto.getParticipantLimit())
                .paid(eventDto.isPaid())
                .eventDate(eventDto.getEventDate())
                .location(eventDto.getLocation())
                .requestModeration(eventDto.isRequestModeration())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedParticipants())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedParticipants())
                .build();
    }

    public static void updateEvent(Event event, UpdateEventBaseRequest updateEvent) {
        if (updateEvent.getAnnotation() != null) {
            if (updateEvent.getAnnotation().length() < 20 || updateEvent.getAnnotation().length() > 2000) {
                throw new UpdateValidationException("The annotation must be more than 3 and less than 120 characters");
            }
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getDescription() != null) {
            if (updateEvent.getDescription().length() < 20 || updateEvent.getDescription().length() > 7000) {
                throw new UpdateValidationException("The description must be more than 20 and less than 7000 characters");
            }
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            if (updateEvent.getParticipantLimit() < 0) {
                throw new UpdateValidationException("The participantLimit must be more or equals 0");
            }
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            if (updateEvent.getTitle().length() < 3 || updateEvent.getTitle().length() > 120) {
                throw new UpdateValidationException("The title must be more than 3 and less than 120 characters");
            }
            event.setTitle(updateEvent.getTitle());
        }
    }
}
