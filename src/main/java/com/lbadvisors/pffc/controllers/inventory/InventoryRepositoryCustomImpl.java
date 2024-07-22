package com.lbadvisors.pffc.controllers.inventory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@Repository
public class InventoryRepositoryCustomImpl implements InventoryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Inventory> searchInventoryItems(String search, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Inventory> query = cb.createQuery(Inventory.class);
        Root<Inventory> inventoryRoot = query.from(Inventory.class);

        List<Predicate> predicates = buildPredicates(search, cb, inventoryRoot);

        query.select(inventoryRoot).where(cb.and(predicates.toArray(new Predicate[0])));

        List<Inventory> resultList = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        // For total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Inventory> countRoot = countQuery.from(Inventory.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(cb.and(buildPredicates(search, cb, countRoot).toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }

    private List<Predicate> buildPredicates(String search, CriteriaBuilder cb, Root<Inventory> inventoryRoot) {
        List<Predicate> predicates = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            String[] words = search.split("\\s+");
            for (String word : words) {

                // for the price, remove trailing zeros
                String activePriceWord = word.contains(".") ? word.replaceAll("0*$", "").replaceAll("\\.$", "") : word;
                activePriceWord = "%" + activePriceWord + "%";

                String escapedWord = "%" + escapeSpecialChars(word.toLowerCase()) + "%";
                Predicate descriptionPredicate = cb.like(cb.lower(inventoryRoot.get("compDescription")), escapedWord, '\\');
                Predicate description2Predicate = cb.like(cb.lower(inventoryRoot.get("packSize")), escapedWord, '\\');
                Predicate description3Predicate = cb.like(cb.lower(inventoryRoot.get("unitType")), escapedWord, '\\');
                Predicate description4Predicate = cb.like(cb.concat("$", cb.toString(inventoryRoot.get("activePrice"))), activePriceWord, '\\'); // TODO: should we really search by
                                                                                                                                                 // ID?
                Predicate description5Predicate = cb.like(cb.toString(inventoryRoot.get("id")), escapedWord, '\\');

                predicates.add(cb.or(descriptionPredicate, description2Predicate, description3Predicate, description4Predicate, description5Predicate));
            }
        }
        return predicates;
    }

    private String escapeSpecialChars(String input) {
        return input.replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
    }
}
