package edu.neumont.bootleg.transit.stopsservice.models;

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
                    "__type": "Location:http://schemas.microsoft.com/search/local/ws/rest/v1",
                    "bbox": [
                        40.762427282429321,
                        -111.89705875058185,
                        40.770152717570674,
                        -111.88345924941815
                    ],
                    "name": "143 S Main St, Salt Lake City, UT 84111",
                    "point": {
                        "type": "Point",
                        "coordinates": [
                            40.76629,
                            -111.890259
                        ]
                    },
                    "address": {
                        "addressLine": "143 S Main St",
                        "adminDistrict": "UT",
                        "adminDistrict2": "Salt Lake County",
                        "countryRegion": "United States",
                        "formattedAddress": "143 S Main St, Salt Lake City, UT 84111",
                        "locality": "Salt Lake City",
                        "postalCode": "84111"
                    },
                    "confidence": "Medium",
                    "entityType": "Address",
                    "geocodePoints": [
                        {
                            "type": "Point",
                            "coordinates": [
                                40.76629,
                                -111.890259
                            ],
                            "calculationMethod": "Rooftop",
                            "usageTypes": [
                                "Display"
                            ]
                        },
                        {
                            "type": "Point",
                            "coordinates": [
                                40.7662875,
                                -111.8909984
                            ],
                            "calculationMethod": "Rooftop",
                            "usageTypes": [
                                "Route"
                            ]
                        }
                    ],
                    "matchCodes": [
                        "Ambiguous"
                    ]
                }
            ]
        }
    ],
    "statusCode": 200,
    "statusDescription": "OK",
    "traceId": "0e60fe49241a40e0a10bb339051cc617|CO00001126|0.0.0.1|Ref A: 825CD42AD73B4A65BA18395C47F41C8D Ref B: CO1EDGE2022 Ref C: 2021-08-11T14:48:19Z"
}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapsLocationResponse {

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
        public String name;
        public Point point;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Point {
        public String type;
        public Double[] coordinates;
    }
}
