package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.dto.UpdateCommentDTO;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestBody @Valid NewCommentDTO newCommentDTO,
                                 @PathVariable @Positive Long userId) {
        log.info("Adding comment for user ID: {}.", userId);
        CommentDto addedComment = commentService.addComment(newCommentDTO, userId);
        log.info("Added comment for user ID: {}. Comment ID: {}", userId, addedComment.getId());
        return addedComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDTO updateCommentDTO,
                                    @PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId) {
        log.info("Updating comment for user ID: {} and comment ID: {}", userId, commentId);
        CommentDto updatedComment = commentService.updateComment(updateCommentDTO, userId, commentId);
        log.info("Updated comment for user ID: {} and comment ID: {}", userId, commentId);
        return updatedComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        log.info("Deleting comment for user ID: {} and comment ID: {}", userId, commentId);
        commentService.deleteUserComment(userId, commentId);
        log.info("Deleted comment for user ID: {} and comment ID: {}", userId, commentId);
    }
}