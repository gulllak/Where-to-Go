package ru.practicum.mainservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Field: name. Error: must not be blank.")
    private String name;
}
