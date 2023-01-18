package com.adrian.library.management.system.repository;

import com.adrian.library.management.system.entity.Borrowing;
import com.adrian.library.management.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Integer> {
    Optional<Borrowing> findByIdAndUser(Integer id, User user);
}
