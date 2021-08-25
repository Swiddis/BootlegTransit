package edu.neumont.bootleg.transit.scheduleservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.neumont.bootleg.transit.scheduleservice.LocationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "stop")
@Table(name = "stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stop_id")
    private Long id;

    private Double lat, lng;

    @ManyToOne
    @JsonIgnore
    private Route route;

    /**
     * Gets the lat/long in the form of "lat,lng"
     *
     * @return
     */
    public String getLatLong() {
        return lat + "," + lng;
    }

}
