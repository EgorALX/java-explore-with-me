package ru.practicum.events.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ViewStatsDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.client.StatsClient;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.repository.LocationRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;

import static ru.practicum.requests.model.Status.CONFIRMED;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsClient statsClient;

    private final CategoryMapper categoryMapper;

    private final UserMapper userMapper;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event fromNewEventDtoToEvent(NewEventDto dto, Category category, User user) {

        Location location = locationRepository.save(dto.getLocation());

        return new Event(
                0L,
                user,
                dto.getAnnotation(),
                category,
                dto.getDescription(),
                LocalDateTime.parse(dto.getEventDate(), formatter),
                location,
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());
    }

    public EventFullDto eventToFullDto(Event event, Integer confirmedRequests, Long views) {
        String publishedOn = null;
        if (event.getPublishedOn() != null) {
            publishedOn = event.getPublishedOn().format(formatter);
        }
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                event.getPaid(),
                event.getEventDate().format(formatter),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getDescription(),
                event.getParticipantLimit(),
                event.getState(),
                event.getCreatedOn().format(formatter),
                event.getLocation(),
                event.getRequestModeration(),
                confirmedRequests,
                publishedOn,
                views
        );
    }

    public EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                getConfirmedRequestsForEvent(event.getId()),
                event.getEventDate().format(formatter),
                event.getId(),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                getView(event.getId()));
    }

    public List<EventShortDto> toEventShortDtoList(List<Event> events,
                                                   Map<Long, Long> viewStatMap,
                                                   Map<Long, Integer> confirmedRequests) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            Long views = viewStatMap.getOrDefault(event.getId(), 0L);
            Integer confirmedRequestsCount = confirmedRequests.getOrDefault(event.getId(), 0);
            dtos.add(new EventShortDto(
                    event.getAnnotation(),
                    categoryMapper.toCategoryDto(event.getCategory()),
                    confirmedRequestsCount,
                    event.getEventDate().format(formatter),
                    event.getId(),
                    userMapper.toUserShortDto(event.getInitiator()),
                    event.getPaid(),
                    event.getTitle(),
                    views
            ));
        }
        dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());

        return dtos;
    }

    public Event toUpdateEvent(Event event, UpdateEventUserRequest updateDto) {
        if (updateDto.getAnnotation() != null) {
            event.setAnnotation(updateDto.getAnnotation());
        }
        if (updateDto.getCategoryId() != null) {
            Long id = updateDto.getCategoryId();
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Category " + id + " not found"));
            event.setCategory(category);
        }
        if (updateDto.getDescription() != null) {
            event.setDescription(updateDto.getDescription());
        }
        if (updateDto.getLocation() != null) {
            event.setLocation(updateDto.getLocation());
        }
        if (updateDto.getPaid() != null) {
            event.setPaid(updateDto.getPaid());
        }
        if (updateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateDto.getParticipantLimit());
        }
        if (updateDto.getRequestModeration() != null) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }
        if (updateDto.getTitle() != null) {
            event.setTitle(updateDto.getTitle());
        }
        return event;
    }

    private Long getView(Long eventId) {
        if (eventId == null || eventId <= 0) {
            return null;
        }
        String uriKey = "/events/" + eventId;
        List<ViewStatsDto> stats = statsClient.retrieveAllStats(
                LocalDateTime.of(2020, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                LocalDateTime.of(2025, 12, 31, 23, 59, 59).format(DATE_TIME_FORMATTER), List.of(uriKey), true);
        for (ViewStatsDto stat : stats) {
            if (stat.getUri().equals(uriKey)) {
                return stat.getHits();
            }
        }
        return null;
    }

    private Integer getConfirmedRequestsForEvent(Long eventId) {
        if (eventId == null || eventId <= 0) {
            return null;
        }
        List<Long> ids = List.of(eventId);
        List<CountDto> confirmedRequests = requestRepository.findByStatus(ids, CONFIRMED);
        if (confirmedRequests.isEmpty()) {
            return null;
        }
        return confirmedRequests.get(0).getCount();
    }
}