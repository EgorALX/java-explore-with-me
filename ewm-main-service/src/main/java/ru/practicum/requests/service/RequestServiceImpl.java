package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.events.dto.CountDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request " + requestId + " not found"));
        request.setStatus(CANCELED);
        return requestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, @PathVariable Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        return requestRepository.findAllByEvent(event).stream()
                .map(request -> new ParticipationRequestDto(request.getId(), request.getCreated().format(DATE_TIME_FORMATTER),
                        request.getEvent().getId(),
                        request.getRequester().getId(),
                        request.getStatus().toString())).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest statusUpdateRequest) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        Long participantLimit = event.getParticipantLimit();
        List<Event> events = List.of(event);
        Map<Long, Long> confirmed = getConfirmedRequests(events);
        Long countOfParticipant = confirmed.getOrDefault(eventId, 0L);
        if (participantLimit < countOfParticipant) {
            throw new ViolationException("exceeding the participant limit");
        }
        List<Request> updatedRequests = requestRepository
                .findAllByIdInAndStatusIs(statusUpdateRequest.getRequestIds(), Status.PENDING);
        if (updatedRequests.size() < statusUpdateRequest.getRequestIds().size()) {
            throw new ViolationException("Status cannot be changed");
        }
        Long count = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= count) {
            throw new ViolationException("Limit of requests reached");
        }
        for (Request request : updatedRequests) {
            if (participantLimit.equals(countOfParticipant)) {
                request.setStatus(Status.REJECTED);
            }
            if (statusUpdateRequest.getStatus().equals(CONFIRMED)) {
                request.setStatus(CONFIRMED);
                countOfParticipant++;
            } else {
                request.setStatus(Status.REJECTED);
            }
        }
        updatedRequests = requestRepository.saveAll(updatedRequests);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                new ArrayList<>(), new ArrayList<>());
        if (updatedRequests.isEmpty()) {
            return result;
        }
        for (Request request : updatedRequests) {
            ParticipationRequestDto dto = new ParticipationRequestDto(request.getId(),
                    request.getCreated().format(DATE_TIME_FORMATTER), request.getEvent().getId(),
                    request.getRequester().getId(), request.getStatus().toString());
            if (request.getStatus().equals(Status.CONFIRMED)) {
                result.getConfirmedRequests().add(dto);
            } else if (request.getStatus().equals(Status.REJECTED)) {
                result.getRejectedRequests().add(dto);
            }
        }
        return result;
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = new ArrayList<>();
        for (Event event : events) {
            ids.add(event.getId());
        }
        List<Object[]> rawResults = requestRepository.findRawByStatus(ids, CONFIRMED);
        List<CountDto> confirmedRequests = rawResults.stream()
                .map(result -> new CountDto((Long) result[0], (Long) result[1]))
                .collect(Collectors.toList());
        return confirmedRequests.stream()
                .collect(Collectors.toMap(CountDto::getId, CountDto::getCount));
    }
}
