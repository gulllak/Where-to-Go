package ru.practicum.statsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ResponseStatsDto;
import ru.practicum.statsservice.exception.ValidationException;
import ru.practicum.statsservice.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<HttpStatus> addHit(@RequestBody @Valid RequestHitDto requestHitDto) {
        statsService.addHit(requestHitDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/stats")
    public List<ResponseStatsDto> getStatistics(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                          @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                          @RequestParam(value = "uris", required = false) List<String> uris,
                                          @RequestParam(value = "unique", defaultValue = "false") boolean unique
                                ) {
        validateTime(start, end);

        return statsService.getStatistics(start, end, uris, unique);
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ValidationException("Время завершения раньше начала");
        } else if (start.isEqual(end)) {
            throw new ValidationException("Время завершения равно времени начала");
        } else if (start.isAfter(end)) {
            throw new ValidationException("Время начала позже завершения");
        }
    }
}
