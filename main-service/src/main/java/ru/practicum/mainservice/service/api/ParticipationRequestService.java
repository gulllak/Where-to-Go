package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto save(long userId, long eventId);

    List<ParticipationRequestDto> getUserRequests(long userId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
