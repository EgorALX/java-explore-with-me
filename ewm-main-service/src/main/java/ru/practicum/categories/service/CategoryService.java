package ru.practicum.categories.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDTO);

    CategoryDto getCategory(Long catId);

    List<CategoryDto> getAllCategories(PageRequest pageRequest);
}
