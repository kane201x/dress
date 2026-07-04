<template>
  <div class="page counting-en-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide text="点数字听发音" />
    <div class="counting-display">
      <div class="number-big" @click="speakCurrent">{{ currentNum }}</div>
      <div class="number-english" @click="speakCurrent">{{ currentWord }}</div>
      <div class="progress-text">{{ numIndex + 1 }} / {{ numberList.length }}</div>
      <div class="number-grid">
        <button v-for="(n, i) in numberList" :key="n"
          :class="{active: i === numIndex}"
          class="num-btn"
          @click="goTo(i)">{{ n }}</button>
      </div>
    </div>
    <div class="nav-row">
      <button @click="prev" :disabled="numIndex === 0">⬅️</button>
      <button @click="next" :disabled="numIndex === numberList.length - 1">➡️</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
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

function numToWords(n) {
  if (n === 0) return 'Zero'
  const ones = ['', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine', 'Ten',
    'Eleven', 'Twelve', 'Thirteen', 'Fourteen', 'Fifteen', 'Sixteen', 'Seventeen', 'Eighteen', 'Nineteen']
  const tens = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety']
  if (n < 20) return ones[n]
  if (n < 100) {
    const t = Math.floor(n / 10)
    const o = n % 10
    return tens[t] + (o ? '-' + ones[o].toLowerCase() : '')
  }
  const h = Math.floor(n / 100)
  const r = n % 100
  return ones[h] + ' Hundred' + (r ? ' and ' + numToWords(r).toLowerCase() : '')
}

const numberList = Array.from({ length: 111 }, (_, i) => i)
const numIndex = ref(0)
const currentNum = computed(() => numberList[numIndex.value])
const currentWord = computed(() => numToWords(currentNum.value))

function speakCurrent() {
  speakEN(currentWord.value)
}

function goTo(i) {
  numIndex.value = i
  nextTick(() => speakCurrent())
}

function prev() {
  if (numIndex.value > 0) {
    numIndex.value--
    nextTick(() => speakCurrent())
  }
}

function next() {
  if (numIndex.value < numberList.length - 1) {
    numIndex.value++
    nextTick(() => speakCurrent())
  } else if (!progressStore.completedModules.countingEn) {
    progressStore.completeModule('countingEn')
    userStore.updateStars(3)
    progressStore.unlockSticker('countingEn')
    celebrationStore.show('🔢', '数数小天才！', '你学会了从 0 数到 110！')
  }
}
</script>

<style scoped>
.counting-en-module {
  background: linear-gradient(180deg, #F0F4FF 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.counting-en-module .counting-display { display: flex; flex-direction: column; align-items: center; gap: 6px; margin-top: 10px; width: 100%; padding: 0 10px; }
.counting-en-module .counting-display .number-big { font-size: 100px; font-weight: bold; color: var(--primary); cursor: pointer; line-height: 1; }
.counting-en-module .counting-display .number-big:active { animation: numberPop 0.3s cubic-bezier(.34, 1.56, .64, 1); }
.counting-en-module .counting-display .number-english { font-size: 28px; color: var(--secondary); font-weight: bold; cursor: pointer; min-height: 40px; text-align: center; padding: 0 10px; }
.counting-en-module .counting-display .number-english:active { animation: numberPop 0.3s cubic-bezier(.34, 1.56, .64, 1); }
.counting-en-module .counting-display .progress-text { font-size: 16px; color: #bbb; }
.counting-en-module .counting-display .number-grid { display: flex; flex-wrap: wrap; gap: 4px; justify-content: center; margin-top: 4px; max-height: 200px; overflow-y: auto; width: 100%; max-width: 440px; padding: 6px 0; }
.counting-en-module .counting-display .number-grid .num-btn {
  width: 36px; height: 36px; border-radius: 8px; border: none; font-size: 13px; font-family: inherit;
  background: #fff; box-shadow: 0 2px 6px rgba(0,0,0,0.08); cursor: pointer; transition: all 0.15s;
  color: var(--text);
}
.counting-en-module .counting-display .number-grid .num-btn:active { transform: scale(0.9); }
.counting-en-module .counting-display .number-grid .num-btn.active { background: var(--secondary); color: #fff; transform: scale(1.1); }
.counting-en-module .nav-row { display: flex; gap: 30px; margin-top: 10px; }
.counting-en-module .nav-row button {
  width: 64px; height: 64px; border-radius: 50%; border: none; font-size: 32px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer; transition: all 0.2s;
}
.counting-en-module .nav-row button:active { transform: scale(0.9); }
.counting-en-module .nav-row button:disabled { opacity: 0.3; }
</style>
