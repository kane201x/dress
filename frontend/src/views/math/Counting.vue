<template>
  <div class="page counting-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">
      {{ muted ? '🔇' : '🔊' }}
    </div>
    <div class="scene" v-if="!showResult">
      <div class="question">{{ countData.question }}</div>
      <div class="objects" :class="'cols-' + gridCols">
        <div v-for="(obj,i) in countData.objects" :key="i"
          class="obj" :class="{counted: countData.counted[i]}"
          @click="countObj(i)"
          :style="objStyle(countData.total)">
          {{ obj }}
        </div>
      </div>
      <div class="counter">
        <span class="num" :class="{'pop': countPop}">{{ countData.current }}</span>
        <span v-if="countData.done"> / {{ countData.total }} ✅</span>
        <span v-else> / {{ countData.total }}</span>
      </div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (countIndex+1)/5*100+'%'}"></div>
      </div>
    </div>
    <div class="scene" v-else>
      <div class="result-title">🎉 答案选一选！</div>
      <div class="result-emoji">🐱</div>
      <div class="result-question">刚才数了几个 {{ currentEmoji }}？</div>
      <div class="answer-options">
        <button v-for="(n,i) in answerOptions" :key="i"
          :class="{
            correct: answered && n === currentTotal,
            wrong: answered && n !== currentTotal && selectedAnswer === n
          }"
          :disabled="answered"
          @click="pickAnswer(n)">{{ n }}</button>
      </div>
      <div class="feedback" v-if="answered">
        <span v-if="answeredCorrect">✅ 答对了！一共 {{ currentTotal }} 个！</span>
        <span v-else>❌ 再数数，一共 {{ currentTotal }} 个哦~</span>
      </div>
      <div class="actions">
        <button v-if="answered && countIndex < 4" class="big-btn secondary" @click="nextCount">➡️ 下一题</button>
        <button v-if="answered && countIndex === 4" class="big-btn yellow" @click="completeCounting">⭐ 领奖励</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue'
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
const dinoText = ref('点点小图标数一数')
const dinoClicks = ref(0)

function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) {
    dinoMood.value = 'excited'
    dinoText.value = '加油！你好棒！'
    setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '点点小图标数一数' }, 2000)
    dinoClicks.value = 0
  }
}

const countEmojis = ['🐱', '🍎', '🌻', '🐦', '🦋', '🐶', '🌸', '⭐', '🐰', '🍭', '🚗', '🎈', '🐟', '🍓', '🌺']
const countIndex = ref(0)
const countPop = ref(false)
const countData = reactive({ question: '', objects: [], total: 0, current: 0, counted: [], done: false })
const showResult = ref(false)
const answered = ref(false)
const answeredCorrect = ref(false)
const selectedAnswer = ref(-1)
const currentTotal = ref(0)
const currentEmoji = ref('')
const answerOptions = ref([])

const gridCols = computed(() => countData.total >= 10 ? 10 : countData.total)

function objStyle(total) {
  if (total > 120) return { fontSize: '18px', padding: '1px' }
  if (total > 80) return { fontSize: '20px', padding: '1px 2px' }
  if (total > 50) return { fontSize: '24px', padding: '2px' }
  if (total > 30) return { fontSize: '28px', padding: '2px' }
  return { fontSize: '36px', padding: '2px 4px' }
}

function randInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min }
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }
function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const countScenes = computed(() => {
  const d = userStore.difficulty
  let minCount, maxCount
  if (d === 1) { minCount = 2; maxCount = 5 }
  else if (d === 2) { minCount = 3; maxCount = 10 }
  else if (d === 3) { minCount = 50; maxCount = 80 }
  else if (d === 4) { minCount = 60; maxCount = 100 }
  else { minCount = 80; maxCount = 150 }
  const pool = []
  for (let i = 0; i < 50; i++) {
    const total = randInt(minCount, maxCount)
    const e = pick(countEmojis)
    pool.push({
      question: e + ' 有几个？',
      objects: Array.from({ length: total }, () => e),
      total,
      emoji: e
    })
  }
  return shuffle(pool).slice(0, 5)
})

function genAnswerOptions(correct) {
  const opts = new Set([correct])
  const d = userStore.difficulty
  const range = d <= 2 ? 3 : d <= 3 ? 5 : 10
  while (opts.size < 4) {
    const offset = randInt(-range, range)
    const n = Math.max(1, correct + offset)
    opts.add(n)
  }
  return shuffle([...opts])
}

function initCount(idx) {
  const s = countScenes.value[idx]
  countData.question = s.question
  countData.objects = [...s.objects]
  countData.total = s.total
  countData.current = 0
  countData.counted = Array(s.objects.length).fill(false)
  countData.done = false
  showResult.value = false
  answered.value = false
  answeredCorrect.value = false
  currentTotal.value = s.total
  currentEmoji.value = s.emoji
  if (!muted.value) speakCN('数一数，有几个？')
}

