package edu.neumont.bootleg.transit.scheduleservice.repositories;

import edu.neumont.bootleg.transit.scheduleservice.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}
