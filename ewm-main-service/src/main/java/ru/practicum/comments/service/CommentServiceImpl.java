package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public CommentDto addComment(NewCommentDto newCommentDto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User " + authorId + " not found"));
        UserShortDto userShortDto = userMapper.toUserShortDto(author);
        Long eventId = newCommentDto.getEventId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        Comment comment = commentRepository.save(commentMapper.toNewComment(newCommentDto, author, event));
        return commentMapper.toCommentDto(comment, userShortDto, eventId);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto, Long authorId, Long commentId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User " + authorId + " not found"));
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        if (!authorId.equals(comment.getAuthor().getId())) throw new ViolationException("Only owner can delete comment");
        Long eventId = comment.getEvent().getId();
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (updateCommentDto.getText() != null) comment.setText(updateCommentDto.getText());
        comment.setUpdated(true);
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(updatedComment, userMapper.toUserShortDto(author), eventId);
    }

    @Override
    @Transactional
    public void deleteUserComment(Long authorId, Long commentId) {
        userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("User " + authorId + " not found"));
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        if (!authorId.equals(comment.getAuthor().getId())) throw new ViolationException("Only owner can delete comment");
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getById(Long commentId) {
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        User author = comment.getAuthor();
        Long eventId = comment.getEvent().getId();
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        return commentMapper.toCommentDto(comment, userMapper.toUserShortDto(author), eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getAllCommentsByEvent(Long eventId, PageRequest pageRequest) {
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageRequest);
        return comments.stream().map(commentMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getAuthorComments(Long authorId, PageRequest pageRequest) {
        User author = userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("User " + authorId + " not found"));
        List<Comment> comments = commentRepository.findAllByAuthor(author, pageRequest);
        return comments.stream().map(commentMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAutorComments(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User " + authorId + " not found"));
        commentRepository.deleteAllByAuthor(author);
    }
}
