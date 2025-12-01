package com.cashflow.service;

import com.cashflow.dto.UserDTO;
import com.cashflow.exception.DuplicateResourceException;
import com.cashflow.exception.ResourceNotFoundException;
import com.cashflow.model.User;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User-related business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Register a new user
     */
    public UserDTO registerUser(UserDTO userDTO) {
        // Check for duplicate username
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateResourceException("User", "username", userDTO.getUsername());
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("User", "email", userDTO.getEmail());
        }

        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword()) // In production, should be encrypted
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phoneNumber(userDTO.getPhoneNumber())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToDTO(user);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToDTO(user);
    }

    /**
     * Authenticate user with username and password
     */
    @Transactional(readOnly = true)
    public UserDTO authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Check if user is an OAuth user (no password set)
        if (user.getPassword() == null || "GOOGLE".equals(user.getAuthProvider())) {
            throw new IllegalArgumentException("This account uses Google Sign-In. Please login with Google.");
        }

        // Verify password (plain text comparison - in production use BCrypt)
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User account is deactivated");
        }

        return mapToDTO(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     */
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update fields
        if (userDTO.getFirstName() != null)
            user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null)
            user.setLastName(userDTO.getLastName());
        if (userDTO.getPhoneNumber() != null)
            user.setPhoneNumber(userDTO.getPhoneNumber());

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    /**
     * Deactivate user (soft delete)
     */
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    /**
     * Map User entity to DTO
     */
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .totalExpenses(user.getExpenses() != null ? user.getExpenses().size() : 0)
                .totalCredits(user.getCredits() != null ? user.getCredits().size() : 0)
                .totalCategories(user.getCategories() != null ? user.getCategories().size() : 0)
                .build();
    }

    /**
     * Find or create OAuth2 user (for Google login)
     */
    public UserDTO findOrCreateOAuth2User(String email, String name, String googleId) {
        // Check if user exists by googleId
        User user = userRepository.findByGoogleId(googleId).orElse(null);

        if (user != null) {
            return mapToDTO(user);
        }

        // Check if user exists by email
        user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // Link Google account to existing user
            user.setGoogleId(googleId);
            user.setAuthProvider("GOOGLE");
            return mapToDTO(userRepository.save(user));
        }

        // Create new user
        String[] nameParts = name != null ? name.split(" ", 2) : new String[] { email.split("@")[0], "" };
        String username = email.split("@")[0] + "_" + System.currentTimeMillis() % 10000;

        user = User.builder()
                .username(username)
                .email(email)
                .googleId(googleId)
                .authProvider("GOOGLE")
                .firstName(nameParts[0])
                .lastName(nameParts.length > 1 ? nameParts[1] : "")
                .isActive(true)
                .build();

        return mapToDTO(userRepository.save(user));
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToDTO(user);
    }
}
