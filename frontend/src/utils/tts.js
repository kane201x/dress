let speakAudio = null
let audioCtxUnlocked = false
let audioCtx = null

function getAudioCtx() {
  if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)()
  return audioCtx
}

export function unlockAudio() {
  if (audioCtxUnlocked) return
  try {
    const ctx = getAudioCtx()
    if (ctx.state === 'suspended') ctx.resume()
    audioCtxUnlocked = true
  } catch (e) {}
}

export function speakCN(text) {
  try {
    window.speechSynthesis.cancel()
    const u = new SpeechSynthesisUtterance(text)
    u.lang = 'zh-CN'
    u.rate = 0.85
    u.pitch = 1.3
    u.volume = 1
    window.speechSynthesis.speak(u)
  } catch (e) {}
}

export function speakEN(text) {
  const txt = (text || '').trim()
  if (!txt) return
  try {
    if (speakAudio) { speakAudio = null }
    const audio = new Audio()
    audio.volume = 1
    audio.src = 'https://translate.googleapis.com/translate_tts?ie=UTF-8&client=gtx&tl=en&q=' + encodeURIComponent(txt)
    speakAudio = audio
    audio.play().catch(() => {
      try {
        window.speechSynthesis.cancel()
        const u = new SpeechSynthesisUtterance(txt)
        u.lang = 'en-US'
        u.rate = 0.9
        u.pitch = 1.2
        u.volume = 1
        window.speechSynthesis.speak(u)
      } catch (e2) {}
    })
  } catch (e) {
    try {
      window.speechSynthesis.cancel()
      const u = new SpeechSynthesisUtterance(txt)
      u.lang = 'en-US'
      u.rate = 0.9
      u.pitch = 1.2
      u.volume = 1
      window.speechSynthesis.speak(u)
    } catch (e2) {}
  }
}

export function playCelebrationSound() {
  try {
    const ctx = new (window.AudioContext || window.webkitAudioContext)()
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
