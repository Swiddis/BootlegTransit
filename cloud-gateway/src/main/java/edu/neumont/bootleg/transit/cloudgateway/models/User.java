package edu.neumont.bootleg.transit.cloudgateway.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class User {
    public String username;
    public String password;
    public List<String> roles;
}
