package edu.neumont.bootleg.transit.scheduleservice.controllers;

import edu.neumont.bootleg.transit.EnvironmentConfiguration;
import edu.neumont.bootleg.transit.scheduleservice.models.*;
import edu.neumont.bootleg.transit.scheduleservice.repositories.RouteRepository;
import edu.neumont.bootleg.transit.scheduleservice.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController("/schedule")
@RequiredArgsConstructor
public class RouteController {

    private final RouteRepository routeRepo;
    private final VehicleRepository vehicleRepo;
    private final RestTemplate restTemplate;
    private final EnvironmentConfiguration conf;
    private String key;

    @PostConstruct
    public void postConstruct() {
        key = conf.get("MAPS_KEY");

//        Route rt = new Route();
//
//        Stop stop = new Stop();
//        stop.setAddress("143 S Main St, Salt Lake City, UT");
//        stop.setRoute(rt);
//        stop.setLat(40.76615637890569);
//        stop.setLng(-111.89084904217947);
//
//        rt.getStops().add(stop);
//
//        Stop stop2 = new Stop();
//        stop2.setAddress("500 S Main St, Salt Lake City, UT");
//        stop2.setRoute(rt);
//        stop2.setLat(40.758622822515065);
//        stop2.setLng(-111.89091582226021);
//
//        rt.getStops().add(stop2);
//
//        routeRepo.save(rt);
//
//        Vehicle veh = new Vehicle();
//        veh.setName("Travja's Car");
//        veh.setRouteId(routeRepo.findAll().stream().findFirst().orElse(new Route()).getId());
//        veh.setRouteIdx(0);
//
//        vehicleRepo.save(veh);
    }

    @GetMapping("/{stop}")
    public List<ScheduleData> findArrivals(@PathVariable long stop) {
        //Get routes with the stop in the list
        List<Route> routes = routeRepo.findAll();
        routes = routes.stream().filter(route -> route.hasStop(stop)).collect(Collectors.toList());
        List<Long> routeIds = routes.stream().mapToLong(route -> route.getId()).boxed().collect(Collectors.toList());

        //Get vehicles on the given route
        List<Vehicle> vehicles = vehicleRepo.findAll();
        vehicles = vehicles.stream().filter(veh -> routeIds.contains(veh.getRouteId())).collect(Collectors.toList());

        List<ScheduleData> data = new ArrayList<>();

        //Filter down to vehicles "heading" to that stop.
        //  Collect the data.
        for (Vehicle vehicle : vehicles) {
            Route route = routes.stream().filter(rt -> rt.getId() == vehicle.getRouteId()).findFirst().orElse(null);
            if (route == null) continue;

            Stop stp = route.getStops().stream().filter(st -> st.getId() == stop).findFirst().orElse(null);
            if (stp == null) continue;

            int index = route.getStops().indexOf(stp);
            if (index > vehicle.getRouteIdx()) {
                ScheduleData dat = new ScheduleData();
                dat.setVehicle(vehicle.getId());

                List<Stop> stops = route.getStops();

                List<String> coordinates = new ArrayList<>();
                for (int i = vehicle.getRouteIdx(); i < index; i++) {
                    coordinates.add(stops.get(i).getLatLong());
                }

                String destination = stp.getLatLong();
                dat.setEta(getETA(destination, coordinates));
                data.add(dat);
            }

        }

        return data;

    }

    public long getETA(String destination, List<String> prevStops) {
        StringBuilder sb = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?")
                .append("&du=mi")
                .append("&key=").append(key);
        sb.append("&wp.1=").append(prevStops.get(0));

        for (int i = 1; i < prevStops.size(); i++) {
            sb.append("&vwp.").append(i + 1).append("=").append(prevStops.get(i));
        }

        sb.append("&wp.").append(prevStops.size() + 1).append("=").append(destination);
        String locationUrl = sb.toString();
        System.out.println(locationUrl);

        MapsResponse result = restTemplate.getForObject(locationUrl, MapsResponse.class);

        return result.getResourceSets().get(0).getResources().get(0).getTravelDuration();
    }
}
