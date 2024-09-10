package com.lbadvisors.pffc.poc_authy;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lbadvisors.pffc.entities.Role;
import com.lbadvisors.pffc.repositories.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }
}