package ru.practicum.categories.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto add(NewCategoryDto dto);

    void delete(Long catId);

    CategoryDto update(Long catId, NewCategoryDto newCategoryDTO);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(PageRequest pageRequest);
}
