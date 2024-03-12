package ru.practicum.mainservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class UpdateEventBaseRequest {
    private @Nullable String annotation;
    private @Nullable Long category;
    private @Nullable String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private @Nullable LocalDateTime eventDate;
    private @Nullable Location location;
    private @Nullable Boolean paid;
    @PositiveOrZero
    private @Nullable Integer participantLimit;
    private @Nullable Boolean requestModeration;
    private @Nullable String title;
}

