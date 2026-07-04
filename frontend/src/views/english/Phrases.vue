<template>
  <div class="page phrase-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide text="点句子听发音" />
    <div class="phrase-display">
      <div class="phrase-emoji" @click="speakEN(phraseData.english)">{{ phraseData.emoji }}</div>
      <div class="phrase-english" @click="speakEN(phraseData.english)">{{ phraseData.english }}</div>
      <div class="phrase-chinese">{{ phraseData.chinese }}</div>
      <div class="phrase-situation">💡 {{ phraseData.situation }}</div>
      <div class="progress-text">{{ phraseIndex + 1 }} / {{ phraseQuestions.length }}</div>
    </div>
    <div class="nav-row">
      <button @click="prev" :disabled="phraseIndex === 0">⬅️</button>
      <button @click="next" :disabled="phraseIndex === phraseQuestions.length - 1">➡️</button>
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
import { phraseQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const phraseIndex = ref(0)
const phraseData = computed(() => phraseQuestions[phraseIndex.value])

function prev() {
  if (phraseIndex.value > 0) {
    phraseIndex.value--
    nextTick(() => speakEN(phraseData.value.english))
  }
}

function next() {
  if (phraseIndex.value < phraseQuestions.length - 1) {
    phraseIndex.value++
    nextTick(() => speakEN(phraseData.value.english))
  } else if (!progressStore.completedModules.phrases) {
    progressStore.completeModule('phrases')
    userStore.updateStars(3)
    progressStore.unlockSticker('phrases')
    celebrationStore.show('💬', '口语小达人！', '你学会了日常英语口语！')
  }
}
</script>

<style scoped>
.phrase-module {
  background: linear-gradient(180deg, #FFF0FF 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.phrase-module .phrase-display { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-top: 30px; width: 100%; padding: 0 20px; }
.phrase-module .phrase-display .phrase-emoji { font-size: 80px; cursor: pointer; animation: dinoBounce 2s ease-in-out infinite; }
.phrase-module .phrase-display .phrase-emoji:active { animation: spin 0.5s ease; }
.phrase-module .phrase-display .phrase-english { font-size: 36px; font-weight: bold; color: var(--secondary); cursor: pointer; text-align: center; line-height: 1.3; }
.phrase-module .phrase-display .phrase-english:active { animation: numberPop 0.3s cubic-bezier(.34, 1.56, .64, 1); }
.phrase-module .phrase-display .phrase-chinese { font-size: 22px; color: #888; }
.phrase-module .phrase-display .phrase-situation { font-size: 16px; color: #aaa; margin-top: 4px; }
.phrase-module .phrase-display .progress-text { font-size: 16px; color: #bbb; margin-top: 6px; }
.phrase-module .nav-row { display: flex; gap: 30px; margin-top: 20px; }
.phrase-module .nav-row button {
  width: 64px; height: 64px; border-radius: 50%; border: none; font-size: 32px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer; transition: all 0.2s;
}
.phrase-module .nav-row button:active { transform: scale(0.9); }
.phrase-module .nav-row button:disabled { opacity: 0.3; }
</style>
