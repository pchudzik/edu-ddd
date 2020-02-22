package com.pchudzik.edu.ddd.its.infrastructure.db;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class _DatabaseContextModule extends AbstractModule {
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
        DataSource dataSource = loggingDataSource(dataSource());

        bind(DataSource.class).toInstance(dataSource);
        bind(Jdbi.class).toInstance(Jdbi.create(dataSource));
        bind(Flyway.class).toInstance(flyway(dataSource));

        bind(TransactionManager.class).to(TransactionManagerImpl.class).in(Singleton.class);
    }

    private DataSource loggingDataSource(DataSource dataSource) {
        return ProxyDataSourceBuilder
                .create(dataSource)
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .name("h2")
                .build();
    }
}
