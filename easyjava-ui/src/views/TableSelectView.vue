<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { consoleApi } from '../api/console'
import { useConsoleStore } from '../stores/console'
import type { TableInfo } from '../types'

const store = useConsoleStore()
const router = useRouter()

const tableRef = ref()
const loading = ref(false)
const error = ref('')
const keyword = ref('')
const prefixOptions = ['ts_', 'tb_', 't_']

const filteredTables = computed(() => {
  const value = keyword.value.trim().toLowerCase()
  if (!value) {
    return store.tables
  }
  return store.tables.filter((table) =>
    table.name.toLowerCase().includes(value) || (table.comment || '').toLowerCase().includes(value)
  )
})

onMounted(async () => {
  if (!store.sessionId) {
    router.replace('/datasource')
    return
  }
  await loadTables()
})

async function loadTables() {
  loading.value = true
  error.value = ''
  try {
    const response = await consoleApi.listTables(store.sessionId)
    store.tables = response.tables
    await nextTick()
    const selected = new Set(store.selectedTables)
    for (const row of store.tables) {
      if (selected.has(row.name) && !tableRef.value?.getSelectionRows?.()?.some((item: TableInfo) => item.name === row.name)) {
        tableRef.value?.toggleRowSelection(row, true)
      }
    }
    store.selectedTables = store.selectedTables.filter((name) =>
      store.tables.some((table) => table.name === name)
    )
  } catch (e: any) {
    if (e?.response?.status === 404) {
      store.clearSession()
      router.replace('/datasource')
      return
    }
    error.value = e?.response?.data?.message || e?.message || '读取表清单失败'
  } finally {
    loading.value = false
  }
}

function onSelectionChange() {
  const rows = (tableRef.value?.getSelectionRows?.() ?? []) as TableInfo[]
  store.selectedTables = rows.map((row) => row.name)
}

function selectAll() {
  tableRef.value?.toggleAllSelection()
}

function clearSelection() {
  tableRef.value?.clearSelection()
  store.selectedTables = []
}

function next() {
  if (store.selectedTables.length === 0) {
    ElMessage.warning('请至少选择一张表')
    return
  }
  router.push('/config')
}
</script>

<template>
  <div class="page-head">
    <div>
      <h2>2. 表选择</h2>
      <p>选择需要生成代码的表；行数为 information_schema 的估算值，字段数为真实统计。</p>
    </div>
    <el-tag type="info" effect="plain">
      已选 {{ store.selectedTables.length }} / 共 {{ store.tables.length }}
    </el-tag>
  </div>

  <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />

  <el-card shadow="never" class="panel">
    <div class="toolbar" style="margin-bottom: 12px">
      <el-input v-model="keyword" placeholder="按表名或注释搜索" clearable style="max-width: 280px" />
      <el-button @click="selectAll">全选当前列表</el-button>
      <el-button @click="clearSelection">清空选择</el-button>
      <el-button :loading="loading" @click="loadTables">刷新</el-button>
      <div class="toolbar-spacer" />
      <span class="hint">共 {{ filteredTables.length }} 张匹配</span>
    </div>

    <el-table ref="tableRef" v-loading="loading" :data="filteredTables" row-key="name" height="480"
      @selection-change="onSelectionChange">
      <el-table-column type="selection" width="48" reserve-selection />
      <el-table-column prop="name" label="表名" min-width="200" />
      <el-table-column prop="comment" label="注释" min-width="220" show-overflow-tooltip />
      <el-table-column prop="rowCount" label="行数（估算）" width="130" align="right" />
      <el-table-column prop="columnCount" label="字段数" width="100" align="right" />
    </el-table>
  </el-card>

  <el-card shadow="never" class="panel" style="margin-top: 16px">
    <div class="section-title">命名前缀</div>
    <p class="hint">生成 Bean 名称时会去掉这些表前缀，例如 ts_user_info → UserInfo。输入后回车可新增。</p>
    <el-select v-model="store.config.table.ignorePrefix" multiple filterable allow-create
      default-first-option placeholder="输入前缀后回车" style="width: 100%; max-width: 520px">
      <el-option v-for="prefix in prefixOptions" :key="prefix" :label="prefix" :value="prefix" />
    </el-select>
  </el-card>

  <div class="actions">
    <el-button @click="router.push('/datasource')">上一步</el-button>
    <el-button type="primary" @click="next">下一步：生成配置</el-button>
  </div>
</template>
