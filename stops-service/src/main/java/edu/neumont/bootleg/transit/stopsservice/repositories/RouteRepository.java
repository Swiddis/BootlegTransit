package edu.neumont.bootleg.transit.stopsservice.repositories;

import edu.neumont.bootleg.transit.stopsservice.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {}
