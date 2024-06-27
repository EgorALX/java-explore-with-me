package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationServiceImpl service;

    @PostMapping
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto dto) {
        return service.addCompilation(dto);
    }

    @DeleteMapping("/compId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/compId")
    public CompilationDto updateCompilation(@PathVariable Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return service.updateCompilation(compId, updateCompilationRequest);
    }
}
