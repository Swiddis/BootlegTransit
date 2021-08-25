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

    private final RouteRepository          routeRepo;
    private final VehicleRepository        vehicleRepo;
    private final RestTemplate             restTemplate;
    private final EnvironmentConfiguration conf;
    private       String                   key;

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

    @GetMapping("/arrivals/{stop}")
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
                dat.setStop(stp.getId());
                dat.setRoute(route.getId());
                data.add(dat);
            }

        }

        return data;

    }

    private double getDistance(Vehicle veh, Stop stop) {
        double vlat = veh.getLat();
        double vlng = veh.getLng();
        double slat = stop.getLat();
        double slng = stop.getLng();
        double dlat = vlat - slat;
        double dlng = vlng - slng;

        return Math.sqrt((dlat * dlat) + (dlng * dlng));
    }

    /**
     * Gets the ETA for a specific stop on a specific route
     *
     * @param routeId - The route ID to get the ETA for
     * @param stopId  - The stop id on that route
     * @return {@link ScheduleData}
     */
    @GetMapping("/eta/{routeId}/{stopId}")
    public ScheduleData findStopETAOnRoute(@PathVariable long routeId, @PathVariable int stopId) {
        //Get routes with the stop in the list
        Route route = routeRepo.findById(routeId).orElse(null);
        if (route == null) return new ScheduleData();

        Stop dest = route.getStops().stream()
                .filter(stp -> stp.getId() == stopId)
                .findFirst().orElse(null);
        if (dest == null) return new ScheduleData();

        return getETA(route, dest);
    }

    /**
     * Gets the ETA for all stops on a specific route
     *
     * @param routeId - The route ID to get the ETAs for
     * @return {@link ScheduleData}
     */
    @GetMapping("/eta/{routeId}")
    public List<ScheduleData> findRouteEtas(@PathVariable long routeId) {
        List<ScheduleData> data = new ArrayList<>();

        //Get routes with the stop in the list
        Route route = routeRepo.findById(routeId).orElse(null);
        if (route == null || route.getStops().size() == 0) return data;

        List<Vehicle> vehicles = vehicleRepo.findAll();
        Vehicle       vehicle  = vehicles.stream().filter(veh -> veh.getRouteId() == route.getId()).findFirst().orElse(null);
        if (vehicle == null) return data;

        data.addAll(getFullETAs(vehicle, route, vehicle.getRouteIdx()));

        return data;
    }


    public ScheduleData getETA(Route route, Stop dest) {
        //Get vehicles on the given route
        List<Vehicle> vehicles = vehicleRepo.findAll();
        vehicles = vehicles.stream().filter(veh -> veh.getRouteId() == route.getId()).collect(Collectors.toList());

        double  distance = 1000000d;
        Vehicle vehicle  = null;
        //Get the closest vehicle to the given stop. This should be used for calculations.
        for (Vehicle veh : vehicles) {
            double dist = getDistance(veh, dest);
            if (dist < distance) {
                distance = dist;
                vehicle = veh;
            }
        }

        int          index       = route.getStops().indexOf(dest);
        List<String> coordinates = new ArrayList<>();
        List<Stop>   stops       = route.getStops();
        ScheduleData data        = new ScheduleData();

        data.setVehicle(vehicle.getId());
        data.setRoute(route.getId());
        data.setStop(dest.getId());

        coordinates.add(vehicle.getLat() + "," + vehicle.getLng());
        for (int i = vehicle.getRouteIdx(); i < index; i++) {
            coordinates.add(stops.get(i).getLatLong());
        }

        String destination = dest.getLatLong();
        data.setEta(getETA(destination, coordinates));

        return data;
    }


    public long getETA(String destination, List<String> prevStops) {
        StringBuilder sb = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?")
                .append("&du=mi")
                .append("&maxSolns=1")
                .append("&ig=false")
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

    public List<ScheduleData> getFullETAs(Vehicle vehicle, Route route, int routeIndex) {
        List<ScheduleData> data = new ArrayList<>();
        List<String> prevStops = route.getStops().stream()
                .map(Stop::getLatLong).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("https://dev.virtualearth.net/REST/v1/Routes/Driving?")
                .append("&du=mi")
                .append("&maxSolns=1")
                .append("&ig=false")
                .append("&key=").append(key);

        int size = prevStops.size();
        int idx  = 0;
        sb.append("&wp.").append(idx++).append("=").append(vehicle.getLat() + "," + vehicle.getLng());
        for (int i = routeIndex; i < Math.min(24 + routeIndex, prevStops.size() + routeIndex); i++) {
            sb.append("&wp.").append(idx++).append("=").append(prevStops.get(i % size));
        }
//        sb.append("&wp.").append(idx++).append("=").append(prevStops.get(routeIndex));

        String locationUrl = sb.toString();
        System.out.println(locationUrl);

        MapsResponse result = restTemplate.getForObject(locationUrl, MapsResponse.class);

        List<RouteLeg> routeLegs = result.getResourceSets().get(0).getResources().get(0).getRouteLegs();
        long           sum       = 0;
        for (int i = 0; i < routeLegs.size(); i++) {
            RouteLeg     leg  = routeLegs.get(i);
            Stop         stop = route.getStops().get((routeIndex + i) % size);
            ScheduleData dat  = new ScheduleData();

            dat.setEta((sum += leg.travelDuration));
            dat.setRoute(route.getId());
            dat.setVehicle(vehicle.getId());
            dat.setStop(stop.getId());

            data.add(dat);
        }

        return data;
    }

}
