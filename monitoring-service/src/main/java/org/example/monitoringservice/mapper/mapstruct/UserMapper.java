package org.example.monitoringservice.mapper.mapstruct;

import org.example.monitoringservice.dto.request.UserDto;
import org.example.monitoringservice.dto.response.UserResponseDto;
import org.example.monitoringservice.model.user.User;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
/**
 * An interface for mapping user data transfer objects (DTO) to user
 * domain objects and vice versa.
 * The USER_MAPPER provides a singleton instance of the UserMapper.
 */
@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDelegate.class)
public interface UserMapper {

    /**
     * Converts a UserDto object to a User object.
     * @param userDto the input UserDto object
     * @return a corresponding User object
     */
    User userDtoToUser(UserDto userDto);
    UserResponseDto userToUserResponse(User user);
}
