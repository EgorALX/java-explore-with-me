package ru.practicum.client;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface Client {

    public void post(HitDto hitDto);

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
