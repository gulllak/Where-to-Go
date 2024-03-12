package ru.practicum.statsservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.entity.Endpoint;
import ru.practicum.statsservice.mapper.EndpointMapper;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    private final EndpointMapper mapper;

    @Override
    public List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Endpoint> endpoints;

        if (uris == null) {
            endpoints = repository.findAllByTimestampBetween(start, end);
        } else {
            endpoints = repository.findAllByTimestampBetweenAndUriIn(start, end, uris);
        }

        if (unique) {
            return endpoints.stream()
                    .collect(Collectors.groupingBy(Endpoint::getUri, Collectors.mapping(Endpoint::getIp, Collectors.toSet())))
                    .entrySet().stream()
                    .map(entry -> mapper.toResponseDto(findAppByUri(endpoints, entry.getKey()), entry.getKey(), (long) entry.getValue().size()))
                    .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            return endpoints.stream().collect(Collectors.groupingBy(Endpoint::getUri, Collectors.counting()))
                    .entrySet().stream()
                    .map(entry -> mapper.toResponseDto(findAppByUri(endpoints, entry.getKey()), entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparingLong(ViewStats::getHits).reversed())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void addHit(RequestHitDto requestHitDto) {
        repository.save(mapper.toEndpoint(requestHitDto));
    }

    private String findAppByUri(List<Endpoint> endpoints, String uri) {
        return endpoints.stream()
                .filter(endpoint -> endpoint.getUri().equals(uri))
                .map(Endpoint::getApp)
                .findFirst()
                .orElse(null);
    }
}
