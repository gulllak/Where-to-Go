package ru.practicum.mainservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class NewCompilationDto {
    private Set<Long> events;
    private boolean pinned = false;
    @NotBlank(message = "Title must not be blank")
    @Size(min = 1, max = 50, message = "Title must be more than 1 and less than 50 characters")
    private String title;
}
