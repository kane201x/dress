import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', name: 'Welcome', component: () => import('../views/Welcome.vue') },
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
  { path: '/menu', name: 'Menu', component: () => import('../views/Menu.vue'), meta: { requiresAuth: true } },
  { path: '/math', name: 'MathMenu', component: () => import('../views/MathMenu.vue'), meta: { requiresAuth: true } },
  { path: '/math/numbers', name: 'Numbers', component: () => import('../views/math/RecognizeNumbers.vue'), meta: { requiresAuth: true } },
  { path: '/math/counting', name: 'Counting', component: () => import('../views/math/Counting.vue'), meta: { requiresAuth: true } },
  { path: '/math/shapes', name: 'Shapes', component: () => import('../views/math/Shapes.vue'), meta: { requiresAuth: true } },
  { path: '/math/compare', name: 'Compare', component: () => import('../views/math/Compare.vue'), meta: { requiresAuth: true } },
  { path: '/math/sorting', name: 'Sorting', component: () => import('../views/math/Sorting.vue'), meta: { requiresAuth: true } },
  { path: '/math/addition', name: 'Addition', component: () => import('../views/math/Addition.vue'), meta: { requiresAuth: true } },
  { path: '/english', name: 'EnglishMenu', component: () => import('../views/EnglishMenu.vue'), meta: { requiresAuth: true } },
  { path: '/english/alphabet', name: 'Alphabet', component: () => import('../views/english/Alphabet.vue'), meta: { requiresAuth: true } },
  { path: '/english/colors', name: 'Colors', component: () => import('../views/english/Colors.vue'), meta: { requiresAuth: true } },
  { path: '/english/animals', name: 'Animals', component: () => import('../views/english/Animals.vue'), meta: { requiresAuth: true } },
  { path: '/english/fruits', name: 'Fruits', component: () => import('../views/english/Fruits.vue'), meta: { requiresAuth: true } },
  { path: '/english/vehicles', name: 'Vehicles', component: () => import('../views/english/Vehicles.vue'), meta: { requiresAuth: true } },
  { path: '/english/body', name: 'Body', component: () => import('../views/english/BodyParts.vue'), meta: { requiresAuth: true } },
  { path: '/english/counting-en', name: 'CountingEN', component: () => import('../views/english/CountingEN.vue'), meta: { requiresAuth: true } },
  { path: '/english/phrases', name: 'Phrases', component: () => import('../views/english/Phrases.vue'), meta: { requiresAuth: true } },
  { path: '/english/songs', name: 'Songs', component: () => import('../views/english/Songs.vue'), meta: { requiresAuth: true } },
  { path: '/poetry', name: 'PoetryMenu', component: () => import('../views/poetry/PoetryMenu.vue'), meta: { requiresAuth: true } },
  { path: '/poetry/:id', name: 'PoemDetail', component: () => import('../views/poetry/PoemDetail.vue'), meta: { requiresAuth: true } },
  { path: '/rewards', name: 'Rewards', component: () => import('../views/Rewards.vue'), meta: { requiresAuth: true } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
