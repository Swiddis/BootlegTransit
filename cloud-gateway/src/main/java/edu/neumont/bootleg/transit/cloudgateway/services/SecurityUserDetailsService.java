package edu.neumont.bootleg.transit.cloudgateway.services;

import edu.neumont.bootleg.transit.cloudgateway.models.SecurityUserDetails;
import edu.neumont.bootleg.transit.cloudgateway.models.User;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecurityUserDetailsService implements ReactiveUserDetailsService {
    private Map<String, UserDetails> users = new HashMap<>();

    public void addUser(String username, UserDetails details) {
        users.put(getKey(username), details);
    }

    public void setUsers(List<User> users) {
        this.users = new HashMap<>();
        for (User user : users) {
            addUser(user.getUsername(), new SecurityUserDetails(user));
        }
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        String      key    = getKey(username);
        UserDetails result = this.users.get(key);
        return (result != null) ? Mono.just(result) : Mono.empty();
    }

    private String getKey(String username) {
        return username.toLowerCase();
    }
}
