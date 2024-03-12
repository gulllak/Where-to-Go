package ru.practicum.statsdto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class RequestHitDto {
    @NotNull
    @NotEmpty(message = "Имя приложения не может быть пустым")
    private String app;
    @NotEmpty(message = "Uri не может быть пустым")
    private String uri;
    @NotEmpty(message = "IP-адрес не может быть пустым")
    private String ip;
    @NotNull(message = "Дата и время не могут отсутствовать")
    private LocalDateTime timestamp;
}
