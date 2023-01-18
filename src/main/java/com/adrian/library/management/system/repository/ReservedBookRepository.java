package com.adrian.library.management.system.repository;

import com.adrian.library.management.system.entity.ReservedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedBookRepository extends JpaRepository<ReservedBook, Integer> {
}
