package ru.practicum.mainservice.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewUserRequest {
    @NotBlank()
    @Size(min = 6, max = 254)
    @Email(message = "Field email not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    @Size(min = 2, max = 250)
    @NotBlank(message = "Field name not valid")
    private String name;
}
