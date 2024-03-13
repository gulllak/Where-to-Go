package ru.practicum.mainservice.service.impl;

import ru.practicum.mainservice.dto.CategoryDto;
import ru.practicum.mainservice.dto.CompilationDto;
import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.NewCategoryDto;
import ru.practicum.mainservice.dto.NewEventDto;
import ru.practicum.mainservice.dto.NewUserRequest;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.dto.UpdateEventBaseRequest;
import ru.practicum.mainservice.dto.UserDto;
import ru.practicum.mainservice.dto.UserShortDto;
import ru.practicum.mainservice.exception.UpdateValidationException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static void updateCategory(Category category, CategoryDto categoryDto) {
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
    }

    public static User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

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
                .category(Mapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(Mapper.toUserShortDto(event.getInitiator()))
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
                .category(Mapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(Mapper.toUserShortDto(event.getInitiator()))
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

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .created(participationRequest.getCreated())
                .build();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<ParticipationRequest> participationRequests) {
        List<ParticipationRequestDto> confirmedRequests = participationRequests.stream()
                .filter(req -> req.getStatus() == RequestStatus.CONFIRMED)
                .map(Mapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = participationRequests.stream()
                .filter(req -> req.getStatus() == RequestStatus.REJECTED)
                .map(Mapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(Mapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventShortDtos)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static void updateCompilation(Compilation compilation, UpdateCompilationRequest updateCompilation) {
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }

        if (updateCompilation.getTitle() != null) {
            if (updateCompilation.getTitle().length() < 1 || updateCompilation.getTitle().length() > 50) {
                throw new UpdateValidationException("The title must be more than 1 and less than 50 characters");
            }
            compilation.setTitle(updateCompilation.getTitle());
        }
    }
}
