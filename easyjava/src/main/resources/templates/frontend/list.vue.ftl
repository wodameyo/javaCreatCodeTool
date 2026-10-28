<#assign pk = table.primaryColumn>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
<#if pk??>  delete${table.beanName}By${pk.upperPropertyName},
</#if>  load${table.beanName}List
} from '../../api/${table.moduleName}'
import type { ${table.beanName}, ${table.queryName} } from '../../types/${table.moduleName}'
import ${table.beanName}Form from './components/${table.beanName}Form.vue'

const loading = ref(false)
const rows = ref<${table.beanName}[]>([])
const total = ref(0)
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formData = ref<${table.beanName}>({})

const listQuery = reactive<${table.queryName}>({
  pageNo: 1,
  pageSize: 10,
<#list table.searchColumns as column>
<#if column.stringType>
  ${column.fuzzyPropertyName}: '',
<#elseif column.temporal>
  ${column.timeStartPropertyName}: '',
  ${column.timeEndPropertyName}: '',
<#else>
  ${column.propertyName}: '',
</#if>
</#list>
})

async function loadData() {
  loading.value = true
  try {
    const page = await load${table.beanName}List(listQuery)
    rows.value = page.list ?? []
    total.value = page.totalCount ?? 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  listQuery.pageNo = 1
  loadData()
}

function handleReset() {
  listQuery.pageNo = 1
<#list table.searchColumns as column>
<#if column.stringType>
  listQuery.${column.fuzzyPropertyName} = ''
<#elseif column.temporal>
  listQuery.${column.timeStartPropertyName} = ''
  listQuery.${column.timeEndPropertyName} = ''
<#else>
  listQuery.${column.propertyName} = ''
</#if>
</#list>
  loadData()
}

function handleSizeChange(size: number) {
  listQuery.pageSize = size
  listQuery.pageNo = 1
  loadData()
}

function openCreate() {
  formMode.value = 'create'
  formData.value = {}
  formVisible.value = true
}
<#if pk??>

function openEdit(row: ${table.beanName}) {
  formMode.value = 'edit'
  formData.value = { ...row }
  formVisible.value = true
}

async function handleDelete(row: ${table.beanName}) {
  try {
    await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await delete${table.beanName}By${pk.upperPropertyName}(row.${pk.propertyName}!)
  ElMessage.success('删除成功')
  loadData()
}
</#if>

function handleSaved() {
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="listQuery" class="search-form" @submit.prevent>
<#list table.searchColumns as column>
<#if column.stringType>
        <el-form-item label="${column.displayName?html}">
          <el-input v-model="listQuery.${column.fuzzyPropertyName}" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
<#elseif column.temporal>
        <el-form-item label="${column.displayName?html}">
          <el-date-picker v-model="listQuery.${column.timeStartPropertyName}" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" clearable style="width: 140px" />
          <span class="range-sep">-</span>
          <el-date-picker v-model="listQuery.${column.timeEndPropertyName}" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" clearable style="width: 140px" />
        </el-form-item>
<#elseif column.booleanType>
        <el-form-item label="${column.displayName?html}">
          <el-select v-model="listQuery.${column.propertyName}" placeholder="全部" clearable style="width: 120px">
            <el-option label="是" value="true" />
            <el-option label="否" value="false" />
          </el-select>
        </el-form-item>
<#else>
        <el-form-item label="${column.displayName?html}">
          <el-input v-model="listQuery.${column.propertyName}" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
</#if>
</#list>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" border stripe>
<#list table.frontendColumns as column>
<#if column.booleanType>
        <el-table-column label="${column.displayName?html}" width="110">
          <template #default="{ row }">
            <el-tag :type="row.${column.propertyName} ? 'success' : 'info'">{{ row.${column.propertyName} ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
<#else>
        <el-table-column prop="${column.propertyName}" label="${column.displayName?html}"<#if column.numericType> width="120"<#else> min-width="140"</#if><#if column.textType> show-overflow-tooltip</#if> />
</#if>
</#list>
<#if pk??>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
</#if>
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="listQuery.pageNo"
          v-model:page-size="listQuery.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <${table.beanName}Form v-model:visible="formVisible" :mode="formMode" :model="formData" @saved="handleSaved" />
  </div>
</template>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.range-sep {
  margin: 0 6px;
  color: #909399;
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
