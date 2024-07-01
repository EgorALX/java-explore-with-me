package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationServiceImpl;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationAdminController {

    private final CompilationServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Starting add");
        CompilationDto result = service.add(dto);
        log.info("Compilation added successfully");
        return result;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        log.info("Starting delete for compId: {}", compId);
        service.delete(compId);
        log.info("Compilation deleted successfully for compId: {}", compId);
    }


    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId, @RequestBody @Valid UpdateCompilationRequest dto) {
        log.info("Starting update for compId: {} with request: {}", compId, dto);
        CompilationDto result = service.update(compId, dto);
        log.info("Compilation updated successfully for compId: {}", compId);
        return result;
    }
}
