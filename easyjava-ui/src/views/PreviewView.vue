<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { consoleApi } from '../api/console'
import { useConsoleStore } from '../stores/console'
import type { FilePreview } from '../types'

const store = useConsoleStore()
const router = useRouter()

const loading = ref(false)
const generating = ref(false)
const error = ref('')
const current = ref<FilePreview | null>(null)
const activeTab = ref('generated')
const writeToDisk = ref(false)

const groups = computed(() => {
  const map: Record<string, FilePreview[]> = {}
  for (const file of store.preview?.files ?? []) {
    if (!map[file.kind]) {
      map[file.kind] = []
    }
    map[file.kind].push(file)
  }
  return Object.entries(map).map(([kind, files]) => ({ kind, label: kindLabel(kind), files }))
})

const changedCount = computed(() =>
  (store.preview?.files ?? []).filter((file) => file.changed).length
)

const existingCount = computed(() => store.preview?.existingCount ?? 0)

onMounted(loadPreview)

async function loadPreview() {
  if (!store.sessionId) {
    router.replace('/datasource')
    return
  }
  if (store.selectedTables.length === 0) {
    router.replace('/tables')
    return
  }
  loading.value = true
  error.value = ''
  try {
    const response = await consoleApi.preview({
      sessionId: store.sessionId,
      tables: store.selectedTables,
      config: store.config
    })
    store.preview = response
    current.value = response.files.find((file) => file.changed) ?? response.files[0] ?? null
    activeTab.value = 'generated'
  } catch (e: any) {
    if (e?.response?.status === 404) {
      store.clearSession()
      router.replace('/datasource')
      return
    }
    error.value = e?.response?.data?.message || e?.message || '预览失败'
  } finally {
    loading.value = false
  }
}

function selectFile(file: FilePreview) {
  current.value = file
  activeTab.value = 'generated'
}

async function generate() {
  if (!store.preview) {
    return
  }
  generating.value = true
  error.value = ''
  try {
    const response = await consoleApi.generate({
      sessionId: store.sessionId,
      tables: store.selectedTables,
      config: store.config,
      writeToDisk: writeToDisk.value
    })
    store.result = response
    router.push('/result')
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || '生成失败'
  } finally {
    generating.value = false
  }
}

function kindLabel(kind: string) {
  if (kind === 'JAVA') {
    return 'Java 源码'
  }
  if (kind === 'RESOURCES') {
    return 'Mapper XML'
  }
  if (kind === 'FRONTEND') {
    return '前端代码'
  }
  return kind
}

function baseName(path: string) {
  const parts = path.split('/')
  return parts[parts.length - 1] || path
}
</script>

<template>
  <div class="page-head">
    <div>
      <h2>4. 预览</h2>
      <p>左侧按类型分组浏览生成文件，右侧查看生成内容与磁盘现有文件；确认后可下载 ZIP 或写入目录。</p>
    </div>
    <el-tag v-if="store.preview" type="info" effect="plain">
      覆盖策略 {{ store.preview.overwritePolicy }}
    </el-tag>
  </div>

  <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />

  <div v-if="store.preview" class="stat-grid">
    <div class="stat-card info">
      <div class="label">文件总数</div>
      <div class="value">{{ store.preview.total }}</div>
    </div>
    <div class="stat-card success">
      <div class="label">将新增</div>
      <div class="value">{{ store.preview.total - existingCount }}</div>
    </div>
    <div class="stat-card warning">
      <div class="label">已存在</div>
      <div class="value">{{ existingCount }}</div>
    </div>
    <div class="stat-card">
      <div class="label">内容有变更</div>
      <div class="value">{{ changedCount }}</div>
    </div>
    <div class="stat-card">
      <div class="label">选中表</div>
      <div class="value">{{ store.selectedTables.length }}</div>
    </div>
  </div>

  <div v-loading="loading">
    <div v-if="store.preview" class="preview-layout">
      <el-card shadow="never" class="panel">
        <div class="toolbar" style="margin-bottom: 10px">
          <span class="section-title" style="margin: 0">文件树</span>
          <div class="toolbar-spacer" />
          <el-tag size="small" type="info">{{ store.preview.files.length }}</el-tag>
        </div>
        <div class="file-tree">
          <div v-for="group in groups" :key="group.kind" class="file-group">
            <div class="group-title">
              <span>{{ group.label }}</span>
              <span>{{ group.files.length }}</span>
            </div>
            <button v-for="file in group.files" :key="file.logicalPath" class="file-item"
              :class="{ active: current?.logicalPath === file.logicalPath }" @click="selectFile(file)">
              <span class="file-name">{{ baseName(file.logicalPath) }}</span>
              <el-tag v-if="!file.exists" size="small" type="success">新增</el-tag>
              <el-tag v-else-if="file.changed" size="small" type="warning">有变更</el-tag>
              <el-tag v-else size="small" type="info">无变化</el-tag>
            </button>
          </div>
        </div>
      </el-card>

      <el-card shadow="never" class="panel">
        <template v-if="current">
          <div class="preview-header">
            <div class="preview-path">
              <div class="code-path">{{ current.logicalPath }}</div>
              <div class="code-path">{{ current.targetPath }}</div>
            </div>
            <div class="toolbar">
              <el-tag size="small">{{ kindLabel(current.kind) }}</el-tag>
              <el-tag v-if="!current.exists" size="small" type="success">新增</el-tag>
              <el-tag v-else-if="current.changed" size="small" type="warning">有变更</el-tag>
              <el-tag v-else size="small" type="info">无变化</el-tag>
            </div>
          </div>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="生成内容" name="generated">
              <pre class="code-block">{{ current.content }}</pre>
            </el-tab-pane>
            <el-tab-pane v-if="current.exists" label="现有文件" name="existing">
              <pre v-if="current.existingContent" class="code-block">{{ current.existingContent }}</pre>
              <el-empty v-else description="现有文件过大或无法读取" />
            </el-tab-pane>
          </el-tabs>

          <el-divider />

          <div class="toolbar">
            <span class="hint">覆盖策略</span>
            <el-select v-model="store.config.output.overwritePolicy" style="width: 220px">
              <el-option label="BACKUP 备份后覆盖" value="BACKUP" />
              <el-option label="OVERWRITE 直接覆盖" value="OVERWRITE" />
              <el-option label="SKIP 跳过已存在" value="SKIP" />
            </el-select>
            <el-button :loading="loading" @click="loadPreview">刷新预览</el-button>
            <div class="toolbar-spacer" />
            <el-switch v-model="writeToDisk" active-text="写入磁盘" inactive-text="仅打包 ZIP" />
            <el-button type="primary" :loading="generating" @click="generate">开始生成</el-button>
          </div>
        </template>

        <el-empty v-else description="暂无预览内容" />
      </el-card>
    </div>

    <el-empty v-else-if="!loading && !error" description="暂无预览数据，请返回配置页重新提交" />
  </div>

  <div class="actions">
    <el-button @click="router.push('/config')">上一步</el-button>
  </div>
</template>

<style scoped>
.preview-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 8px;
}

.preview-path {
  min-width: 0;
}

.preview-path .code-path + .code-path {
  margin-top: 2px;
  color: #9ca3af;
}
</style>
