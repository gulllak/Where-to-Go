package ru.practicum.mainservice.mapper;

import ru.practicum.mainservice.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainservice.dto.ParticipationRequestDto;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.RequestStatus;

import java.util.List;
import java.util.stream.Collectors;

public class ParticipationRequestMapper {
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
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = participationRequests.stream()
                .filter(req -> req.getStatus() == RequestStatus.REJECTED)
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
