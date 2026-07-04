import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export function login(email, password) {
  return api.post('/auth/login', { email, password })
}

export function register(name, email, password) {
  return api.post('/auth/register', { name, email, password })
}

export function getUser(id) {
  return api.get(`/user/${id}`)
}

export function updateDifficulty(id, level) {
  return api.put(`/user/${id}/difficulty`, { level })
}

export function updateStars(id, delta) {
  return api.put(`/user/${id}/stars`, { delta })
}

export function completeModule(userId, moduleKey) {
  return api.post('/progress/module', { userId, moduleKey })
}

export function getProgress(userId) {
  return api.get(`/progress/${userId}`)
}

export function unlockSticker(userId, stickerKey) {
  return api.post('/progress/sticker', { userId, stickerKey })
}

export function getPoems(category) {
  const params = category ? { category } : {}
  return api.get('/learn/poems', { params })
}

export function getPoem(id) {
  return api.get(`/learn/poems/${id}`)
}

export function getAlphabet() {
  return api.get('/learn/alphabet')
}

export function getColors() {
  return api.get('/learn/colors')
}

export function getShapes() {
  return api.get('/learn/shapes')
}

export function getComparePairs() {
  return api.get('/learn/compare')
}

export function getVocab(category) {
  return api.get(`/learn/vocab/${category}`)
}

export function speakTTS(text, language) {
  return api.post('/tts/speak', { text, language }, { responseType: 'blob' })
}

export default api
