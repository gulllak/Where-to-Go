package ru.practicum.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RequestHitDto {
    @NotBlank(message = "Имя приложения не может быть пустым")
    private String app;
    @NotBlank(message = "Uri не может быть пустым")
    private String uri;
    @NotBlank(message = "IP-адрес не может быть пустым")
    private String ip;
    @NotNull(message = "Дата и время не могут отсутствовать")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
