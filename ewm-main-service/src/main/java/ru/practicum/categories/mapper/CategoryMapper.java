package ru.practicum.categories.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category toCategory(NewCategoryDto dto) {
        return new Category(null, dto.getName());
    }
}
