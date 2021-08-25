package edu.neumont.bootleg.transit.scheduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatrixResponse {

    public String authenticationResultCode,
            brandLogoUrl,
            copyright;

    public int    statusCode;
    public String statusDescription;

    public List<ResourceSet> resourceSets = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResourceSet {
        public int            estimatedTotal;
        public List<Resource> resources = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resource {
        public List<Location>     destinations = new ArrayList<>();
        public List<Location>     origins      = new ArrayList<>();
        public List<MatrixResult> results      = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MatrixResult {
        public LocalDateTime departureTime;
        public int           destinationIndex;
        public int           originIndex;
        public long          totalWalkDuration;
        public double        travelDistance;
        public long          travelDuration;
    }

}
