<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <div class="animal-area">
      <DinoGuide text="点图片听发音" />
      <div class="animal-emoji" @click="speakEN(fruitData.english)">{{ fruitData.emoji }}</div>
      <div class="hint">这是什么水果？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in fruitOptions" :key="i"
          :class="{correct: fruitFeedback==='correct' && fruitPick===i, wrong: fruitFeedback==='wrong' && fruitPick===i}"
          @click="speakEN(opt); pickFruit(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ fruitFeedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ fruitIndex+1 }} / {{ fruitQuestions.length }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useProgressStore } from '../../stores/progress'
import { useCelebrationStore } from '../../stores/celebration'
import { speakEN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import DinoGuide from '../../components/DinoGuide.vue'
import { fruitQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}

const fruitIndex = ref(0)
const fruitData = computed(() => fruitQuestions[fruitIndex.value])
const fruitOptions = computed(() => shuffle([...fruitData.value.options]))
const fruitFeedback = ref('')
const fruitPick = ref(-1)
const fruitFeedbackText = ref('')

function resetFruit() { fruitFeedback.value = ''; fruitPick.value = -1; fruitFeedbackText.value = '' }

function pickFruit(i) {
  if (fruitFeedback.value) return
  fruitPick.value = i
  if (fruitOptions.value[i] === fruitData.value.english) {
    fruitFeedback.value = 'correct'
    fruitFeedbackText.value = '🎉 ' + fruitData.value.english + '！'
    setTimeout(() => {
      if (fruitIndex.value < fruitQuestions.length - 1) {
        fruitIndex.value++
        resetFruit()
      } else {
        if (!progressStore.completedModules.fruits) {
          progressStore.completeModule('fruits')
          userStore.updateStars(3)
          progressStore.unlockSticker('fruits')
          celebrationStore.show('🍎', '水果大王！', '你学会了 12 种水果英语！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1200)
  } else {
    fruitFeedback.value = 'wrong'
    fruitFeedbackText.value = '😅 再想想~'
    setTimeout(() => resetFruit(), 600)
  }
}
</script>
