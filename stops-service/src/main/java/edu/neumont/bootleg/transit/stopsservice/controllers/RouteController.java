package edu.neumont.bootleg.transit.stopsservice.controllers;

import edu.neumont.bootleg.transit.EnvironmentConfiguration;
import edu.neumont.bootleg.transit.stopsservice.models.Route;
import edu.neumont.bootleg.transit.stopsservice.models.Stop;
import edu.neumont.bootleg.transit.stopsservice.repositories.RouteRepository;
import edu.neumont.bootleg.transit.stopsservice.repositories.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController("/route")
@RequiredArgsConstructor
public class RouteController {

    public static String                   key;
    private final RouteRepository          repo;
    private final StopRepository           stopRepo;
    private final RestTemplate             restTemplate;
    private final EnvironmentConfiguration conf;

    @PostConstruct
    public void postConstruct() {
        key = conf.get("MAPS_KEY");
    }

    @GetMapping("/route")
    public List<Route> findAll() {
        return repo.findAll();
    }

    @GetMapping("/route/{id}")
    public Route findById(@PathVariable Long id) {
        return repo.getById(id);
    }

    @PostMapping("/route")
    public Route newRoute(@RequestBody Route route) {
        return repo.save(route);
    }

    @PatchMapping("/route/{id}")
    public Route patchRoute(@PathVariable Long id, @RequestBody Route route) {
        Route updating = repo.getById(id);

        if (route.getStops() != null) {
            updating.setStops(route.getStops());
        }

        if (route.getColor() != null) {
            updating.setColor(route.getColor());
        }

        repo.save(updating);
        return updating;
    }

    @DeleteMapping("/route/{id}")
    public void deleteRoute(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
