package ru.practicum.compilations.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ViewStatsDto;
import ru.practicum.client.StatsClient;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.CountDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;


import org.springframework.transaction.annotation.Transactional;
import ru.practicum.requests.repository.RequestRepository;

import static ru.practicum.requests.model.Status.CONFIRMED;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final StatsClient client;

    private final EventMapper eventMapper;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pined, PageRequest pageRequest) {
        List<Compilation> compilations;
        if (pined == null) {
            compilations = compilationRepository.findAll(pageRequest).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pined, pageRequest);
        }
        Set<Event> allEvents = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());
        Map<Long, Long> views = getViews(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);

        List<CompilationDto> compilationDtos;

        compilationDtos = compilationMapper.toCompilationDtoList(compilations, confirmed, views);
        return compilationDtos;
    }

    private Map<Long, Long> getConfirmedRequests(Set<Event> events) {
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

    private Map<Long, Long> getViews(Set<Event> events) {
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

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        Set<Event> allEvents = compilation.getEvents();
        Map<Long, Long> views = getViews(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);

        List<EventShortDto> eventShortDtos = allEvents.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmed.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto dto) {
        HashSet<Event> events = new HashSet<>();
        if (dto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
        }
        Compilation compilation = compilationMapper.toCompilation(dto, events);

        Set<Event> allEvents = compilation.getEvents();
        Map<Long, Long> views = getViews(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);

        List<EventShortDto> eventShortDtos = allEvents.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmed.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation), eventShortDtos);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation not found"));
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        List<Long> ids = dto.getEvents();
        if (ids == null) {
            throw new NotFoundException("Events not found");
        }
        HashSet<Event> events = new HashSet<>(eventRepository.findAllByIdIn(ids));
        if (dto.getEvents() != null) {
            compilation.setEvents(events);
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        Set<Event> allEvents = compilation.getEvents();
        Map<Long, Long> views = getViews(allEvents);
        Map<Long, Long> confirmed = getConfirmedRequests(allEvents);

        List<EventShortDto> eventShortDtos = allEvents.stream()
                .map(event -> eventMapper.toEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmed.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation), eventShortDtos);
    }
}
