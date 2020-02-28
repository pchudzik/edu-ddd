package com.pchudzik.edu.ddd.its.user.access;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionType {
    ADMINISTRATOR(false),
    USER_MANAGER(false),
    ROLES_MANAGER(false),
    CREATE_PROJECT(false),
    SINGLE_PROJECT_MANAGER(true),
    ISSUE_MANAGER(true),
    ACCESS_ISSUE(true),
    ACCESS_PROJECT(true);

    @Getter(AccessLevel.PACKAGE)
    private final boolean projectLevel;
}
