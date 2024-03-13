package ru.practicum.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.ParticipationRequest;
import ru.practicum.mainservice.model.RequestStatus;
import ru.practicum.mainservice.model.User;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    int countParticipationRequestByEventIdAndStatus(long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByRequester(User requester);

    List<ParticipationRequest> findAllByEvent(Event event);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);
}
