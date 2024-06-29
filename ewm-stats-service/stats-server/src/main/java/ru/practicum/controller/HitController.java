package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HitController {

    private final HitService service;

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody HitDto hitDto) {
        log.info("Adding hit with DTO: {}", hitDto);
        service.addHit(hitDto);
        log.info("Hit added successfully");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> retrieveAllStats(@RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
                                               @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
                                               @RequestParam(required = false) List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Retrieving stats from {} to {}, URIs: {}, Unique: {}", start, end, uris, unique);
        if (start.isAfter(end)) {
            throw new RuntimeException("Start i after end");
        }
        List<ViewStatsDto> stats = service.getAll(start, end, uris, unique);
        log.info("Stats retrieved successfully: {}", stats.size());
        return stats;
    }
}
