package ru.practicum.requests.service;

import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                EventRequestStatusUpdateRequest statusUpdateRequest);
}
