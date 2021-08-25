package edu.neumont.bootleg.transit.scheduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrixRequest {

    private List<Location> origins      = new ArrayList();
    private List<Location> destinations = new ArrayList();

    private String
            travelMode   = "driving",
            distanceUnit = "mile",
            timeUnit     = "second";
}
