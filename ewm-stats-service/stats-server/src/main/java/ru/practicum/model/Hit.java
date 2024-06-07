package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "hits")
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
