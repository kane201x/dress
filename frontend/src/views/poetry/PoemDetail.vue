<template>
  <div class="page poem-detail" :class="poemBgClass">
    <TopBar back-route="/poetry" @back="router.push('/poetry')" />
    <div v-if="currentPoem" class="poem-content">
      <div class="p-emoji-big">{{ currentPoem.emoji }}</div>
      <div class="p-title-big">{{ currentPoem.title }}</div>
      <div class="p-author-big">{{ currentPoem.author }} · {{ currentPoem.dynasty }}</div>
      <div class="p-text">
        <div v-for="(line,li) in currentPoem.lines" :key="li"
          class="p-line-text"
          :class="{highlight: poemHighlight === li}"
          @click="speakLine(li)">
          <ruby v-for="(ch,ci) in getLineChars(line)" :key="ci">
            {{ ch.char }}<rt>{{ ch.py }}</rt>
          </ruby>
        </div>
      </div>
      <div class="action-row">
        <button class="listen-btn" @click="recitePoem">🔊 听全诗</button>
        <button v-if="!isPoemDone" class="done-btn" @click="completePoem">⭐ 背完了</button>
        <button v-else class="done-btn" style="opacity:0.6">✅ 已背诵</button>
      </div>
      <div class="poem-nav">
        <button class="nav-btn" @click="prevPoem" :disabled="currentIndex <= 0">⬅️ 上一首</button>
        <span class="nav-progress">{{ currentIndex+1 }} / {{ allPoems.length }}</span>
        <button class="nav-btn" @click="nextPoem" :disabled="currentIndex >= allPoems.length-1">下一首 ➡️</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { useProgressStore } from '../../stores/progress'
import { useCelebrationStore } from '../../stores/celebration'
import { speakCN } from '../../utils/tts'
import TopBar from '../../components/TopBar.vue'
import { allPoems } from '../../utils/poems'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

const currentIndex = ref(parseInt(route.params.id) || 0)
const poemHighlight = ref(-1)

const currentPoem = computed(() => allPoems[currentIndex.value])
const isPoemDone = computed(() => progressStore.completedModules['poem' + currentIndex.value])
const poemBgClass = computed(() => {
  const p = currentPoem.value
  if (!p || !p.bg) return 'poem-bg-spring'
  return 'poem-bg-' + p.bg
})

function getLineChars(line) {
  if (!window.pinyinPro) return [...line].map(char => ({ char, py: '' }))
  try {
    const result = window.pinyinPro(line, { toneType: 'symbol', type: 'array' })
    if (Array.isArray(result) && result.length > 0) {
      const chars = [...line]
      return chars.map((char, i) => ({ char, py: i < result.length ? result[i] || '' : '' }))
    }
  } catch (e) {}
  return [...line].map(char => ({ char, py: '' }))
}

function speakLine(i) {
  poemHighlight.value = i
  const line = currentPoem.value.lines[i]
  speakCN(line)
  setTimeout(() => poemHighlight.value = -1, 1500)
}

function recitePoem() {
  const p = currentPoem.value
  let text = p.title + '，' + p.author + '，'
  text += p.lines.join('，')
  speakCN(text)
}

async function completePoem() {
  const key = 'poem' + currentIndex.value
  if (!progressStore.completedModules[key]) {
    await progressStore.completeModule(key)
    await userStore.updateStars(2)
    const done = progressStore.poetryDoneCount
    if (done >= 150) {
      await progressStore.unlockSticker('poemAll')
      celebrationStore.show('📜', '古诗小才子！', '你背完了 150 首古诗！')
    } else {
      celebrationStore.show('📜', '背完啦！', '已背诵 ' + done + '/150 首，继续加油！')
    }
  }
}

function prevPoem() {
  if (currentIndex.value > 0) {
    currentIndex.value--
    poemHighlight.value = -1
    router.replace('/poetry/' + currentIndex.value)
  }
}

function nextPoem() {
  if (currentIndex.value < allPoems.length - 1) {
    currentIndex.value++
    poemHighlight.value = -1
    router.replace('/poetry/' + currentIndex.value)
  }
}

watch(() => route.params.id, (id) => {
  currentIndex.value = parseInt(id) || 0
  poemHighlight.value = -1
})
</script>

<style scoped>
.poem-detail {
  padding-top: 70px;
  justify-content: flex-start;
}
.poem-detail .poem-content { display: flex; flex-direction: column; align-items: center; gap: 10px; margin-top: 6px; width: 100%; padding: 0 20px; }
.poem-detail .poem-content .p-emoji-big { font-size: 64px; }
.poem-detail .poem-content .p-title-big { font-size: 34px; font-weight: bold; color: var(--purple); }
.poem-detail .poem-content .p-author-big { font-size: 20px; color: #999; }
.poem-detail .poem-content .p-text {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  margin-top: 8px; padding: 20px 24px;
  background: rgba(255, 255, 255, 0.8); border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06); width: 100%; max-width: 400px;
}
.poem-detail .poem-content .p-text .p-line-text {
  font-size: 26px; line-height: 1.6; text-align: center;
  color: var(--text); cursor: pointer; padding: 4px 12px;
  border-radius: 12px; transition: all 0.2s;
}
.poem-detail .poem-content .p-text .p-line-text:active { background: rgba(167, 139, 250, 0.15); }
.poem-detail .poem-content .p-text .p-line-text.highlight { background: rgba(167, 139, 250, 0.2); }
.poem-detail .poem-content .action-row { display: flex; gap: 16px; margin-top: 10px; }
.poem-detail .poem-content .action-row button {
  padding: 12px 28px; border-radius: 40px; border: none;
  font-size: 22px; font-family: inherit; cursor: pointer;
  transition: all 0.2s; box-shadow: 0 4px 0 rgba(0, 0, 0, 0.1);
}
.poem-detail .poem-content .action-row button:active { transform: translateY(3px); box-shadow: 0 1px 0 rgba(0, 0, 0, 0.1); }
.poem-detail .poem-content .action-row .listen-btn { background: var(--purple); color: #fff; }
.poem-detail .poem-content .action-row .done-btn { background: var(--yellow); color: var(--text); }
.poem-detail .poem-content .poem-nav { display: flex; align-items: center; gap: 12px; margin-top: 10px; }
.poem-detail .poem-content .poem-nav .nav-btn {
  padding: 8px 18px; border-radius: 30px; border: none; font-size: 18px;
  font-family: inherit; cursor: pointer; background: var(--purple);
  color: #fff; transition: all 0.2s; box-shadow: 0 3px 0 rgba(0,0,0,0.1);
}
.poem-detail .poem-content .poem-nav .nav-btn:active { transform: translateY(2px); box-shadow: 0 1px 0 rgba(0,0,0,0.1); }
.poem-detail .poem-content .poem-nav .nav-btn:disabled { opacity: 0.4; transform: none; }
.poem-detail .poem-content .poem-nav .nav-progress { font-size: 16px; color: #aaa; min-width: 60px; text-align: center; }
</style>
