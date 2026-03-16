package com.kodnest.app.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Specifies that the userId will be auto-generated.
    private Integer userId; 
    // Stores the unique identifier for each user.

    @Column(nullable = false, unique = true) 
    // Ensures that the username is unique and cannot be null.
    private String username; 
    // Stores the username for user authentication.

    @Column(nullable = false, unique = true) 
    // Ensures that the email is unique and cannot be null.
    private String email; 
    // Stores the user's email for communication and login.

    @Column(nullable = false) 
    // Ensures that the password cannot be null.
    private String password; 
    // Stores the hashed password for user authentication.

    @Enumerated(EnumType.STRING) 
    // Maps the role as a string value (e.g., ADMIN or CUSTOMER).
    @Column(nullable = false)
    private Role role; 
    // Stores the user's role.

    @Column(nullable = false, updatable = false) 
    // Ensures the creation timestamp is set and cannot be updated.
    private LocalDateTime createdAt = LocalDateTime.now(); 
    // Automatically sets the creation timestamp.

    @Column(nullable = false) 
    // Ensures the updated timestamp is set and can be updated.
    private LocalDateTime updatedAt = LocalDateTime.now(); 
    // Automatically updates the timestamp on modification.

    // 🔹 Default constructor (required by JPA)
    public User() {
    }

    // 🔹 Constructor with ID
    public User(Integer userId, String username, String email, String password,
                Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🔹 Constructor without ID
    public User(String username, String email, String password,
                Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🔹 Getters & Setters

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

	@Override
	public String toString() {
		return "User [userId=" + userId + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", role=" + role + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, email, password, role, updatedAt, userId, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(createdAt, other.createdAt) && Objects.equals(email, other.email)
				&& Objects.equals(password, other.password) && role == other.role
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(userId, other.userId)
				&& Objects.equals(username, other.username);
	}
}

