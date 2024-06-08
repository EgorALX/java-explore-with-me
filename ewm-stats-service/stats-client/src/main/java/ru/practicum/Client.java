package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface Client {

    public HitDto post(HitDto hitDto);

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
