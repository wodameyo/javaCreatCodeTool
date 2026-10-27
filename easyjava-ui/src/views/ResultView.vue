<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { consoleApi } from '../api/console'
import { useConsoleStore } from '../stores/console'

const store = useConsoleStore()
const router = useRouter()

const result = computed(() => store.result)

const lists = computed(() => {
  if (!result.value) {
    return []
  }
  return [
    { key: 'created', label: '新增', type: 'success', items: result.value.summary.created },
    { key: 'overwritten', label: '覆盖', type: 'warning', items: result.value.summary.overwritten },
    { key: 'backedUp', label: '备份', type: 'info', items: result.value.summary.backedUp },
    { key: 'skipped', label: '跳过', type: 'info', items: result.value.summary.skipped }
  ]
})

function download() {
  if (!result.value) {
    return
  }
  window.location.href = consoleApi.downloadUrl(result.value.token)
}

function restart() {
  store.reset()
  router.push('/datasource')
}
</script>

<template>
  <div class="page-head">
    <div>
      <h2>5. 生成结果</h2>
      <p>本次生成的统计与文件清单。ZIP 下载链接一次性有效，失效后可返回预览重新生成。</p>
    </div>
    <el-tag v-if="result" :type="result.summary.dryRun ? 'warning' : 'success'" effect="plain">
      {{ result.summary.dryRun ? '仅打包 ZIP' : '已写入磁盘' }}
    </el-tag>
  </div>

  <template v-if="result">
    <el-alert v-if="result.summary.dryRun" type="warning" show-icon :closable="false" class="error-alert"
      title="本次未写入磁盘：文件已打包为 ZIP，可按需下载后手动放入目标工程。" />
    <el-alert v-else type="success" show-icon :closable="false" class="error-alert"
      title="已按覆盖策略写入目标目录，同时生成了 ZIP 备份。" />

    <div class="stat-grid">
      <div class="stat-card success">
        <div class="label">新增</div>
        <div class="value">{{ result.summary.created.length }}</div>
      </div>
      <div class="stat-card warning">
        <div class="label">覆盖</div>
        <div class="value">{{ result.summary.overwritten.length }}</div>
      </div>
      <div class="stat-card">
        <div class="label">备份</div>
        <div class="value">{{ result.summary.backedUp.length }}</div>
      </div>
      <div class="stat-card">
        <div class="label">跳过</div>
        <div class="value">{{ result.summary.skipped.length }}</div>
      </div>
      <div class="stat-card info">
        <div class="label">文件总数</div>
        <div class="value">{{ result.fileCount }}</div>
      </div>
      <div class="stat-card">
        <div class="label">耗时</div>
        <div class="value">{{ result.elapsedMs }}<span style="font-size: 12px"> ms</span></div>
      </div>
    </div>

    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <div>
          <div class="section-title" style="margin-bottom: 4px">下载 ZIP</div>
          <div class="hint">{{ result.zipFileName }}</div>
        </div>
        <div class="toolbar-spacer" />
        <el-button type="primary" @click="download">下载生成的代码</el-button>
      </div>
    </el-card>

    <el-card shadow="never" class="panel" style="margin-top: 16px">
      <el-collapse>
        <el-collapse-item v-for="list in lists" :key="list.key" :name="list.key">
          <template #title>
            <span style="display: flex; align-items: center; gap: 8px">
              {{ list.label }}
              <el-tag size="small" :type="list.type as any">{{ list.items.length }}</el-tag>
            </span>
          </template>
          <div v-if="list.items.length === 0" class="hint">无文件</div>
          <template v-else>
            <div class="list-scroll">
              <div v-for="item in list.items" :key="item" class="path-row">{{ item }}</div>
            </div>
          </template>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <div class="actions">
      <el-button @click="router.push('/preview')">返回预览</el-button>
      <el-button @click="restart">重新开始</el-button>
    </div>
  </template>

  <el-card v-else shadow="never" class="panel">
    <el-empty description="还没有生成结果">
      <el-button type="primary" @click="router.push('/preview')">去预览并生成</el-button>
    </el-empty>
  </el-card>
</template>

<style scoped>
.list-scroll {
  max-height: 260px;
  overflow: auto;
}
</style>
