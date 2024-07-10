package ru.practicum.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.users.dto.UserShortDto;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private UserShortDto author;

    private EventShortDto event;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdOn;

    private Boolean updated;
}
