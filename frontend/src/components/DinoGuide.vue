<template>
  <div class="dino-guide">
    🦕
    <span class="dino-bubble">{{ text }}</span>
  </div>
</template>

<script setup>
defineProps({
  text: { type: String, default: '' }
})
</script>
