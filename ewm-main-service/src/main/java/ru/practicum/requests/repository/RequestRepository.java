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

    @Query("SELECT r.event.id AS eventId, COUNT(r.id) AS count FROM Request r " +
            "WHERE r.status = :status AND r.event.id IN (SELECT id FROM Event WHERE id IN (:ids)) " +
            "GROUP BY r.event.id")
    List<CountDto> findByStatus(@Param("ids") List<Long> ids, @Param("status") Status status);

    List<Request> findAllByEvent(Event event);

    Boolean existsByEventAndRequester(Event event, User user);

    List<Request> findAllByRequesterId(Long userId);
}
