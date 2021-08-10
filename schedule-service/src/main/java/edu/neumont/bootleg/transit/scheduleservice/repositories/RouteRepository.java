package edu.neumont.bootleg.transit.scheduleservice.repositories;

import edu.neumont.bootleg.transit.scheduleservice.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
