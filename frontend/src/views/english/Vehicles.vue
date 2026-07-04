<template>
  <div class="page animal-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <div class="animal-area">
      <DinoGuide text="点图片听发音" />
      <div class="animal-emoji" @click="speakEN(vehicleData.english)">{{ vehicleData.emoji }}</div>
      <div class="hint">这是什么交通工具？</div>
      <div class="animal-options">
        <button v-for="(opt,i) in vehicleOptions" :key="i"
          :class="{correct: vehicleFeedback==='correct' && vehiclePick===i, wrong: vehicleFeedback==='wrong' && vehiclePick===i}"
          @click="speakEN(opt); pickVehicle(i)">{{ opt }}</button>
      </div>
      <div class="feedback">{{ vehicleFeedbackText }}</div>
      <div class="progress" style="font-size:16px;color:#bbb;margin-top:6px;">{{ vehicleIndex+1 }} / {{ vehicleQuestions.length }}</div>
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
import { vehicleQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

function shuffle(arr) {
  const a = [...arr]
  for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]] }
  return a
}

const vehicleIndex = ref(0)
const vehicleData = computed(() => vehicleQuestions[vehicleIndex.value])
const vehicleOptions = computed(() => shuffle([...vehicleData.value.options]))
const vehicleFeedback = ref('')
const vehiclePick = ref(-1)
const vehicleFeedbackText = ref('')

function resetVehicle() { vehicleFeedback.value = ''; vehiclePick.value = -1; vehicleFeedbackText.value = '' }

function pickVehicle(i) {
  if (vehicleFeedback.value) return
  vehiclePick.value = i
  if (vehicleOptions.value[i] === vehicleData.value.english) {
    vehicleFeedback.value = 'correct'
    vehicleFeedbackText.value = '🎉 ' + vehicleData.value.english + '！'
    setTimeout(() => {
      if (vehicleIndex.value < vehicleQuestions.length - 1) {
        vehicleIndex.value++
        resetVehicle()
      } else {
        if (!progressStore.completedModules.vehicles) {
          progressStore.completeModule('vehicles')
          userStore.updateStars(3)
          progressStore.unlockSticker('vehicles')
          celebrationStore.show('🚗', '交通小专家！', '你学会了 10 种交通工具！')
        } else { celebrationStore.show('⭐', '全部完成！', '') }
      }
    }, 1200)
  } else {
    vehicleFeedback.value = 'wrong'
    vehicleFeedbackText.value = '😅 再想想~'
    setTimeout(() => resetVehicle(), 600)
  }
}
</script>

<style scoped>
.animal-module {
  background: linear-gradient(180deg, #FFF0F0 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.animal-module .animal-area { display: flex; flex-direction: column; align-items: center; gap: 12px; margin-top: 10px; width: 100%; }
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
</style>
