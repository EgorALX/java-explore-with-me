package ru.practicum.compilations.dto;

import java.util.List;
import lombok.*;
import ru.practicum.events.dto.EventShortDto;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
