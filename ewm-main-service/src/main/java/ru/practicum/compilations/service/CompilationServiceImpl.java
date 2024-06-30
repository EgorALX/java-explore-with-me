package ru.practicum.compilations.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.model.NotFoundException;


import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final CompilationMapper compilationMapper;

    private final EventRepository eventRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pined, PageRequest pageRequest) {
        List<Compilation> compilations;
        if (pined == null) {
            compilations = compilationRepository.findAll(pageRequest).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pined, pageRequest);
        }
        return compilations.stream().map(compilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto dto) {
        Compilation compilation = compilationMapper.toCompilation(dto);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
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
        HashSet<Event> events = new HashSet<>(eventRepository.findAllByIdIn(dto.getEvents()));
        if (dto.getEvents() != null) {
            compilation.setEvents(events);
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }
}
