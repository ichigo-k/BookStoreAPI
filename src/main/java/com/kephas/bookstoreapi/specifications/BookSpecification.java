package com.kephas.bookstoreapi.specifications;

import com.kephas.bookstoreapi.entities.Book;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> title == null || title.isBlank()
                ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(String authorName) {
        return (root, query, cb) -> authorName == null || authorName.isBlank()
                ? null
                : cb.like(cb.lower(root.join("author", JoinType.LEFT).get("name")), "%" + authorName.toLowerCase() + "%");
    }

    public static Specification<Book> hasCategory(String categoryName) {
        return (root, query, cb) -> categoryName == null || categoryName.isBlank()
                ? null
                : cb.like(cb.lower(root.join("category", JoinType.LEFT).get("name")), "%" + categoryName.toLowerCase() + "%");
    }

    public static Specification<Book> publishedInYear(Integer year) {
        return (root, query, cb) -> {
            if (year == null) return null;
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            return cb.between(root.get("publicationDate"), start, end);
        };
    }
}
