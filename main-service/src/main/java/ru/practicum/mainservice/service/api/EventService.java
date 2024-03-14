package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.GetEventsRequest;
import ru.practicum.mainservice.dto.NewEventDto;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.dto.UpdateEventAdminRequest;
import ru.practicum.mainservice.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto eventDto, long userId);

    List<EventShortDto> getEvents(long userId, int from, int size);

    EventFullDto getEvent(long userId, long eventId);

    EventFullDto getEvent(long eventId);

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEvent);

    List<EventFullDto> findByAdmin(GetEventsRequest request);

    List<ParticipationRequestDto> getEventParticipants(long userId, long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdate);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> find(GetEventsRequest request);
}
