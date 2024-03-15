package ru.practicum.mainservice.mapper;

import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.NewCommentDto;
import ru.practicum.mainservice.model.Comment;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .build();
    }
}
