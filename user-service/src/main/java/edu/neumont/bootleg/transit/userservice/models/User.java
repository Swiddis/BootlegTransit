package edu.neumont.bootleg.transit.userservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Document
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
    @JsonProperty(access=Access.WRITE_ONLY)
    public String password;

    @JsonIgnore
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

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

    public void setPassword(String password) {
        this.password = encoder.encode(password);
    }
}
