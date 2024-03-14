package ru.practicum.mainservice.mapper;

import ru.practicum.mainservice.dto.CompilationDto;
import ru.practicum.mainservice.dto.EventShortDto;
import ru.practicum.mainservice.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.exception.UpdateValidationException;
import ru.practicum.mainservice.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventShortDtos)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static void updateCompilation(Compilation compilation, UpdateCompilationRequest updateCompilation) {
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }

        if (updateCompilation.getTitle() != null) {
            if (updateCompilation.getTitle().length() < 1 || updateCompilation.getTitle().length() > 50) {
                throw new UpdateValidationException("The title must be more than 1 and less than 50 characters");
            }
            compilation.setTitle(updateCompilation.getTitle());
        }
    }
}
