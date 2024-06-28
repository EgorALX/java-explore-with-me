package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.EventState;
import ru.practicum.location.model.Location;
import ru.practicum.users.dto.UserShortDto;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private Boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private String description;

    private Integer participantLimit;

    private EventState state;

    private String createdOn;

    private Location location;

    private Boolean requestModeration;

    private Long confirmedRequests;

    private String publishedOn;

    private Long views;
}
