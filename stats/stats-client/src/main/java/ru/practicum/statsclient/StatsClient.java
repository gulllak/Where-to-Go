package ru.practicum.statsclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsdto.ViewStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String application = "ewm-main-server";

    public StatsClient(RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public void addHit(HttpServletRequest httpRequest) {
        RequestHitDto requestHitDto = RequestHitDto.builder()
                .app(application)
                .ip(httpRequest.getRemoteAddr())
                .uri(httpRequest.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        post("/hit", requestHitDto);
    }

    public List<ViewStats> getStatistics(ViewStatsRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> param = Map.of(
                "start", request.getStart().format(formatter),
                "end", request.getEnd().format(formatter),
                "uris", request.getUris().toArray(),
                "unique", request.isUnique()
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", param);
        return objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
    }
}
