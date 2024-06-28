package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
@Validated
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Starting addCategory method");
        CategoryDto result = categoryService.addCategory(dto);
        log.info("Successfully added category");
        return result;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long catId) {
        log.info("Starting deleteCategory method for categoryId: {}", catId);
        categoryService.deleteCategory(catId);
        log.info("Successfully deleted category with id: {}", catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid NewCategoryDto newCategoryDTO,
                                      @PathVariable @Positive Long catId) {
        log.info("Starting updateCategory method for categoryId: {}", catId);
        CategoryDto result = categoryService.updateCategory(catId, newCategoryDTO);
        log.info("Successfully updated category with id: {}", catId);
        return result;
    }
}
