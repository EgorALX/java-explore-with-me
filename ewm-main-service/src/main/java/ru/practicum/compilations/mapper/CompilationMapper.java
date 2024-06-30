package ru.practicum.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;

import java.util.HashSet;
import ru.practicum.events.repository.EventRepository;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(),
                compilation.getEvents().stream().map(eventMapper::toEventShortDto).collect(Collectors.toList()),
                compilation.getPinned(),
                compilation.getTitle());
    }

    public Compilation toCompilation(NewCompilationDto dto) {
        HashSet<Event> events = new HashSet<>();
        if (dto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
        }
        Compilation compilation = new Compilation(
                null,
                dto.getPinned(),
                dto.getTitle(),
                events);
        return compilation;
    }

}
