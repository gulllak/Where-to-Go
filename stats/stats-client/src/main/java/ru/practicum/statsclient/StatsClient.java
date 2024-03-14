package ru.practicum.statsclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsclient.exception.ClientServerException;
import ru.practicum.statsdto.RequestHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsdto.ViewStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String application = "ewm-main-server";

    public StatsClient(@Value("${STAT-SERVER.URL}") String server, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(server))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public void addHit(HttpServletRequest httpRequest) {
        RequestHitDto requestHitDto = new RequestHitDto();
                requestHitDto.setApp(application);
                requestHitDto.setUri(httpRequest.getRequestURI());
                requestHitDto.setIp(httpRequest.getRemoteAddr());
                requestHitDto.setTimestamp(LocalDateTime.now());

        post("/hit", requestHitDto);
    }

    public List<ViewStats> getStatistics(ViewStatsRequest request) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> param = Map.of(
                "start", request.getStart().format(formatter),
                "end", request.getEnd().format(formatter),
                "uris", String.join(",", request.getUris()),
                "unique", request.isUnique()
        );
        ResponseEntity<Object> response;

        try {
            response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", param);
        } catch (Exception e) {
            throw new ClientServerException("Client not available");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ClientServerException("Client server return status: " + response.getStatusCode());
        }

        try {
            return objectMapper.convertValue(response.getBody(), new TypeReference<ArrayList<ViewStats>>() {
            });
        } catch (IllegalArgumentException e) {
            throw new ClientServerException("Error converting the response from the statistics server");
        }
    }
}
