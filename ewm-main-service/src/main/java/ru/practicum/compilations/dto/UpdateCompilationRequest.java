package ru.practicum.compilations.dto;

import lombok.*;
import java.util.List;

import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}