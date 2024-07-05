package ru.practicum.events.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.EventParams;
import ru.practicum.events.model.GetEventsRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest);

    EventFullDto add(Long userId, NewEventDto eventDto);

    EventFullDto getByUserAndEvent(Long userId, @PathVariable Long eventId);

    EventFullDto updateByEvent(Long userId, Long eventId, UpdateEventUserRequest updateDto);

    List<EventFullDto> getEvents(EventParams eventParams, PageRequest pageRequest);

    EventFullDto updateByEvent(Long eventId, UpdateEventAdminRequest updateEventDto);

    List<EventShortDto> getAllPublished(GetEventsRequest getEventsRequest, PageRequest pageRequest);

    EventFullDto getById(Long id, HttpServletRequest request);
}
