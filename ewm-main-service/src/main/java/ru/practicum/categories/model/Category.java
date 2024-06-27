package ru.practicum.categories.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@Setter
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "category_name", unique = true)
    private String name;
}
