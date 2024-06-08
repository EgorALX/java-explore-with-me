package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.Mapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.HitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository repository;

    private final Mapper mapper;

    @Override
    public HitDto addHit(HitDto hitDto) {
        Hit hit = mapper.toHit(hitDto);
        return mapper.toDto(repository.save(hit));
    }

    @Override
    public List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null) {
            uris = List.of();
        }
        List<ViewStatsDto> stats;
        if (unique) {
            stats = repository.findAllUnique(start, end, uris);
        } else {
            stats = repository.findAll(start, end, uris);
        }
        return stats;
    }
}
