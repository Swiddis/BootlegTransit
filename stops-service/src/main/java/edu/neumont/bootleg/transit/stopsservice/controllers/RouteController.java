package edu.neumont.bootleg.transit.stopsservice.controllers;

import edu.neumont.bootleg.transit.stopsservice.models.Route;
import edu.neumont.bootleg.transit.stopsservice.repositories.RouteRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/route")
public class RouteController {
    private final RouteRepository repo;

    RouteController(RouteRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/route")
    List<Route> findAll() {
        return repo.findAll();
    }

    @GetMapping("/route/{id}")
    Route findById(@PathVariable Long id) {
        return repo.getById(id);
    }

    @PostMapping("/route")
    Route newRoute(@RequestBody Route route) {
        return repo.save(route);
    }

    @PatchMapping("/route/{id}")
    Route patchRoute(@PathVariable Long id, @RequestBody Route route) {
        Route updating = repo.getById(id);

        if (route.getStops() != null) {
            updating.setStops(route.getStops());
        }

        repo.save(updating);
        return updating;
    }

    @DeleteMapping("/route/{id}")
    void deleteRoute(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
