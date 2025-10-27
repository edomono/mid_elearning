package com.mid.intern.mid_elearning.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

    // ==================== FIELD ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 50, message = "Username harus antara 3-50 karakter")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String role = "USER"; // Default role

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean approved = false; // Default belum disetujui admin

    // ==================== CONSTRUCTOR ====================

    public User() {}

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = (role != null && !role.isBlank()) ? role.toUpperCase() : "USER";
        this.approved = false;
    }

    // ==================== GETTER & SETTER ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = (username != null) ? username.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim().toLowerCase() : null;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            this.role = "USER";
        } else {
            this.role = role.toUpperCase().replace("ROLE_", ""); // normalisasi
        }
    }

    public Boolean getApproved() {
        return approved;
    }

    public boolean isApproved() {
        return Boolean.TRUE.equals(this.approved);
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }


    // ==================== HELPER METHOD ====================

    /** Mengembalikan format role sesuai standar Spring Security ("ROLE_...") */
    public String getFormattedRole() {
        return "ROLE_" + this.role.toUpperCase();
    }

    /** Untuk debug/logging */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", approved=" + approved +
                '}';
    }
}
