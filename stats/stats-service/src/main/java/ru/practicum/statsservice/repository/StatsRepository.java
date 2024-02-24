package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.statsservice.entity.Endpoint;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Endpoint, Long> {
    List<Endpoint> findAllByRequestTimeAfterAndRequestTimeBeforeAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<Endpoint> findAllByRequestTimeAfterAndRequestTimeBefore(LocalDateTime start, LocalDateTime end);
}
