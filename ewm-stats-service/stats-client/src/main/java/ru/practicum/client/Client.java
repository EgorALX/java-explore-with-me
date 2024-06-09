package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface Client {

    public void addHit(HitDto hitDto);

    public ResponseEntity<Object> retrieveAllStats(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> uris,
                                                   boolean unique);
}
