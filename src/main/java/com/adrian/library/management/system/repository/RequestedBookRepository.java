package com.adrian.library.management.system.repository;

import com.adrian.library.management.system.entity.RequestedBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestedBookRepository extends JpaRepository<RequestedBook, Integer> {
}
