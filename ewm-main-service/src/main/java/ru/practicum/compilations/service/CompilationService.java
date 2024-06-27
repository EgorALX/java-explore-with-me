package ru.practicum.compilations.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pined, PageRequest pageRequest);

    CompilationDto getCompilation(Long compId);

    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
