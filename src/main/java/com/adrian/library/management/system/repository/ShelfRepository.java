package com.adrian.library.management.system.repository;

import com.adrian.library.management.system.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    Optional<Shelf> findByBooksId(Integer bookId);
}