function countObj(i) {
  if (countData.done || countData.counted[i]) return
  countData.counted[i] = true
  countData.current++
  countPop.value = true
  setTimeout(() => countPop.value = false, 300)
  if (!muted.value) speakCN(String(countData.current))
  if (countData.current === countData.total) {
    countData.done = true
    dinoMood.value = 'excited'
    if (!muted.value) setTimeout(() => speakCN('太棒了！数完了！'), 300)
    setTimeout(() => {
      answerOptions.value = genAnswerOptions(countData.total)
      showResult.value = true
      dinoText.value = '选一选正确答案吧'
      if (!muted.value) speakCN('选一选，一共有几个？')
    }, 600)
  }
}

function pickAnswer(n) {
  if (answered.value) return
  answered.value = true
  selectedAnswer.value = n
  answeredCorrect.value = n === currentTotal.value
  if (answeredCorrect.value) {
    dinoMood.value = 'excited'
    dinoText.value = '答对啦！真聪明！'
    if (!muted.value) { speakCN('答对了！一共' + currentTotal.value + '个！'); speakCN('你真棒！') }
    setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '继续加油！' }, 1500)
  } else {
    dinoMood.value = 'thinking'
    dinoText.value = '再数一次看看~'
    if (!muted.value) { speakCN(n + ''); speakCN('再数一次看看') }
    setTimeout(() => { dinoMood.value = 'happy' }, 2000)
  }
}

function nextCount() {
  countIndex.value++
  dinoText.value = '下一个来啦！'
  initCount(countIndex.value)
}

async function completeCounting() {
  if (!progressStore.completedModules.counting) {
    await progressStore.completeModule('counting')
    await userStore.updateStars(3)
    await progressStore.unlockSticker('counting')
    celebrationStore.show('🐱', '数数高手！', '你会数数啦！')
  } else { celebrationStore.show('⭐', '已经完成啦！', '') }
}

initCount(0)
</script>

<style scoped>
.counting-module {
  background: linear-gradient(180deg, #F0FFF4 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn {
  position: absolute; top: 76px; right: 12px;
  font-size: 28px; cursor: pointer; user-select: none;
  z-index: 10;
}
.counting-module .scene { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-top: 6px; width: 100%; padding: 0 1px; flex: 1; justify-content: center; }
.counting-module .scene .question { font-size: 26px; color: #666; font-weight: bold; }
.counting-module .scene .objects {
  display: grid; gap: 1px; justify-items: center; align-items: center;
  padding: 4px; min-height: 60px; max-width: 100%; width: 100%; box-sizing: border-box;
}
.counting-module .scene .objects.cols-10 { grid-template-columns: repeat(10, 1fr); }
.counting-module .scene .objects .obj {
  cursor: pointer; transition: all 0.2s; user-select: none;
  border-radius: 8px; background: rgba(255, 255, 255, 0.6);
  width: 100%; min-width: 0; text-align: center; line-height: 1.2;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.counting-module .scene .objects .obj:active { transform: scale(1.4); background: rgba(255,255,200,0.8); }
.counting-module .scene .objects .obj.counted { opacity: 0.2; transform: scale(0.7); }
.counting-module .scene .counter { font-size: 44px; font-weight: bold; color: var(--secondary); margin-top: 6px; }
.counting-module .scene .counter .num.pop { animation: numberPop 0.3s ease; }
.progress-bar {
  width: min(300px, 70vw); height: 8px; background: #e0e0e0;
  border-radius: 4px; margin: 6px 0; overflow: hidden;
}
.progress-bar .progress-fill { height: 100%; background: var(--secondary); border-radius: 4px; transition: width 0.4s ease; }
.result-title { font-size: 28px; font-weight: bold; color: var(--primary); }
.result-emoji { font-size: 80px; margin: 6px 0; }
.result-question { font-size: 22px; color: #666; }
.answer-options { display: flex; gap: 14px; flex-wrap: wrap; justify-content: center; margin: 10px 0; }
.answer-options button {
  width: 72px; height: 72px; border-radius: 20px; border: 4px solid #ddd;
  background: #fff; font-size: 32px; font-weight: bold; font-family: inherit;
  cursor: pointer; transition: all 0.2s; box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.answer-options button:active { transform: scale(0.9); }
.answer-options button.correct { border-color: var(--secondary); background: #E8F8F5; animation: correctWiggle 0.4s ease; }
.answer-options button.wrong { border-color: var(--primary); background: #FFF0F0; }
.answer-options button:disabled { opacity: 0.6; cursor: default; }
.counting-module .scene .feedback { font-size: 24px; min-height: 36px; font-weight: bold; }
.counting-module .scene .actions { display: flex; gap: 10px; margin-top: 8px; align-items: center; }
</style>
