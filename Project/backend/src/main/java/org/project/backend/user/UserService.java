package org.project.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public User findOrCreateUser(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        return existingUser.orElseGet(() -> {
            // Create a new user if not found
            User newUser = new User();
            newUser.setEmail(email);
            // You may want to set other properties as needed
            userRepository.save(newUser);
            return newUser;
        });
    }
}
