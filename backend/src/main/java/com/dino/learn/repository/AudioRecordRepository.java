package com.dino.learn.repository;

import com.dino.learn.entity.AudioRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AudioRecordRepository extends JpaRepository<AudioRecord, Long> {
    Optional<AudioRecord> findByFileKey(String fileKey);
}
