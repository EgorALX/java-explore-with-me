package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Mapper {
    public Hit toHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.parse(hitDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public HitDto toDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(hit.getTimestamp()));
    }

}
