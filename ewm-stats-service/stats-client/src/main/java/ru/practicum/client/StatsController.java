package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient client;
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHitRecord(@RequestBody HitDto endpoint) {
        log.info("Creating hit record with data: {}", endpoint);
        client.post(endpoint);
        log.info("Created hit record successfully");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> fetchStats(@RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = PATTERN) LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Received stats request for start: {}, end: {}", start, end);
        List<ViewStatsDto> stats = client.get(start, end, uris, unique);
        log.info("Sent stats response with size: {}", stats.size());

        return stats;
    }

}
