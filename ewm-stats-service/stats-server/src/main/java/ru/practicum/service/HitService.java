package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    void addHit(HitDto hitDto);

    List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}