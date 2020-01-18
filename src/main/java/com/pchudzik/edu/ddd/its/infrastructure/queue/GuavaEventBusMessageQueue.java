package com.pchudzik.edu.ddd.its.infrastructure.queue;


import com.google.common.eventbus.EventBus;

import java.util.Collection;

class GuavaEventBusMessageQueue implements MessageQueue {
    private final EventBus eventBus = new EventBus();

    public GuavaEventBusMessageQueue(Collection<MessageListener> listeners) {
        listeners.forEach(eventBus::register);
    }

    @Override
    public void publish(Message message) {
        eventBus.post(message);
    }
}
