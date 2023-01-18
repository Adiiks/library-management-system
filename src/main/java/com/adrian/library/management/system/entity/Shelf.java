package com.adrian.library.management.system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer shelfNumber;
    private Integer floor;

    @OneToMany(mappedBy = "shelf")
    Set<Book> books = new HashSet<>();
}
