package edu.neumont.bootleg.transit.vehicleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity(name = "vehicle")
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vehicle_id")
    private Long id;

    @Column(name = "vehicle_name")
    private String name;

    @Column(name = "vehicle_route_id")
    private Long routeId;

    @Column(name = "vehicle_route_idx")
    private Integer routeIdx;

    @Column(name = "vehicle_location_lat")
    private Double lat;

    @Column(name = "vehicle_location_lng")
    private Double lng;
}
