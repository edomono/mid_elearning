package com.mid.intern.mid_elearning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mid.intern.mid_elearning.model.User;
import com.mid.intern.mid_elearning.repository.UserRepository;
import com.mid.intern.mid_elearning.repository.DiscussionRepository;
import com.mid.intern.mid_elearning.repository.SubmissionRepository; // Import SubmissionRepository
import com.mid.intern.mid_elearning.dto.UserRegistrationDto;
import com.mid.intern.mid_elearning.dto.ParticipantRegistrationDto;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository; // Inject SubmissionRepository

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =============================================================
    // 👥 USER RETRIEVAL
    // =============================================================
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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // No user logged in
        }
        String username = authentication.getName();
        return getUserByUsername(username).orElse(null);
    }

    // =============================================================
    // ✅ EXISTENCE CHECK
    // =============================================================
    public boolean existsByEmailOrUsername(String email, String username) {
        return userRepository.findByEmail(email).isPresent()
            || userRepository.findByUsername(username).isPresent();
    }


    public void prepareAndSaveNewUser(User user) {
        // Trim input
        if (user.getUsername() != null) user.setUsername(user.getUsername().trim());
        if (user.getEmail() != null) user.setEmail(user.getEmail().trim());

        // Default password jika kosong
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword("default123");
        }

        // Encode password jika belum terenkripsi
        String pw = user.getPassword();
        boolean looksEncoded = pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$");
        if (!looksEncoded) {
            user.setPassword(passwordEncoder.encode(pw));
        }

        // Default role & approval
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        user.setApproved(true);

        userRepository.save(user);
    }

    public void registerNewUser(UserRegistrationDto registrationDto) {
        if (existsByEmailOrUsername(registrationDto.getEmail(), registrationDto.getUsername())) {
            throw new IllegalStateException("Email atau Username sudah terdaftar!");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        String role = registrationDto.getRole();
        if ("STUDENT".equals(role)) {
            role = "USER";
        }
        user.setRole(role != null && !role.isBlank() ? role : "USER");
        user.setApproved(false); // New users need admin approval

        userRepository.save(user);
    }

    public void registerParticipant(ParticipantRegistrationDto participantDto) {
        if (existsByEmailOrUsername(participantDto.getEmail(), participantDto.getUsername())) {
            throw new IllegalStateException("Email atau Username sudah terdaftar!");
        }

        User user = new User();
        user.setUsername(participantDto.getUsername());
        user.setEmail(participantDto.getEmail());
        user.setPassword(passwordEncoder.encode(participantDto.getPassword()));
        String role = participantDto.getRole();
        if ("STUDENT".equals(role)) {
            role = "USER";
        }
        user.setRole(role != null && !role.isBlank() ? role : "USER");
        user.setApproved(true); // Admin added participants are approved by default

        userRepository.save(user);
    }

    // =============================================================
    // ✏️ USER UPDATE
    public void updateUser(Long id, User updatedUser) {
        userRepository.findById(id).ifPresent(existing -> {
            existing.setUsername(updatedUser.getUsername());
            existing.setEmail(updatedUser.getEmail());
            existing.setRole(updatedUser.getRole());

            // Update password jika diisi baru
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                String pw = updatedUser.getPassword();
                boolean looksEncoded = pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$");
                if (!looksEncoded) {
                    existing.setPassword(passwordEncoder.encode(pw));
                } else {
                    existing.setPassword(pw);
                }
            }

            userRepository.save(existing);
        });
    }

    // =============================================================
    // 🔐 SAVE USER (General)
    // =============================================================
    public void saveUser(User user) {
        String pw = user.getPassword();
        if (pw != null) {
            boolean looksEncoded = pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$");
            if (!looksEncoded) {
                user.setPassword(passwordEncoder.encode(pw));
            }
        }
        userRepository.save(user);
    }

    // =============================================================
    // 🗑️ DELETE USER
    // =============================================================
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            // Delete all submissions associated with this user first
            submissionRepository.deleteAll(submissionRepository.findByUser(user));
            // Now delete the user
            discussionRepository.deleteAll(discussionRepository.findBySender(user));
            userRepository.deleteById(id);
        });
    }

    // =============================================================
    // 🧾 APPROVAL MANAGEMENT
    // =============================================================
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
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    // =============================================================
    // 🔑 PASSWORD MANAGEMENT
    // =============================================================
    public void updateUserPassword(User user, String newPassword) {
        if (user != null && newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
}
