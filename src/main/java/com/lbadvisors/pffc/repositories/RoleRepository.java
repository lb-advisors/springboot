package com.lbadvisors.pffc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lbadvisors.pffc.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // Optional<Role> findByName(ERole name);

    // Optional<Role> findById(Long id);

}
