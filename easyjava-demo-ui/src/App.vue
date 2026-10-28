<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { tableRoutes } from './router/tables'

const route = useRoute()
const activeMenu = computed(() => route.path)
const menus = tableRoutes.map((item) => ({
  path: item.path,
  title: String(item.meta?.title ?? item.path)
}))
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">easyjava-demo-ui</div>
      <el-menu :default-active="activeMenu" router>
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          {{ item.title }}
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>
</template>

<style>
html,
body,
#app {
  height: 100%;
  margin: 0;
}

body {
  font-family: 'Helvetica Neue', Arial, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: #f5f7fa;
}

.layout {
  height: 100%;
}

.aside {
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.brand {
  height: 56px;
  line-height: 56px;
  padding-left: 20px;
  font-weight: 600;
  font-size: 16px;
  color: #409eff;
}

.main {
  padding: 20px;
  box-sizing: border-box;
}
</style>
