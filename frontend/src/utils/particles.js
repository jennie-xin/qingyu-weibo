const COLORS = ['#F2A7B0', '#A8D8EA', '#C3B1E1', '#B5EAD7', '#FFEAA7', '#FFB7B2', '#FF6B6B']

export const createLikeParticles = (event) => {
  const btn = event.currentTarget
  const rect = btn.getBoundingClientRect()
  const x = rect.left + rect.width / 2
  const y = rect.top + rect.height / 2

  const particleCount = 18
  const container = document.createElement('div')
  container.style.cssText = `
    position: fixed;
    left: ${x}px;
    top: ${y}px;
    pointer-events: none;
    z-index: 9999;
  `
  document.body.appendChild(container)

  const styleEl = document.createElement('style')
  let keyframes = ''

  for (let i = 0; i < particleCount; i++) {
    const particle = document.createElement('span')
    const angle = (360 / particleCount) * i + (Math.random() - 0.5) * 20
    const distance = 35 + Math.random() * 50
    const size = 5 + Math.random() * 8
    const color = COLORS[Math.floor(Math.random() * COLORS.length)]
    const duration = 0.5 + Math.random() * 0.4

    particle.style.cssText = `
      position: absolute;
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: ${color};
      left: 0;
      top: 0;
      opacity: 0;
      transform: translate(-50%, -50%);
      animation: particle-fly-${i} ${duration}s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
    `

    const radians = (angle * Math.PI) / 180
    const tx = Math.cos(radians) * distance
    const ty = Math.sin(radians) * distance

    keyframes += `
      @keyframes particle-fly-${i} {
        0% {
          transform: translate(-50%, -50%) scale(1);
          opacity: 1;
        }
        60% {
          opacity: 0.8;
        }
        100% {
          transform: translate(calc(-50% + ${tx}px), calc(-50% + ${ty}px)) scale(0);
          opacity: 0;
        }
      }
    `
    container.appendChild(particle)
  }

  styleEl.textContent = keyframes
  document.head.appendChild(styleEl)

  setTimeout(() => {
    container.remove()
    styleEl.remove()
  }, 1000)
}
