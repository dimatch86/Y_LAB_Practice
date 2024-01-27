package org.example.monitoringservice.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoleType {
    USER("USER", "Подача показаний, просмотр только соих показаний"),
    ADMIN("ADMIN", "Подача показаний, просмотр показаний всех пользователей");

    private final String role;
    private final String description;
}
