package com.pchudzik.edu.ddd.its.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
class DatabaseCleaner {
    private final Jdbi jdbi;

    public void cleanupDb() {
        setReferentialIntegrity(false);
        findAllTables(Set.of("flyway_schema_history")).forEach(this::truncateTable);
        setReferentialIntegrity(true);
    }

    private void truncateTable(String table) {
        jdbi.withHandle(h -> h.execute("truncate table " + table));
    }

    private void setReferentialIntegrity(boolean enabled) {
        jdbi.withHandle(h -> h
                .createCall("SET REFERENTIAL_INTEGRITY :value")
                .bind("value", enabled)
                .invoke());
    }

    private Set<String> findAllTables(Collection<String> excluded) {
        return jdbi.withHandle(h ->
                h
                        .select("show tables")
                        .map(r -> r.getColumn("TABLE_NAME", String.class))
                        .stream()
                        .filter(t -> !excluded.contains(t))
                        .collect(toSet()));
    }
}
