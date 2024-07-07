package com.lbadvisors.pffc.controllers.profiles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipToRepository extends JpaRepository<ShipTo, Integer> {
    Optional<ShipTo> findById(int id);
}
