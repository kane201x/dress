<template>
  <div class="page phrase-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide text="点句子听发音" />
    <div class="situation-bar">
      <span v-for="s in situations" :key="s" class="situation-chip"
        :class="{active: selectedSituation === s}"
        @click="selectSituation(s)">{{ s }}</span>
    </div>
    <div class="phrase-display">
      <div class="phrase-emoji" @click="speakEN(phraseData.english)">{{ phraseData.emoji }}</div>
      <div class="phrase-english" @click="speakEN(phraseData.english)">{{ phraseData.english }}</div>
      <div class="phrase-chinese">{{ phraseData.chinese }}</div>
      <div class="phrase-situation">💡 {{ phraseData.situation }}</div>
      <div class="progress-text">{{ phraseIndex + 1 }} / {{ filteredPhrases.length }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (phraseIndex+1)/filteredPhrases.length*100+'%'}"></div>
      </div>
    </div>
    <div class="nav-row">
      <button @click="first" :disabled="phraseIndex === 0">⏮️</button>
      <button @click="prev" :disabled="phraseIndex === 0">⬅️</button>
      <button @click="toggleList">📋</button>
      <button @click="randomJump">🎲</button>
      <button @click="next" :disabled="phraseIndex === filteredPhrases.length - 1">➡️</button>
      <button @click="last" :disabled="phraseIndex === filteredPhrases.length - 1">⏭️</button>
    </div>
    <div v-if="showList" class="list-overlay" @click.self="showList = false">
      <div class="list-panel">
        <div class="list-header">
          <span>全部对话 ({{ filteredPhrases.length }})</span>
          <button class="close-btn" @click="showList = false">✕</button>
        </div>
        <div class="list-scroll">
          <div v-for="(p, i) in filteredPhrases" :key="i" class="list-item"
            :class="{active: i === phraseIndex}"
            @click="jumpTo(i)">
            <span class="item-emoji">{{ p.emoji }}</span>
            <div class="item-text">
              <span class="item-en">{{ p.english }}</span>
              <span class="item-cn">{{ p.chinese }}</span>
            </div>
            <span class="item-sit">{{ p.situation }}</span>
          </div>
        </div>
      </div>
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

const situations = [...new Set(phraseQuestions.map(p => p.situation))]
const selectedSituation = ref('')
const phraseIndex = ref(0)
const showList = ref(false)

const filteredPhrases = computed(() =>
  selectedSituation.value
    ? phraseQuestions.filter(p => p.situation === selectedSituation.value)
    : phraseQuestions
)

const phraseData = computed(() => filteredPhrases.value[phraseIndex.value])

function toggleList() { showList.value = !showList.value }

function jumpTo(i) {
  phraseIndex.value = i
  showList.value = false
  nextTick(() => speakEN(phraseData.value.english))
}

function selectSituation(s) {
  selectedSituation.value = selectedSituation.value === s ? '' : s
  phraseIndex.value = 0
  nextTick(() => speakEN(phraseData.value.english))
}

function first() { if (phraseIndex.value > 0) { phraseIndex.value = 0; nextTick(() => speakEN(phraseData.value.english)) } }
function last() { if (phraseIndex.value < filteredPhrases.value.length - 1) { phraseIndex.value = filteredPhrases.value.length - 1; nextTick(() => speakEN(phraseData.value.english)) } }
function randomJump() { const n = Math.floor(Math.random() * filteredPhrases.value.length); phraseIndex.value = n; nextTick(() => speakEN(phraseData.value.english)) }

function prev() {
  if (phraseIndex.value > 0) {
    phraseIndex.value--
    nextTick(() => speakEN(phraseData.value.english))
  }
}

function next() {
  if (phraseIndex.value < filteredPhrases.value.length - 1) {
    phraseIndex.value++
    nextTick(() => speakEN(phraseData.value.english))
  } else if (!selectedSituation.value && !progressStore.completedModules.phrases) {
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
.situation-bar {
  display: flex; gap: 6px; overflow-x: auto; padding: 8px 12px;
  scrollbar-width: none; white-space: nowrap; margin-top: 4px;
  -webkit-overflow-scrolling: touch;
}
.situation-chip {
  padding: 4px 14px; border-radius: 20px; background: #f0f0f0;
  font-size: 13px; color: #888; cursor: pointer; flex-shrink: 0;
  transition: all 0.2s; user-select: none;
}
.situation-chip.active { background: var(--secondary); color: #fff; }
.situation-chip:active { transform: scale(0.93); }
.phrase-module .phrase-display { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-top: 20px; width: 100%; padding: 0 20px; }
.phrase-module .phrase-display .phrase-emoji { font-size: 80px; cursor: pointer; animation: dinoBounce 2s ease-in-out infinite; }
.phrase-module .phrase-display .phrase-emoji:active { animation: spin 0.5s ease; }
.phrase-module .phrase-display .phrase-english { font-size: 36px; font-weight: bold; color: var(--secondary); cursor: pointer; text-align: center; line-height: 1.3; }
.phrase-module .phrase-display .phrase-english:active { animation: numberPop 0.3s cubic-bezier(.34, 1.56, .64, 1); }
.phrase-module .phrase-display .phrase-chinese { font-size: 22px; color: #888; }
.phrase-module .phrase-display .phrase-situation { font-size: 16px; color: #aaa; margin-top: 4px; }
.phrase-module .phrase-display .progress-text { font-size: 16px; color: #bbb; margin-top: 6px; }
.phrase-module .phrase-display .progress-bar { width: min(280px, 60vw); height: 6px; background: #e0e0e0; border-radius: 3px; margin-top: 4px; overflow: hidden; }
.phrase-module .phrase-display .progress-bar .progress-fill { height: 100%; background: var(--secondary); border-radius: 3px; transition: width 0.3s ease; }
.phrase-module .nav-row { display: flex; gap: 12px; margin-top: 24px; }
.phrase-module .nav-row button {
  width: 56px; height: 56px; border-radius: 50%; border: none; font-size: 24px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer; transition: all 0.2s;
}
.phrase-module .nav-row button:active { transform: scale(0.9); }
.phrase-module .nav-row button:disabled { opacity: 0.3; }
.list-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 100;
  display: flex; align-items: center; justify-content: center;
}
.list-panel {
  background: #fff; border-radius: 20px; width: min(600px, 92vw);
  max-height: 80vh; display: flex; flex-direction: column; overflow: hidden;
  box-shadow: 0 8px 40px rgba(0,0,0,0.2);
}
.list-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 14px 18px; font-size: 18px; font-weight: bold; color: #555;
  border-bottom: 1px solid #eee; flex-shrink: 0;
}
.list-header .close-btn {
  width: 36px; height: 36px; border-radius: 50%; border: none;
  background: #f0f0f0; font-size: 18px; cursor: pointer; color: #888;
}
.list-scroll { flex: 1; overflow-y: auto; padding: 8px 0; }
.list-item {
  display: flex; align-items: center; gap: 12px; padding: 10px 18px;
  cursor: pointer; transition: background 0.15s; border-left: 3px solid transparent;
}
.list-item:active { background: #f5f5f5; }
.list-item.active { background: #E8F8F5; border-left-color: var(--secondary); }
.list-item .item-emoji { font-size: 28px; flex-shrink: 0; }
.list-item .item-text { flex: 1; display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.list-item .item-text .item-en { font-size: 16px; font-weight: 600; color: #333; }
.list-item .item-text .item-cn { font-size: 14px; color: #999; }
.list-item .item-sit { font-size: 12px; color: #bbb; flex-shrink: 0; }
</style>
