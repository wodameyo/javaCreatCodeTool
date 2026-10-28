<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { ProductInfo } from '../../../types/product-info'
import { addProductInfo, updateProductInfoById } from '../../../api/product-info'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  model: ProductInfo
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  saved: []
}>()

const formRef = ref<FormInstance>()
const form = ref<ProductInfo>({})
const saving = ref(false)

const rules: FormRules = {
  productName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { max: 64, message: '长度不能超过 64', trigger: 'blur' },
  ],
  productCode: [
    { required: true, message: '请输入商品编码', trigger: 'blur' },
    { max: 32, message: '长度不能超过 32', trigger: 'blur' },
  ],
  category: [
    { required: true, message: '请输入商品分类', trigger: 'blur' },
  ],
  price: [
    { required: true, message: '请输入售价', trigger: 'blur' },
  ],
  stock: [
    { required: true, message: '请输入库存', trigger: 'blur' },
  ],
  onSale: [
    { required: true, message: '请输入是否上架', trigger: 'blur' },
  ],
  createTime: [
    { required: true, message: '请输入创建时间', trigger: 'blur' },
  ],
}

function pad(value: number) {
  return value < 10 ? '0' + value : String(value)
}

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      form.value = { ...props.model }
      if (props.mode === 'create') {
        const now = new Date()
        form.value.createTime = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds())
        if (form.value.onSale === undefined) {
          form.value.onSale = false
        }
      }
    }
  }
)

const title = computed(() =>
  props.mode === 'create' ? '新增商品信息表' : '编辑商品信息表'
)

async function submit() {
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  saving.value = true
  try {
    if (props.mode === 'create') {
      await addProductInfo({ ...form.value })
    } else {
      await updateProductInfoById({ ...form.value }, form.value.id!)
    }
    ElMessage.success('保存成功')
    emit('saved')
    emit('update:visible', false)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    width="640px"
    destroy-on-close
    @update:model-value="emit('update:visible', $event)"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="form.productName" maxlength="64" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="商品编码" prop="productCode">
        <el-input v-model="form.productCode" maxlength="32" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="商品分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择商品分类" clearable style="width: 100%">
          <el-option label="ELECTRONICS" value="ELECTRONICS" />
          <el-option label="BOOKS" value="BOOKS" />
          <el-option label="FOOD" value="FOOD" />
          <el-option label="OTHER" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item label="售价" prop="price">
        <el-input-number v-model="form.price" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input-number v-model="form.stock" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="是否上架" prop="onSale">
        <el-switch v-model="form.onSale" />
      </el-form-item>
      <el-form-item label="商品描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="form.createTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>
