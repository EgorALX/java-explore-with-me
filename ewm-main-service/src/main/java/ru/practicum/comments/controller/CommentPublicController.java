package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService service;

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable long commentId) {
        log.info("Getting comment by ID: {}", commentId);
        CommentDto comment = service.getById(commentId);
        log.info("Found comment: {}", comment);
        return comment;
    }

    @GetMapping("/events/{eventId}")
    public List<CommentShortDto> getAllCommentsByEvent(@PathVariable Long eventId, @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        log.info("Fetching comments for event ID: {} with pagination", eventId);
        List<CommentShortDto> comments = service.getAllCommentsByEvent(eventId, pageRequest);
        log.info("Fetched {} comments for event ID: {}", comments.size(), eventId);
        return comments;
    }
}
