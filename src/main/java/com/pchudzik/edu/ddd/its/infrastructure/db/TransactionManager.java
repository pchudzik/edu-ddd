package com.pchudzik.edu.ddd.its.infrastructure.db;

public interface TransactionManager {
    <R> R inTransaction(TransactionCallback<R> transactionCallback);

    void useTransaction(TransactionAction callback);

    interface TransactionAction {
        void execute();
    }

    interface TransactionCallback<T> {
        T execute();
    }

    class NoTransactionManager implements TransactionManager {
        @Override
        public <R> R inTransaction(TransactionCallback<R> transactionCallback) {
            return transactionCallback.execute();
        }

        @Override
        public void useTransaction(TransactionAction callback) {
            callback.execute();
        }
    }
}
