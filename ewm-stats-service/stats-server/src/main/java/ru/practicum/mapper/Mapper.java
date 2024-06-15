package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.HitDto;
import ru.practicum.model.Hit;

@Component
public class Mapper {
    public Hit toHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp());
    }
}
