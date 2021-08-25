package edu.neumont.bootleg.transit.stopsservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.List;

@Entity(name = "route")
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "route_id")
    private Long id;

    @OneToMany(mappedBy = "route", fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Stop> stops;

    private String name, color = "green";

    public void addStop(Stop stop) {
        stop.setRoute(this);
        stops.add(stop);
    }

    public void setStop(int index, Stop stop) {
        stop.setRoute(this);
        stops.set(index, stop);
    }

    public boolean hasStop(long stopId) {
        return stops.stream().filter(stop -> stop.getId() == stopId).count() > 0;
    }

    public void setStops(List<Stop> stops) {
        if (this.stops != null)
            this.stops.forEach(stop -> stop.setRoute(null));
        stops.forEach(stop -> stop.setRoute(this));
        this.stops = stops;
    }
}
