package com.kephas.bookstoreapi.repositories;

import com.kephas.bookstoreapi.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Category findCategoryById(UUID id);
}
