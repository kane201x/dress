<template>
  <div class="page menu">
    <TopBar :showBack="false" />
    <div class="greeting">
      <span class="dino-small">🦕</span>
      <span class="name">{{ userStore.userName }}</span>，今天想学什么？
    </div>
    <div class="diff-selector" style="margin-bottom:8px;">
      <button v-for="(dl,i) in diffLabels" :key="i" :class="{active: userStore.difficulty===i+1}" @click="changeDiff(i+1)">{{ diffEmojis[i] }} {{ dl }}</button>
    </div>
    <div class="subject-grid">
      <button class="subject-card card-math" @click="router.push('/math')">
        <span class="icon">🔢</span>
        <span class="label">学数学</span>
        <span class="sub">数字 / 数数 / 形状 / 比较 / 排序 / 加法</span>
        <span class="prog">{{ store.mathDoneCount }}/6 模块</span>
      </button>
      <button class="subject-card card-english" @click="router.push('/english')">
        <span class="icon">🔤</span>
        <span class="label">学英语</span>
        <span class="sub">字母 / 颜色 / 动物</span>
        <span class="prog">{{ store.engDoneCount }}/6 模块</span>
      </button>
      <button class="subject-card card-poetry" @click="router.push('/poetry')">
        <span class="icon">📜</span>
        <span class="label">背古诗</span>
        <span class="sub">150 首经典古诗，分类背诵</span>
        <span class="prog">{{ store.poetryDoneCount }}/150 首</span>
      </button>
      <button class="subject-card card-rewards" @click="router.push('/rewards')">
        <span class="icon">⭐</span>
        <span class="label">我的奖励</span>
        <span class="sub">{{ userStore.stars }} 颗星星</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useProgressStore } from '../stores/progress'
import TopBar from '../components/TopBar.vue'

const router = useRouter()
const userStore = useUserStore()
const store = useProgressStore()

const diffLabels = ['简单', '复杂', '困难', '超级困难']
const diffEmojis = ['🌱', '🌿', '🔥', '💎']

function changeDiff(level) { userStore.updateDifficulty(level) }
</script>

<style scoped>
.menu {
  background: linear-gradient(180deg, #E0F7FA 0%, #FFF8E7 50%, #FFF0F0 100%);
  gap: 6px;
  padding-top: 60px;
  justify-content: flex-start;
}
.menu .greeting { font-size: 28px; margin-top: 24px; text-align: center; }
.menu .greeting .name { color: var(--primary); }
.menu .greeting .dino-small {
  display: inline-block;
  font-size: 36px;
  animation: dinoBounce 2s ease-in-out infinite;
}
.menu .subject-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  width: 100%;
  max-width: 460px;
  padding: 0 10px;
  margin-top: 10px;
}
.menu .subject-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 20px 10px;
  border-radius: var(--radius);
  border: none;
  background: #fff;
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: all 0.2s;
  min-height: 150px;
}
.menu .subject-card:active { transform: scale(0.95); }
.menu .subject-card .icon { font-size: 56px; }
.menu .subject-card .label { font-size: 24px; font-weight: bold; }
.menu .subject-card .sub { font-size: 15px; color: #999; }
.menu .subject-card.card-math { border-bottom: 6px solid var(--primary); }
.menu .subject-card.card-english { border-bottom: 6px solid var(--secondary); }
.menu .subject-card.card-poetry { border-bottom: 6px solid var(--purple); }
.menu .subject-card.card-rewards { border-bottom: 6px solid var(--yellow); }
.menu .subject-card .prog { font-size: 14px; color: #bbb; margin-top: 2px; }
</style>
