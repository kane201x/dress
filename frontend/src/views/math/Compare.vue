<template>
  <div class="page compare-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <div class="compare-area">
      <DinoGuide text="点一点，比一比" />
      <div class="question">{{ compareData.question }}</div>
      <div class="pair">
        <div v-for="(item,i) in compareData.items" :key="i" class="item"
          :class="{correct: compareData.feedback==='correct' && compareData.selected===i, wrong: compareData.feedback==='wrong' && compareData.selected===i}"
          @click="pickCompare(i)">
          <span :class="item.big ? 'emoji-big' : 'emoji-small'">{{ item.emoji }}</span>
          <span class="label">{{ item.label }}</span>
        </div>
      </div>
      <div class="feedback">{{ compareData.feedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ compareIndex+1 }} / {{ compareScenes.length }}</div>
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

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const comparePairs = [
  { big: '🐘', bigL: '大象', small: '🐭', smallL: '老鼠' },
  { big: '🚗', bigL: '汽车', small: '🚲', smallL: '自行车' },
  { big: '🏠', bigL: '房子', small: '⛺', smallL: '帐篷' },
  { big: '🌳', bigL: '大树', small: '🌷', smallL: '小花' },
  { big: '🐋', bigL: '鲸鱼', small: '🐟', smallL: '小鱼' },
  { big: '🦒', bigL: '长颈鹿', small: '🐰', smallL: '兔子' },
  { big: '🌞', bigL: '太阳', small: '🌙', smallL: '月亮' },
  { big: '🏔️', bigL: '大山', small: '🪨', smallL: '石头' },
  { big: '🦅', bigL: '老鹰', small: '🐦', smallL: '小鸟' },
  { big: '🐉', bigL: '恐龙', small: '🦎', smallL: '蜥蜴' },
  { big: '🍉', bigL: '西瓜', small: '🍇', smallL: '葡萄' },
  { big: '🌊', bigL: '大海', small: '💧', smallL: '水滴' },
]

const compareScenes = computed(() => {
  const qty = 50
  const scenes = []
  for (let i = 0; i < qty; i++) {
    const pair = pick(comparePairs)
    const askBig = Math.random() > 0.5
    const items = shuffle([
      { emoji: pair.big, label: pair.bigL, big: true },
      { emoji: pair.small, label: pair.smallL, big: false }
    ])
    const answer = items.findIndex(it => askBig ? it.big : !it.big)
    scenes.push({ question: askBig ? '谁更大？' : '谁更小？', items, answer })
  }
  return scenes
})

const compareIndex = ref(0)
const compareData = reactive({ question: '', items: [], answer: 0, selected: -1, feedback: '', feedbackText: '' })

function initCompare(idx) {
  const s = compareScenes.value[idx]
  compareData.question = s.question
  compareData.items = s.items.map(it => ({ ...it }))
  compareData.answer = s.answer
  compareData.selected = -1
  compareData.feedback = ''
  compareData.feedbackText = ''
}

function pickCompare(i) {
  if (compareData.feedback) return
  compareData.selected = i
  if (i === compareData.answer) {
    compareData.feedback = 'correct'
    compareData.feedbackText = '🎉 答对啦！'
    speakCN('对了！')
    setTimeout(() => {
      if (compareIndex.value < compareScenes.value.length - 1) {
        compareIndex.value++
        initCompare(compareIndex.value)
        nextTick(() => speakCN(compareData.question))
      } else {
        if (!progressStore.completedModules.compare) {
          progressStore.completeModule('compare')
          userStore.updateStars(3)
          progressStore.unlockSticker('compare')
          celebrationStore.show('🐘', '比较大王！', '你学会了比大小！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1000)
  } else {
    compareData.feedback = 'wrong'
    compareData.feedbackText = '😅 再想想~'
    speakCN('再想想')
    setTimeout(() => { compareData.feedback = ''; compareData.feedbackText = ''; compareData.selected = -1 }, 600)
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
.compare-module .compare-area { display: flex; flex-direction: column; align-items: center; gap: 12px; margin-top: 10px; width: 100%; }
.compare-module .compare-area .question { font-size: 24px; color: #666; }
.compare-module .compare-area .pair { display: flex; gap: 30px; align-items: flex-end; justify-content: center; }
.compare-module .compare-area .pair .item {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  cursor: pointer; padding: 12px 20px; border-radius: 24px;
  transition: all 0.2s; background: rgba(255, 255, 255, 0.5);
}
.compare-module .compare-area .pair .item:active { transform: scale(0.93); }
.compare-module .compare-area .pair .item .emoji-big { font-size: 80px; }
.compare-module .compare-area .pair .item .emoji-small { font-size: 50px; }
.compare-module .compare-area .pair .item .label { font-size: 20px; color: #888; }
.compare-module .compare-area .pair .item.correct { background: rgba(78, 205, 196, 0.3) !important; animation: correctWiggle 0.4s ease; }
.compare-module .compare-area .pair .item.wrong { animation: shake 0.3s ease; }
.compare-module .compare-area .feedback { font-size: 32px; min-height: 48px; }
</style>
