package com.pchudzik.edu.ddd.its.infrastructure.queue;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;

class GuavaEventBusMessageQueue implements MessageQueue {
    private final EventBus eventBus;

    public GuavaEventBusMessageQueue() {
        this(null);
    }

    GuavaEventBusMessageQueue(SubscriberExceptionHandler exceptionHandler) {
        eventBus = exceptionHandler != null ? new EventBus(exceptionHandler) : new EventBus();
    }

    @Override
    public void publish(Message message) {
        eventBus.post(message);
    }

    @Override
    public void registerListener(Object listener) {
        eventBus.register(listener);
    }
}
