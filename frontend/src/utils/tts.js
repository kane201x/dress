let audioCtx = null
let audioCtxUnlocked = false
let currentSource = null
const audioCache = {}

function getAudioCtx() {
  if (!audioCtx) {
    audioCtx = new (window.AudioContext || window.webkitAudioContext)()
  }
  return audioCtx
}

export function unlockAudio() {
  if (audioCtxUnlocked) return
  try {
    const ctx = getAudioCtx()
    if (ctx.state === 'suspended') ctx.resume()
    audioCtxUnlocked = true
  } catch (e) {}
  if (window.speechSynthesis) {
    window.speechSynthesis.cancel()
    const u = new SpeechSynthesisUtterance(' ')
    u.volume = 0
    window.speechSynthesis.speak(u)
  }
}

function resumeCtx() {
  try {
    const ctx = getAudioCtx()
    if (ctx.state === 'suspended') ctx.resume()
  } catch (e) {}
}

function stopCurrent() {
  try {
    if (currentSource) {
      try { currentSource.stop() } catch (e) {}
      currentSource = null
    }
  } catch (e) {}
}

function playWithAudioContext(arrayBuffer) {
  return new Promise((resolve) => {
    try {
      const ctx = getAudioCtx()
      ctx.decodeAudioData(arrayBuffer.slice(0), (buffer) => {
        stopCurrent()
        const source = ctx.createBufferSource()
        source.buffer = buffer
        source.connect(ctx.destination)
        source.start(0)
        currentSource = source
        resolve(true)
      }, () => resolve(false))
    } catch (e) {
      resolve(false)
    }
  })
}

function playWithAudioElement(url) {
  return new Promise((resolve) => {
    try {
      const audio = new Audio()
      audio.volume = 1
      audio.src = url
      let done = false
      const finish = (ok) => { if (!done) { done = true; resolve(ok) } }
      audio.onended = () => finish(true)
      audio.onerror = () => finish(false)
      audio.play().then(() => {}).catch(() => finish(false))
      setTimeout(() => finish(true), 10000)
    } catch (e) {
      resolve(false)
    }
  })
}

async function playBackendTts(text, language) {
  const cacheKey = language + '|' + text
  if (audioCache[cacheKey] === 'loading') return false
  if (audioCache[cacheKey] && audioCache[cacheKey] !== 'loading') {
    const cached = audioCache[cacheKey]
    if (cached instanceof ArrayBuffer) {
      return await playWithAudioContext(cached)
    }
    return await playWithAudioElement(cached)
  }
  audioCache[cacheKey] = 'loading'
  try {
    const res = await fetch('/api/tts/speak', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(localStorage.getItem('token') ? { 'Authorization': 'Bearer ' + localStorage.getItem('token') } : {})
      },
      body: JSON.stringify({ text, language })
    })
    if (!res.ok) throw new Error('HTTP ' + res.status)
    const arrayBuffer = await res.arrayBuffer()
    if (!arrayBuffer || arrayBuffer.byteLength === 0) throw new Error('Empty audio')
    audioCache[cacheKey] = arrayBuffer
    const ok = await playWithAudioContext(arrayBuffer)
    if (ok) return true
    const blob = new Blob([arrayBuffer], { type: 'audio/mpeg' })
    const blobUrl = URL.createObjectURL(blob)
    audioCache[cacheKey] = blobUrl
    return await playWithAudioElement(blobUrl)
  } catch (e) {
    delete audioCache[cacheKey]
    const getUrl = '/api/tts/speak?text=' + encodeURIComponent(text) + '&language=' + encodeURIComponent(language)
    return await playWithAudioElement(getUrl)
  }
}

function androidSpeak(text, language) {
  if (window.AndroidTts) {
    try {
      window.AndroidTts.speakWithLanguage(text, language)
      return true
    } catch (e) {}
  }
  return false
}

function browserSpeak(text, lang, rate, pitch) {
  try {
    if (window.speechSynthesis) {
      window.speechSynthesis.cancel()
      const u = new SpeechSynthesisUtterance(text)
      u.lang = lang
      u.rate = rate
      u.pitch = pitch
      u.volume = 1
      window.speechSynthesis.speak(u)
    }
  } catch (e) {}
}

export async function speakCN(text) {
  if (androidSpeak(text, 'Chinese')) return
  resumeCtx()
  const ok = await playBackendTts(text, 'Chinese')
  if (!ok) {
    browserSpeak(text, 'zh-CN', 0.85, 1.3)
  }
}

export async function speakEN(text) {
  const txt = (text || '').trim()
  if (!txt) return
  if (androidSpeak(txt, 'English')) return
  resumeCtx()
  const ok = await playBackendTts(text, 'English')
  if (ok) return
  browserSpeak(txt, 'en-US', 0.9, 1.2)
}

export function playCelebrationSound() {
  try {
    const ctx = getAudioCtx()
    if (ctx.state === 'suspended') ctx.resume()
    ;[800, 1200].forEach((freq, i) => {
      const osc = ctx.createOscillator()
      const gain = ctx.createGain()
      osc.connect(gain)
      gain.connect(ctx.destination)
      osc.frequency.value = freq
      gain.gain.setValueAtTime(0.3, ctx.currentTime + i * 0.15)
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + i * 0.15 + 0.5)
      osc.start(ctx.currentTime + i * 0.15)
      osc.stop(ctx.currentTime + i * 0.15 + 0.5)
    })
  } catch (e) {}
}
