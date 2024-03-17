package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.GetCommentsRequest;
import ru.practicum.mainservice.dto.NewCommentDto;
import ru.practicum.mainservice.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentDto getOwnComment(long userId, long commentId);

    CommentDto getComment(long eventId, long commentId);

    List<CommentDto> getOwnComments(long userId);

    void delete(long userId, long commentId);

    CommentDto update(long userId, long commentId, UpdateCommentRequest updateCommentRequest);

    List<CommentDto> getComments(long eventId);

    CommentDto getCommentByAdmin(long commentId);

    void deleteCommentByAdmin(long commentId);

    List<CommentDto> getCommentsByAdmin(GetCommentsRequest build);
}
