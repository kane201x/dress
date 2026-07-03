<template>
  <div class="progress-bar">
    <div class="progress-fill" :style="{ width: percent + '%' }"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: { type: Number, default: 0 },
  total: { type: Number, default: 1 }
})

const percent = computed(() => Math.min(100, (props.current / props.total) * 100))
</script>

<style scoped>
.progress-bar {
  width: 100%;
  height: 12px;
  background: #eee;
  border-radius: 6px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: var(--secondary);
  border-radius: 6px;
  transition: width 0.3s ease;
}
</style>
