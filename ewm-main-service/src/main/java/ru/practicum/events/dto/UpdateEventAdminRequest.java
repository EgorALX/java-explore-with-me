package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    private Long id;

    @Length(min = 20, max = 2000)
    private String annotation;

    private Integer category;

    @Length(min = 20, max = 7000)
    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventState stateAction;

    @Length(min = 3, max = 120)
    private String title;
}
