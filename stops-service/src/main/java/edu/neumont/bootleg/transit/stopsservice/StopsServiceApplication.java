package edu.neumont.bootleg.transit.stopsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "edu.neumont.bootleg.transit")
@ComponentScan(basePackages = "edu.neumont.bootleg.transit")
@EnableJpaRepositories(basePackages = "edu.neumont.bootleg.transit")
public class StopsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StopsServiceApplication.class, args);
	}

}
