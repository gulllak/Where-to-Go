package ru.practicum.mainservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.model.EventState;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class EventFullDto extends EventBase {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private boolean paid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private String description;
    private int participantLimit;
    private EventState state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private Location location;
    private boolean requestModeration;
    private long confirmedRequests;
    private LocalDateTime publishedOn;
    private long views;
}
