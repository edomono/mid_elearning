package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() ||
            userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setApproved(false);
        userRepository.save(user);
        return true;
    }

    public void saveUser(User user) {
        // Jika password belum ter-encode, encode dulu
        String pw = user.getPassword();
        if (pw != null) {
            boolean looksEncoded = pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$");
            if (!looksEncoded) {
                user.setPassword(passwordEncoder.encode(pw));
            }
        }
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getApprovedUsers() {
        return userRepository.findByApprovedTrue();
    }

    public List<User> getPendingUsers() {
        return userRepository.findByApprovedFalse();
    }

    public void approveUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setApproved(true);
            userRepository.save(user);
        });
    }

    public void rejectUser(Long id) {
        userRepository.findById(id).ifPresent(user -> userRepository.delete(user));
    }
}
