package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.EventSort;
import ru.practicum.events.service.EventServiceImpl;
import ru.practicum.exception.model.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventServiceImpl service;

    @GetMapping
    public List<EventShortDto> getAllPublished(
            @RequestParam(defaultValue = "") @Size(min = 2) String text,
            @RequestParam(defaultValue = "") List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
        log.info("Starting getAllPublished. Parameters: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeStart!= null && rangeEnd!= null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Date exception");
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<EventShortDto> events = service.getAllPublished(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                pageRequest, request);
        log.info("Finished getAllPublished. Returned {} events.", events.size());
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Long id,
                                HttpServletRequest request) {
        log.info("Starting getById for id: {}. Request: {}", id, request);
        EventFullDto event = service.getById(id, request);
        log.info("Finished getById for id: {}. Result: {}", id, event);
        return event;
    }
}
