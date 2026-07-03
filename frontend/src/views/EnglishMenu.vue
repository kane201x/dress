<template>
  <div class="page submenu english-menu">
    <TopBar back-route="/menu" @back="router.push('/menu')" />
    <div class="sub-title"><span class="sub-icon">🔤</span>学英语 <span style="font-size:16px;color:#aaa;">({{ store.engDoneCount }}/6)</span></div>
    <div class="module-grid" style="grid-template-columns:1fr 1fr 1fr;">
      <button class="module-card" :class="{completed: store.completedModules.alphabet}" @click="router.push('/english/alphabet')">
        <span class="icon">🔤</span><span class="label">字母</span><span class="badge">A~Z</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.colors}" @click="router.push('/english/colors')">
        <span class="icon">🎨</span><span class="label">颜色</span><span class="badge">12色</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.animals}" @click="router.push('/english/animals')">
        <span class="icon">🐶</span><span class="label">动物</span><span class="badge">15种</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.fruits}" @click="router.push('/english/fruits')">
        <span class="icon">🍎</span><span class="label">水果</span><span class="badge">12种</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.vehicles}" @click="router.push('/english/vehicles')">
        <span class="icon">🚗</span><span class="label">交通工具</span><span class="badge">10种</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.body}" @click="router.push('/english/body')">
        <span class="icon">👶</span><span class="label">身体部位</span><span class="badge">10个</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useProgressStore } from '../stores/progress'
import TopBar from '../components/TopBar.vue'

const router = useRouter()
const store = useProgressStore()
</script>

<style scoped>
.submenu {
  padding-top: 70px;
  justify-content: flex-start;
  gap: 6px;
}
.submenu .sub-title { font-size: 30px; font-weight: bold; margin-top: 20px; text-align: center; }
.submenu .sub-title .sub-icon { font-size: 40px; display: block; }
.submenu .module-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  width: 100%;
  max-width: 460px;
  padding: 0 10px;
  margin-top: 10px;
}
.submenu .module-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 16px 8px;
  border-radius: var(--radius);
  border: none;
  background: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s;
  min-height: 120px;
  position: relative;
}
.submenu .module-card:active { transform: scale(0.95); }
.submenu .module-card .icon { font-size: 44px; }
.submenu .module-card .label { font-size: 19px; font-weight: bold; }
.submenu .module-card .badge { font-size: 13px; color: #aaa; }
.submenu .module-card.completed::after {
  content: '✅';
  position: absolute;
  top: 6px;
  right: 6px;
  font-size: 18px;
}
.submenu.english-menu { background: linear-gradient(180deg, #F0FFF4 0%, #FFF8E7 100%); }
.submenu.english-menu .module-card { border-bottom: 4px solid var(--secondary); }
</style>
