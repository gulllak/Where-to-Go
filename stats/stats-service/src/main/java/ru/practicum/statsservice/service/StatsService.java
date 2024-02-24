package ru.practicum.statsservice.service;

import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ResponseStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ResponseStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void addHit(RequestHitDto requestHitDto);
}
