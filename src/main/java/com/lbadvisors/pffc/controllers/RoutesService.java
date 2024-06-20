package com.lbadvisors.pffc.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutesService implements IRoutes {

    @Autowired
    RoutesRepository routesRepository;

    @Override
    public List<Routes> getAllRoutes() {
        return this.routesRepository.findAll();
    }

    @Override
    public Optional<Routes> getRoute(int id) {
        return this.routesRepository.findById(id);
    }
}
