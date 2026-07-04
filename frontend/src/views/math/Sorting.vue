<template>
  <div class="page sort-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="sort-area">
      <div class="question">{{ sortData.question }}</div>
      <div class="sort-items">
        <div v-for="(item,i) in sortData.items" :key="i" class="sort-item"
          :class="{
            correct: sortData.feedback==='correct' && sortData.selectedIndex===i,
            wrong: sortData.feedback==='wrong' && sortData.selectedIndex===i,
            selected: i===sortData.selectedForPick
          }"
          :style="{animationDelay: i*0.06+'s'}"
          @click="clickSortItem(i)">
          <span class="emoji">{{ item.e }}</span>
          <span class="label">{{ item.l }}</span>
          <span class="value">{{ item.v }}</span>
        </div>
      </div>
      <div v-if="sortData.phase==='arrange'" class="sort-actions">
        <button v-if="sortData.items.length>1" @click="swapItems">交换</button>
        <button v-if="sortData.items.length>1" @click="checkOrder">检查</button>
      </div>
      <div v-if="sortData.phase==='pick'" class="pick-options">
        <button v-for="(opt,i) in sortData.options" :key="i"
          :class="{correct: sortData.feedback==='correct' && sortData.selectedPick===i, wrong: sortData.feedback==='wrong' && sortData.selectedPick===i}"
          @click="clickPick(i)">{{ opt.label }}</button>
      </div>
      <div class="feedback">{{ sortData.feedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (sortIndex+1)/5*100+'%'}"></div>
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
const dinoText = ref('排一排，谁最大？')
const dinoClicks = ref(0)
function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '排序真好玩！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '排一排，谁最大？' }, 2000); dinoClicks.value = 0 }
}

function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const sortPools = [
  { items: [{ e: '🐭', l: '老鼠', v: 1 }, { e: '🐱', l: '小猫', v: 2 }, { e: '🐶', l: '小狗', v: 3 }, { e: '🐘', l: '大象', v: 4 }], asc: true },
  { items: [{ e: '🍇', l: '葡萄', v: 1 }, { e: '🍎', l: '苹果', v: 2 }, { e: '🍉', l: '西瓜', v: 3 }], asc: true },
  { items: [{ e: '🌷', l: '小花', v: 1 }, { e: '🌳', l: '大树', v: 2 }, { e: '🏔️', l: '大山', v: 3 }], asc: true },
  { items: [{ e: '💧', l: '小水滴', v: 1 }, { e: '🪣', l: '水桶', v: 2 }, { e: '🌊', l: '大海', v: 3 }], asc: true },
  { items: [{ e: '🐟', l: '小鱼', v: 1 }, { e: '🐬', l: '海豚', v: 2 }, { e: '🐋', l: '鲸鱼', v: 3 }], asc: true },
  { items: [{ e: '🪨', l: '小石头', v: 1 }, { e: '⚽', l: '皮球', v: 2 }, { e: '🏀', l: '篮球', v: 3 }, { e: '🏐', l: '排球', v: 4 }], asc: true },
  { items: [{ e: '🐜', l: '蚂蚁', v: 1 }, { e: '🐰', l: '兔子', v: 2 }, { e: '🦊', l: '狐狸', v: 3 }], asc: true },
  { items: [{ e: '1️⃣', l: '一', v: 1 }, { e: '2️⃣', l: '二', v: 2 }, { e: '3️⃣', l: '三', v: 3 }], asc: true },
  { items: [{ e: '1️⃣', l: '一', v: 1 }, { e: '2️⃣', l: '二', v: 2 }, { e: '3️⃣', l: '三', v: 3 }, { e: '4️⃣', l: '四', v: 4 }], asc: true },
  { items: [{ e: '🔢', l: '数字', v: 5 }, { e: '🔢', l: '数字', v: 10 }, { e: '🔢', l: '数字', v: 15 }], asc: true },
  { items: [{ e: '🔢', l: '数字', v: 2 }, { e: '🔢', l: '数字', v: 4 }, { e: '🔢', l: '数字', v: 6 }, { e: '🔢', l: '数字', v: 8 }], asc: true },
  { items: [{ e: '🔢', l: '数字', v: 1 }, { e: '🔢', l: '数字', v: 3 }, { e: '🔢', l: '数字', v: 5 }, { e: '🔢', l: '数字', v: 7 }, { e: '🔢', l: '数字', v: 9 }], asc: true },
]

const sortQuestions = computed(() => {
  const pool = []
  for (let i = 0; i < 50; i++) {
    const s = pick(sortPools)
    const shuffled = shuffle(s.items)
    pool.push({ question: s.asc ? '从最小排到最大' : '从最大排到最小', items: shuffled, answer: [...shuffled].sort((a, b) => s.asc ? a.v - b.v : b.v - a.v) })
  }
  return shuffle(pool).slice(0, 5)
})

const sortIndex = ref(0)
const sortData = reactive({
  question: '', items: [], answer: [], phase: 'arrange',
  selectedForPick: -1, selectedIndex: -1, selectedPick: -1,
  options: [], feedback: '', feedbackText: ''
})

