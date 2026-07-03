<template>
  <div class="top-bar">
    <button v-if="showBack" class="back" @click="$emit('back')">⬅️</button>
    <button v-else class="back" @click="$router.push('/menu')">🏠</button>
    <div class="stars-display">
      <span class="star-icon">⭐</span>
      <span>{{ userStore.stars }}</span>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../stores/user'

const userStore = useUserStore()

defineProps({
  showBack: { type: Boolean, default: true },
  backRoute: { type: String, default: '' },
  title: { type: String, default: '' }
})

defineEmits(['back'])
</script>

<style scoped>
.top-bar {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 10;
}
.top-bar .back {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  font-size: 28px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}
.top-bar .back:active { transform: scale(0.9); }
.top-bar .stars-display {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px 18px;
  border-radius: 30px;
  font-size: 22px;
  font-weight: bold;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.top-bar .stars-display .star-icon {
  font-size: 28px;
  animation: starFloat 2s ease-in-out infinite;
}
</style>
