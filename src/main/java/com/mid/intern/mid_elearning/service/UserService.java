package com.mid.intern.mid_elearning.service;


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

    public boolean registerUser(User user) {
        // Cek duplikat email atau username
        if (userRepository.findByEmail(user.getEmail()).isPresent() ||
            userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;
        }

        // Hash password sebelum disimpan
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}
