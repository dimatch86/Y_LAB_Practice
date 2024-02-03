package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserMapper {

    public User userDtoToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .password(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()))
                .role(RoleType.valueOf(userDto.getRole()))
                .build();
    }
}
