<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <div class="animal-area">
      <DinoGuide text="点图片听发音" />
      <div class="animal-emoji" @click="speakEN(bodyData.english)">{{ bodyData.emoji }}</div>
      <div class="hint">这是身体的哪一部分？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in bodyOptions" :key="i"
          :class="{correct: bodyFeedback==='correct' && bodyPick===i, wrong: bodyFeedback==='wrong' && bodyPick===i}"
          @click="speakEN(opt); pickBody(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ bodyFeedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ bodyIndex+1 }} / {{ bodyQuestions.length }}</div>
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
import { bodyQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}

const bodyIndex = ref(0)
const bodyData = computed(() => bodyQuestions[bodyIndex.value])
const bodyOptions = computed(() => shuffle([...bodyData.value.options]))
const bodyFeedback = ref('')
const bodyPick = ref(-1)
const bodyFeedbackText = ref('')

function resetBody() { bodyFeedback.value = ''; bodyPick.value = -1; bodyFeedbackText.value = '' }

function pickBody(i) {
  if (bodyFeedback.value) return
  bodyPick.value = i
  if (bodyOptions.value[i] === bodyData.value.english) {
    bodyFeedback.value = 'correct'
    bodyFeedbackText.value = '🎉 ' + bodyData.value.english + '！'
    setTimeout(() => {
      if (bodyIndex.value < bodyQuestions.length - 1) {
        bodyIndex.value++
        resetBody()
      } else {
        if (!progressStore.completedModules.body) {
          progressStore.completeModule('body')
          userStore.updateStars(3)
          progressStore.unlockSticker('body')
          celebrationStore.show('👶', '身体小博士！', '你学会了 10 个身体部位！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1200)
  } else {
    bodyFeedback.value = 'wrong'
    bodyFeedbackText.value = '😅 再想想~'
    setTimeout(() => resetBody(), 600)
  }
}
</script>
