package ru.practicum.mainservice.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainservice.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.NewEventDto;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.dto.UpdateEventUserRequest;
import ru.practicum.mainservice.service.api.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable("userId") long userId,
                          @RequestParam(value = "from", defaultValue = "0") int from,
                          @RequestParam(value = "size", defaultValue = "10") int size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable("userId") long userId,
                                 @Valid @RequestBody NewEventDto eventDto) {
        return eventService.addEvent(eventDto, userId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable("userId") long userId,
                                 @PathVariable("eventId") long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable("userId") long userId,
                                    @PathVariable("eventId") long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEvent) {
        return eventService.updateEventByUser(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable("userId") long userId,
                                                        @PathVariable("eventId") long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable("userId") long userId,
                                                              @PathVariable("eventId") long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdate) {
        return eventService.changeRequestStatus(userId, eventId, eventRequestStatusUpdate);
    }
}
