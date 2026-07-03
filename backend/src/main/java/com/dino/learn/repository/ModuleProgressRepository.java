package com.dino.learn.repository;

import com.dino.learn.entity.ModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {
    List<ModuleProgress> findByUserId(Long userId);
    Optional<ModuleProgress> findByUserIdAndModuleKey(Long userId, String moduleKey);
}
