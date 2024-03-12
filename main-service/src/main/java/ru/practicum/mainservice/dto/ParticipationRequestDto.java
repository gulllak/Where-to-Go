package ru.practicum.mainservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.model.RequestStatus;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ParticipationRequestDto {
    private long id;
    private long requester;
    private long event;
    private RequestStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
