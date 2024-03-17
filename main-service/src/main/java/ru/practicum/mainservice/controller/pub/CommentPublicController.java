package ru.practicum.mainservice.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.service.api.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping(params = "commentId")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDto getComment(@PathVariable("eventId") long eventId,
                                 @RequestParam("commentId") long commentId) {
        return commentService.getComment(eventId, commentId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable("eventId") long eventId) {
        return commentService.getComments(eventId);
    }
}
