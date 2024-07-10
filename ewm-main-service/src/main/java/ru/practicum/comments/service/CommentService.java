package ru.practicum.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.dto.UpdateCommentDTO;

import java.util.List;

public interface CommentService {
    CommentDto addComment(NewCommentDTO newCommentDTO, Long userId);

    CommentDto updateComment(UpdateCommentDTO updateCommentDTO, Long userId, Long commentId);

    void deleteUserComment(Long userId, Long commentId);

    CommentDto getById(Long commentId);

    List<CommentShortDto> getAllCommentsByEvent(Long eventId, PageRequest pageRequest);

    void deleteComment(Long commentId);

    List<CommentShortDto> getAuthorComments(Long userId, PageRequest pageRequest);

    void deleteUserComments(Long userId);
}
