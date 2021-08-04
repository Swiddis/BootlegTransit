package edu.neumont.bootleg.transit;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    private final EnvironmentConfiguration environment;

    public DatabaseConfiguration(EnvironmentConfiguration environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://" + environment.get("DATABASE_HOST") + "/" + environment.get("DATABASE_NAME") + "?createDatabaseIfNotExist=true");
        dataSourceBuilder.username(environment.get("DATABASE_USERNAME"));
        dataSourceBuilder.password(environment.get("DATABASE_PASSWORD"));
        return dataSourceBuilder.build();
    }

}
