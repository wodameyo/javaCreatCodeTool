<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { consoleApi } from '../api/console'
import { useConsoleStore } from '../stores/console'

const store = useConsoleStore()
const router = useRouter()

const error = ref('')
const loading = ref(false)

const validPackage = computed(() =>
  /^[A-Za-z_][A-Za-z0-9_]*(\.[A-Za-z_][A-Za-z0-9_]*)*$/.test(store.config.packageBase)
)

onMounted(async () => {
  if (!store.sessionId) {
    router.replace('/datasource')
    return
  }
  if (store.selectedTables.length === 0) {
    router.replace('/tables')
    return
  }
  if (store.configLoaded) {
    return
  }
  loading.value = true
  try {
    const defaults = await consoleApi.defaults()
    store.applyDefaults(defaults)
  } catch {
    store.configLoaded = true
  } finally {
    loading.value = false
  }
})

function next() {
  error.value = ''
  if (!validPackage.value) {
    error.value = '包名不合法，例如 com.example.demo'
    return
  }
  if (!store.config.output.backendDir || !store.config.output.resourcesDir) {
    error.value = 'Java 输出目录与资源目录不能为空'
    return
  }
  if (store.config.frontend.enabled && !store.config.output.frontendDir) {
    error.value = '启用前端生成时，前端目录不能为空'
    return
  }
  if (store.config.frontend.enabled && !store.config.frontend.typescript) {
    error.value = '当前仅支持 TypeScript 前端模板'
    return
  }
  router.push('/preview')
}
</script>

<template>
  <div class="page-head">
    <div>
      <h2>3. 生成配置</h2>
      <p>配置包名、输出目录、覆盖策略与后端代码风格，配置将转换为服务端的 GeneratorConfig。</p>
    </div>
    <el-tag type="info" effect="plain">已选 {{ store.selectedTables.length }} 张表</el-tag>
  </div>

  <el-alert v-if="error" :title="error" type="error" show-icon :closable="false" class="error-alert" />

  <el-card v-loading="loading" shadow="never" class="panel">
    <div class="section-title">基础信息</div>
    <el-row :gutter="16">
      <el-col :md="12" :sm="24">
        <el-form label-width="150px" label-position="left">
          <el-form-item label="基础包名" :error="validPackage ? '' : '包名不合法'">
            <el-input v-model="store.config.packageBase" placeholder="com.example.demo" />
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :md="12" :sm="24">
        <el-form label-width="150px" label-position="left">
          <el-form-item label="作者">
            <el-input v-model="store.config.author" placeholder="easyjava" />
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <el-divider />

    <div class="section-title">输出目录</div>
    <el-form label-width="150px" label-position="left">
      <el-form-item label="Java 源码目录">
        <el-input v-model="store.config.output.backendDir" placeholder="easyjava-demo/src/main/java" />
      </el-form-item>
      <el-form-item label="资源目录">
        <el-input v-model="store.config.output.resourcesDir" placeholder="easyjava-demo/src/main/resources" />
      </el-form-item>
      <el-form-item label="前端目录">
        <el-input v-model="store.config.output.frontendDir" :disabled="!store.config.frontend.enabled"
          placeholder="easyjava-demo-ui" />
      </el-form-item>
      <el-form-item label="覆盖策略">
        <el-radio-group v-model="store.config.output.overwritePolicy">
          <el-radio-button value="BACKUP">BACKUP 备份后覆盖</el-radio-button>
          <el-radio-button value="OVERWRITE">OVERWRITE 直接覆盖</el-radio-button>
          <el-radio-button value="SKIP">SKIP 跳过已存在</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="store.allowedOutputRoots.length" label="允许根目录">
        <div class="hint">{{ store.allowedOutputRoots.join('；') }}</div>
      </el-form-item>
    </el-form>

    <el-divider />

    <div class="section-title">后端选项</div>
    <el-form label-width="150px" label-position="left">
      <el-row :gutter="16">
        <el-col :md="8" :sm="24">
          <el-form-item label="Lombok">
            <el-switch v-model="store.config.backend.lombok" />
          </el-form-item>
        </el-col>
        <el-col :md="16" :sm="24">
          <el-form-item label="日期类型">
            <el-radio-group v-model="store.config.backend.dateType">
              <el-radio-button value="LEGACY">LEGACY（java.util.Date）</el-radio-button>
              <el-radio-button value="LOCAL">LOCAL（java.time）</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :md="8" :sm="24">
          <el-form-item label="批量操作事务">
            <el-switch v-model="store.config.backend.transaction" />
          </el-form-item>
        </el-col>
        <el-col :md="16" :sm="24">
          <el-form-item label="@JsonIgnore 字段">
            <el-select v-model="store.config.backend.ignoreJsonFields" multiple filterable allow-create
              default-first-option placeholder="输入属性名后回车" style="width: 100%">
              <el-option v-for="field in ['status', 'taotle']" :key="field" :label="field" :value="field" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <el-divider />

    <div class="section-title">前端选项</div>
    <el-form label-width="150px" label-position="left">
      <el-row :gutter="16">
        <el-col :md="8" :sm="24">
          <el-form-item label="生成前端">
            <el-switch v-model="store.config.frontend.enabled" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="仅生成脚手架">
            <el-switch v-model="store.config.frontend.scaffoldOnly"
              :disabled="!store.config.frontend.enabled" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="TypeScript">
            <el-switch v-model="store.config.frontend.typescript"
              :disabled="!store.config.frontend.enabled" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :md="8" :sm="24">
          <el-form-item label="包管理器">
            <el-select v-model="store.config.frontend.packageManager"
              :disabled="!store.config.frontend.enabled" style="width: 100%">
              <el-option label="npm" value="npm" />
              <el-option label="pnpm" value="pnpm" />
              <el-option label="yarn" value="yarn" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="接口前缀">
            <el-input v-model="store.config.frontend.apiBasePath"
              :disabled="!store.config.frontend.enabled" placeholder="/api" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="前端覆盖策略">
            <el-select v-model="store.config.frontend.overwritePolicy"
              :disabled="!store.config.frontend.enabled" style="width: 100%">
              <el-option label="BACKUP 备份后覆盖" value="BACKUP" />
              <el-option label="OVERWRITE 直接覆盖" value="OVERWRITE" />
              <el-option label="SKIP 跳过已存在" value="SKIP" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <div class="hint">脚手架文件（package.json、vite.config.ts 等）首次生成后默认跳过；路由表按标记块增量合并。</div>
    </el-form>

    <el-collapse>
      <el-collapse-item title="高级：子包名" name="packages">
        <el-row :gutter="16">
          <el-col v-for="key in ['po', 'query', 'vo', 'utils', 'enums', 'exception', 'mappers', 'service', 'serviceImpl', 'controller']"
            :key="key" :md="8" :sm="24">
            <el-form label-width="110px" label-position="left" style="margin-top: 6px">
              <el-form-item :label="key">
                <el-input v-model="store.config.packages[key as keyof typeof store.config.packages]" />
              </el-form-item>
            </el-form>
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
  </el-card>

  <div class="actions">
    <el-button @click="router.push('/tables')">上一步</el-button>
    <el-button type="primary" @click="next">预览生成结果</el-button>
  </div>
</template>
