package com.pchudzik.edu.ddd.its.infrastructure.queue;

import com.google.inject.AbstractModule;

public class MessagingContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MessageQueue.class).to(GuavaEventBusMessageQueue.class);
    }
}
