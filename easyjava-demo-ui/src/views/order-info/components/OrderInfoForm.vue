<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { OrderInfo } from '../../../types/order-info'
import { addOrderInfo, updateOrderInfoById } from '../../../api/order-info'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  model: OrderInfo
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  saved: []
}>()

const formRef = ref<FormInstance>()
const form = ref<OrderInfo>({})
const saving = ref(false)

const rules: FormRules = {
  orderNo: [
    { required: true, message: '请输入订单号', trigger: 'blur' },
    { max: 32, message: '长度不能超过 32', trigger: 'blur' },
  ],
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' },
  ],
  productId: [
    { required: true, message: '请输入商品ID', trigger: 'blur' },
  ],
  quantity: [
    { required: true, message: '请输入数量', trigger: 'blur' },
  ],
  amount: [
    { required: true, message: '请输入订单金额', trigger: 'blur' },
  ],
  orderStatus: [
    { required: true, message: '请输入订单状态', trigger: 'blur' },
  ],
  remark: [
    { max: 255, message: '长度不能超过 255', trigger: 'blur' },
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
      }
    }
  }
)

const title = computed(() =>
  props.mode === 'create' ? '新增订单信息表' : '编辑订单信息表'
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
      await addOrderInfo({ ...form.value })
    } else {
      await updateOrderInfoById({ ...form.value }, form.value.id!)
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
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="form.orderNo" maxlength="32" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="用户ID" prop="userId">
        <el-input-number v-model="form.userId" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="商品ID" prop="productId">
        <el-input-number v-model="form.productId" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number v-model="form.quantity" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="订单金额" prop="amount">
        <el-input-number v-model="form.amount" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-input-number v-model="form.orderStatus" :controls="false" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" maxlength="255" show-word-limit clearable />
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
