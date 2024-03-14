package ru.practicum.mainservice.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.EventBase;
import ru.practicum.mainservice.dto.EventFullDto;
import ru.practicum.mainservice.dto.GetEventsRequest;
import ru.practicum.mainservice.service.api.EventService;
import ru.practicum.statsclient.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;

    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<? extends EventBase> getEvents(@RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) String text,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest httpRequest) {
        List<? extends EventBase> result = eventService.find(
                GetEventsRequest.builder()
                        .categories(categories)
                        .text(text)
                        .paid(paid)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .onlyAvailable(onlyAvailable)
                        .sort(sort)
                        .from(from)
                        .size(size)
                        .build()
        );
        statsClient.addHit(httpRequest);

        return result;
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable("id") long eventId,
                                 HttpServletRequest httpRequest) {
        EventFullDto eventFullDto = eventService.getEvent(eventId);
        statsClient.addHit(httpRequest);

        return eventFullDto;
    }
}
