package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.DateConverter;
import org.mindrot.jbcrypt.BCrypt;

/**
 * This class is a delegate for the UserMapper interface.
 */
public abstract class UserMapperDelegate implements UserMapper {

    /**
     * Method to convert UserDto to User by making necessary changes such
     * as setting email to lowercase,
     * hashing password and converting role string to RoleType enum.
     * @param userDto the UserDto object to be converted
     * @return the User object after conversion
     */
    public User userDtoToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail().toLowerCase())
                .password(BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt()))
                .role(RoleType.valueOf("ROLE_".concat(userDto.getRole().trim().toUpperCase())))
                .build();
    }

    @Override
    public UserResponseDto userToUserResponse(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .personalAccount(String.valueOf(user.getPersonalAccount()))
                .registrationDate(DateConverter.formatDate(user.getRegistrationDate()))
                .build();
    }
}
