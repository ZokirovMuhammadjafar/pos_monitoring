package com.pos.monitoring.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    private final Environment environment;

    @Autowired
    public JpaConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        config.setUsername(environment.getProperty("spring.datasource.username"));
        config.setPassword(environment.getProperty("spring.datasource.password"));
        config.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));

        config.setMaximumPoolSize(environment.getProperty("spring.datasource.hikari.maximumPoolSize", Integer.class));
        config.setMinimumIdle(environment.getProperty("spring.datasource.hikari.minimumIdle", Integer.class));
        config.setIdleTimeout(environment.getProperty("spring.datasource.hikari.idleTimeout", Long.class));
        config.setMaxLifetime(environment.getProperty("spring.datasource.hikari.maxLifetime", Long.class));
        config.setLeakDetectionThreshold(environment.getProperty("spring.datasource.hikari.leakDetectionThreshold", Long.class));
        config.setConnectionTimeout(environment.getProperty("spring.datasource.hikari.connectionTimeout", Long.class));

        return new HikariDataSource(config);
    }
}
