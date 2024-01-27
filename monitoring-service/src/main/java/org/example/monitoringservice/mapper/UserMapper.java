package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.UserDto;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;

public class UserMapper {

    public User userDtoToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .role(RoleType.valueOf(userDto.getRole()))
                .build();
    }
}
