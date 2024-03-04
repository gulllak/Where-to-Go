package ru.practicum.mainservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@Builder
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(min = 1, max = 50)
    private String name;
}
