package com.lbadvisors.pffc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lbadvisors.pffc.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
