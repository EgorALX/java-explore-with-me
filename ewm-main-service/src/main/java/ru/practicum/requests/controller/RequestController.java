package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@PathVariable @Positive Long userId,
                                       @RequestParam @Positive Long eventId) {
        log.info("Starting addRequest for userId: {}, eventId: {}", userId, eventId);
        ParticipationRequestDto participationRequestDto = requestService.addRequest(userId, eventId);
        log.info("Request added successfully for userId: {}, eventId: {}", userId, eventId);
        return participationRequestDto;
    }

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable @Positive Long userId) {
        log.info("Starting getRequests for userId: {}", userId);
        List<ParticipationRequestDto> requests = requestService.getAllRequests(userId);
        log.info("Fetched {} requests for userId: {}", requests.size(), userId);
        return requests;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable @Positive Long userId,
                                          @PathVariable @Positive Long requestId) {
        log.info("Starting cancelRequest for userId: {}, requestId: {}", userId, requestId);
        ParticipationRequestDto participationRequestDto = requestService.cancelRequest(userId, requestId);
        log.info("Request cancelled successfully for userId: {}, requestId: {}", userId, requestId);
        return participationRequestDto;
    }
}
