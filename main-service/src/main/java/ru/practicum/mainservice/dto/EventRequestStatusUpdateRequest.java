package ru.practicum.mainservice.dto;

import lombok.Data;
import ru.practicum.mainservice.annotation.RejectedOrConfirmed;
import ru.practicum.mainservice.model.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Request IDs cannot be null")
    @NotEmpty(message = "Request IDs cannot be empty")
    private List<@NotNull(message = "Request ID cannot be null") Long> requestIds;
    @RejectedOrConfirmed
    private RequestStatus status;
}
