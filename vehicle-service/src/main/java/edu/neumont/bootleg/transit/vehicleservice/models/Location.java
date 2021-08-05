package edu.neumont.bootleg.transit.vehicleservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "location")
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "location_id")
    private Long id;

    @Column(name = "location_lat")
    private Double lat;

    @Column(name = "location_lng")
    private Double lng;

    @OneToOne(mappedBy="location")
    private Vehicle vehicle;
}
