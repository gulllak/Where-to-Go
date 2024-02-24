package ru.practicum.statsdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
