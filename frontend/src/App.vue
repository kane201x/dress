<template>
  <div @click="handleFirstClick">
    <router-view v-slot="{ Component, route }">
      <transition name="page" mode="out-in">
        <component :is="Component" :key="route.path" />
      </transition>
    </router-view>
    <CelebrationOverlay
      :visible="celebrationStore.visible"
      :emoji="celebrationStore.emoji"
      :title="celebrationStore.title"
      :subtitle="celebrationStore.subtitle"
      @close="celebrationStore.hide"
    />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'
import { useProgressStore } from './stores/progress'
import { useCelebrationStore } from './stores/celebration'
import { unlockAudio } from './utils/tts'
import CelebrationOverlay from './components/CelebrationOverlay.vue'

const router = useRouter()
const userStore = useUserStore()
const progressStore = useProgressStore()
const celebrationStore = useCelebrationStore()

let audioUnlocked = false

function handleFirstClick() {
  if (!audioUnlocked) {
    unlockAudio()
    audioUnlocked = true
  }
}

onMounted(() => {
  progressStore.loadFromStorage()
})
</script>
