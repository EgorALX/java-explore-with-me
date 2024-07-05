package ru.practicum.requests.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated().format(DATE_TIME_FORMATTER),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }

    public EventRequestStatusUpdateResult toResult(List<Request> updatedRequests) {
        if (updatedRequests == null || updatedRequests.isEmpty()) {
            return new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                new ArrayList<>(), new ArrayList<>());

        for (Request request : updatedRequests) {
            ParticipationRequestDto dto = toParticipationRequestDto(request);
            if (request.getStatus().equals(Status.CONFIRMED)) {
                result.getConfirmedRequests().add(dto);
            } else if (request.getStatus().equals(Status.REJECTED)) {
                result.getRejectedRequests().add(dto);
            }
        }
        return result;
    }

}
