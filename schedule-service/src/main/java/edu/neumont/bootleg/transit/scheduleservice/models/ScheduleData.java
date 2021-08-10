package edu.neumont.bootleg.transit.scheduleservice.models;

import lombok.Getter;
import lombok.Setter;

public class ScheduleData {

    /*

        "arrivals": [{
            "vehicle": 1,
            "eta": 60
        }, {
            "vehicle": 2,
            "eta": 320
        }]

     */

    @Getter
    @Setter
    public long vehicle;

    @Getter
    @Setter
    //ETA is in seconds
    public long eta;

}
