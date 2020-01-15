package com.pchudzik.edu.ddd.its.infrastructure.db;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class DatabaseContextModule extends AbstractModule {
    private static DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setUsername("sa");

        return new HikariDataSource(hikariConfig);
    }

    private static Flyway flyway(DataSource dataSource) {
        Flyway flyway = new FluentConfiguration()
                .dataSource(dataSource)
                .locations("db")
                .load();
        flyway.migrate();
        return flyway;
    }

    @Override
    protected void configure() {
        DataSource dataSource = dataSource();

        bind(DataSource.class).toInstance(dataSource);
        bind(Jdbi.class).toInstance(Jdbi.create(dataSource));
        bind(Flyway.class).toInstance(flyway(dataSource));

        bind(TransactionManager.class).to(TransactionManagerImpl.class);
    }
}
