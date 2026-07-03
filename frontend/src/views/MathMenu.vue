<template>
  <div class="page submenu math-menu">
    <TopBar back-route="/menu" @back="router.push('/menu')" />
    <div class="sub-title"><span class="sub-icon">🔢</span>学数学</div>
    <div class="module-grid">
      <button class="module-card" :class="{completed: store.completedModules.numbers}" @click="router.push('/math/numbers')">
        <span class="icon">🔢</span><span class="label">认识数字</span><span class="badge">1 2 3 ~</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.counting}" @click="router.push('/math/counting')">
        <span class="icon">🐱</span><span class="label">数一数</span><span class="badge">有几个？</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.shapes}" @click="router.push('/math/shapes')">
        <span class="icon">🔵</span><span class="label">认识形状</span><span class="badge">圆形在哪？</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.compare}" @click="router.push('/math/compare')">
        <span class="icon">🐘</span><span class="label">比大小</span><span class="badge">谁更大？</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.sorting}" @click="router.push('/math/sorting')">
        <span class="icon">📊</span><span class="label">数字排序</span><span class="badge">排一排</span>
      </button>
      <button class="module-card" :class="{completed: store.completedModules.addition}" @click="router.push('/math/addition')">
        <span class="icon">➕</span><span class="label">简单加法</span><span class="badge">1+1=？</span>
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
.submenu.math-menu { background: linear-gradient(180deg, #FFF0F0 0%, #FFF8E7 100%); }
.submenu.math-menu .module-card { border-bottom: 4px solid var(--primary); }
</style>
