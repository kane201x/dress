<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="animal-area">
      <div class="animal-emoji" @click="speakAnimal(animalData.english)">{{ animalData.emoji }}</div>
      <div class="hint">这是什么动物？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in animalOptions" :key="i"
          :class="{correct: animalFeedback==='correct' && animalPick===i, wrong: animalFeedback==='wrong' && animalPick===i}"
          @click="speakAnimal(opt); pickAnimal(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ animalFeedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (animalIndex+1)/5*100+'%'}"></div>
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
import { animalQuestions } from '../../utils/quizData'

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
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '小动物们好可爱！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '点图片听发音' }, 2000) }
}

function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const sessionQuestions = computed(() => shuffle(animalQuestions).slice(0, 5))
const animalIndex = ref(0)
const animalData = computed(() => sessionQuestions.value[animalIndex.value])
const animalOptions = computed(() => shuffle([...animalData.value.options]))
const animalFeedback = ref('')
const animalPick = ref(-1)
const animalFeedbackText = ref('')

function resetAnimal() { animalFeedback.value = ''; animalPick.value = -1; animalFeedbackText.value = '' }

function speakAnimal(text) { if (text && !muted.value) speakEN(text) }

nextTick(() => { if (!muted.value) speakCN('这是什么动物？') })

function pickAnimal(i) {
  if (animalFeedback.value) return
  animalPick.value = i
  if (animalOptions.value[i] === animalData.value.english) {
    animalFeedback.value = 'correct'
    animalFeedbackText.value = '🎉 ' + animalData.value.english + '！'
    dinoMood.value = 'excited'; dinoText.value = '答对啦！教教我~'
    if (!muted.value) { speakEN(animalData.value.english); speakCN('你真棒！') }
    setTimeout(() => {
      if (animalIndex.value < sessionQuestions.value.length - 1) {
        animalIndex.value++; resetAnimal()
        dinoMood.value = 'happy'; dinoText.value = '下一个动物来啦'
        if (!muted.value) speakCN('这是什么动物？')
      } else {
        if (!progressStore.completedModules.animals) {
          progressStore.completeModule('animals'); userStore.updateStars(3); progressStore.unlockSticker('animals')
          celebrationStore.show('🐶', '动物专家！', '你学会了动物英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1500)
  } else {
    animalFeedback.value = 'wrong'
    animalFeedbackText.value = '😅 再想想~'
    if (!muted.value) speakEN('Try again')
    setTimeout(() => resetAnimal(), 600)
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
.progress-bar .progress-fill { height: 100%; background: var(--pink); border-radius: 4px; transition: width 0.4s ease; }
</style>
