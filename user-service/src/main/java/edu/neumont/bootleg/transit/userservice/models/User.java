package edu.neumont.bootleg.transit.userservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Getter
    @Setter
    @Id
    public String username;
    @Getter
    @Setter
    public String email;
    @Getter
    @Setter
    @JsonIgnore
    public String password;

    @Getter
    @Setter
    public List<String> roles = new ArrayList<>();
//    "username": "etoastie",
//            "email": "toast@gmail.com",
//            "password": "password",
//            "roles": ["USER", "ADMIN"]

    public boolean hasRole(String role) {
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }

}
