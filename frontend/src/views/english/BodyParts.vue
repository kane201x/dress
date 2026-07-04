<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide :text="dinoText" :mood="dinoMood" @tap="tapDino" />
    <div class="mute-btn" @click="muted = !muted">{{ muted ? '🔇' : '🔊' }}</div>
    <div class="animal-area">
      <div class="animal-emoji" @click="speakItem(bodyData.english)">{{ bodyData.emoji }}</div>
      <div class="hint">这是身体的哪一部分？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in bodyOptions" :key="i"
          :class="{correct: bodyFeedback==='correct' && bodyPick===i, wrong: bodyFeedback==='wrong' && bodyPick===i}"
          @click="speakItem(opt); pickBody(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ bodyFeedbackText }}</div>
      <div class="progress-bar">
        <div class="progress-fill" :style="{width: (bodyIndex+1)/5*100+'%'}"></div>
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
import { bodyQuestions } from '../../utils/quizData'

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
  if (dinoClicks.value >= 3) { dinoMood.value = 'excited'; dinoText.value = '我们的身体真奇妙！'; setTimeout(() => { dinoMood.value = 'happy'; dinoText.value = '点图片听发音' }, 2000); dinoClicks.value = 0 }
}

function shuffle(arr) { const a = [...arr]; for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }; return a }

const sessionQuestions = computed(() => shuffle(bodyQuestions).slice(0, 5))
const bodyIndex = ref(0)
const bodyData = computed(() => sessionQuestions.value[bodyIndex.value])
const bodyOptions = computed(() => shuffle([...bodyData.value.options]))
const bodyFeedback = ref('')
const bodyPick = ref(-1)
const bodyFeedbackText = ref('')

function resetBody() { bodyFeedback.value = ''; bodyPick.value = -1; bodyFeedbackText.value = '' }
function speakItem(text) { if (text && !muted.value) speakEN(text) }

nextTick(() => { if (!muted.value) speakCN('这是身体哪个部位？') })

function pickBody(i) {
  if (bodyFeedback.value) return
  bodyPick.value = i
  if (bodyOptions.value[i] === bodyData.value.english) {
    bodyFeedback.value = 'correct'
    bodyFeedbackText.value = '🎉 ' + bodyData.value.english + '！'
    dinoMood.value = 'excited'; dinoText.value = '答对啦！'
    if (!muted.value) { speakEN(bodyData.value.english); speakCN('你真棒！') }
    setTimeout(() => {
      if (bodyIndex.value < sessionQuestions.value.length - 1) {
        bodyIndex.value++; resetBody()
        dinoMood.value = 'happy'; dinoText.value = '下一个来啦'
        if (!muted.value) speakCN('这是身体哪个部位？')
      } else {
        if (!progressStore.completedModules.body) {
          progressStore.completeModule('body'); userStore.updateStars(3); progressStore.unlockSticker('body')
          celebrationStore.show('👶', '身体小博士！', '你学会了身体部位英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1500)
  } else {
    bodyFeedback.value = 'wrong'
    bodyFeedbackText.value = '😅 再想想~'
    if (!muted.value) speakEN('Try again')
    setTimeout(() => resetBody(), 600)
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
.progress-bar .progress-fill { height: 100%; background: var(--purple); border-radius: 4px; transition: width 0.4s ease; }
</style>
