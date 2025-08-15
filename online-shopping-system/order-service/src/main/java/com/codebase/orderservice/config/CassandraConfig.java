package com.codebase.orderservice.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;

@Configuration
public class CassandraConfig {

    @Bean
    public CassandraTemplate cassandraTemplate(CqlSession session) {
        // Let Spring Boot create the CqlSession from your application.yml,
        // we only expose a CassandraTemplate bean for repositories.
        return new CassandraTemplate(session);
    }
}
