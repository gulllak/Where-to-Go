package ru.practicum.mainservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private String email;
    private Long id;
    private String name;
}
