package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Register user (dipakai saat registrasi normal)
    public boolean registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent() ||
            userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    // Save user (dipakai admin untuk menambah/update user)
    public void saveUser(User user) {
        Optional<User> existingOpt = userRepository.findByEmail(user.getEmail());

        if (existingOpt.isEmpty()) {
            // New user
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword("default123");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } else {
            // Update existing user (do not re-encode or overwrite password unless provided)
            User existingUser = existingOpt.get();

            // Update username if provided
            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                existingUser.setUsername(user.getUsername());
            }
            // Update role if provided
            if (user.getRole() != null && !user.getRole().isBlank()) {
                existingUser.setRole(user.getRole());
            }

            userRepository.save(existingUser);
        }
    }

    // Ambil semua user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Ambil user by id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 🔥 Method yang memperbaiki error: hapus user berdasarkan id
    public void deleteUser(Long id) {
        if (id == null) return;
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    // Helper lookup
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
