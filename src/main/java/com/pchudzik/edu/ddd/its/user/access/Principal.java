package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.UserId;

public interface Principal {
    static Principal of(UserId user) {
        return () -> user;
    }

    UserId getUserId();
}
