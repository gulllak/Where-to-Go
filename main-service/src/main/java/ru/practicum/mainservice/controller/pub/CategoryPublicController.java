package ru.practicum.mainservice.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.CategoryDto;
import ru.practicum.mainservice.service.api.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0") int from,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        return categoryService.getAll(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable("catId") long id) {
        return categoryService.getCategory(id);
    }
}
