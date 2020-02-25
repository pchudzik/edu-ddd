package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Command;

public interface AuthorizedCommand extends Command {
    Principal getPrincipal();
}
