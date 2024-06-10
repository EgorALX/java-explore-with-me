package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ViewStatsDto(count(DISTINCT h.ip), h.app, h.uri) " +
            "FROM Hit AS h WHERE h.timestamp BETWEEN :start AND :end AND (:uris IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findAllUnique(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ViewStatsDto(count(h.ip), h.app, h.uri) " +
            "FROM Hit AS h WHERE h.timestamp " +
            "BETWEEN :start AND :end AND (COALESCE(:uris, null) IS NULL OR h.uri IN :uris) " +
            "GROUP BY h.app, h.uri ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> findAll(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("uris") List<String> uris);

}
