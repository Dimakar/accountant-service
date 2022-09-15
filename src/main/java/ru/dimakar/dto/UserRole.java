package ru.dimakar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserRole {
    ROLE_ADMINISTRATOR("admin"),
    ROLE_USER("business"),
    ROLE_ACCOUNTANT("business"),
    ROLE_AUDITOR("business");

    @Getter
    final String group;
}
