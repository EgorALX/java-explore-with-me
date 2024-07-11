package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{authorId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody @Valid NewCommentDto newCommentDTO,
                                 @PathVariable @Positive Long authorId) {
        log.info("Adding comment for user ID: {}.", authorId);
        CommentDto addedComment = commentService.addComment(newCommentDTO, authorId);
        log.info("Added comment for user ID: {}. Comment ID: {}", authorId, addedComment.getId());
        return addedComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDTO,
                                    @PathVariable @Positive Long authorId,
                                    @PathVariable @Positive Long commentId) {
        log.info("Updating comment for user ID: {} and comment ID: {}", authorId, commentId);
        CommentDto updatedComment = commentService.updateComment(updateCommentDTO, authorId, commentId);
        log.info("Updated comment for user ID: {} and comment ID: {}", authorId, commentId);
        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long authorId,
                              @PathVariable @Positive Long commentId) {
        log.info("Deleting comment for user ID: {} and comment ID: {}", authorId, commentId);
        commentService.deleteUserComment(authorId, commentId);
        log.info("Deleted comment for user ID: {} and comment ID: {}", authorId, commentId);
    }
}