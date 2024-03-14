package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.CategoryDto;
import ru.practicum.mainservice.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(NewCategoryDto newCategoryDto);

    void delete(long id);

    CategoryDto update(CategoryDto categoryDto);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getCategory(long id);
}
