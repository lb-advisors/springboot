package com.lbadvisors.pffc.controllers;

import java.util.List;
import java.util.Optional;

public interface IRoutes {

    List<Routes> getAllRoutes();

    Optional<Routes> getRoute(int id);

}
