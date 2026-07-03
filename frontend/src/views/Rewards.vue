<template>
  <div class="page rewards-page">
    <TopBar back-route="/menu" @back="router.push('/menu')" />
    <div class="title">⭐ 我的星星 ⭐</div>
    <div class="star-collection">
      <div v-for="i in store.maxStars" :key="i" class="star-item" :class="{filled: i<=userStore.stars, empty: i>userStore.stars}">
        {{ i<=userStore.stars ? '⭐' : '☆' }}
      </div>
    </div>
    <div class="star-count">已获得 {{ userStore.stars }} / {{ store.maxStars }} 颗星星</div>
    <div class="stickers">
      <div v-for="(s,i) in store.stickers" :key="i" class="sticker" :class="{locked: !s.unlocked, unlocked: s.unlocked}">
        {{ s.unlocked ? s.emoji : '🔒' }}
      </div>
    </div>
    <button class="big-btn yellow" style="margin-top:16px" @click="celebrate">🎉 放烟花</button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useProgressStore } from '../stores/progress'
import { useCelebrationStore } from '../stores/celebration'
import TopBar from '../components/TopBar.vue'

const router = useRouter()
const userStore = useUserStore()
const store = useProgressStore()
const celebrationStore = useCelebrationStore()

function celebrate() {
  celebrationStore.show('🎉', '继续加油！', '')
}
</script>

<style scoped>
.rewards-page {
  background: linear-gradient(180deg, #FFFBEB 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.rewards-page .title { font-size: 32px; margin-top: 16px; }
.rewards-page .star-collection {
  display: flex; gap: 10px; flex-wrap: wrap;
  justify-content: center; margin-top: 16px; padding: 0 20px;
}
.rewards-page .star-collection .star-item { font-size: 44px; transition: all 0.3s; }
.rewards-page .star-collection .star-item.empty { filter: grayscale(1); opacity: 0.3; }
.rewards-page .star-collection .star-item.filled { animation: starFloat 2s ease-in-out infinite; }
.rewards-page .star-count { font-size: 28px; margin-top: 8px; color: #888; }
.rewards-page .stickers {
  display: flex; gap: 16px; flex-wrap: wrap; justify-content: center; margin-top: 20px;
}
.rewards-page .stickers .sticker {
  width: 80px; height: 80px; border-radius: 20px;
  background: #fff; display: flex; align-items: center;
  justify-content: center; font-size: 40px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.rewards-page .stickers .sticker.locked { filter: grayscale(1); opacity: 0.3; }
.rewards-page .stickers .sticker.unlocked { animation: starFloat 2s ease-in-out infinite; }
</style>
