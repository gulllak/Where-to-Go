package ru.practicum.mainservice.mapper;

import ru.practicum.mainservice.dto.CategoryDto;
import ru.practicum.mainservice.dto.NewCategoryDto;
import ru.practicum.mainservice.model.Category;

public class CategoryMapper {
    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static void updateCategory(Category category, CategoryDto categoryDto) {
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
    }
}
