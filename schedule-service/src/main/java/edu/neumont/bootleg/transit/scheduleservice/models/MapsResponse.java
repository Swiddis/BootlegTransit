package edu.neumont.bootleg.transit.scheduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapsResponse {

    public String authenticationResultCode,
            brandLogoUrl,
            copyright;

    public int statusCode;
    public String statusDescription;

    public List<ResourceSet> resourceSets = new ArrayList<>();


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResourceSet {
        public int estimatedTotal;
        public List<Resource> resources = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resource {
        public String distanceUnit,
                durationUnit,
                trafficCongestion,
                travelMode;
        public double travelDistance;
        public long travelDuration,
                travelDurationTraffic;
    }

}
