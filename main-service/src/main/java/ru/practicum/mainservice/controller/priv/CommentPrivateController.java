package ru.practicum.mainservice.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.NewCommentDto;
import ru.practicum.mainservice.dto.UpdateCommentRequest;
import ru.practicum.mainservice.service.api.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable("userId") long userId,
                                 @PathVariable("eventId") long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDto getOwnComment(@PathVariable("userId") long userId,
                                    @PathVariable("commentId") long commentId) {
        return commentService.getOwnComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentDto> getOwnComments(@PathVariable("userId") long userId) {
        return commentService.getOwnComments(userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") long userId,
                              @PathVariable("commentId") long commentId) {
        commentService.delete(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDto updateComment(@PathVariable("userId") long userId,
                                    @PathVariable("commentId") long commentId,
                                    @RequestBody @Valid UpdateCommentRequest updateCommentRequest) {
        return commentService.update(userId, commentId, updateCommentRequest);
    }
}
