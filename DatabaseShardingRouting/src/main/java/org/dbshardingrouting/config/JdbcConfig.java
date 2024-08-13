package org.dbshardingrouting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }
}
