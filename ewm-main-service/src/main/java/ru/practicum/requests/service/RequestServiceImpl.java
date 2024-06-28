package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.events.model.EventState.PUBLISHED;
import static ru.practicum.requests.model.Status.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final EventMapper eventMapper;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (event.getInitiator().equals(user)) {
            throw new ViolationException("Event initiator is unable to make a request");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ViolationException("Event not found");
        }
        if (requestRepository.existsByEventAndRequester(event, user)) {
            throw new ViolationException("Unable to make a request");
        }
        Long count = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= count) {
            throw new ViolationException("Limit of requests reached");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        Status status;
        if (event.getRequestModeration()) {
            status = Status.PENDING;
        } else {
            status = Status.CONFIRMED;
        }
        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(status);
        }
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request " + requestId + " not found"));
        request.setStatus(CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}
