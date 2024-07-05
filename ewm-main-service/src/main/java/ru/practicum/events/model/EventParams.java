package ru.practicum.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParams {
    private List<Long> userIds;

    private List<EventState> states;

    private List<Long> categoriesIds;

    private LocalDateTime start;

    private LocalDateTime end;
}
