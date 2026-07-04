<template>
  <div class="page shape-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="shape-area">
      <div class="target" :style="{color: shapeData.color}" @click="speakShapeName">{{ shapeData.targetEmoji }}</div>
      <div class="hint">找到一样的形状吧！</div>
      <div class="options">
        <button v-for="(opt,i) in shapeData.options" :key="i"
          :class="{correct: shapeData.feedback==='correct' && shapeData.selected===i, wrong: shapeData.feedback==='wrong' && shapeData.selected===i}"
          @click="pickShape(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ shapeData.feedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (shapeIndex+1)/5*100+'%'}"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useProgressStore } from '../../stores/progress'
import { useCelebrationStore } from '../../stores/celebration'
import { speakCN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import DinoGuide from '../../components/DinoGuide.vue'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const muted = ref(false)
const dinoMood = ref('happy')
const dinoText = ref('找出一样的形状')
const dinoClicks = ref(0)
function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) {
    dinoMood.value = 'excited'; dinoText.value = '你真聪明！'
    setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '找出一样的形状' }, 2000); dinoClicks.value = 0
  }
}

function speakShapeName() { if (!muted.value) speakCN(shapeData.name) }
function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const shapePool = [
  { emoji: '🔴', name: '圆形', color: '#FF6B6B' },
  { emoji: '🟦', name: '方形', color: '#4ECDC4' },
  { emoji: '🔺', name: '三角形', color: '#A78BFA' },
  { emoji: '⭐', name: '星形', color: '#FFE66D' },
  { emoji: '❤️', name: '心形', color: '#F472B6' },
  { emoji: '⬟', name: '五边形', color: '#FB923C' },
  { emoji: '🔷', name: '菱形', color: '#95E1D3' },
  { emoji: '🥚', name: '椭圆', color: '#FFD93D' },
  { emoji: '⬡', name: '六边形', color: '#FFA502' },
  { emoji: '🌙', name: '月牙', color: '#F8BBD0' },
]

const shapeList = computed(() => {
  const d = userStore.difficulty
  const pool = d === 1 ? shapePool.slice(0, 5) : d === 2 ? shapePool.slice(0, 7) : shapePool
  const result = []
  for (let i = 0; i < 20; i++) {
    const target = pick(pool)
    const others = pool.filter(s => s.emoji !== target.emoji)
    const needed = d === 1 ? 2 : 3
    const shuffledOthers = shuffle(others)
    const wrong = []
    for (let j = 0; j < needed && j < shuffledOthers.length; j++) wrong.push(shuffledOthers[j].emoji)
    result.push({ targetEmoji: target.emoji, name: target.name, options: shuffle([target.emoji, ...wrong]), color: target.color })
  }
  return shuffle(result).slice(0, 5)
})

const shapeIndex = ref(0)
const shapeData = reactive({ targetEmoji: '', name: '', color: '', options: [], selected: -1, feedback: '', feedbackText: '' })

function initShape(idx) {
  const s = shapeList.value[idx]
  shapeData.targetEmoji = s.targetEmoji
  shapeData.name = s.name
  shapeData.color = s.color
  shapeData.options = [...s.options]
  shapeData.selected = -1
  shapeData.feedback = ''
  shapeData.feedbackText = ''
  if (!muted.value) setTimeout(() => speakCN('找出' + s.name), 300)
}

function pickShape(i) {
  if (shapeData.feedback) return
  shapeData.selected = i
  const target = shapeList.value[shapeIndex.value]
  if (shapeData.options[i] === target.targetEmoji) {
    shapeData.feedback = 'correct'
    shapeData.feedbackText = '🎉 答对啦！'
    dinoMood.value = 'excited'; dinoText.value = '太棒了！这是' + target.name
    if (!muted.value) { speakCN('对了！这是' + target.name); speakCN('你真棒！') }
    setTimeout(() => {
      if (shapeIndex.value < shapeList.value.length - 1) {
        shapeIndex.value++
        dinoMood.value = 'happy'; dinoText.value = '下一个形状来啦'
        initShape(shapeIndex.value)
      } else {
        if (!progressStore.completedModules.shapes) {
          progressStore.completeModule('shapes'); userStore.updateStars(3); progressStore.unlockSticker('shapes')
          celebrationStore.show('🔵', '形状大师！', '你认识了所有形状！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 3000)
  } else {
    shapeData.feedback = 'wrong'
    shapeData.feedbackText = '😅 再试试~'
    if (!muted.value) speakCN('再试试')
    setTimeout(() => { shapeData.feedback = ''; shapeData.feedbackText = ''; shapeData.selected = -1 }, 600)
  }
}

initShape(0)
</script>

<style scoped>
.shape-module {
  background: linear-gradient(180deg, #F3E8FF 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn { position: absolute; top: 76px; right: 12px; font-size: 28px; cursor: pointer; user-select: none; z-index: 10; }
.shape-module .shape-area { display: flex; flex-direction: column; align-items: center; gap: 14px; margin-top: 10px; width: 100%; flex: 1; justify-content: center; }
.shape-module .shape-area .target { font-size: 100px; animation: dinoBounce 2s ease-in-out infinite; cursor: pointer; }
.shape-module .shape-area .hint { font-size: 20px; color: #888; }
.shape-module .shape-area .options { display: flex; gap: 20px; margin-top: 4px; }
.shape-module .shape-area .options button {
  font-size: 70px; width: 100px; height: 100px; border-radius: 24px;
  border: 4px solid transparent; background: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); cursor: pointer;
  transition: all 0.2s; display: flex; align-items: center; justify-content: center;
}
.shape-module .shape-area .options button:active { transform: scale(0.9); }
.shape-module .shape-area .options button.correct { border-color: var(--secondary); background: #E8F8F5; animation: correctWiggle 0.4s ease; }
.shape-module .shape-area .options button.wrong { border-color: var(--primary); background: #FFF0F0; animation: shake 0.3s ease; }
.shape-module .shape-area .feedback { font-size: 32px; min-height: 48px; }
.progress-bar { width: min(300px, 70vw); height: 8px; background: #e0e0e0; border-radius: 4px; margin: 4px 0; overflow: hidden; }
.progress-bar .progress-fill { height: 100%; background: var(--purple); border-radius: 4px; transition: width 0.4s ease; }
</style>
