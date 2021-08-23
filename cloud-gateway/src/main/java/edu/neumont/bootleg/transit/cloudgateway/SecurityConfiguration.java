package edu.neumont.bootleg.transit.cloudgateway;

import edu.neumont.bootleg.transit.cloudgateway.models.SecurityUserDetails;
import edu.neumont.bootleg.transit.cloudgateway.models.User;
import edu.neumont.bootleg.transit.cloudgateway.repositories.UserRepository;
import edu.neumont.bootleg.transit.cloudgateway.services.SecurityUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
    public void init() {
        userRepository.count().subscribe(l -> {
            if (l == 0) {
                User user = new User();
                user.setUsername("admin");
                user.setPassword(passwordEncoder().encode("admin"));
                user.setRoles(Collections.singletonList("USER"));
                userRepository.save(user)
                        .subscribe(us -> securityUserDetailsService().addUser("admin", new SecurityUserDetails(us)));
            }
        });

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            //Update user cache
            userRepository.findAll().buffer()
                    .subscribe(user -> securityUserDetailsService().setUsers(user));
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Bean
    public SecurityUserDetailsService securityUserDetailsService() {
        return new SecurityUserDetailsService();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf().disable();

        httpSecurity.authorizeExchange()
                .pathMatchers("/user-service/user/auth")
                .hasRole("USER")
                .and()
                .httpBasic();

        httpSecurity.authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/user-service/user")
                .permitAll();

        httpSecurity.authorizeExchange()
                .pathMatchers("/*/actuator/**")
                .permitAll()
                .pathMatchers(HttpMethod.GET)
                .permitAll();

        httpSecurity.authorizeExchange()
                .anyExchange()
                .hasRole("USER")
                .and()
                .httpBasic();

        return httpSecurity.build();
    }
}
