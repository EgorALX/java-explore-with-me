package ru.practicum.client;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface Client {

    public void addHit(HitDto hitDto);

    public List<ViewStatsDto> retrieveAllStats(String start, String end, List<String> uris, boolean unique);
}
