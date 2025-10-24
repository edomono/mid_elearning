package com.mid.intern.mid_elearning.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123"; // ganti dengan password yang mau di-hash
        String hash = encoder.encode(password);
        System.out.println("BCrypt hash: " + hash);
    }
}
