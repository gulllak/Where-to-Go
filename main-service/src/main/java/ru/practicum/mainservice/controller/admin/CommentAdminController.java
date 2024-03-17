package ru.practicum.mainservice.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CommentDto;
import ru.practicum.mainservice.dto.GetCommentsRequest;
import ru.practicum.mainservice.service.api.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDto getComment(@PathVariable("commentId") long commentId) {
        return commentService.getCommentByAdmin(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }


    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@RequestParam("users") List<Long> users,
                                       @RequestParam("events") List<Long> events,
                                       @RequestParam("text") String text,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return commentService.getCommentsByAdmin(
                GetCommentsRequest.builder()
                .users(users)
                .events(events)
                .text(text)
                .from(from)
                .size(size)
                .build()
        );
    }
}
