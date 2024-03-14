package ru.practicum.mainservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    private String title;
}