function initSort(idx) {
  const q = sortQuestions.value[idx]
  sortData.question = q.question
  sortData.items = q.items.map(it => ({ ...it }))
  sortData.answer = q.answer.map(it => ({ ...it }))
  sortData.phase = 'arrange'
  sortData.selectedForPick = -1
  sortData.selectedIndex = -1
  sortData.selectedPick = -1
  sortData.feedback = ''
  sortData.feedbackText = ''
  if (!muted.value) setTimeout(() => speakCN(q.question + '，点两个交换位置'), 400)
}

function clickSortItem(i) {
  if (sortData.phase !== 'arrange') return
  if (sortData.selectedForPick === i) { sortData.selectedForPick = -1; return }
  if (sortData.selectedForPick === -1) { sortData.selectedForPick = i; return }
  const arr = sortData.items
  ;[arr[sortData.selectedForPick], arr[i]] = [arr[i], arr[sortData.selectedForPick]]
  sortData.selectedForPick = -1
}

function swapItems() {
  if (sortData.selectedForPick === -1) return
  const other = (sortData.selectedForPick + 1) % sortData.items.length
  const arr = sortData.items
  ;[arr[sortData.selectedForPick], arr[other]] = [arr[other], arr[sortData.selectedForPick]]
  sortData.selectedForPick = -1
}

function checkOrder() {
  if (sortData.selectedForPick !== -1) sortData.selectedForPick = -1
  const correct = sortData.items.every((it, i) => it.v === sortData.answer[i].v)
  if (correct) {
    sortData.feedback = 'correct'
    sortData.feedbackText = '🎉 排对了！'
    dinoMood.value = 'excited'; dinoText.value = '排序真厉害！'
    if (!muted.value) { speakCN('真棒！排对了！') }
    setTimeout(() => {
      if (sortIndex.value < sortQuestions.value.length - 1) {
        sortIndex.value++
        dinoMood.value = 'happy'; dinoText.value = '再来一排'
        initSort(sortIndex.value)
      } else {
        if (!progressStore.completedModules.sorting) {
          progressStore.completeModule('sorting'); userStore.updateStars(3); progressStore.unlockSticker('sorting')
          celebrationStore.show('🔢', '排序小达人！', '你学会了排序！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1000)
  } else {
    sortData.feedback = 'wrong'
    sortData.feedbackText = '😅 再调整一下~'
    if (!muted.value) { speakCN(sortData.items.map(i => i.l).join('、')); speakCN('再调整一下') }
    setTimeout(() => { sortData.feedback = ''; sortData.feedbackText = '' }, 800)
  }
}

initSort(0)
</script>

<style scoped>
.sort-module {
  background: linear-gradient(180deg, #EDF5FF 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn { position: absolute; top: 76px; right: 12px; font-size: 28px; cursor: pointer; user-select: none; z-index: 10; }
.sort-module .sort-area { display: flex; flex-direction: column; align-items: center; gap: 10px; margin-top: 10px; width: 100%; flex: 1; justify-content: center; }
.sort-module .sort-area .question { font-size: 24px; color: #666; font-weight: bold; }
.sort-module .sort-area .sort-items { display: flex; gap: 16px; flex-wrap: wrap; justify-content: center; }
.sort-module .sort-area .sort-items .sort-item {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 10px 16px; border-radius: 20px; background: #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  cursor: pointer; transition: all 0.2s; animation: itemFadeIn 0.3s ease both;
}
.sort-module .sort-area .sort-items .sort-item:active { transform: scale(0.93); }
.sort-module .sort-area .sort-items .sort-item .emoji { font-size: 60px; }
.sort-module .sort-area .sort-items .sort-item .label { font-size: 18px; color: #888; }
.sort-module .sort-area .sort-items .sort-item .value { font-size: 24px; font-weight: bold; color: var(--secondary); }
.sort-module .sort-area .sort-items .sort-item.selected { box-shadow: 0 0 0 4px var(--orange); }
.sort-module .sort-area .sort-items .sort-item.correct { background: rgba(78, 205, 196, 0.3) !important; animation: correctWiggle 0.4s ease; }
.sort-module .sort-area .sort-items .sort-item.wrong { animation: shake 0.3s ease; }
.sort-module .sort-area .sort-actions { display: flex; gap: 20px; margin-top: 4px; }
.sort-module .sort-area .sort-actions button {
  padding: 10px 30px; border-radius: 16px; border: none;
  background: var(--orange); color: #fff; font-size: 18px; font-family: inherit;
  cursor: pointer; transition: all 0.2s;
}
.sort-module .sort-area .sort-actions button:active { transform: scale(0.93); }
.sort-module .sort-area .feedback { font-size: 32px; min-height: 48px; }
.progress-bar { width: min(300px, 70vw); height: 8px; background: #e0e0e0; border-radius: 4px; margin: 4px 0; overflow: hidden; }
.progress-bar .progress-fill { height: 100%; background: #4ECDC4; border-radius: 4px; transition: width 0.4s ease; }
</style>
