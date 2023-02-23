package com.pact.consumer.producer;

import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final List<User> users = Arrays.asList(
            new User(1, "Jane Doe"),
            new User(2, "John Doe")
    );

    public List<User> getAllUsers() {
        return users;
    }

    public Optional<User> getUser(int id) {
        return users.stream()
                .filter(u -> u.id() == id)
                .findFirst();
    }
}
