package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationServiceImpl;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationPublicController {

    private final CompilationServiceImpl service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Starting getCompilations with params: pinned={}, from={}, size={}", pinned, from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CompilationDto> compilations = service.getCompilations(pinned, pageRequest);
        log.info("Fetched {} compilations", compilations.size());
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        log.info("Starting getCompilation for compId: {}", compId);
        CompilationDto compilationDto = service.getCompilation(compId);
        log.info("Compilation fetched successfully for compId: {}", compId);
        return compilationDto;
    }

}
