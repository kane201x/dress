<template>
  <div class="page compare-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="compare-area">
      <div class="question">{{ compareData.question }}</div>
      <div class="pair">
        <div v-for="(item,i) in compareData.items" :key="i" class="item"
          :class="{correct: compareData.feedback==='correct' && compareData.selected===i, wrong: compareData.feedback==='wrong' && compareData.selected===i}"
          @click="pickCompare(i)">
          <span class="num">{{ item.num }}</span>
          <span class="emoji">{{ item.emoji }}</span>
        </div>
      </div>
      <div class="feedback">{{ compareData.feedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (compareIndex+1)/5*100+'%'}"></div>
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
const dinoText = ref('比一比谁更大')
const dinoClicks = ref(0)
function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '比大小真好玩！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '比一比谁更大' }, 2000); dinoClicks.value = 0 }
}

function randInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min }
function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const numEmojis = ['🍎', '🐱', '⭐', '🌸', '🍭', '🦋', '🌻', '🎈', '🍓', '🐶', '🚗', '🐰', '🐟', '🌈', '🍪']

const compareScenes = computed(() => {
  const d = userStore.difficulty
  const maxNum = d === 1 ? 5 : d === 2 ? 10 : d === 3 ? 20 : d === 4 ? 50 : 100
  const scenes = []
  for (let i = 0; i < 20; i++) {
    let a, b
    do { a = randInt(1, maxNum); b = randInt(1, maxNum) } while (a === b)
    const askBig = Math.random() > 0.5
    const items = shuffle([
      { num: a, emoji: pick(numEmojis) },
      { num: b, emoji: pick(numEmojis) }
    ])
    const answer = items.findIndex(it => askBig ? it.num === Math.max(a, b) : it.num === Math.min(a, b))
    scenes.push({ question: askBig ? '哪个数字更大？' : '哪个数字更小？', items, answer, a, b })
  }
  return shuffle(scenes).slice(0, 5)
})

function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const compareIndex = ref(0)
const compareData = reactive({ question: '', items: [], answer: 0, a: 0, b: 0, selected: -1, feedback: '', feedbackText: '' })

function initCompare(idx) {
  const s = compareScenes.value[idx]
  compareData.question = s.question
  compareData.items = s.items.map(it => ({ ...it }))
  compareData.answer = s.answer
  compareData.a = s.a
  compareData.b = s.b
  compareData.selected = -1
  compareData.feedback = ''
  compareData.feedbackText = ''
  if (!muted.value) setTimeout(() => speakCN(s.question), 300)
}

function pickCompare(i) {
  if (compareData.feedback) return
  compareData.selected = i
  const pickedNum = compareData.items[i].num
  if (i === compareData.answer) {
    compareData.feedback = 'correct'
    compareData.feedbackText = '🎉 答对啦！'
    dinoMood.value = 'excited'; dinoText.value = '比一比真聪明！'
    if (!muted.value) { speakCN(pickedNum + ''); speakCN('对了！'); speakCN('你真棒！') }
    setTimeout(() => {
      if (compareIndex.value < compareScenes.value.length - 1) {
        compareIndex.value++
        dinoMood.value = 'happy'; dinoText.value = '下一对来啦'
        initCompare(compareIndex.value)
      } else {
        if (!progressStore.completedModules.compare) {
          progressStore.completeModule('compare'); userStore.updateStars(3); progressStore.unlockSticker('compare')
          celebrationStore.show('🔢', '比较大王！', '你学会了比大小！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1200)
  } else {
    compareData.feedback = 'wrong'
    compareData.feedbackText = '😅 再想想~'
    if (!muted.value) { speakCN(pickedNum + ''); speakCN('再想想') }
    setTimeout(() => { compareData.feedback = ''; compareData.feedbackText = ''; compareData.selected = -1 }, 800)
  }
}

initCompare(0)
</script>

<style scoped>
.compare-module {
  background: linear-gradient(180deg, #FFF7ED 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn { position: absolute; top: 76px; right: 12px; font-size: 28px; cursor: pointer; user-select: none; z-index: 10; }
.compare-module .compare-area { display: flex; flex-direction: column; align-items: center; gap: 12px; margin-top: 10px; width: 100%; flex: 1; justify-content: center; }
.compare-module .compare-area .question { font-size: 26px; color: #666; font-weight: bold; }
.compare-module .compare-area .pair { display: flex; gap: 30px; align-items: flex-end; justify-content: center; }
.compare-module .compare-area .pair .item {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  cursor: pointer; padding: 16px 28px; border-radius: 24px;
  transition: all 0.2s; background: rgba(255, 255, 255, 0.5);
  min-width: 100px;
}
.compare-module .compare-area .pair .item:active { transform: scale(0.93); }
.compare-module .compare-area .pair .item .num { font-size: 64px; font-weight: bold; color: var(--text); }
.compare-module .compare-area .pair .item .emoji { font-size: 40px; }
.compare-module .compare-area .pair .item.correct { background: rgba(78, 205, 196, 0.3) !important; animation: correctWiggle 0.4s ease; }
.compare-module .compare-area .pair .item.wrong { animation: shake 0.3s ease; }
.compare-module .compare-area .feedback { font-size: 32px; min-height: 48px; }
.progress-bar { width: min(300px, 70vw); height: 8px; background: #e0e0e0; border-radius: 4px; margin: 4px 0; overflow: hidden; }
.progress-bar .progress-fill { height: 100%; background: var(--orange); border-radius: 4px; transition: width 0.4s ease; }
</style>
