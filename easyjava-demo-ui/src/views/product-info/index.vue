<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteProductInfoById,
  loadProductInfoList
} from '../../api/product-info'
import type { ProductInfo, ProductInfoQuery } from '../../types/product-info'
import ProductInfoForm from './components/ProductInfoForm.vue'

const loading = ref(false)
const rows = ref<ProductInfo[]>([])
const total = ref(0)
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formData = ref<ProductInfo>({})

const listQuery = reactive<ProductInfoQuery>({
  pageNo: 1,
  pageSize: 10,
  id: '',
  productNameFuzzy: '',
  productCodeFuzzy: '',
  categoryFuzzy: '',
  price: '',
  stock: '',
  onSale: '',
  descriptionFuzzy: '',
  createTimeStart: '',
  createTimeEnd: '',
})

async function loadData() {
  loading.value = true
  try {
    const page = await loadProductInfoList(listQuery)
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
  listQuery.productNameFuzzy = ''
  listQuery.productCodeFuzzy = ''
  listQuery.categoryFuzzy = ''
  listQuery.price = ''
  listQuery.stock = ''
  listQuery.onSale = ''
  listQuery.descriptionFuzzy = ''
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

function openEdit(row: ProductInfo) {
  formMode.value = 'edit'
  formData.value = { ...row }
  formVisible.value = true
}

async function handleDelete(row: ProductInfo) {
  try {
    await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  await deleteProductInfoById(row.id!)
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
        <el-form-item label="商品名称">
          <el-input v-model="listQuery.productNameFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="商品编码">
          <el-input v-model="listQuery.productCodeFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-input v-model="listQuery.categoryFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="售价">
          <el-input v-model="listQuery.price" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input v-model="listQuery.stock" placeholder="精确匹配" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="是否上架">
          <el-select v-model="listQuery.onSale" placeholder="全部" clearable style="width: 120px">
            <el-option label="是" value="true" />
            <el-option label="否" value="false" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="listQuery.descriptionFuzzy" placeholder="模糊匹配" clearable @keyup.enter="handleSearch" />
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
        <el-table-column prop="productName" label="商品名称" min-width="140" />
        <el-table-column prop="productCode" label="商品编码" min-width="140" />
        <el-table-column prop="category" label="商品分类" min-width="140" />
        <el-table-column prop="price" label="售价" width="120" />
        <el-table-column prop="stock" label="库存" width="120" />
        <el-table-column label="是否上架" width="110">
          <template #default="{ row }">
            <el-tag :type="row.onSale ? 'success' : 'info'">{{ row.onSale ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="商品描述" min-width="140" show-overflow-tooltip />
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

    <ProductInfoForm v-model:visible="formVisible" :mode="formMode" :model="formData" @saved="handleSaved" />
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
