package ru.practicum.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

@Component
public class LocationMapper {

    public Location toModel(LocationDto dto) {
        return new Location(0L, dto.getLat(), dto.getLon());
    }
}
