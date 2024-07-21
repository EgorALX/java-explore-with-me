package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        commentService.deleteComment(commentId);
        log.info("Deleted comment with ID: {}", commentId);
    }

    @GetMapping("/users/{authorId}")
    public List<CommentShortDto> getAuthorComments(@PathVariable @Positive Long authorId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        log.info("Fetching author comments for user ID: {}. Pagination: page={}, size={}", authorId, page, size);
        List<CommentShortDto> comments = commentService.getAuthorComments(authorId, pageRequest);
        log.info("Fetched {} comments for user ID: {}", comments.size(), authorId);
        return comments;
    }

    @DeleteMapping("/users/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthorComments(@PathVariable @Positive Long authorId) {
        log.info("Deleting all comments for user ID: {}", authorId);
        commentService.deleteAutorComments(authorId);
        log.info("Deleted all comments for user ID: {}", authorId);
    }
}
