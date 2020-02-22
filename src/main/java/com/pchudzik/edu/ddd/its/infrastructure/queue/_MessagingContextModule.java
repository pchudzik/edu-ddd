package com.pchudzik.edu.ddd.its.infrastructure.queue;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

import javax.inject.Singleton;
import java.util.stream.Collectors;

public class _MessagingContextModule extends AbstractModule {

    @Singleton
    @Provides
    public MessageQueue messageQueue(Injector injector) {
        return new GuavaEventBusMessageQueue(injector
                .getAllBindings()
                .keySet().stream()
                .filter(k -> MessageQueue.MessageListener.class.isAssignableFrom(k.getTypeLiteral().getRawType()))
                .map(k -> (Key<MessageQueue.MessageListener>) k)
                .map(injector::getInstance)
                .collect(Collectors.toSet()));
    }
}
