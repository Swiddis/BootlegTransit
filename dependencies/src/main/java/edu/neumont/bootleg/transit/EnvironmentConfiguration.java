package edu.neumont.bootleg.transit;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvironmentConfiguration {

    private final Environment environment;

    public EnvironmentConfiguration(Environment environment) {
        this.environment = environment;
    }

    public String get(String variable) {
        return environment.getProperty(variable);
    }

}
