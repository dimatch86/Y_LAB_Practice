package org.example.monitoringservice.security;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(MessageFormat.format("User with email {0} not found", username)));
        return new AppUserDetails(user);
    }
}
