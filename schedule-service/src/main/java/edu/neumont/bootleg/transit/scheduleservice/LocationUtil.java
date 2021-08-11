package edu.neumont.bootleg.transit.scheduleservice;

import edu.neumont.bootleg.transit.EnvironmentConfiguration;
import edu.neumont.bootleg.transit.scheduleservice.models.MapsLocationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class LocationUtil {
    public static String key;
    @Autowired
    private static RestTemplate restTemplate;
    @Autowired
    private static EnvironmentConfiguration conf;

    static {
        key = conf.get("MAPS_KEY");
    }

    public static String getAddress(String coordinates) {
        String requestUrl = "http://dev.virtualearth.net/REST/v1/Locations/" +
                coordinates +
                "&key=" + key;

        MapsLocationResponse response = restTemplate.getForObject(requestUrl, MapsLocationResponse.class);
        return response.getResourceSets().get(0).getResources().get(0).getName();
    }

    public static MapsLocationResponse.Point getLatLong(String address) {
        String requestUrl = "http://dev.virtualearth.net/REST/v1/Locations" +
                "?addressLine=" + address +
                "&maxPoints=1" +
                "&key=" + key;

        MapsLocationResponse response = restTemplate.getForObject(requestUrl, MapsLocationResponse.class);
        return response.getResourceSets().get(0).getResources().get(0).getPoint();
    }
}
