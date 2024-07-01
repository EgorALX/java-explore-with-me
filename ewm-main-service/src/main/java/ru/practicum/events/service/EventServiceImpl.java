package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.events.model.*;
import ru.practicum.events.model.EventState;
import ru.practicum.events.repository.*;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.exception.model.AccessException;
import ru.practicum.exception.model.ValidationException;
import ru.practicum.exception.model.ViolationException;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.events.model.EventState.*;
import static ru.practicum.requests.model.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final StatsClient statsClient;

    private final StatsClient client;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RequestRepository requestRepository;

    private final RequestMapper requestMapper;

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUser(Long userId, PageRequest pageRequest) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (events == null) {
            return List.of();
        }
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViews(events);
        return eventMapper.toEventShortDtoList(events, viewStats, confirmedRequests);
    }

    @Override
    @Transactional
    public EventFullDto add(Long userId, NewEventDto eventDto) {
        Long categoryId = eventDto.getCategory();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventMapper.fromNewEventDtoToEvent(eventDto, category, user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);
        Event newEvent = eventRepository.save(event);
        return eventMapper.eventToFullDto(newEvent, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByUserAndEvent(Long userId, @PathVariable Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViews(eventList);
        return eventMapper.eventToFullDto(event, confirmedRequests.getOrDefault(eventId, 0L),
                viewStats.getOrDefault(eventId, 0L));
    }

    @Override
    @Transactional
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (!event.getInitiator().equals(user)) {
            throw new AccessException("No access");
        }
        if (event.getState().equals(PUBLISHED)) {
            throw new AccessException("State have to be PENDING or CANCELED");
        }
        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(CANCELED);
                    break;
                default:
                    throw new NotFoundException("Unknown state : " + updateDto.getStateAction());
            }
        }
        Event updatedEvent = eventMapper.toUpdateEvent(event, updateDto);
        event = eventRepository.save(updatedEvent);
        List<Event> eventList = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventList);
        Map<Long, Long> viewStats = getViews(eventList);
        return eventMapper.eventToFullDto(event, confirmedRequests.getOrDefault(eventId, 0L),
                viewStats.getOrDefault(eventId, 0L));
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

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserAndEvent(Long userId, @PathVariable Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        return requestRepository.findAllByEvent(event).stream()
                .map(requestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    private Map<Long, Long> getViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> eventUrisAndIds = new HashMap<>();
        for (Event event : events) {
            String key = "/events/" + event.getId();
            eventUrisAndIds.put(key, event.getId());
        }
        List<ViewStatsDto> stats = client.retrieveAllStats(
                LocalDateTime.of(2020, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                LocalDateTime.of(2025, 12, 31, 23, 59, 59)
                        .format(DATE_TIME_FORMATTER), List.copyOf(eventUrisAndIds.keySet()), true);
        Map<Long, Long> result = new HashMap<>();
        for (ViewStatsDto stat : stats) {
            if (eventUrisAndIds.containsKey(stat.getUri())) {
                Long eventId = eventUrisAndIds.get(stat.getUri());
                result.put(eventId, stat.getHits());
            }
        }
        return result;
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
        return requestMapper.toResult(updatedRequests);
    }

    @Override
    @Transactional
    public EventFullDto adminUpdate(Long eventId, UpdateEventAdminRequest updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (!event.getState().equals(PENDING)) {
            throw new ViolationException("Event data cannot be changed");
        }
        if (LocalDateTime.now().isAfter(event.getEventDate())) {
            throw new ValidationException("The start must be no earlier than the publication date");
        }
        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(locationRepository.save(locationMapper.toModel(updateEventDto.getLocation())));
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(event.getCategory().getId()).orElseThrow(() ->
                    new NotFoundException("Category " + event.getCategory().getId() + " not found"));
            event.setCategory(category);
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getStateAction() != null) {
            switch (updateEventDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(PUBLISHED);
                    break;
                default:
                    throw new ValidationException("There is no such state " + updateEventDto.getStateAction());
            }
        }
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.eventToFullDto(updatedEvent, 0L, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> usersIds, List<EventState> states,
                                        List<Long> categoriesIds, LocalDateTime start, LocalDateTime end,
                                        PageRequest pageRequest) {
        List<Specification<Event>> specifications = new ArrayList<>();
        if (!states.isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state")).value(states));
        }
        if (!usersIds.isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("initiator").get("id"))
                    .value(usersIds));
        }
        if (!categoriesIds.isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(categoriesIds));
        }
        if (start != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("eventDate"), start));
        }
        if (end != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("eventDate"), end));
        }

        List<Event> events = eventRepository.findAll(specifications
                .stream()
                .reduce(Specification::and)
                .orElse(null), pageRequest).toList();
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViews(events);

        return events.stream()
                .map(event -> eventMapper.eventToFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewStats.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublished(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               EventSort sort, PageRequest pageRequest, HttpServletRequest request) {
        addStats(request);
        List<Specification<Event>> specifications = new ArrayList<>();
        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("state"))
                .value(List.of(PUBLISHED)));
        if (!text.isBlank()) {
            String searchText = "%" + text.toLowerCase() + "%";
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchText),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchText)));
        }
        if (!categories.isEmpty()) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("category").get("id"))
                    .value(categories));
        }
        if (paid != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
        }
        if (rangeStart != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder
                    .lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        specifications = specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
        Specification<Event> s = specifications
                .stream()
                .reduce(Specification::and).orElse(null);
        List<Event> events = eventRepository.findAll(s, pageRequest).toList();

        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        if (onlyAvailable) {
            events = events
                    .stream()
                    .filter(event -> event.getParticipantLimit() > confirmedRequests.getOrDefault(event.getId(), 0L))
                    .collect(Collectors.toList());
        }
        Map<Long, Long> viewStats = getViews(events);
        if (sort == null) return eventMapper.toEventShortDtoList(events, viewStats, confirmedRequests);
        switch (sort) {
            case VIEWS:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(event -> viewStats.getOrDefault(event.getId(), 0L)))
                        .collect(Collectors.toList());
            case EVENT_DATE:
                events = events
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .collect(Collectors.toList());
            default:
                return eventMapper.toEventShortDtoList(events, viewStats, confirmedRequests);
        }
    }

    @Transactional(readOnly = true)
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        addStats(request);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event " + eventId + " not found"));
        if (!event.getState().equals(PUBLISHED)) throw new NotFoundException("Event " + eventId + " not found");
        List<Event> events = List.of(event);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(events);
        Map<Long, Long> viewStats = getViews(events);
        return eventMapper.eventToFullDto(event, confirmedRequests.getOrDefault(eventId, 0L),
                viewStats.getOrDefault(eventId, 0L));
    }

    private void addStats(HttpServletRequest request) {
        statsClient.addHit(HitDto.builder()
                .app("explore-with-me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

}
