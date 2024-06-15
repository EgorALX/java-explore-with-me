package ru.practicum;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStatsDto {
    private Long hits;
    private String app;
    private String uri;
}