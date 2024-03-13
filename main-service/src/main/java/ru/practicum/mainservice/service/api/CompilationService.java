package ru.practicum.mainservice.service.api;

import ru.practicum.mainservice.dto.CompilationDto;
import ru.practicum.mainservice.dto.NewCompilationDto;
import ru.practicum.mainservice.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    void delete(long compId);

    CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compId);
}
