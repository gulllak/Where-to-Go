package ru.practicum.mainservice.service.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.GetCommentsRequest;
import ru.practicum.mainservice.dto.NewCommentDto;
import ru.practicum.mainservice.dto.UpdateCommentRequest;
import ru.practicum.mainservice.exception.AccessDeniedException;
import ru.practicum.mainservice.exception.EntityNotFoundException;
import ru.practicum.mainservice.mapper.CommentMapper;
import ru.practicum.mainservice.model.Comment;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.QComment;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.CommentRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.api.CommentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto addComment(long userId, long eventId, NewCommentDto newCommentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        if (!event.isPublished()) {
            throw new EntityNotFoundException("You cannot view an unpublished event.");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));

        Comment comment = CommentMapper.toComment(newCommentDto);

        comment.setEvent(event);
        comment.setAuthor(author);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getOwnComment(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (comment.getAuthor().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d does not have access to comment id=%d", userId, commentId));
        }

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getOwnComments(long userId) {
        return commentRepository.findAllByAuthorId(userId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getComment(long eventId, long commentId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getComments(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));

        return commentRepository.findAllByEvent(event).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByAdmin(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        return CommentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getCommentsByAdmin(GetCommentsRequest request) {
        QComment qComment = QComment.comment;

        List<BooleanExpression> predicates = new ArrayList<>();

        if (request.getUsers() != null) {
            predicates.add(qComment.author.id.in(request.getUsers()));
        }

        if (request.getEvents() != null) {
            predicates.add(qComment.event.id.in(request.getEvents()));
        }

        if (request.getText() != null) {
            predicates.add(qComment.text.containsIgnoreCase(request.getText()));
        }

        Predicate predicate = predicates.stream()
                .reduce(BooleanExpression::and)
                .orElse(null);
        List<Comment> comments;

        if (predicate != null) {
            comments = commentRepository.findAll(predicate, getPageable(request.getFrom(), request.getSize())).getContent();
        } else {
            comments = commentRepository.findAll(getPageable(request.getFrom(), request.getSize())).getContent();
        }

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(long userId, long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (comment.getAuthor().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d does not have access to comment id=%d", userId, commentId));
        }
        commentRepository.delete(comment);
    }

    @Transactional
    @Override
    public CommentDto update(long userId, long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));

        if (comment.getAuthor().getId() != userId) {
            throw new AccessDeniedException(String.format("User with id=%d does not have access to comment id=%d", userId, commentId));
        }

        comment.setText(updateCommentRequest.getText());
        comment.setUpdated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Pageable getPageable(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
