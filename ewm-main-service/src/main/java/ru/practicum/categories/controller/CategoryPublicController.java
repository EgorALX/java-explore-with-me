package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/categories")
@RestController
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable @Positive Long catId) {
        log.info("Starting getById method for categoryId: {}", catId);
        CategoryDto result = categoryService.getById(catId);
        log.info("Successfully retrieved category with id: {}", catId);
        return result;
    }

    @GetMapping
    public List<CategoryDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Starting getAll method with pagination parameters: from={}, size={}", from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CategoryDto> result = categoryService.getAll(pageRequest);
        log.info("Successfully retrieved all categories with pagination parameters: from={}, size={}", from, size);
        return result;
    }
}
