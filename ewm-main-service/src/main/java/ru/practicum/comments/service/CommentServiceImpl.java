package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatsClient;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.dto.NewCommentDTO;
import ru.practicum.comments.dto.UpdateCommentDTO;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.dto.CountDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.requests.model.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final UserMapper userMapper;

    private final EventMapper eventMapper;

    private final RequestRepository requestRepository;

    private final StatsClient client;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final LocalDateTime MIN = LocalDateTime.of(2020, 1, 1, 0, 0);

    @Override
    @Transactional
    public CommentDto addComment(NewCommentDTO newCommentDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        UserShortDto userShortDto = userMapper.toUserShortDto(user);
        Long eventId = newCommentDTO.getEventId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViews(eventList);
        EventShortDto eventShortDto = eventMapper.toEventShortDto(event, viewStats.getOrDefault(eventId, 0L),
                confirmedRequests.getOrDefault(eventId, 0L));
        Comment comment = commentRepository.save(commentMapper.toNewComment(newCommentDTO, user, event));
        return commentMapper.toCommentDto(comment, userShortDto, eventShortDto);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDTO updateCommentDTO, Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        Long eventId = comment.getEvent().getId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViews(eventList);
        EventShortDto eventShortDto = eventMapper.toEventShortDto(event, viewStats.getOrDefault(eventId, 0L),
                confirmedRequests.getOrDefault(eventId, 0L));

        if (updateCommentDTO.getText() != null) comment.setText(updateCommentDTO.getText());
        comment.setUpdated(true);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(updatedComment, userMapper.toUserShortDto(user), eventShortDto);
    }

    @Override
    @Transactional
    public void deleteUserComment(Long userId, Long commentId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        if (!userId.equals(comment.getUser().getId())) throw new ViolationException("Only owner can delete comment");
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getById(Long commentId) {
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        User user = comment.getUser();
        Long eventId = comment.getEvent().getId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViews(eventList);
        EventShortDto eventShortDto = eventMapper.toEventShortDto(event, viewStats.getOrDefault(eventId, 0L),
                confirmedRequests.getOrDefault(eventId, 0L));

        return commentMapper.toCommentDto(comment, userMapper.toUserShortDto(user), eventShortDto);
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
        commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment " + commentId + " not found"));
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getAuthorComments(Long userId, PageRequest pageRequest) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Comment> comments = commentRepository.findAllByUserId(userId, pageRequest);
        return comments.stream().map(commentMapper::toShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserComments(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        commentRepository.deleteAllByUserId(userId);
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) return Collections.emptyMap();
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        List<CountDto> results = requestRepository.findByStatus(ids, CONFIRMED);
        return results.stream().collect(Collectors.toMap(CountDto::getEventId, CountDto::getCount));
    }

    private Map<Long, Long> getViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> eventUrisAndIds = new HashMap<>();
        for (Event event : events) {
            String key = "/events/" + event.getId();
            eventUrisAndIds.put(key, event.getId());
        }
        List<ViewStatsDto> stats = client.retrieveAllStats(
                MIN.format(DATE_TIME_FORMATTER),
                LocalDateTime.now().format(DATE_TIME_FORMATTER), List.copyOf(eventUrisAndIds.keySet()), true);
        Map<Long, Long> result = new HashMap<>();
        for (ViewStatsDto stat : stats) {
            if (eventUrisAndIds.containsKey(stat.getUri())) {
                Long eventId = eventUrisAndIds.get(stat.getUri());
                result.put(eventId, stat.getHits());
            }
        }
        return result;
    }
}
