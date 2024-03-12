package ru.practicum.mainservice.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.CompilationDto;
import ru.practicum.mainservice.dto.NewCompilationDto;
import ru.practicum.mainservice.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.exception.EntityNotFoundException;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.QCompilation;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.api.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        if(newCompilationDto.getEvents() != null) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        }

        Compilation compilation = Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();

        return Mapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId)));

        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId)));

        if (updateCompilation.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilation.getEvents()));
            compilation.setEvents(events);
        }

        Mapper.updateCompilation(compilation, updateCompilation);

        return Mapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public Set<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        BooleanExpression predicate = null;
        if (pinned != null) {
            predicate = QCompilation.compilation.pinned.eq(pinned);
        }
        List<Compilation> compilations;

        if (predicate != null) {
            compilations = compilationRepository.findAll(predicate, getPageable(from, size)).getContent();
        } else {
            compilations = compilationRepository.findAll(getPageable(from, size)).getContent();
        }

        return compilations.stream()
                .map(Mapper::toCompilationDto)
                .collect(Collectors.toSet());
    }

    @Override
    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Compilation with id=%d was not found", compId)));

        return Mapper.toCompilationDto(compilation);
    }

    private Pageable getPageable(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
