package ru.practicum.mainservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank(message = "Field cannot be empty")
    @Size(min = 10, max = 2000, message = "Comment should be greater than 10 and less 2000 character.")
    private String text;
}
