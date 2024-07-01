package ru.practicum.events.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.EventParams;
import ru.practicum.events.model.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest);

    EventFullDto add(Long userId, NewEventDto eventDto);

    EventFullDto getByUserAndEvent(Long userId, @PathVariable Long eventId);

    EventFullDto updateByEvent(Long userId, Long eventId, UpdateEventUserRequest updateDto);

    List<EventFullDto> getEvents(EventParams eventParams, PageRequest pageRequest);

    EventFullDto updateByEvent(Long eventId, UpdateEventAdminRequest updateEventDto);

    List<EventShortDto> getAllPublished(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                        PageRequest pageRequest, HttpServletRequest request);

    EventFullDto getById(Long id, HttpServletRequest request);
}
