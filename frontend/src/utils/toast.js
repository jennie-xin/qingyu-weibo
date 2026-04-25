import { ref } from 'vue'

const toasts = ref([])
let toastId = 0

export const useToast = () => {
  const show = (message, type = 'info', duration = 3000) => {
    const id = ++toastId
    toasts.value.push({ id, message, type })
    setTimeout(() => {
      toasts.value = toasts.value.filter(t => t.id !== id)
    }, duration)
  }

  const success = (message) => show(message, 'success')
  const error = (message) => show(message, 'error')
  const info = (message) => show(message, 'info')

  return { toasts, show, success, error, info }
}
