package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
                .role(RoleType.valueOf(userDto.getRole().toUpperCase()))
                .build();
    }

    @Override
    public UserResponseDto userToUserResponse(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .personalAccount(String.valueOf(user.getPersonalAccount()))
                .registrationDate(formatDate(user.getRegistrationDate()))
                .build();
    }

    /**
     * Formats the input Instant date to a string representation with
     * the pattern "yyyy-MM-dd HH:mm:ss".
     * @param date the input Instant date
     * @return the formatted date string
     */
    private static String formatDate(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = date.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.format(formatter);
    }
}
