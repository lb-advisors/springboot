package com.lbadvisors.pffc.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lbadvisors.pffc.entities.Role;
import com.lbadvisors.pffc.poc_authy.ERole;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
