<template>
  <div class="page add-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <div class="add-area">
      <DinoGuide text="数一数总共有几个？" />
      <div class="equation">
        <div class="group">
          <span v-for="e in addData.leftItems" :key="'l'+e[0]" style="animation:itemFadeIn 0.3s ease both">{{ e[1] }}</span>
        </div>
        <span class="op">+</span>
        <div class="group">
          <span v-for="e in addData.rightItems" :key="'r'+e[0]" style="animation:itemFadeIn 0.3s ease both">{{ e[1] }}</span>
        </div>
        <span class="eq">=</span>
        <span class="op" style="font-size:50px">?</span>
      </div>
      <div class="add-options">
        <button v-for="(opt,i) in addData.options" :key="i"
          :class="{correct: addData.feedback==='correct' && addData.selected===i, wrong: addData.feedback==='wrong' && addData.selected===i}"
          @click="pickAdd(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ addData.feedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:8px;">{{ addIndex+1 }} / {{ addQuestions.length }}</div>
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

function randInt(min, max) { return Math.floor(Math.random() * (max - min + 1)) + min }
function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}
function pick(arr) { return arr[Math.floor(Math.random() * arr.length)] }

const addEmojis = ['🍎', '🐱', '⭐', '🌸', '🍭', '🦋', '🌻', '🎈', '🍓', '🐶', '🚗', '🐰']

const addQuestions = computed(() => {
  const d = userStore.difficulty
  const qty = 50
  const maxL = d === 1 ? 3 : d === 2 ? 6 : d === 3 ? 10 : 15
  const maxR = d === 1 ? 3 : d === 2 ? 6 : d === 3 ? 10 : 15
  const result = []
  for (let i = 0; i < qty; i++) {
    const left = randInt(d === 1 ? 1 : 1, maxL)
    const right = randInt(d === 1 ? 1 : 1, maxR)
    const ans = left + right
    const opts = new Set([ans])
    opts.add(ans + randInt(1, d === 1 ? 2 : 3))
    opts.add(Math.max(1, ans - randInt(1, d === 1 ? 2 : 3)))
    if (d >= 3) opts.add(ans + randInt(1, 5))
    const optArr = shuffle([...opts]).slice(0, d === 1 ? 3 : d === 2 ? 3 : 4)
    if (!optArr.includes(ans)) optArr[randInt(0, optArr.length - 1)] = ans
    result.push({ left, right, emoji: pick(addEmojis), answer: ans, options: shuffle(optArr) })
  }
  return result
})

const addIndex = ref(0)
const addData = reactive({ leftItems: [], rightItems: [], options: [], answer: 0, selected: -1, feedback: '', feedbackText: '' })

function initAdd(idx) {
  const q = addQuestions.value[idx]
  addData.leftItems = Array.from({ length: q.left }, (_, i) => [i, q.emoji])
  addData.rightItems = Array.from({ length: q.right }, (_, i) => [i, q.emoji])
  addData.options = [...q.options]
  addData.answer = q.answer
  addData.selected = -1
  addData.feedback = ''
  addData.feedbackText = ''
}

function pickAdd(i) {
  if (addData.feedback) return
  addData.selected = i
  if (addData.options[i] === addData.answer) {
    addData.feedback = 'correct'
    addData.feedbackText = '🎉 答对啦！' + addData.answer
    speakCN('等于' + addData.answer)
    setTimeout(() => {
      if (addIndex.value < addQuestions.value.length - 1) {
        addIndex.value++
        initAdd(addIndex.value)
      } else {
        if (!progressStore.completedModules.addition) {
          progressStore.completeModule('addition')
          userStore.updateStars(3)
          progressStore.unlockSticker('addition')
          celebrationStore.show('➕', '加法小天才！', '你学会了加法！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1000)
  } else {
    addData.feedback = 'wrong'
    addData.feedbackText = '😅 再数数~'
    speakCN('再数数')
    setTimeout(() => { addData.feedback = ''; addData.feedbackText = ''; addData.selected = -1 }, 600)
  }
}

initAdd(0)
</script>

<style scoped>
.add-module {
  background: linear-gradient(180deg, #FFF0F6 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.add-module .add-area { display: flex; flex-direction: column; align-items: center; gap: 10px; margin-top: 10px; width: 100%; }
.add-module .add-area .equation {
  display: flex; align-items: center; gap: 10px;
  font-size: 50px; font-weight: bold; color: var(--text); flex-wrap: wrap; justify-content: center;
}
.add-module .add-area .equation .group { display: flex; flex-wrap: wrap; max-width: 400px; justify-content: center; }
.add-module .add-area .equation .group span { width: 10%; flex: 0 0 10%; text-align: center; line-height: 1.5; }
.add-module .add-area .equation .op { color: var(--primary); }
.add-module .add-area .equation .eq { color: var(--secondary); }
.add-module .add-area .add-options { display: flex; gap: 20px; margin-top: 8px; }
.add-module .add-area .add-options button {
  width: 90px; height: 90px; border-radius: 24px; border: 4px solid transparent;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  font-size: 40px; font-weight: bold; font-family: inherit;
  cursor: pointer; transition: all 0.2s; color: var(--text);
}
.add-module .add-area .add-options button:active { transform: scale(0.9); }
.add-module .add-area .add-options button.correct { border-color: var(--secondary); background: #E8F8F5; animation: correctWiggle 0.4s ease; }
.add-module .add-area .add-options button.wrong { border-color: var(--primary); background: #FFF0F0; animation: shake 0.3s ease; }
.add-module .add-area .feedback { font-size: 32px; min-height: 48px; }
</style>
