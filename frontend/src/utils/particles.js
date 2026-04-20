const COLORS = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7']

export const createLikeParticles = (event) => {
  const btn = event.currentTarget
  const rect = btn.getBoundingClientRect()
  const x = rect.left + rect.width / 2
  const y = rect.top + rect.height / 2

  const particleCount = 12
  const container = document.createElement('div')
  container.style.cssText = `
    position: fixed;
    left: ${x}px;
    top: ${y}px;
    pointer-events: none;
    z-index: 9999;
  `
  document.body.appendChild(container)

  for (let i = 0; i < particleCount; i++) {
    const particle = document.createElement('span')
    const angle = (360 / particleCount) * i
    const distance = 20 + Math.random() * 30
    const size = 4 + Math.random() * 6
    const color = COLORS[Math.floor(Math.random() * COLORS.length)]
    const duration = 0.4 + Math.random() * 0.3

    particle.style.cssText = `
      position: absolute;
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: ${color};
      left: 0;
      top: 0;
      transform: translate(-50%, -50%);
      animation: particle-fly-${i} ${duration}s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
    `

    const radians = (angle * Math.PI) / 180
    const tx = Math.cos(radians) * distance
    const ty = Math.sin(radians) * distance

    const style = document.createElement('style')
    style.textContent = `
      @keyframes particle-fly-${i} {
        0% {
          transform: translate(-50%, -50%) scale(1);
          opacity: 1;
        }
        100% {
          transform: translate(calc(-50% + ${tx}px), calc(-50% + ${ty}px)) scale(0);
          opacity: 0;
        }
      }
    `
    document.head.appendChild(style)
    container.appendChild(particle)

    setTimeout(() => {
      style.remove()
    }, duration * 1000 + 100)
  }

  setTimeout(() => {
    container.remove()
  }, 800)
}

export const heartBeatAnimation = (element) => {
  element.style.animation = 'none'
  element.offsetHeight
  element.style.animation = 'heartBeat 0.4s ease'
}
