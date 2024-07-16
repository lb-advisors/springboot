package com.lbadvisors.pffc.controllers.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer>, InventoryRepositoryCustom {

    // @NonNull
    // Page<Inventory> findAll(@NonNull Pageable pageable);

    // @Query("SELECT i FROM Inventory i WHERE " +
    // "LOWER(i.compDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    // "LOWER(i.packSize) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    // "LOWER(i.unitType) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
    // "CAST(i.activePrice AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
    // +
    // "CAST(i.id AS string) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    // Page<Inventory> searchByMultipleFields(@Param("searchTerm") String
    // searchTerm, Pageable pageable);

    // eContaining(String name, Pageable pageable);

}
