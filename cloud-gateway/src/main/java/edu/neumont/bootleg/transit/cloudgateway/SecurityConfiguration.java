package edu.neumont.bootleg.transit.cloudgateway;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
//@EnableWebSecurity
@EnableWebFluxSecurity
//@AutoConfigureAfter({ReactiveSecurityAutoConfiguration.class})
public class SecurityConfiguration/* extends WebSecurityConfigurerAdapter*/ {

//    @Bean
//    public WebFilterChainProxy webFilterChainProxy() {
//        return new WebFilterChainProxy();
//    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("user")
                .password("password")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.csrf().disable()
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic();

        System.out.println(" ----------------- HEY WE DID IT -----------------------");
//
//        httpSecurity.securityMatcher(ServerWebExchangeMatchers.pathMatchers("/actuator/**"))
//                .authorizeExchange()
//                .pathMatchers("/actuator/**")
//                .hasRole("ACTUATOR_ADMIN_ROLE");
//
//        httpSecurity.authorizeExchange()
//                .anyExchange().hasRole("USER")
//                .and().httpBasic();

        return httpSecurity.build();
    }

//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication().withUser("user").password("password")
//                .roles("USER").and().withUser("admin").password("admin")
//                .roles("ADMIN");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//                .anyRequest().authenticated()
//                .and().httpBasic();
//        http.build();
//    }

}
