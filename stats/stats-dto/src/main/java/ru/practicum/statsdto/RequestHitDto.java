package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RequestHitDto {
    @NotNull
    @NotEmpty(message = "Имя приложения не может быть пустым")
    private String app;
    @NotEmpty(message = "Uri не может быть пустым")
    private String uri;
    @NotEmpty(message = "IP-адрес не может быть пустым")
    private String ip;
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestTime;
}
