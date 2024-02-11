package org.example.monitoringservice.mapper;

import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.model.user.User;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
/**
 * An interface for mapping user data transfer objects (DTO) to user
 * domain objects and vice versa.
 * The USER_MAPPER provides a singleton instance of the UserMapper.
 */
@Mapper
@DecoratedWith(UserMapperDelegate.class)
public interface UserMapper {

    /**
     * Singleton instance of the UserMapper for mapping user DTOs
     * to domain objects and vice versa.
     */
    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    /**
     * Converts a UserDto object to a User object.
     * @param userDto the input UserDto object
     * @return a corresponding User object
     */
    User userDtoToUser(UserDto userDto);
    UserResponseDto userToUserResponse(User user);
}
