package ru.practicum.comments.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Comment toNewComment(NewCommentDTO newCommentDTO, User user, Event event) {
        return new Comment(0L, newCommentDTO.getText(), event, user, LocalDateTime.now(), false);
    }

    public CommentDto toCommentDto(Comment comment, UserShortDto userShortDto, EventShortDto eventShortDto) {
        return new CommentDto(comment.getId(), comment.getText(), userShortDto, eventShortDto,
                comment.getCreated().format(formatter), comment.getUpdated());
    }

    public CommentShortDto toShortDto(Comment comment) {
        return new CommentShortDto(comment.getId(), comment.getText(), userMapper.toUserShortDto(comment.getUser()),
                comment.getCreated().format(formatter), comment.getUpdated());
    }
}
