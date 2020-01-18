package com.pchudzik.edu.ddd.its.infrastructure.queue;

public interface MessageQueue {
    void publish(Message message);

    interface MessageListener {
    }

    interface Message {
    }
}
