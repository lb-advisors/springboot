package com.lbadvisors.pffc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lbadvisors.pffc.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api")
public class RoutesController {

    @Autowired
    private RoutesService routesService;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello world!";
    }

    @GetMapping(value = "/routes")
    public List<Routes> getAllroutes() {
        return routesService.getAllRoutes();
    }

    @GetMapping(value = "/routes/{id}")
    public ResponseEntity<Routes> getRouteById(@PathVariable("id") int id) throws Exception {

        Routes route = routesService.getRoute(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Route with ID %s not found", id)));
        return new ResponseEntity<>(route, HttpStatus.OK);
        // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
