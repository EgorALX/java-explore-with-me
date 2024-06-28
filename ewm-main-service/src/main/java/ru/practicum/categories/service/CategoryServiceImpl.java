package ru.practicum.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.model.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        Category category = categoryMapper.toCategory(dto);
        Category newCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(newCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Category " + catId + " not found"));
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDTO) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category " + catId + " not found"));
        category.setName(newCategoryDTO.getName());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category " + catId + " not found"));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(PageRequest pageRequest) {
        List<Category> categories = categoryRepository.findAll(pageRequest).toList();
        return categories.stream().map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }
}
