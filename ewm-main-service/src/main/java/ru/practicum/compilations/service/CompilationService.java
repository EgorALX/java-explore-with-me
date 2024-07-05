package ru.practicum.compilations.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

public interface CompilationService {
    List<CompilationDto> getAll(Boolean pined, PageRequest pageRequest);

    CompilationDto getById(Long compId);

    CompilationDto add(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
