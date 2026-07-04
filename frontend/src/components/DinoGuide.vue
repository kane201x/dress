<template>
  <div class="dino-guide" :class="[mood]" @click="$emit('tap')">
    <div class="dino-char">
      <span class="dino-emoji">{{ dinoEmoji }}</span>
      <div class="dino-eyes">{{ dinoEyes }}</div>
      <div class="dino-sparkle" v-if="mood==='excited'">✨</div>
    </div>
    <div class="dino-bubble" v-if="text">
      <span class="bubble-arrow"></span>
      {{ text }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: { type: String, default: '' },
  mood: { type: String, default: 'happy' }
})

defineEmits(['tap'])

const dinoEmoji = computed(() => {
  const moods = { happy: '🦕', excited: '🦖', thinking: '🦕', sad: '🦕', love: '🦕' }
  return moods[props.mood] || '🦕'
})

const dinoEyes = computed(() => {
  const eyes = { happy: '◕‿◕', excited: '☆▽☆', thinking: '◔_◔', sad: '◕_◕', love: '♥‿♥' }
  return eyes[props.mood] || '◕‿◕'
})
</script>

<style scoped>
.dino-guide {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  margin: 4px 0 2px;
  animation: dinoFloat 2s ease-in-out infinite;
  cursor: pointer;
  user-select: none;
  position: relative;
}
.dino-guide .dino-char {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.dino-guide .dino-emoji { font-size: 36px; line-height: 1; }
.dino-guide .dino-eyes {
  font-size: 10px;
  margin-top: -6px;
  color: #555;
  background: rgba(255,255,255,0.7);
  border-radius: 8px;
  padding: 1px 4px;
  white-space: nowrap;
}
.dino-guide .dino-sparkle {
  position: absolute;
  top: -8px;
  right: -10px;
  font-size: 16px;
  animation: dinoSpin 1s ease-in-out infinite;
}
.dino-guide .dino-bubble {
  background: #fff;
  border-radius: 12px 12px 12px 4px;
  padding: 6px 12px;
  font-size: 14px;
  color: #666;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  max-width: 220px;
  line-height: 1.4;
  position: relative;
  margin-bottom: 4px;
  animation: popIn 0.3s cubic-bezier(.34, 1.56, .64, 1);
}
.dino-guide .dino-bubble .bubble-arrow {
  position: absolute;
  left: -6px;
  bottom: 8px;
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-right: 6px solid #fff;
}
.dino-guide.excited { animation: dinoBounce 0.6s ease-in-out infinite; }
.dino-guide.thinking { animation: dinoFloat 1.5s ease-in-out infinite; }
.dino-guide:active { transform: scale(1.1); }

@keyframes dinoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
@keyframes dinoBounce {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-8px) rotate(3deg); }
}
</style>
