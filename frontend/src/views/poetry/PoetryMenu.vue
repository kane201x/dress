<template>
  <div class="page submenu poetry-menu">
    <TopBar back-route="/menu" @back="router.push('/menu')" />
    <div class="sub-title"><span class="sub-icon">📜</span>背古诗 <span style="font-size:16px;color:#aaa;">({{ store.poetryDoneCount }}/150)</span></div>
    <div class="cat-filter">
      <button v-for="cat in poemCategories" :key="cat" :class="{active: cat===poemCategory}" @click="poemCategory=cat">{{ cat }}</button>
    </div>
    <div class="poem-list">
      <button v-for="(p,i) in filteredPoems" :key="p._id" class="poem-card" @click="openPoem(i)">
        <span class="p-emoji">{{ p.emoji }}</span>
        <div class="p-info">
          <span class="p-title">{{ p.title }} <span v-if="store.completedModules['poem'+p._id]" style="font-size:14px;">✅</span></span>
          <span class="p-author">{{ p.author }} · {{ p.cat }}</span>
          <span class="p-line">"{{ p.lines[0] }}"</span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useProgressStore } from '../../stores/progress'
import TopBar from '../../components/TopBar.vue'
import { allPoems } from '../../utils/poems'

const router = useRouter()
const store = useProgressStore()

const poemsWithId = allPoems.map((p, i) => { p._id = i; return p })

const poemCategory = ref('全部')
const poemCategories = computed(() => {
  return ['全部', ...new Set(poemsWithId.map(p => p.cat))]
})
const filteredPoems = computed(() => {
  if (poemCategory.value === '全部') return poemsWithId
  return poemsWithId.filter(p => p.cat === poemCategory.value)
})

function openPoem(i) {
  const p = filteredPoems.value[i]
  const realIndex = poemsWithId.indexOf(p)
  router.push('/poetry/' + realIndex)
}
</script>

<style scoped>
.submenu {
  padding-top: 70px;
  justify-content: flex-start;
  gap: 6px;
}
.submenu .sub-title { font-size: 30px; font-weight: bold; margin-top: 20px; text-align: center; }
.submenu .sub-title .sub-icon { font-size: 40px; display: block; }
.poetry-menu { background: linear-gradient(180deg, #F3E8FF 0%, #FFF8E7 100%); }
.poetry-menu .cat-filter {
  display: flex; gap: 5px; flex-wrap: wrap; justify-content: center;
  padding: 0 10px; margin: 4px 0; width: 100%; max-width: 460px;
}
.poetry-menu .cat-filter button {
  padding: 4px 12px; border-radius: 16px; border: 2px solid var(--purple);
  background: #fff; color: var(--purple); font-size: 13px;
  font-family: inherit; cursor: pointer; transition: all 0.2s; white-space: nowrap;
}
.poetry-menu .cat-filter button.active { background: var(--purple); color: #fff; }
.poetry-menu .cat-filter button:active { transform: scale(0.93); }
.poetry-menu .poem-list {
  display: flex; flex-direction: column; gap: 8px; width: 100%;
  max-width: 460px; padding: 0 10px 20px; margin-top: 4px;
  overflow-y: auto; max-height: calc(100vh - 280px); -webkit-overflow-scrolling: touch;
}
.poetry-menu .poem-list .poem-card {
  display: flex; align-items: center; gap: 16px; padding: 16px 20px;
  border-radius: 20px; border: none; background: #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08); cursor: pointer;
  transition: all 0.2s; width: 100%; text-align: left; font-family: inherit;
}
.poetry-menu .poem-list .poem-card:active { transform: scale(0.97); }
.poetry-menu .poem-list .poem-card .p-emoji { font-size: 40px; }
.poetry-menu .poem-list .poem-card .p-info { display: flex; flex-direction: column; gap: 2px; }
.poetry-menu .poem-list .poem-card .p-info .p-title { font-size: 22px; font-weight: bold; color: var(--text); }
.poetry-menu .poem-list .poem-card .p-info .p-author { font-size: 16px; color: #999; }
.poetry-menu .poem-list .poem-card .p-info .p-line { font-size: 15px; color: #bbb; margin-top: 2px; }
</style>
