package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.CategoryDto;
import ru.practicum.mainservice.dto.NewCategoryDto;

public interface CategoryService {
    CategoryDto save(NewCategoryDto newCategoryDto);
}
