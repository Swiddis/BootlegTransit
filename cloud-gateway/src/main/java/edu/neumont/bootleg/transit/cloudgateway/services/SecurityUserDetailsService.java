package edu.neumont.bootleg.transit.cloudgateway.services;

import edu.neumont.bootleg.transit.cloudgateway.models.SecurityUserDetails;
import edu.neumont.bootleg.transit.cloudgateway.models.User;
import edu.neumont.bootleg.transit.cloudgateway.repositories.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class SecurityUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findById(username);
        if (user.isPresent()) {
            return Mono.just(new SecurityUserDetails(user.get()));
        }
        throw new UsernameNotFoundException(username);
    }
}
