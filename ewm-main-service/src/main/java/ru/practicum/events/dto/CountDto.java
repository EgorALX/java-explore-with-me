package ru.practicum.events.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountDto {

    private Long id;

    private Long count;
}
