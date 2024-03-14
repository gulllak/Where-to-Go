package ru.practicum.mainservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Field: name. Error: must not be blank.")
    @Size(min = 1, max = 50)
    private String name;
}
