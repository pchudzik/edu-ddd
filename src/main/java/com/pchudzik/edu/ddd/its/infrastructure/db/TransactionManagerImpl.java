package com.pchudzik.edu.ddd.its.infrastructure.db;

import org.jdbi.v3.core.Jdbi;

import javax.inject.Inject;

class TransactionManagerImpl implements TransactionManager {
    private final Jdbi jdbi;

    @Inject
    TransactionManagerImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public <R> R inTransaction(TransactionCallback<R> transactionCallback) {
        return jdbi.inTransaction(h -> transactionCallback.execute());
    }

    public void useTransaction(TransactionAction callback) {
        jdbi.useTransaction(h -> callback.execute());
    }

}
