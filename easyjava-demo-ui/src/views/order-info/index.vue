<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteOrderInfoById,
  loadOrderInfoList
} from '../../api/order-info'
import type { OrderInfo, OrderInfoQuery } from '../../types/order-info'
import OrderInfoForm from './components/OrderInfoForm.vue'

const loading = ref(false)
const rows = ref<OrderInfo[]>([])
const total = ref(0)
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formData = ref<OrderInfo>({})

const listQuery = reactive<OrderInfoQuery>({
  pageNo: 1,
  pageSize: 10,
  id: '',
  orderNoFuzzy: '',
  userId: '',
  productId: '',
  quantity: '',
  amount: '',
  orderStatus: '',
  remarkFuzzy: '',
  createTimeStart: '',
  createTimeEnd: '',
})

async function loadData() {
  loading.value = true
  try {
    const page = await loadOrderInfoList(listQuery)
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
  listQuery.orderNoFuzzy = ''
  listQuery.userId = ''
  listQuery.productId = ''
  listQuery.quantity = ''
  listQuery.amount = ''
  listQuery.orderStatus = ''
  listQuery.remarkFuzzy = ''
  listQuery.createTimeStart = ''
  listQuery.createTimeEnd = ''
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

function openEdit(row: OrderInfo) {
  formMode.value = 'edit'
  formData.value = { ...row }
  formVisible.value = true
}

async function handleDelete(row: OrderInfo) {
  try {
    await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteOrderInfoById(row.id!)
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
        <el-form-item label="主键">
          <el-input v-model="listQuery.id" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="listQuery.orderNoFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="listQuery.userId" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="商品ID">
          <el-input v-model="listQuery.productId" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="listQuery.quantity" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input v-model="listQuery.amount" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-input v-model="listQuery.orderStatus" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="listQuery.remarkFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="listQuery.createTimeStart" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" clearable style="width: 140px" />
          <span class="range-sep">-</span>
          <el-date-picker v-model="listQuery.createTimeEnd" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" clearable style="width: 140px" />
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
        <el-table-column prop="id" label="主键" width="120" />
        <el-table-column prop="orderNo" label="订单号" min-width="140" />
        <el-table-column prop="userId" label="用户ID" width="120" />
        <el-table-column prop="productId" label="商品ID" width="120" />
        <el-table-column prop="quantity" label="数量" width="120" />
        <el-table-column prop="amount" label="订单金额" width="120" />
        <el-table-column prop="orderStatus" label="订单状态" width="120" />
        <el-table-column prop="remark" label="备注" min-width="140" />
        <el-table-column prop="createTime" label="创建时间" min-width="140" />
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

    <OrderInfoForm v-model:visible="formVisible" :mode="formMode" :model="formData" @saved="handleSaved" />
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
