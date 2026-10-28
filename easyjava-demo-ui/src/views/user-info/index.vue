<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteUserInfoById,
  loadUserInfoList
} from '../../api/user-info'
import type { UserInfo, UserInfoQuery } from '../../types/user-info'
import UserInfoForm from './components/UserInfoForm.vue'

const loading = ref(false)
const rows = ref<UserInfo[]>([])
const total = ref(0)
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formData = ref<UserInfo>({})

const listQuery = reactive<UserInfoQuery>({
  pageNo: 1,
  pageSize: 10,
  id: '',
  userNameFuzzy: '',
  productNameFuzzy: '',
  createTimeStart: '',
  createTimeEnd: '',
  createDateStart: '',
  createDateEnd: '',
  isDel: '',
})

async function loadData() {
  loading.value = true
  try {
    const page = await loadUserInfoList(listQuery)
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
  listQuery.id = ''
  listQuery.userNameFuzzy = ''
  listQuery.productNameFuzzy = ''
  listQuery.createTimeStart = ''
  listQuery.createTimeEnd = ''
  listQuery.createDateStart = ''
  listQuery.createDateEnd = ''
  listQuery.isDel = ''
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

function openEdit(row: UserInfo) {
  formMode.value = 'edit'
  formData.value = { ...row }
  formVisible.value = true
}

async function handleDelete(row: UserInfo) {
  try {
    await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteUserInfoById(row.id!)
  ElMessage.success('删除成功')
  loadData()
}

function handleSaved() {
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="listQuery" class="search-form" @submit.prevent>
        <el-form-item label="这是id">
          <el-input v-model="listQuery.id" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="listQuery.userNameFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="listQuery.productNameFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="listQuery.createTimeStart" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" clearable style="width: 140px" />
          <span class="range-sep">-</span>
          <el-date-picker v-model="listQuery.createTimeEnd" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="创建日期">
          <el-date-picker v-model="listQuery.createDateStart" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" clearable style="width: 140px" />
          <span class="range-sep">-</span>
          <el-date-picker v-model="listQuery.createDateEnd" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item label="用户是否存在，0：删除 1：正常">
          <el-input v-model="listQuery.isDel" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="openCreate">新增</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="这是id" width="120" />
        <el-table-column prop="userName" label="用户昵称" min-width="140" />
        <el-table-column prop="productName" label="商品名称" min-width="140" />
        <el-table-column prop="createTime" label="创建时间" min-width="140" />
        <el-table-column prop="createDate" label="创建日期" min-width="140" />
        <el-table-column prop="isDel" label="用户是否存在，0：删除 1：正常" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <UserInfoForm v-model:visible="formVisible" :mode="formMode" :model="formData" @saved="handleSaved" />
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
