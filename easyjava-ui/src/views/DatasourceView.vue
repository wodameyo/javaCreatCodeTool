<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { consoleApi } from '../api/console'
import { useConsoleStore } from '../stores/console'

const store = useConsoleStore()
const router = useRouter()

const form = reactive({
  host: store.connection.host,
  port: store.connection.port,
  database: store.connection.database,
  username: store.connection.username,
  password: ''
})

const loading = ref(false)
const error = ref('')
const connected = computed(() => Boolean(store.sessionId))

const jdbcPreview = computed(() => {
  return `jdbc:mysql://${form.host}:${form.port}/${form.database}`
    + '?useUnicode=true&characterEncoding=UTF-8&useSSL=false'
    + '&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
})

async function testConnection() {
  error.value = ''
  if (!form.host || !form.database || !form.username) {
    error.value = '主机、数据库名和用户名不能为空'
    return
  }
  loading.value = true
  try {
    const response = await consoleApi.testConnection({ ...form })
    store.connection = {
      host: form.host,
      port: form.port,
      database: form.database,
      username: form.username
    }
    store.saveConnection()
    store.setSession(response)
    ElMessage.success('连接成功')
  } catch (e: any) {
    store.clearSession()
    error.value = e?.response?.data?.message || e?.message || '连接失败'
  } finally {
    loading.value = false
  }
}

function next() {
  if (!store.sessionId) {
    error.value = '请先测试连接'
    return
  }
  router.push('/tables')
}
</script>

<template>
  <div class="page-head">
    <div>
      <h2>1. 数据源</h2>
      <p>输入 MySQL 连接信息并测试连接。密码只会发送到本机服务端，并保存在内存会话中。</p>
    </div>
    <el-tag v-if="connected" type="success" effect="plain">会话已建立</el-tag>
  </div>

  <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />

  <el-card shadow="never" class="panel">
    <el-form label-width="130px" @submit.prevent>
      <el-row :gutter="16">
        <el-col :md="12" :sm="24">
          <el-form-item label="主机">
            <el-input v-model="form.host" placeholder="localhost" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item label="端口">
            <el-input-number v-model="form.port" :min="1" :max="65535" controls-position="right"
              style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="16">
        <el-col :md="12" :sm="24">
          <el-form-item label="数据库名">
            <el-input v-model="form.database" placeholder="test" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="root" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="密码">
        <el-input v-model="form.password" type="password" show-password autocomplete="off"
          placeholder="仅用于本次连接，不会写入配置文件" />
      </el-form-item>

      <el-form-item label="JDBC URL">
        <div class="jdbc-preview">{{ jdbcPreview }}</div>
      </el-form-item>

      <el-form-item>
        <div class="toolbar">
          <el-button type="primary" :loading="loading" @click="testConnection">测试连接</el-button>
          <el-button :disabled="!connected" @click="next">下一步：选择表</el-button>
        </div>
      </el-form-item>
    </el-form>

    <el-alert v-if="connected && store.productName" type="success" :closable="false" show-icon
      :title="`连接成功：${store.productName} ${store.productVersion}；会话有效期 ${store.sessionTtlMinutes} 分钟`" />
  </el-card>
</template>

<style scoped>
.jdbc-preview {
  width: 100%;
  padding: 6px 12px;
  border-radius: 6px;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  font-family: 'JetBrains Mono', Consolas, monospace;
  font-size: 12px;
  color: #475569;
  word-break: break-all;
}
</style>
