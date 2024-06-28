package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventServiceImpl;
import ru.practicum.exception.model.ValidationException;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventServiceImpl service;

    @GetMapping
    public List<EventShortDto> getEventsByUser(
            @PathVariable @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0", required = false) @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Positive int size) {
        log.info("Starting getEventsByUser for userId: {}", userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<EventShortDto> list = service.getEventsByUser(userId, pageRequest);
        log.info("Finished getEventsByUser for userId: {}. Returning {} events.", userId, list.size());
        return list;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        log.info("Starting addEvent for userId: {}. Event data: {}", userId, dto);
        if (dto.getEventDate() != null
                && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Date error");
        }
        EventFullDto result = service.addEvent(userId, dto);
        log.info("Finished addEvent for userId: {}. Result: {}", userId, result);
        return result;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserAndEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Starting getByUserAndEvent for userId: {} and eventId: {}", userId, eventId);
        EventFullDto result = service.getByUserAndEvent(userId, eventId);
        log.info("Finished getByUserAndEvent for userId: {} and eventId: {}. Result: {}", userId, eventId, result);
        return result;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest
    ) {
        log.info("Starting updateEvent for userId: {}, eventId: {}. Request data: {}", userId, eventId, updateEventUserRequest);
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Date error");
        }
        EventFullDto result = service.updateEvent(userId, eventId, updateEventUserRequest);
        log.info("Finished updateEvent for userId: {}, eventId: {}. Result: {}", userId, eventId, result);
        return result;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Starting getRequestsByUserAndEvent for userId: {} and eventId: {}", userId, eventId);
        List<ParticipationRequestDto> requests = service.getRequestsByUserAndEvent(userId, eventId);
        log.info("Finished getRequestsByUserAndEvent for userId: {} and eventId: {}. Found {} requests.", userId, eventId, requests.size());
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Starting updateRequestStatus for userId: {}, eventId: {}. Status update request: {}", userId, eventId, statusUpdateRequest);
        EventRequestStatusUpdateResult result = service.updateRequestStatus(userId, eventId, statusUpdateRequest);
        log.info("Finished updateRequestStatus for userId: {}, eventId: {}. Result: {}", userId, eventId, result);
        return result;
    }
}
