<template>
  <aside class="left-sidebar">
    <div class="sidebar-nav">
      <h4 class="sidebar-section-title">频道</h4>
      <ul class="nav-list">
        <li
          v-for="cat in categories"
          :key="cat.key"
          class="nav-item"
          :class="{ active: activeCategory === cat.key }"
          @click="selectCategory(cat)"
        >
          <span class="nav-item-label">{{ cat.label }}</span>
        </li>
      </ul>
    </div>

    <div class="sidebar-hot" v-if="hotSearches.length">
      <h4 class="sidebar-section-title">热搜榜</h4>
      <div class="hot-list">
        <a
          v-for="(item, index) in hotSearches"
          :key="item.keyword"
          class="hot-item"
          @click="goSearch(item.keyword)"
        >
          <span class="hot-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="hot-name">{{ item.keyword }}</span>
          <span class="hot-count">{{ item.search_count }}次</span>
        </a>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { searchApi } from '../api'

const emit = defineEmits(['select'])
const router = useRouter()

const categories = [
  { key: 'recommend', label: '推荐', topic: null },
  { key: 'travel', label: '文旅', topic: '文旅' },
  { key: 'life', label: '生活', topic: '生活' },
  { key: 'society', label: '社会', topic: '社会' },
]

const activeCategory = ref('recommend')
const hotSearches = ref([])

const selectCategory = (cat) => {
  activeCategory.value = cat.key
  emit('select', cat.topic)
}

const goSearch = (keyword) => {
  router.push(`/search?q=${encodeURIComponent(keyword)}`)
}

onMounted(async () => {
  try {
    const res = await searchApi.getHot()
    hotSearches.value = res.data.slice(0, 5)
  } catch (e) { /* silent */ }
})
</script>

<style>
.left-sidebar {
  position: sticky;
  top: 80px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-section-title {
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--color-text);
  margin-bottom: 8px;
  padding: 0 10px;
}

.sidebar-nav {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 14px 8px;
  box-shadow: var(--shadow-card);
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: var(--transition);
  font-size: 0.9rem;
  color: var(--color-text);
}

.nav-item:hover {
  background: var(--color-hover);
  color: var(--color-primary);
}

.nav-item.active {
  background: rgba(242, 167, 176, 0.12);
  color: var(--color-primary);
  font-weight: 600;
}

.sidebar-hot {
  background: var(--color-card);
  border-radius: var(--radius-md);
  padding: 14px 8px;
  box-shadow: var(--shadow-card);
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.hot-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  transition: var(--transition);
  font-size: 0.85rem;
  text-decoration: none;
  cursor: pointer;
}

.hot-item:hover {
  background: var(--color-hover);
}

.hot-rank {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  color: var(--color-text-light);
  font-family: var(--font-mono);
}

.hot-rank.top {
  color: var(--color-primary);
}

.hot-name {
  flex: 1;
  color: var(--color-text);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-item:hover .hot-name {
  color: var(--color-primary);
}

.hot-count {
  font-size: 0.7rem;
  color: var(--color-text-light);
  font-family: var(--font-mono);
}
</style>
