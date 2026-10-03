package com.payshield.service;

import com.payshield.entity.User;
import com.payshield.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // GET ALL USERS
    // =========================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // =========================
    // CREATE USER
    // =========================

    public User saveUser(User user) {

        if (user.getEmail() == null ||
                user.getEmail().isBlank()) {

            throw new RuntimeException("Email is required");
        }

        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            throw new RuntimeException("Password is required");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }

    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(Long id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(existingUser.getId())) {
            throw new RuntimeException(
                    "You are not allowed to delete this user");
        }

        userRepository.delete(existingUser);
    }

    // =========================
    // UPDATE USER
    // =========================

    public User updateUser(Long id, User updatedUser) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(existingUser.getId())) {
            throw new RuntimeException(
                    "You are not allowed to update this user");
        }

        if (updatedUser.getName() != null &&
                !updatedUser.getName().isBlank()) {

            existingUser.setName(updatedUser.getName());
        }

        if (updatedUser.getEmail() != null &&
                !updatedUser.getEmail().isBlank()) {

            if (!updatedUser.getEmail()
                    .equals(existingUser.getEmail()) &&
                    userRepository.findByEmail(
                            updatedUser.getEmail()
                    ).isPresent()) {

                throw new RuntimeException(
                        "Email already registered");
            }

            existingUser.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        return userRepository.save(existingUser);
    }

    // =========================
    // CURRENT USER
    // =========================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Current user not found"));
    }
}