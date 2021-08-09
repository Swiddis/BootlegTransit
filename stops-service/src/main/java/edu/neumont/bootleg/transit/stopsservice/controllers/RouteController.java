package edu.neumont.bootleg.transit.stopsservice.controllers;

import edu.neumont.bootleg.transit.EnvironmentConfiguration;
import edu.neumont.bootleg.transit.stopsservice.models.Route;
import edu.neumont.bootleg.transit.stopsservice.repositories.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController("/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository repo;
    private final RestTemplate restTemplate;
    private final EnvironmentConfiguration conf;
    private String key;

    @PostConstruct
    public void postConstruct() {
        key = conf.get("MAPS_KEY");
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

    @GetMapping("/test")
    public String test() {
        System.out.println("KEY IS: " + key);
        String locationUrl = "http://dev.virtualearth.net/REST/v1/Locations/47.64054,-122.12934?key=" + key;

        ResponseEntity<String> result = restTemplate.getForEntity(locationUrl, String.class);

        return result.getBody();
    }
}
