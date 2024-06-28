package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.events.model.EventStatus;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

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

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    private EventStatus stateAction;

    @Length(min = 3, max = 120)
    private String title;
}
