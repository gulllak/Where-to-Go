package ru.practicum.statsdto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class ViewStatsRequest {
    @Builder.Default
    private LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
    @Builder.Default
    private LocalDateTime end = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
    @Builder.Default
    private Set<String> uris = new HashSet<>();
    private boolean unique;
}
