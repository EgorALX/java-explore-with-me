package ru.practicum.events.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CategoryMapper categoryMapper;

    private final UserMapper userMapper;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    public Event fromNewEventDtoToEvent(NewEventDto dto, Category category, User user) {

        Location location = locationRepository.save(dto.getLocation());

        return new Event(
                0L,
                user,
                dto.getAnnotation(),
                category,
                dto.getDescription(),
                dto.getEventDate(),
                location,
                dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration(),
                dto.getTitle());
    }

    public EventFullDto eventToFullDto(Event event, Long confirmedRequests, Long views) {
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

    public EventShortDto toEventShortDto(Event event, Long views, Long confirmed) {
        return new EventShortDto(
                event.getAnnotation(),
                categoryMapper.toCategoryDto(event.getCategory()),
                confirmed,
                event.getEventDate().format(formatter),
                event.getId(),
                userMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views);
    }

    public List<EventShortDto> toEventShortDtoList(List<Event> events,
                                                   Map<Long, Long> viewStatMap,
                                                   Map<Long, Long> confirmedRequests) {
        List<EventShortDto> dtos = new ArrayList<>();
        for (Event event : events) {
            Long views = viewStatMap.getOrDefault(event.getId(), 0L);
            Long confirmedRequestsCount = confirmedRequests.getOrDefault(event.getId(), 0L);
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
}