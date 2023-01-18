package com.adrian.library.management.system.specification;

import com.adrian.library.management.system.entity.Book;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class BookSpecification implements Specification<Book> {

    private SearchCriteria searchCriteria;

    @Override
    public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        if (searchCriteria == null || searchCriteria.key() == null || searchCriteria.value() == null) {
            return null;
        }

        String key = searchCriteria.key();
        Object value = searchCriteria.value();

        return switch (key) {
            case "title" ->
                criteriaBuilder.like(root.get(key), value + "%");
            case "author", "publisher" ->
                    criteriaBuilder.equal(root.get(key), value);
            case "category" ->
                    criteriaBuilder.equal(root.get("category").get("name"), value);
            default -> null;
        };
    }
}
