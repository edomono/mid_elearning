package com.mid.intern.mid_elearning.service;

import com.mid.intern.mid_elearning.dto.ParticipantRegistrationDto;
import com.mid.intern.mid_elearning.dto.UserRegistrationDto;
import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerParticipant(ParticipantRegistrationDto participantDto) {
        // Placeholder for participant registration logic
        User user = new User();
        user.setUsername(participantDto.getUsername());
        user.setEmail(participantDto.getEmail());
        user.setPassword(passwordEncoder.encode(participantDto.getPassword()));
        user.setRole(participantDto.getRole() != null ? participantDto.getRole() : "USER");
        userRepository.save(user);
    }

    public void registerNewUser(UserRegistrationDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("USER"); // Default role for new registrations
        user.setApproved(false); // New users need approval
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        // Placeholder for fetching all users
        return userRepository.findAll();
    }

    public User getCurrentUser() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getPendingUsers() {
        return userRepository.findByApprovedFalse();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void approveUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setApproved(true);
            userRepository.save(user);
        });
    }

    public void rejectUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setApproved(false);
            userRepository.save(user);
        });
    }

    public void updateUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}