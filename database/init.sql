-- Dino Learning Database Schema
CREATE DATABASE IF NOT EXISTS knowall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE knowall;

-- Users table
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  stars INT DEFAULT 0,
  difficulty INT DEFAULT 1 COMMENT '1=简单 2=复杂 3=困难 4=超级困难',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Module progress tracking
CREATE TABLE module_progress (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  module_key VARCHAR(50) NOT NULL COMMENT 'e.g. numbers, counting, shapes, alphabet, animals, poem0...',
  completed TINYINT(1) DEFAULT 0,
  completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_module (user_id, module_key),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sticker unlocks
CREATE TABLE stickers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  sticker_key VARCHAR(50) NOT NULL,
  unlocked TINYINT(1) DEFAULT 0,
  unlocked_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_sticker (user_id, sticker_key),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Poems table (reference data)
CREATE TABLE poems (
  id INT AUTO_INCREMENT PRIMARY KEY,
  emoji VARCHAR(10) NOT NULL,
  title VARCHAR(100) NOT NULL,
  author VARCHAR(50) NOT NULL,
  dynasty VARCHAR(20) NOT NULL,
  category VARCHAR(20) NOT NULL COMMENT '咏物,山水,四季,田园,思乡,友情,边塞,哲理,节日,童趣,花卉,励志,爱国,送别,闲适',
  bg VARCHAR(20) NOT NULL COMMENT 'background theme',
  line1 VARCHAR(200) NOT NULL,
  line2 VARCHAR(200) NOT NULL,
  line3 VARCHAR(200) NOT NULL DEFAULT '',
  line4 VARCHAR(200) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- English vocabulary categories
CREATE TABLE vocab_categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  `key` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- English vocabulary items
CREATE TABLE vocab_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  category_key VARCHAR(50) NOT NULL,
  emoji VARCHAR(10) NOT NULL,
  english VARCHAR(100) NOT NULL,
  chinese VARCHAR(100) NOT NULL,
  option1 VARCHAR(100) NOT NULL,
  option2 VARCHAR(100) NOT NULL,
  option3 VARCHAR(100) NOT NULL,
  sort_order INT DEFAULT 0,
  FOREIGN KEY (category_key) REFERENCES vocab_categories(`key`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Alphabet table
CREATE TABLE alphabet (
  id INT AUTO_INCREMENT PRIMARY KEY,
  upper_char CHAR(1) NOT NULL,
  lower_char CHAR(1) NOT NULL,
  word VARCHAR(100) NOT NULL,
  emoji VARCHAR(10) NOT NULL,
  sentence VARCHAR(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Colors table
CREATE TABLE colors (
  id INT AUTO_INCREMENT PRIMARY KEY,
  color_hex VARCHAR(7) NOT NULL,
  english VARCHAR(50) NOT NULL,
  chinese VARCHAR(50) NOT NULL,
  has_border TINYINT(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Shapes table
CREATE TABLE shapes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  emoji VARCHAR(10) NOT NULL,
  name VARCHAR(50) NOT NULL,
  color VARCHAR(7) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Compare pairs table
CREATE TABLE compare_pairs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  big_emoji VARCHAR(10) NOT NULL,
  big_label VARCHAR(50) NOT NULL,
  small_emoji VARCHAR(10) NOT NULL,
  small_label VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Audio cache table (TTS)
CREATE TABLE audio_records (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  file_key VARCHAR(255) NOT NULL UNIQUE,
  text_content VARCHAR(500) NOT NULL,
  language VARCHAR(20) NOT NULL,
  file_size BIGINT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_file_key (file_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
