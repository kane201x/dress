<template>
  <div class="page color-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="color-area">
      <div class="color-swatch"
        :style="{background: colorData.color, border: colorData.border ? '4px solid #ccc' : '4px solid rgba(255,255,255,0.5)'}"
        @click="speakColor(colorData.english)"></div>
      <div class="color-name" :style="{color: colorData.color}" @click="speakColor(colorData.english)">{{ colorData.english }}</div>
      <div class="color-hint" @click="speakColor(colorData.english)">🔊 找到 {{ colorData.chinese }} <span style="font-size:14px;color:#bbb;">(点我发音)</span></div>
      <div class="color-options">
        <button v-for="(c,i) in currentOptions" :key="i"
          :style="{background: c}"
          :class="{correct: colorFeedback==='correct' && colorPick===i, wrong: colorFeedback==='wrong' && colorPick===i}"
          @click="speakColor(colorNameFromCode(c)); pickColor(i)">
          <span class="color-label">{{ colorNameFromCode(c) }}</span>
        </button>
      </div>
      <div class="feedback">{{ colorFeedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (colorIndex+1)/5*100+'%'}"></div>
      </div>
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

const muted = ref(false)
const dinoMood = ref('happy')
const dinoText = ref('点颜色听发音')
const dinoClicks = ref(0)
function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '颜色真漂亮！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '点颜色听发音' }, 2000); dinoClicks.value = 0 }
}

const allColors = [
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

function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const colorQuestions = computed(() => shuffle(allColors).slice(0, 5))

const colorIndex = ref(0)
const colorData = computed(() => colorQuestions.value[colorIndex.value])
const currentOptions = computed(() => shuffle(allColors.map(c => c.color)))
const colorFeedback = ref('')
const colorPick = ref(-1)
const colorFeedbackText = ref('')

const colorMap = Object.fromEntries(allColors.map(c => [c.color, c.english]))

function colorNameFromCode(code) { return colorMap[code] || '' }
function speakColor(text) { if (text && !muted.value) speakEN(text) }

function resetColor() { colorFeedback.value = ''; colorPick.value = -1; colorFeedbackText.value = '' }

function pickColor(i) {
  if (colorFeedback.value) return
  colorPick.value = i
  if (currentOptions.value[i] === colorData.value.color) {
    colorFeedback.value = 'correct'
    colorFeedbackText.value = '🎉 是 ' + colorData.value.english + '！'
    dinoMood.value = 'excited'; dinoText.value = '答对啦！是' + colorData.value.english
    if (!muted.value) { speakEN(colorNameFromCode(currentOptions.value[i])); speakEN('Good job!') }
    setTimeout(() => {
      if (colorIndex.value < colorQuestions.value.length - 1) {
        colorIndex.value++; resetColor()
        dinoMood.value = 'happy'; dinoText.value = '下一个颜色来啦'
      } else {
        if (!progressStore.completedModules.colors) {
          progressStore.completeModule('colors'); userStore.updateStars(3); progressStore.unlockSticker('colors')
          celebrationStore.show('🎨', '色彩大师！', '你学会了颜色英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1500)
  } else {
    colorFeedback.value = 'wrong'
    colorFeedbackText.value = '😅 再试试~'
    if (!muted.value) { speakEN(colorNameFromCode(currentOptions.value[i])); speakEN('Try again') }
    setTimeout(() => resetColor(), 800)
  }
}
</script>

<style scoped>
.color-module {
  background: linear-gradient(180deg, #FFFBEB 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn { position: absolute; top: 76px; right: 12px; font-size: 28px; cursor: pointer; user-select: none; z-index: 10; }
.color-module .color-area { display: flex; flex-direction: column; align-items: center; gap: 14px; margin-top: 4px; width: 100%; flex: 1; justify-content: center; }
.color-module .color-area .color-swatch { width: 140px; height: 140px; border-radius: 30px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15); transition: all 0.3s; cursor: pointer; }
.color-module .color-area .color-swatch:active { transform: scale(1.05); }
.color-module .color-area .color-name { font-size: 42px; font-weight: bold; transition: all 0.3s; cursor: pointer; }
.color-module .color-area .color-hint { font-size: 18px; color: #888; cursor: pointer; }
.color-module .color-area .color-options { display: flex; gap: 14px; flex-wrap: wrap; justify-content: center; }
.color-module .color-area .color-options button {
  width: 80px; height: 80px; border-radius: 20px; border: 4px solid transparent;
  cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative; overflow: hidden;
}
.color-module .color-area .color-options button .color-label {
  position: absolute; bottom: 4px; left: 50%; transform: translateX(-50%);
  font-size: 11px; font-weight: bold; color: #fff;
  text-shadow: 0 1px 3px rgba(0,0,0,0.6), 0 0 2px rgba(0,0,0,0.4);
  white-space: nowrap; pointer-events: none;
}
.color-module .color-area .color-options button:active { transform: scale(0.9); }
.color-module .color-area .color-options button.correct { border-color: #333; animation: correctWiggle 0.4s ease; }
.color-module .color-area .color-options button.wrong { animation: shake 0.3s ease; }
.color-module .color-area .feedback { font-size: 32px; min-height: 48px; }
.progress-bar { width: min(300px, 70vw); height: 8px; background: #e0e0e0; border-radius: 4px; margin: 4px 0; overflow: hidden; }
.progress-bar .progress-fill { height: 100%; background: var(--orange); border-radius: 4px; transition: width 0.4s ease; }
</style>
