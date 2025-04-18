package com.backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Status {

    SAMOFINANSIRANJE("samof", "Самофинансирање"),
    BUDZET("budzet", "Буџет"),
    MIROVANJE("mirovanje", "Мировање");

    private final String id;
    private final String description;

    public Status findById(String id) {
        return Arrays.stream(Status.values())
                .filter(status -> Objects.equals(status.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Status type"));
    }

    public static Status findByDescription(String description) {
        return Arrays.stream(Status.values())
                .filter(status -> Objects.equals(status.getDescription(), description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Status description " + description));
    }
}
