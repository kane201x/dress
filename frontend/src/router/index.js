import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Welcome', component: () => import('../views/Welcome.vue') },
  { path: '/menu', name: 'Menu', component: () => import('../views/Menu.vue') },
  { path: '/math', name: 'MathMenu', component: () => import('../views/MathMenu.vue') },
  { path: '/math/numbers', name: 'Numbers', component: () => import('../views/math/RecognizeNumbers.vue') },
  { path: '/math/counting', name: 'Counting', component: () => import('../views/math/Counting.vue') },
  { path: '/math/shapes', name: 'Shapes', component: () => import('../views/math/Shapes.vue') },
  { path: '/math/compare', name: 'Compare', component: () => import('../views/math/Compare.vue') },
  { path: '/math/sorting', name: 'Sorting', component: () => import('../views/math/Sorting.vue') },
  { path: '/math/addition', name: 'Addition', component: () => import('../views/math/Addition.vue') },
  { path: '/english', name: 'EnglishMenu', component: () => import('../views/EnglishMenu.vue') },
  { path: '/english/alphabet', name: 'Alphabet', component: () => import('../views/english/Alphabet.vue') },
  { path: '/english/colors', name: 'Colors', component: () => import('../views/english/Colors.vue') },
  { path: '/english/animals', name: 'Animals', component: () => import('../views/english/Animals.vue') },
  { path: '/english/fruits', name: 'Fruits', component: () => import('../views/english/Fruits.vue') },
  { path: '/english/vehicles', name: 'Vehicles', component: () => import('../views/english/Vehicles.vue') },
  { path: '/english/body', name: 'Body', component: () => import('../views/english/BodyParts.vue') },
  { path: '/poetry', name: 'PoetryMenu', component: () => import('../views/poetry/PoetryMenu.vue') },
  { path: '/poetry/:id', name: 'PoemDetail', component: () => import('../views/poetry/PoemDetail.vue') },
  { path: '/rewards', name: 'Rewards', component: () => import('../views/Rewards.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
