<template>
  <div class="page song-module">
    <TopBar back-route="/english" @back="router.push('/english')" />
    <template v-if="showDir">
      <DinoGuide text="选择一首儿歌" />
      <div class="song-dir">
        <button v-for="(s, i) in songQuestions" :key="i" class="dir-card" @click="selectSong(i)">
          <span class="dir-emoji">{{ s.emoji }}</span>
          <span class="dir-title">{{ s.title }}</span>
        </button>
      </div>
    </template>
    <template v-else>
      <DinoGuide text="点歌名或▶️听歌词" />
      <div class="song-display">
        <div class="song-emoji">{{ songData.emoji }}</div>
        <div class="song-title" @click="singSong">{{ songData.title }}</div>
        <div class="song-lyrics">
          <p v-for="(line, i) in songData.lines" :key="i"
            :class="{highlight: i === currentLine}"
            class="lyric-line">{{ line }}</p>
        </div>
        <div class="progress-text">{{ songIndex + 1 }} / {{ songQuestions.length }}</div>
      </div>
      <div class="nav-row">
        <button @click="prev" :disabled="songIndex === 0">⬅️</button>
        <button class="play-btn" @click="singSong">▶️</button>
        <button @click="next" :disabled="songIndex === songQuestions.length - 1">➡️</button>
        <button class="dir-btn" @click="showDir = true">📋</button>
      </div>
    </template>
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
import { songQuestions } from '../../utils/quizData'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const songIndex = ref(0)
const currentLine = ref(-1)
const singing = ref(false)
const showDir = ref(false)
const songData = computed(() => songQuestions[songIndex.value])

function speakLine(text, delay) {
  return new Promise(resolve => {
    speakEN(text)
    setTimeout(resolve, delay)
  })
}

async function singSong() {
  if (singing.value) return
  singing.value = true
  currentLine.value = -1
  speakEN(songData.value.title)
  await new Promise(r => setTimeout(r, 2000))
  for (let i = 0; i < songData.value.lines.length; i++) {
    currentLine.value = i
    const line = songData.value.lines[i]
    const delay = Math.max(2800, line.length * 120)
    await speakLine(line, delay)
  }
  currentLine.value = -1
  singing.value = false
}

function selectSong(i) {
  songIndex.value = i
  currentLine.value = -1
  showDir.value = false
}

function prev() {
  if (songIndex.value > 0) {
    songIndex.value--
    currentLine.value = -1
  }
}

function next() {
  if (songIndex.value < songQuestions.length - 1) {
    songIndex.value++
    currentLine.value = -1
  } else if (!progressStore.completedModules.songs) {
    progressStore.completeModule('songs')
    userStore.updateStars(3)
    progressStore.unlockSticker('songs')
    celebrationStore.show('🎵', '儿歌小歌星！', '你学会了 100 首英语儿歌！')
  }
}
</script>

<style scoped>
.song-module {
  background: linear-gradient(180deg, #FFF8F0 0%, #F0FFF4 100%);
  padding-top: 70px;
  justify-content: flex-start;
}
.song-module .song-dir {
  display: grid; grid-template-columns: 1fr 1fr; gap: 8px;
  padding: 4px 10px 20px; width: 100%; max-width: 500px; overflow-y: auto;
}
.song-module .song-dir .dir-card {
  display: flex; align-items: center; gap: 6px; padding: 8px 6px;
  border: none; border-radius: 12px; background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06); cursor: pointer;
  transition: all 0.15s; text-align: left;
}
.song-module .song-dir .dir-card:active { transform: scale(0.97); background: #f9f9f9; }
.song-module .song-dir .dir-card .dir-emoji { font-size: 24px; flex-shrink: 0; }
.song-module .song-dir .dir-card .dir-title { font-size: 13px; color: var(--text); font-weight: 500; line-height: 1.2; }
.song-module .song-display { display: flex; flex-direction: column; align-items: center; gap: 6px; margin-top: 10px; width: 100%; padding: 0 16px; }
.song-module .song-display .song-emoji { font-size: 60px; animation: dinoBounce 2s ease-in-out infinite; }
.song-module .song-display .song-title { font-size: 26px; font-weight: bold; color: var(--secondary); cursor: pointer; text-align: center; }
.song-module .song-display .song-title:active { animation: numberPop 0.3s cubic-bezier(.34, 1.56, .64, 1); }
.song-module .song-display .song-lyrics { margin-top: 8px; text-align: center; min-height: 160px; display: flex; flex-direction: column; align-items: center; gap: 3px; max-height: 300px; overflow-y: auto; width: 100%; }
.song-module .song-display .song-lyrics .lyric-line { font-size: 17px; color: #888; margin: 0; padding: 2px 8px; border-radius: 6px; transition: all 0.3s; }
.song-module .song-display .song-lyrics .lyric-line.highlight { font-size: 21px; color: var(--text); font-weight: bold; background: rgba(78, 205, 196, 0.1); }
.song-module .song-display .progress-text { font-size: 16px; color: #bbb; margin-top: 4px; }
.song-module .nav-row { display: flex; gap: 14px; margin-top: 10px; align-items: center; }
.song-module .nav-row button {
  width: 56px; height: 56px; border-radius: 50%; border: none; font-size: 24px;
  background: #fff; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer; transition: all 0.2s;
}
.song-module .nav-row button:active { transform: scale(0.9); }
.song-module .nav-row button:disabled { opacity: 0.3; }
.song-module .nav-row .play-btn { width: 64px; height: 64px; font-size: 30px; background: var(--secondary); color: #fff; }
.song-module .nav-row .dir-btn { font-size: 22px; background: #f0f0f0; }
</style>
