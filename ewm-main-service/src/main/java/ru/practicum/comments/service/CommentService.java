package ru.practicum.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(NewCommentDto newCommentDTO, Long userId);

    CommentDto updateComment(UpdateCommentDto updateCommentDto, Long userId, Long commentId);

    void deleteUserComment(Long userId, Long commentId);

    CommentDto getById(Long commentId);

    List<CommentShortDto> getAllCommentsByEvent(Long eventId, PageRequest pageRequest);

    void deleteComment(Long commentId);

    List<CommentShortDto> getAuthorComments(Long userId, PageRequest pageRequest);

    void deleteAutorComments(Long userId);
}
