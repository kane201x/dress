<template>
  <div class="page sort-module">
    <TopBar back-route="/math" @back="router.push('/math')" />
    <div class="sort-area">
      <DinoGuide text="从最小到最大按顺序排" />
      <div class="hint">从最小的开始，按顺序点击数字！</div>
      <div class="targets">
        <div v-for="(slot, i) in sortSlots" :key="i" class="slot"
          :class="{filled: slot !== null, highlight: sortNextIdx === i}"
          @click="slotClick(i)">
          {{ slot !== null ? slot : (sortNextIdx === i ? '⬇️' : '?') }}
        </div>
      </div>
      <div class="sort-box">
        <div v-for="(n, i) in sortNumbers" :key="i" class="sort-num"
          :class="{placed: sortPlaced[i]}"
          @click="placeNumber(i)">
          {{ n }}
        </div>
      </div>
      <div class="result">{{ sortResult }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ sortRound+1 }} / {{ sortTotalRounds }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
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

const sortRound = ref(0)
const sortTotalRounds = 50
const sortNumbers = ref([])
const sortSorted = ref([])
const sortSlots = ref([])
const sortPlaced = ref([])
const sortNextIdx = ref(0)
const sortResult = ref('')

function initSort() {
  const d = userStore.difficulty
  const patterns = [
    () => { const t = 5; const s = randInt(1, 8); return Array.from({ length: t }, (_, i) => s + i) },
    () => { const t = randInt(5, 6); const s = randInt(1, 12); return Array.from({ length: t }, (_, i) => s + i) },
    () => { const t = 5; const st = randInt(1, 3); const s = randInt(1, 8); return Array.from({ length: t }, (_, i) => s + i * st) },
    () => { const t = randInt(4, 6); const st = randInt(1, 5); const s = randInt(1, 15); return Array.from({ length: t }, (_, i) => s + i * st) },
  ]
  const idx = Math.min(d - 1, patterns.length - 1)
  const sorted = patterns[idx]()
  const shuffled = [...sorted]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]]
  }
  sortSorted.value = sorted
  sortNumbers.value = shuffled
  sortSlots.value = Array(sorted.length).fill(null)
  sortPlaced.value = Array(sorted.length).fill(false)
  sortNextIdx.value = 0
  sortResult.value = ''
}

function placeNumber(i) {
  if (sortPlaced.value[i]) return
  const num = sortNumbers.value[i]
  const expected = sortSorted.value[sortNextIdx.value]
  if (num === expected) {
    sortPlaced.value[i] = true
    sortSlots.value[sortNextIdx.value] = num
    speakCN(String(num))
    sortNextIdx.value++
    if (sortNextIdx.value >= sortSorted.value.length) {
      sortResult.value = '🎉 排好啦！'
      setTimeout(() => {
        if (sortRound.value < sortTotalRounds - 1) {
          sortRound.value++
          initSort()
          sortResult.value = ''
          nextTick(() => speakCN('继续排序吧'))
        } else if (!progressStore.completedModules.sorting) {
          progressStore.completeModule('sorting')
          userStore.updateStars(3)
          progressStore.unlockSticker('sorting')
          setTimeout(() => celebrationStore.show('📊', '排序小能手！', '你会按顺序排数字啦！'), 600)
        }
      }, 800)
    }
  } else {
    const nextNum = sortSorted.value[sortNextIdx.value]
    sortResult.value = '😅 再想想，下一个应该是 ' + nextNum
    setTimeout(() => { sortResult.value = '' }, 1200)
  }
}

function slotClick(i) {
  if (sortSlots.value[i] !== null) {
    const num = sortSlots.value[i]
    sortSlots.value[i] = null
    const idx = sortNumbers.value.indexOf(num)
    if (idx !== -1) { sortPlaced.value[idx] = false }
    sortNextIdx.value = i
    sortResult.value = ''
  }
}

initSort()
</script>

<style scoped>
.sort-module {
  background: linear-gradient(180deg, #FFFBEB 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.sort-module .sort-area { display: flex; flex-direction: column; align-items: center; gap: 14px; margin-top: 10px; width: 100%; }
.sort-module .sort-area .hint { font-size: 22px; color: #888; text-align: center; }
.sort-module .sort-area .sort-box { display: flex; gap: 12px; flex-wrap: wrap; justify-content: center; }
.sort-module .sort-area .sort-box .sort-num {
  width: 72px; height: 72px; border-radius: 20px; border: 4px solid #eee;
  background: #fff; font-size: 40px; font-weight: bold;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s; color: var(--text);
}
.sort-module .sort-area .sort-box .sort-num:active { transform: scale(0.9); }
.sort-module .sort-area .sort-box .sort-num.placed {
  border-color: var(--secondary); background: #E8F8F5;
  opacity: 0.5; cursor: default; transform: scale(0.9);
}
.sort-module .sort-area .targets { display: flex; gap: 12px; flex-wrap: wrap; justify-content: center; min-height: 72px; }
.sort-module .sort-area .targets .slot {
  width: 72px; height: 72px; border-radius: 20px; border: 4px dashed #ddd;
  display: flex; align-items: center; justify-content: center;
  font-size: 40px; font-weight: bold; color: var(--secondary);
  transition: all 0.3s; background: rgba(255, 255, 255, 0.5);
}
.sort-module .sort-area .targets .slot.filled {
  border-style: solid; border-color: var(--secondary);
  background: #E8F8F5; animation: numberPop 0.3s ease;
}
.sort-module .sort-area .targets .slot.highlight { border-color: var(--primary); animation: pulse 1s ease-in-out infinite; }
.sort-module .sort-area .result { font-size: 28px; font-weight: bold; min-height: 40px; }
</style>
