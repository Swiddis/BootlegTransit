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

    @Column(name = "stop_address")
    private String address;

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

    /**
     * Update the coordinates as "lat,lng" and update address automatically
     *
     * @param coords - "lat,lng" format of coordinates to set
     */
    public void setCoordinates(String coords) {
        String address = LocationUtil.getAddress(coords);
        this.address = address;
        String[] split = coords.split(",[ ]?");
        setLat(Double.parseDouble(split[0]));
        setLng(Double.parseDouble(split[1]));
    }

    /**
     * Sets the address and automatically updates the coordinates
     *
     * @param address - The address to set for the stop
     */
    public void setAddress(String address) {
        MapsLocationResponse.Point point = LocationUtil.getLatLong(address);
        setLat(point.coordinates[0]);
        setLng(point.coordinates[1]);
        this.address = address;
    }
}
