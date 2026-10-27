<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { consoleApi } from '../api/console'

const version = ref('1.0-SNAPSHOT')
const phase = ref('Phase 2')
const allowedRoots = ref<string[]>([])
const error = ref('')

onMounted(async () => {
  try {
    const defaults = await consoleApi.defaults()
    version.value = defaults.version
    phase.value = defaults.phase
    allowedRoots.value = defaults.allowedOutputRoots
  } catch (e: any) {
    error.value = e?.response?.data?.message || e?.message || '无法读取服务端配置'
  }
})
</script>

<template>
  <div class="page-head">
    <div>
      <h2>关于 easyjava 控制台</h2>
      <p>读取 MySQL 表结构，按 FreeMarker 模板生成 Spring Boot 3.2 + MyBatis 后端代码。</p>
    </div>
    <el-tag type="info" effect="plain">{{ phase }}</el-tag>
  </div>

  <el-alert v-if="error" :title="error" type="warning" show-icon :closable="false" class="error-alert" />

  <el-card shadow="never" class="panel">
    <div class="section-title">基本信息</div>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="版本">{{ version }}</el-descriptions-item>
      <el-descriptions-item label="当前阶段">{{ phase }}</el-descriptions-item>
      <el-descriptions-item label="支持的数据库">MySQL 8.x（元数据层预留其他数据库扩展）</el-descriptions-item>
      <el-descriptions-item label="生成引擎">easyjava core · FreeMarker 模板</el-descriptions-item>
      <el-descriptions-item label="允许输出根目录" :span="2">
        <span class="code-path">{{ allowedRoots.join('；') || '启动目录' }}</span>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>

  <el-card shadow="never" class="panel" style="margin-top: 16px">
    <div class="section-title">REST API</div>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="POST /api/console/datasource/test">测试连接并建立会话</el-descriptions-item>
      <el-descriptions-item label="POST /api/console/datasource/tables">读取表清单（会话级连接参数）</el-descriptions-item>
      <el-descriptions-item label="POST /api/console/datasource/columns">读取指定表的列与索引</el-descriptions-item>
      <el-descriptions-item label="POST /api/console/generate/preview">返回 GenerationPlan（路径、内容、是否已存在）</el-descriptions-item>
      <el-descriptions-item label="POST /api/console/generate">执行生成，返回摘要与 downloadToken</el-descriptions-item>
      <el-descriptions-item label="GET /api/console/generate/download/{token}">下载一次性 ZIP</el-descriptions-item>
      <el-descriptions-item label="POST /api/console/generate/plan/diff">与磁盘文件比对</el-descriptions-item>
      <el-descriptions-item label="GET /api/console/config/defaults">默认配置与安全边界</el-descriptions-item>
      <el-descriptions-item label="GET /api/console/health">健康检查</el-descriptions-item>
    </el-descriptions>
  </el-card>

  <el-card shadow="never" class="panel" style="margin-top: 16px">
    <div class="section-title">安全约束</div>
    <ul class="hint">
      <li>服务默认只绑定 127.0.0.1，可通过 SERVER_PORT 调整端口。</li>
      <li>数据库密码只保存在当前进程内存，不写日志、不回显，响应中的 JDBC URL 已脱敏。</li>
      <li>写入磁盘前会校验目标路径必须位于允许根目录内，防止路径穿越。</li>
      <li>ZIP 下载 token 短时有效且只能使用一次。</li>
    </ul>
  </el-card>
</template>
