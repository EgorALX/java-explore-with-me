package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationServiceImpl;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationServiceImpl service;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pined,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getCompilations(pined, pageRequest);
    }

    @GetMapping("/compId")
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return service.getCompilation(compId);
    }

}
