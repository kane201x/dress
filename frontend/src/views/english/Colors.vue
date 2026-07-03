<template>
  <div class="page color-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <div class="color-area">
      <DinoGuide text="点颜色听发音" />
      <div class="color-swatch"
        :style="{background: colorData.color, border: colorData.border ? '4px solid #ccc' : '4px solid rgba(255,255,255,0.5)'}"
        @click="speakEN(colorData.english)"></div>
      <div class="color-name" :style="{color: colorData.color}">{{ colorData.english }}</div>
      <div class="color-hint" @click="speakEN(colorData.english)">🔊 找到 {{ colorData.chinese }} <span style="font-size:14px;color:#bbb;">(点我发音)</span></div>
      <div class="color-options">
        <button v-for="(c,i) in colorOptions" :key="i"
          :style="{background: c}"
          :class="{correct: colorFeedback==='correct' && colorPick===i, wrong: colorFeedback==='wrong' && colorPick===i}"
          @click="pickColor(i)"></button>
      </div>
      <div class="feedback">{{ colorFeedbackText }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useProgressStore } from '../../stores/progress'
import { useCelebrationStore } from '../../stores/celebration'
import { speakEN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import DinoGuide from '../../components/DinoGuide.vue'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const colorQuestions = [
  { color: '#FF4757', english: 'Red', chinese: '红色' },
  { color: '#2ED573', english: 'Green', chinese: '绿色' },
  { color: '#1E90FF', english: 'Blue', chinese: '蓝色' },
  { color: '#FFA502', english: 'Orange', chinese: '橙色' },
  { color: '#FFD93D', english: 'Yellow', chinese: '黄色' },
  { color: '#A78BFA', english: 'Purple', chinese: '紫色' },
  { color: '#FF6B9D', english: 'Pink', chinese: '粉色' },
  { color: '#2C3E50', english: 'Black', chinese: '黑色' },
  { color: '#FFFFFF', english: 'White', chinese: '白色' },
  { color: '#8B4513', english: 'Brown', chinese: '棕色' },
  { color: '#95A5A6', english: 'Gray', chinese: '灰色' },
  { color: '#FFD700', english: 'Gold', chinese: '金色' },
]

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}

const colorIndex = ref(0)
const colorData = computed(() => colorQuestions[colorIndex.value])
const colorOptions = computed(() => shuffle(colorQuestions.map(c => c.color)))
const colorFeedback = ref('')
const colorPick = ref(-1)
const colorFeedbackText = ref('')

function resetColor() { colorFeedback.value = ''; colorPick.value = -1; colorFeedbackText.value = '' }

function pickColor(i) {
  if (colorFeedback.value) return
  colorPick.value = i
  if (colorOptions.value[i] === colorData.value.color) {
    colorFeedback.value = 'correct'
    colorFeedbackText.value = '🎉 是 ' + colorData.value.english + '！'
    speakEN(colorData.value.english)
    setTimeout(() => {
      if (colorIndex.value < colorQuestions.length - 1) {
        colorIndex.value++
        resetColor()
      } else {
        if (!progressStore.completedModules.colors) {
          progressStore.completeModule('colors')
          userStore.updateStars(3)
          progressStore.unlockSticker('colors')
          celebrationStore.show('🎨', '色彩大师！', '你学会了颜色英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1200)
  } else {
    colorFeedback.value = 'wrong'
    colorFeedbackText.value = '😅 再试试~'
    setTimeout(() => resetColor(), 600)
  }
}
</script>

<style scoped>
.color-module {
  background: linear-gradient(180deg, #FFFBEB 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.color-module .color-area { display: flex; flex-direction: column; align-items: center; gap: 14px; margin-top: 10px; width: 100%; }
.color-module .color-area .color-swatch { width: 140px; height: 140px; border-radius: 30px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15); transition: all 0.3s; cursor: pointer; }
.color-module .color-area .color-swatch:active { transform: scale(1.05); }
.color-module .color-area .color-name { font-size: 42px; font-weight: bold; transition: all 0.3s; }
.color-module .color-area .color-hint { font-size: 18px; color: #888; cursor: pointer; }
.color-module .color-area .color-options { display: flex; gap: 14px; flex-wrap: wrap; justify-content: center; }
.color-module .color-area .color-options button {
  width: 80px; height: 80px; border-radius: 20px; border: 4px solid transparent;
  cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.color-module .color-area .color-options button:active { transform: scale(0.9); }
.color-module .color-area .color-options button.correct { border-color: #333; animation: correctWiggle 0.4s ease; }
.color-module .color-area .color-options button.wrong { animation: shake 0.3s ease; }
.color-module .color-area .feedback { font-size: 32px; min-height: 48px; }
</style>
