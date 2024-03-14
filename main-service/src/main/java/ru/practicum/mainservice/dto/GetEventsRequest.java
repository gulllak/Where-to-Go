package ru.practicum.mainservice.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.mainservice.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class GetEventsRequest extends EventBase {
    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private String sort;
    private int from;
    private int size;
}
