package edu.neumont.bootleg.transit.scheduleservice.controllers;

import edu.neumont.bootleg.transit.EnvironmentConfiguration;
import edu.neumont.bootleg.transit.scheduleservice.models.Route;
import edu.neumont.bootleg.transit.scheduleservice.models.ScheduleData;
import edu.neumont.bootleg.transit.scheduleservice.models.Stop;
import edu.neumont.bootleg.transit.scheduleservice.models.Vehicle;
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

        Route rt = new Route();

        Stop stop = new Stop();
        stop.setAddress("143 S Main St, Salt Lake City, UT");
        stop.setRoute(rt);
        stop.setLat(40.76615637890569);
        stop.setLng(-111.89084904217947);

        rt.getStops().add(stop);

        Stop stop2 = new Stop();
        stop2.setAddress("500 S Main St, Salt Lake City, UT");
        stop2.setRoute(rt);
        stop2.setLat(40.758622822515065);
        stop2.setLng(-111.89091582226021);

        rt.getStops().add(stop2);

        routeRepo.save(rt);

        Vehicle veh = new Vehicle();
        veh.setName("Travja's Car");
        veh.setRouteId(routeRepo.findAll().stream().findFirst().orElse(new Route()).getId());
        veh.setRouteIdx(0);

        vehicleRepo.save(veh);
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
                for (int i = vehicle.getRouteIdx(); i <= index; i++) {
                    coordinates.add(stops.get(i).getLatLong());
                }

                String coords = String.join(";", coordinates);
                dat.setEta(getETA(coords));
                data.add(dat);
            }

        }

        return data;

    }

    public long getETA(String coords) {
        //TODO Calculate ETA

        /*
         {
            "authenticationResultCode": "ValidCredentials",
            "brandLogoUri": "http://dev.virtualearth.net/Branding/logo_powered_by.png",
            "copyright": "Copyright © 2021 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.",
            "resourceSets": [
                {
                    "estimatedTotal": 1,
                    "resources": [
                        {
                            "__type": "Route:http://schemas.microsoft.com/search/local/ws/rest/v1",
                            "bbox": [
                                40.75848,
                                -111.899309,
                                40.769271,
                                -111.888297
                            ],
                            "id": "v70,h965020220,i0,a0,cen-US,dAAAAAAAAAAA1,y0,s1,m1,o1,t4,wEqL6enhiREDDUkZMjvlbwA2~ANN2ApxJL3oHAADgAXnV3j4A0~VyBTb3V0aCBUZW1wbGU1~~~~~~~~v12,wyIKHaRFiREBcdbOrA_lbwA2~ANN2ApxRCXoHAADgAS89xj4C0~UyBNYWluIFN00~~~1~~~~~v12,wyFl6jRphREAuBszDBPlbwA2~ANN2ApyxdnoHAADgAdVM7z4A0~UyBNYWluIFN00~~~~~~~~v12,k0",
                            "distanceUnit": "Mile",
                            "durationUnit": "Second",
                            "price": -1,
                            "routeLegs": [...],
                            "trafficCongestion": "Mild",
                            "trafficDataUsed": "None",
                            "travelDistance": 1.757238,
                            "travelDuration": 509,
                            "travelDurationTraffic": 598,
                            "travelMode": "Driving"
                        }
                    ]
                }
            ],
            "statusCode": 200,
            "statusDescription": "OK",
            "traceId": "714df002612a4ff2b18b3f96d35c0156|CO00004A15|0.0.0.0|CO0000089A, Leg0-CO00002B2D"
        }
         */

//        String locationUrl = "http://dev.virtualearth.net/REST/v1/Locations/47.64054,-122.12934?key=" + key;
//
//        ResponseEntity<String> result = restTemplate.getForEntity(locationUrl, String.class);
//
//        return result.getBody();
        return 5l;
    }
}
