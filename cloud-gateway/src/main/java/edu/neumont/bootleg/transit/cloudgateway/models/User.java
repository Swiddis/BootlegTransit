package edu.neumont.bootleg.transit.cloudgateway.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document
public class User {
    @Id
    public String username;
    public String password;
    public List<String> roles;
}
