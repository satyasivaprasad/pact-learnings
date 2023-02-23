package com.pact.consumer.producer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") int id) {
        return userRepository.getUser(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
