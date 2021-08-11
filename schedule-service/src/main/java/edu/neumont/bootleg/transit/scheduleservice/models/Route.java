package edu.neumont.bootleg.transit.scheduleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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

    @OneToMany(mappedBy = "route")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Stop> stops;

    public void addStop(Stop stop) {
        stops.add(stop);
    }

    public void setStop(int index, Stop stop) {
        stops.set(index, stop);
    }

    public boolean hasStop(long stopId) {
        return stops.stream().filter(stop -> stop.getId() == stopId).count() > 0;
    }
}
