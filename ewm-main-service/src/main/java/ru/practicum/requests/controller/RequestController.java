package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable @Positive Long userId,
                                              @RequestParam @Positive Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable @Positive Long userId) {
        return requestService.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
