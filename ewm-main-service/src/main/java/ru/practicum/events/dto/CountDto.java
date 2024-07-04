package ru.practicum.events.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountDto {

    private Long eventId;

    private Long count;
}
