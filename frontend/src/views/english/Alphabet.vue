<template>
  <div class="page alpha-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <DinoGuide text="点字母听发音" />
    <div class="alpha-display">
      <div class="letter-pair" @click="speakLetter">
        <span class="letter-upper" :class="{anim: alphaAnim}">{{ alphaData.upper }}</span>
        <span class="letter-lower" :class="{anim: alphaAnim}">{{ alphaData.lower }}</span>
      </div>
      <div class="word-row">
        <span class="word-text">{{ alphaData.word }}</span>
        <span class="word-emoji">{{ alphaData.emoji }}</span>
      </div>
      <div class="sentence">{{ alphaData.sentence }}</div>
      <div class="phonics">🔊 点字母听发音</div>
    </div>
    <div class="nav-row">
      <button @click="prevAlpha" :disabled="alphaIndex === 0">⬅️</button>
      <button @click="nextAlpha" :disabled="alphaIndex === alphaList.length-1">➡️</button>
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

const alphaList = [
  { upper: 'A', lower: 'a', word: 'Apple', emoji: '🍎', sentence: 'A is for Apple' },
  { upper: 'B', lower: 'b', word: 'Ball', emoji: '⚽', sentence: 'B is for Ball' },
  { upper: 'C', lower: 'c', word: 'Cat', emoji: '🐱', sentence: 'C is for Cat' },
  { upper: 'D', lower: 'd', word: 'Dog', emoji: '🐶', sentence: 'D is for Dog' },
  { upper: 'E', lower: 'e', word: 'Elephant', emoji: '🐘', sentence: 'E is for Elephant' },
  { upper: 'F', lower: 'f', word: 'Fish', emoji: '🐟', sentence: 'F is for Fish' },
  { upper: 'G', lower: 'g', word: 'Girl', emoji: '👧', sentence: 'G is for Girl' },
  { upper: 'H', lower: 'h', word: 'Hat', emoji: '🎩', sentence: 'H is for Hat' },
  { upper: 'I', lower: 'i', word: 'Ice cream', emoji: '🍦', sentence: 'I is for Ice cream' },
  { upper: 'J', lower: 'j', word: 'Juice', emoji: '🧃', sentence: 'J is for Juice' },
  { upper: 'K', lower: 'k', word: 'Kite', emoji: '🪁', sentence: 'K is for Kite' },
  { upper: 'L', lower: 'l', word: 'Lion', emoji: '🦁', sentence: 'L is for Lion' },
  { upper: 'M', lower: 'm', word: 'Monkey', emoji: '🐵', sentence: 'M is for Monkey' },
  { upper: 'N', lower: 'n', word: 'Nest', emoji: '🪺', sentence: 'N is for Nest' },
  { upper: 'O', lower: 'o', word: 'Orange', emoji: '🍊', sentence: 'O is for Orange' },
  { upper: 'P', lower: 'p', word: 'Pig', emoji: '🐷', sentence: 'P is for Pig' },
  { upper: 'Q', lower: 'q', word: 'Queen', emoji: '👑', sentence: 'Q is for Queen' },
  { upper: 'R', lower: 'r', word: 'Rabbit', emoji: '🐰', sentence: 'R is for Rabbit' },
  { upper: 'S', lower: 's', word: 'Star', emoji: '⭐', sentence: 'S is for Star' },
  { upper: 'T', lower: 't', word: 'Train', emoji: '🚂', sentence: 'T is for Train' },
  { upper: 'U', lower: 'u', word: 'Umbrella', emoji: '☂️', sentence: 'U is for Umbrella' },
  { upper: 'V', lower: 'v', word: 'Violin', emoji: '🎻', sentence: 'V is for Violin' },
  { upper: 'W', lower: 'w', word: 'Watermelon', emoji: '🍉', sentence: 'W is for Watermelon' },
  { upper: 'X', lower: 'x', word: 'Xylophone', emoji: '🎹', sentence: 'X is for Xylophone' },
  { upper: 'Y', lower: 'y', word: 'Yoyo', emoji: '🪀', sentence: 'Y is for Yoyo' },
  { upper: 'Z', lower: 'z', word: 'Zebra', emoji: '🦓', sentence: 'Z is for Zebra' },
]

const alphaIndex = ref(0)
const alphaAnim = ref(false)
const alphaData = computed(() => alphaList[alphaIndex.value])

function speakLetter() {
  alphaAnim.value = true
  setTimeout(() => alphaAnim.value = false, 400)
  speakEN('letter ' + alphaData.value.upper + ', ' + alphaData.value.lower + ', ' + alphaData.value.word)
}

function prevAlpha() {
  if (alphaIndex.value > 0) {
    alphaIndex.value--
    alphaAnim.value = true
    setTimeout(() => alphaAnim.value = false, 400)
    nextTick(() => speakLetter())
  }
}

function nextAlpha() {
  if (alphaIndex.value < alphaList.length - 1) {
    alphaIndex.value++
    alphaAnim.value = true
    setTimeout(() => alphaAnim.value = false, 400)
    nextTick(() => speakLetter())
  } else if (!progressStore.completedModules.alphabet) {
    progressStore.completeModule('alphabet')
    userStore.updateStars(3)
    progressStore.unlockSticker('alphabet')
    celebrationStore.show('🔤', '字母小达人！', '你学会了 A~Z 全部字母！')
  }
}
</script>

<style scoped>
.alpha-module {
  background: linear-gradient(180deg, #F0FFF4 0%, #FFF8E7 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.alpha-module .alpha-display { display: flex; flex-direction: column; align-items: center; gap: 4px; margin-top: 10px; }
.alpha-module .alpha-display .letter-pair { display: flex; align-items: baseline; gap: 6px; cursor: pointer; line-height: 1; }
.alpha-module .alpha-display .letter-pair:active { transform: scale(1.05); }
.alpha-module .alpha-display .letter-upper {
  font-size: 120px; font-weight: bold; color: var(--secondary);
  text-shadow: 4px 4px 0 rgba(78, 205, 196, 0.2); transition: all 0.2s;
}
.alpha-module .alpha-display .letter-lower {
  font-size: 70px; font-weight: bold; color: var(--secondary);
  opacity: 0.6; text-shadow: 2px 2px 0 rgba(78, 205, 196, 0.15); transition: all 0.2s;
}
.alpha-module .alpha-display .letter-upper.anim,
.alpha-module .alpha-display .letter-lower.anim { animation: numberPop 0.4s cubic-bezier(.34, 1.56, .64, 1); }
.alpha-module .alpha-display .word-row { display: flex; align-items: center; gap: 10px; font-size: 36px; margin-top: 4px; }
.alpha-module .alpha-display .word-row .word-text { font-size: 32px; color: var(--text); font-weight: bold; }
.alpha-module .alpha-display .word-row .word-emoji { font-size: 50px; }
.alpha-module .alpha-display .sentence { font-size: 20px; color: #888; margin-top: 2px; }
.alpha-module .alpha-display .phonics { font-size: 24px; color: #aaa; margin-top: 2px; }
.alpha-module .nav-row { display: flex; gap: 30px; margin-top: 16px; }
.alpha-module .nav-row button {
  width: 64px; height: 64px; border-radius: 50%; border: none; font-size: 32px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer; transition: all 0.2s;
}
.alpha-module .nav-row button:active { transform: scale(0.9); }
.alpha-module .nav-row button:disabled { opacity: 0.3; }
</style>
