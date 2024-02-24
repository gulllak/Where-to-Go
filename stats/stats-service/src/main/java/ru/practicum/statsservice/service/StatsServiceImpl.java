package ru.practicum.statsservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ResponseStatsDto;
import ru.practicum.statsservice.entity.Endpoint;
import ru.practicum.statsservice.mapper.EndpointMapper;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    private final EndpointMapper mapper;

    @Override
    public List<ResponseStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Endpoint> endpoints;

        if (uris == null) {
            endpoints = repository.findAllByRequestTimeAfterAndRequestTimeBefore(start, end);
        } else {
            endpoints = repository.findAllByRequestTimeAfterAndRequestTimeBeforeAndUriIn(start, end, uris);
        }

        if (unique) {
            return endpoints.stream()
                    .collect(Collectors.groupingBy(Endpoint::getUri, Collectors.mapping(Endpoint::getIp, Collectors.toSet())))
                    .entrySet().stream()
                    .map(entry -> mapper.toResponseDto(findAppByUri(endpoints, entry.getKey()), entry.getKey(), (long) entry.getValue().size()))
                    .collect(Collectors.toList());
        } else {
            return endpoints.stream().collect(Collectors.groupingBy(Endpoint::getUri, Collectors.counting()))
                    .entrySet().stream()
                    .map(entry -> mapper.toResponseDto(findAppByUri(endpoints, entry.getKey()), entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }
    }

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
