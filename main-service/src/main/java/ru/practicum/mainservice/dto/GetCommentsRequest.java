package ru.practicum.mainservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetCommentsRequest {
    private List<Long> users;
    private List<Long> events;
    private String text;
    private int from;
    private int size;
}
