package org.example.monitoringservice.service;

import lombok.RequiredArgsConstructor;

import org.example.loggingstarter.aop.Loggable;
import org.example.monitoringservice.dto.request.LoginRequestDto;
import org.example.monitoringservice.dto.response.AuthResponseDto;
import org.example.monitoringservice.exception.custom.UserAlreadyExistException;
import org.example.monitoringservice.exception.custom.UserNotFoundException;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepository;
import org.example.monitoringservice.security.AppUserDetails;
import org.example.monitoringservice.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Implementation of the Authentication Service interface.
 */
@Service
@Loggable
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Registers a new user.
     * @param user the user to be registered
     * @throws UserAlreadyExistException if the user is already registered
     */
    @Override
    public void registerUser(User user) {
        userRepository.saveUser(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    /**
     * Logs a user in.
     * @param loginRequestDto the user's login request
     * @throws UserNotFoundException if the user is not found
     */
    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email, loginRequestDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return AuthResponseDto.builder()
                .token(jwtUtils.generateJwtToken(userDetails))
                .email(userDetails.getUsername())
                .roles(roles)
                .build();
    }
}
