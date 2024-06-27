package ru.practicum.events.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.EventSort;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest);

    EventFullDto addEvent(Long userId, NewEventDto eventDto);

    EventFullDto getByUserAndEvent(Long userId, @PathVariable Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateDto);

    List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, @PathVariable Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest);
    List<EventFullDto> getEvents(List<Long> users, List<EventState> statesList, List<Long> categories, LocalDateTime rangeStart,
              LocalDateTime rangeEnd, PageRequest pageRequest);

    EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest updateEventDto);

    List<EventShortDto> getAllPublished(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort,
                                        PageRequest pageRequest, HttpServletRequest request);

    EventFullDto getById(Long id, HttpServletRequest request);
}
