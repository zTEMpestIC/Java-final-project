package com.flowstudy.dto;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.flowstudy.core.User;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDTO createUser(String username, String email, String passwordHash) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        User user = new User(username, email, passwordHash);
        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    public Optional<UserDTO> getUserById(UUID id) {
        return userRepository.findById(id)
            .map(this::convertToDTO);
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(this::convertToDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::convertToDTO);
    }

    public UserDTO updateUser(UUID id, String email, String passwordHash) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        
        if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        if (email != null) {
            user.setEmail(email);
        }
        if (passwordHash != null) {
            user.setPasswordHash(passwordHash);
        }
        
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserDTO convertToDTO(User entity) {
        return new UserDTO(
            entity.getId(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getCreatedAt()
        );
    }

    public record UserDTO(UUID id, String username, String email, java.time.LocalDateTime createdAt) {}
}
