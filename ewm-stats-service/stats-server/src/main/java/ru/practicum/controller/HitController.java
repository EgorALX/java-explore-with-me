package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {

    private final HitService service;

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS";

    @PostMapping("/hit")
    public void addHit(@Valid @RequestBody HitDto hitDto) {
        service.addHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> retrieveAllStats(@RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime start,
                                               @RequestParam @DateTimeFormat(pattern = DATE_PATTERN) LocalDateTime end,
                                               @RequestParam(required = false) List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return service.getAll(start, end, uris, unique);
    }
}
