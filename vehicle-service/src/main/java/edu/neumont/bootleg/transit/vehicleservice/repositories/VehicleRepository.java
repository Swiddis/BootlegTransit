package edu.neumont.bootleg.transit.vehicleservice.repositories;

import edu.neumont.bootleg.transit.vehicleservice.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {}
