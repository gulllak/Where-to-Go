package ru.practicum.statsservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ResponseStatsDto;
import ru.practicum.statsservice.entity.Endpoint;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EndpointMapper {
    Endpoint toEndpoint(RequestHitDto requestHitDto);

    ResponseStatsDto toResponseDto(String app, String uri, Long hits);
}
