<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

const steps = [
  { path: '/datasource', label: '数据源' },
  { path: '/tables', label: '表选择' },
  { path: '/config', label: '生成配置' },
  { path: '/preview', label: '预览' },
  { path: '/result', label: '生成结果' }
]

function isActive(path: string) {
  return route.path === path
}
</script>

<template>
  <div class="app-shell">
    <header class="app-header">
      <div class="brand">
        <div class="logo">EJ</div>
        <div>
          <h1>easyjava 控制台</h1>
          <p>连接 MySQL · 选择表 · 配置 · 预览 · 生成 ZIP</p>
        </div>
      </div>
      <nav class="nav">
        <router-link v-for="step in steps" :key="step.path" :to="step.path" :class="{ 'router-link-active': isActive(step.path) }">
          {{ step.label }}
        </router-link>
        <router-link to="/about" :class="{ 'router-link-active': isActive('/about') }">关于</router-link>
      </nav>
    </header>

    <main class="app-main">
      <router-view />
    </main>

    <footer class="app-footer">
      服务仅绑定 127.0.0.1 · 数据库密码只保存在服务端内存会话中 · ZIP 下载链接一次性有效
    </footer>
  </div>
</template>
