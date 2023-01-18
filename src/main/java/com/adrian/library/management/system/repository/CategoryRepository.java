package com.adrian.library.management.system.repository;

import com.adrian.library.management.system.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
