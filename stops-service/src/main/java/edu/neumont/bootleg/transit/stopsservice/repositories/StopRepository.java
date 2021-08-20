package edu.neumont.bootleg.transit.stopsservice.repositories;

import edu.neumont.bootleg.transit.stopsservice.models.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, Long> {
}
