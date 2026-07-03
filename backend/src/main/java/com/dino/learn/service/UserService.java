package com.dino.learn.service;

import com.dino.learn.dto.UserVO;
import com.dino.learn.entity.User;
import com.dino.learn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String name) {
        return userRepository.findByName(name)
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name);
                    return userRepository.save(user);
                });
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
        return new UserVO(user.getId(), user.getName(), user.getStars(), user.getDifficulty());
    }
}
