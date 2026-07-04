package com.dino.learn.service;

import com.dino.learn.dto.UserVO;
import com.dino.learn.entity.User;
import com.dino.learn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        String salt = generateSalt();
        String hashed = hashPassword(rawPassword, salt);
        user.setPassword(salt + ":" + hashed);
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));
        String stored = user.getPassword();
        int colon = stored.indexOf(':');
        if (colon < 0) throw new RuntimeException("Invalid password format");
        String salt = stored.substring(0, colon);
        String expectedHash = stored.substring(colon + 1);
        String actualHash = hashPassword(rawPassword, salt);
        if (!expectedHash.equals(actualHash)) {
            throw new RuntimeException("Wrong password");
        }
        return user;
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("User not found: " + name));
    }

    @Transactional
    public UserVO updateStars(Long id, int delta) {
        User user = getUser(id);
        user.setStars(Math.max(0, user.getStars() + delta));
        userRepository.save(user);
        return toVO(user);
    }

    @Transactional
    public UserVO updateDifficulty(Long id, int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Difficulty must be between 1 and 5");
        }
        User user = getUser(id);
        user.setDifficulty(level);
        userRepository.save(user);
        return toVO(user);
    }

    public UserVO toVO(User user) {
        return new UserVO(user.getId(), user.getName(), user.getEmail(), user.getStars(), user.getDifficulty());
    }
}
