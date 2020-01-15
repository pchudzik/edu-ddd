package com.pchudzik.edu.ddd.its.infrastructure.queue;

public interface MessageQueue {
    void publish(Message message);

    void registerListener(Object listener);

    interface Message {
    }
}
