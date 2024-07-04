package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.dto.CountDto;
import ru.practicum.events.model.Event;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.Status;
import ru.practicum.users.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByIdInAndStatusIs(List<Long> requestIds, Status status);

    @Query("SELECT new ru.practicum.events.dto.CountDto(r.event.id, COUNT(r.id) AS count) " +
            "FROM Request AS r WHERE r.status IS (:status) AND r.event.id IN (:ids) GROUP BY r.event")
    List<CountDto> findByStatus(@Param("ids") List<Long> ids,
                                @Param("status") Status status);

    List<Request> findAllByEvent(Event event);

    Boolean existsByEventAndRequester(Event event, User user);

    List<Request> findAllByRequesterId(Long userId);

    Long countByEventIdAndStatus(Long eventId, Status status);
}
