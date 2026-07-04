<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="animal-area">
      <div class="animal-emoji" @click="speakItem(fruitData.english)">{{ fruitData.emoji }}</div>
      <div class="hint">这是什么水果？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in fruitOptions" :key="i"
          :class="{correct: fruitFeedback==='correct' && fruitPick===i, wrong: fruitFeedback==='wrong' && fruitPick===i}"
          @click="speakItem(opt); pickFruit(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ fruitFeedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (fruitIndex+1)/5*100+'%'}"></div>
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
import { speakEN, speakCN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import DinoGuide from '../../components/DinoGuide.vue'
import { fruitQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const muted = ref(false)
const dinoMood = ref('happy')
const dinoText = ref('点图片听发音')
const dinoClicks = ref(0)
function tapDino() {
  dinoClicks.value++
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '水果好香甜！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '点图片听发音' }, 2000); dinoClicks.value = 0 }
}

function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const sessionQuestions = computed(() => shuffle(fruitQuestions).slice(0, 5))
const fruitIndex = ref(0)
const fruitData = computed(() => sessionQuestions.value[fruitIndex.value])
const fruitOptions = computed(() => shuffle([...fruitData.value.options]))
const fruitFeedback = ref('')
const fruitPick = ref(-1)
const fruitFeedbackText = ref('')

function resetFruit() { fruitFeedback.value = ''; fruitPick.value = -1; fruitFeedbackText.value = '' }
function speakItem(text) { if (text && !muted.value) speakEN(text) }

nextTick(() => { if (!muted.value) speakCN('这是什么水果？') })

function pickFruit(i) {
  if (fruitFeedback.value) return
  fruitPick.value = i
  if (fruitOptions.value[i] === fruitData.value.english) {
    fruitFeedback.value = 'correct'
    fruitFeedbackText.value = '🎉 ' + fruitData.value.english + '！'
    dinoMood.value = 'excited'; dinoText.value = '好吃的水果！'
    if (!muted.value) { speakEN(fruitOptions.value[i]); speakCN('你真棒！') }
    setTimeout(() => {
      if (fruitIndex.value < sessionQuestions.value.length - 1) {
        fruitIndex.value++; resetFruit()
        dinoMood.value = 'happy'; dinoText.value = '下一个水果来啦'
        if (!muted.value) speakCN('这是什么水果？')
      } else {
        if (!progressStore.completedModules.fruits) {
          progressStore.completeModule('fruits'); userStore.updateStars(3); progressStore.unlockSticker('fruits')
          celebrationStore.show('🍎', '水果大王！', '你学会了水果英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1500)
  } else {
    fruitFeedback.value = 'wrong'
    fruitFeedbackText.value = '😅 再想想~'
    if (!muted.value) { speakEN(fruitOptions.value[i]); speakEN('Try again') }
    setTimeout(() => resetFruit(), 800)
  }
}
</script>

<style scoped>
.animal-module {
  background: linear-gradient(180deg, #FFF0F0 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.mute-btn { position: absolute; top: 76px; right: 12px; font-size: 28px; cursor: pointer; user-select: none; z-index: 10; }
.animal-module .animal-area { display: flex; flex-direction: column; align-items: center; gap: 12px; margin-top: 4px; width: 100%; flex: 1; justify-content: center; }
.animal-module .animal-area .animal-emoji { font-size: 100px; animation: dinoBounce 2s ease-in-out infinite; cursor: pointer; }
.animal-module .animal-area .animal-emoji:active { animation: spin 0.5s ease; }
.animal-module .animal-area .hint { font-size: 20px; color: #888; }
.animal-module .animal-area .animal-options { display: flex; gap: 14px; flex-wrap: wrap; justify-content: center; }
.animal-module .animal-area .animal-options button {
  padding: 14px 28px; border-radius: 20px; border: 4px solid transparent;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  font-size: 28px; font-weight: bold; font-family: inherit;
  cursor: pointer; transition: all 0.2s; color: var(--text);
}
.animal-module .animal-area .animal-options button:active { transform: scale(0.9); }
.animal-module .animal-area .animal-options button.correct { border-color: var(--secondary); background: #E8F8F5; animation: correctWiggle 0.4s ease; }
.animal-module .animal-area .animal-options button.wrong { border-color: var(--primary); background: #FFF0F0; animation: shake 0.3s ease; }
.animal-module .animal-area .feedback { font-size: 32px; min-height: 48px; }
.progress-bar { width: min(300px, 70vw); height: 8px; background: #e0e0e0; border-radius: 4px; margin: 4px 0; overflow: hidden; }
.progress-bar .progress-fill { height: 100%; background: var(--orange); border-radius: 4px; transition: width 0.4s ease; }
</style>
