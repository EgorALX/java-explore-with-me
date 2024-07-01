package ru.practicum.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;

import java.util.ArrayList;
import java.util.HashSet;
import ru.practicum.events.repository.EventRepository;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(compilation.getId(),
                events,
                compilation.getPinned(),
                compilation.getTitle());
    }

    public Compilation toCompilation(NewCompilationDto dto) {
        HashSet<Event> events = new HashSet<>();
        if (dto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
        }
        return new Compilation(
                null,
                dto.getPinned(),
                dto.getTitle(),
                events);
    }

    public List<CompilationDto> toCompilationDtoList(
            List<Compilation> compilations,
            Map<Long, Long> confirmed,
            Map<Long, Long> views) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = new CompilationDto(compilation.getId(),
                    eventMapper.toEventShortDtoList(new ArrayList<>(compilation.getEvents()), views, confirmed),
                    compilation.getPinned(),
                    compilation.getTitle());
            result.add(compilationDto);
        }
        return result;
    }

}
