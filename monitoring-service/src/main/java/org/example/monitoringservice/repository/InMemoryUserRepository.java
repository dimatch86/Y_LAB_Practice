package org.example.monitoringservice.repository;

import org.example.monitoringservice.model.user.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final List<User> users = new ArrayList<>();

    @Override
    public void saveUser(User user) {
        users.add(user);
    }

    @Override
    public Optional<User> findByEmail(String userEmail) {
        return users.stream().filter(user -> user.getEmail().equals(userEmail))
                .findFirst();
    }
}
