package ru.practicum.client;

import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;

import java.util.List;

public interface Client {

    void addHit(HitDto hitDto);

    List<ViewStatsDto> retrieveAllStats(String start, String end, List<String> uris, boolean unique);
}
