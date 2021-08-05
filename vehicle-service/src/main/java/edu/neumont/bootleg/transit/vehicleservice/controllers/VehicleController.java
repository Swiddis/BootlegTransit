package edu.neumont.bootleg.transit.vehicleservice.controllers;

import edu.neumont.bootleg.transit.vehicleservice.models.Vehicle;
import edu.neumont.bootleg.transit.vehicleservice.repositories.VehicleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/vehicle")
public class VehicleController {
    private final VehicleRepository repo;

    VehicleController(VehicleRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/vehicle")
    List<Vehicle> findAll() {
        return repo.findAll();
    }

    @GetMapping("/vehicle/{id}")
    Vehicle findById(@PathVariable Long id) {
        return repo.getById(id);
    }

    @PostMapping("/vehicle")
    Vehicle newVehicle(@RequestBody Vehicle vehicle) {
        return repo.save(vehicle);
    }

    @PatchMapping("/vehicle/{id}")
    Vehicle patchVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        Vehicle updating = repo.getById(id);

        if (vehicle.getName() != null) {
            updating.setName(vehicle.getName());
        }
        if (vehicle.getLocation() != null) {
            updating.setLocation(vehicle.getLocation());
        }
        if (vehicle.getRouteId() != null) {
            updating.setRouteId(vehicle.getRouteId());
        }
        if (vehicle.getRouteIdx() != null) {
            updating.setRouteIdx(vehicle.getRouteIdx());
        }

        repo.save(updating);
        return updating;
    }

    @DeleteMapping("/vehicle/{id}")
    void deleteVehicle(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
