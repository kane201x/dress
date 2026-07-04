# Dino 学习乐园 🦕

一个面向 3-8 岁儿童的互动教育 Web App，涵盖**数学**、**英语**和**古诗**学习。通过可爱的恐龙吉祥物 Dino、星星奖励和贴纸收集系统，让学习变得有趣。

---

## 功能

### 📐 数学
- 数字认识（1~20） · 数数 · 几何图形 · 比大小 · 数字排序 · 加法
- 难度自适应（4 级）：Easy / Medium / Hard / Super Hard

### 🔤 英语
- 字母 A~Z 闪卡 · 颜色（12色） · 动物（145种） · 水果 · 交通工具 · 身体部位
- 图文配对选择题，带 TTS 发音

### 📜 古诗
- 147 首经典古诗，12 个分类（咏物、山水、四季、田园、思乡等）
- 拼音标注（pinyin-pro）· 逐行 TTS 朗读 · 12 种动态背景主题

### 🎮 游戏化
- 完成模块获得 ⭐ 星星（最多 50 颗）
- 解锁 🎯 贴纸（14 枚）
- 模块完成时撒花 + 庆祝音效
- 点击 Dino 5 次隐藏彩蛋（+1 星）

---

## 技术栈

| 层 | 技术 |
|---|---|
| **前端** | Vue 3 (Composition API + `<script setup>`) · Vite 5 · Pinia · Vue Router 4 · Axios · pinyin-pro |
| **后端** | Spring Boot 3.2.0 · Java 17 · Spring Data JPA · MySQL 8.0 · MinIO (S3) · JJWT |
| **AI** | Alibaba DashScope / Tongyi Qianwen（`qwen3-tts-flash` TTS） |
| **Android** | WebView wrapper · AGP 8.1.0 · compileSdk 34 · minSdk 24 |
| **部署** | Docker Compose（MySQL + MinIO + Backend + Frontend Nginx） |

---

## 快速开始

依赖：Docker + Docker Compose

```bash
# 克隆仓库
git clone <repo-url> dress
cd dress

# 启动全部服务
docker compose up -d

# 访问 http://localhost → 前端界面
# 后端 API：http://localhost:8080
# MinIO Console：http://localhost:9001
```

### 环境变量

```yaml
DB_URL:      jdbc:mysql://*:3306/knowall?...
AI_MODEL:    glm/glm-5
TTS_MODEL:   qwen3-tts-flash
TTS_VOICE:   CHERRY
JWT_SECRET:  MailuoSecretKey...
```

---

## 项目结构

```
dress/
├── frontend/              # Vue 3 SPA
│   ├── src/
│   │   ├── views/         # 页面组件（Welcome · Menu · Math · English · Poetry）
│   │   ├── components/    # 通用组件（TopBar · DinoGuide · CelebrationOverlay）
│   │   ├── stores/        # Pinia 状态管理（user · progress · celebration）
│   │   ├── services/      # Axios API 客户端
│   │   ├── utils/         # 工具（tts.js · poems.js · quizData.js）
│   │   └── router/        # 路由定义（18 条路由）
│   ├── index.html
│   ├── nginx.conf         # Nginx 反向代理 /api → backend:8080
│   └── package.json
│
├── backend/               # Spring Boot REST API
│   ├── src/main/java/com/dino/learn/
│   │   ├── controller/    # Auth · Learning · Progress · TTS · User
│   │   ├── service/       # TtsService · MinioService · ProgressService
│   │   ├── repository/    # JPA Repositories
│   │   ├── entity/        # User · Poem · VocabItem · AudioRecord
│   │   └── config/        # JWT · CORS · DashScope · MinIO · Security
│   └── pom.xml
│
├── android/               # Android WebView 外壳
│   ├── app/src/main/java/
│   │   └── MainActivity.java  # WebView + Android TTS Bridge
│   └── app/build.gradle
│
├── database/              # SQL 初始化脚本
│   ├── init.sql
│   └── seed.sql
│
└── docker-compose.yml
```

---

## Android APK

Android 客户端是一个 WebView 外壳，自动加载前端界面并注入原生 TTS 引擎。

```bash
cd android

# Windows
gradlew assembleDebug

# Linux / macOS
./gradlew assembleDebug

# APK 输出
# app/build/outputs/apk/debug/app-debug.apk
```

APK 内置前端 dist，加载方式为 `loadDataWithBaseURL()`，资源请求走 `http://ip:3000`。

### 原生 TTS 桥接

Android 端通过 `@JavascriptInterface` 向 WebView 注入 `window.AndroidTts` 对象：

| 方法 | 说明 |
|---|---|
| `speak(text)` | 中文朗读 |
| `speakWithLanguage(text, language)` | 指定语言朗读（Chinese / English） |
| `stop()` | 停止朗读 |
| `isReady()` | TTS 引擎是否就绪 |

---

## TTS 架构（4 级降级）

```
speakCN(text) / speakEN(text)
  │
  ├─ [Tier 1] Android 原生 TTS (window.AndroidTts)
  │    └─ 仅在 Android WebView 中可用
  │
  ├─ [Tier 2] 后端 AI TTS API
  │    ├─ POST /api/tts/speak { text, language }
  │    ├─ DashScope qwen3-tts-flash 合成
  │    └─ MinIO 缓存 + MySQL 录音记录
  │
  ├─ [Tier 3] 浏览器 SpeechSynthesis
  │    └─ window.speechSynthesis.speak(utterance)
  │
  └─ [Tier 4] 静默失败
```

对于 Android 设备，优先走原生 TTS（离线、低延迟、无需 API 密钥）；iOS 设备走 `SpeechSynthesis` 降级（内置中文语音包）。

---

## API 端点

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/login` | 登录 / 注册 |
| GET | `/api/user/{id}` | 获取用户信息 |
| PUT | `/api/user/{id}/difficulty` | 修改难度级别 |
| PUT | `/api/user/{id}/stars` | 增减星星 |
| GET | `/api/progress/{userId}` | 获取学习进度 |
| POST | `/api/progress/module` | 完成模块 |
| POST | `/api/progress/sticker` | 解锁贴纸 |
| GET | `/api/learn/poems` | 古诗列表 |
| GET | `/api/learn/poems/{id}` | 古诗详情 |
| GET | `/api/learn/vocab/{cat}` | 词汇列表 |
| POST/GET | `/api/tts/speak` | TTS 语音合成 |

---

## 开发

```bash
# 前端开发服务器（热更新）
cd frontend
npm install
npm run dev          # → localhost:3000

# 后端（需 Java 17 + Maven）
cd backend
mvn spring-boot:run  # → localhost:8080
```

---

## 许可证

MIT
