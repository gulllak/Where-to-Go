package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.NewCommentDto;

public interface CommentService {
    CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto);
}
