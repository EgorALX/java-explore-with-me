package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequest;
import ru.practicum.events.model.EventParams;
import ru.practicum.events.model.EventState;
import ru.practicum.events.service.EventServiceImpl;
import ru.practicum.exception.model.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
public class EventAdminController {

    private final EventServiceImpl service;

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam(defaultValue = "") List<Long> users,
            @RequestParam(defaultValue = "") List<String> states,
            @RequestParam(defaultValue = "") List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size
    ) {
        log.info("Starting getAll. Parameters: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<EventState> statesList = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                EventState addedState = EventState.of(state)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
                statesList.add(addedState);
            }
        }
        List<EventFullDto> fullDtoList = service.getEvents(new EventParams(users, statesList, categories, rangeStart,
                rangeEnd), pageRequest);
        log.info("Finished getEvents. Returned {} events.", fullDtoList.size());
        return fullDtoList;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto adminUpdate(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Starting update for eventId: {}. Update request: {}", eventId, updateEventAdminRequest);
        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Date error");
        }
        EventFullDto updatedEvent = service.adminUpdate(eventId, updateEventAdminRequest);
        log.info("Finished update for eventId: {}. Updated event: {}", eventId, updatedEvent);
        return updatedEvent;
    }

}
