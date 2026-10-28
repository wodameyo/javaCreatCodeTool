<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { UserInfo } from '../../../types/user-info'
import { addUserInfo, updateUserInfoById } from '../../../api/user-info'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  model: UserInfo
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  saved: []
}>()

const formRef = ref<FormInstance>()
const form = ref<UserInfo>({})
const saving = ref(false)

const rules: FormRules = {
  userName: [
    { max: 32, message: '长度不能超过 32', trigger: 'blur' },
  ],
  productName: [
    { max: 64, message: '长度不能超过 64', trigger: 'blur' },
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
        form.value.createDate = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate())
        if (form.value.isDel === undefined) {
          form.value.isDel = false
        }
      }
    }
  }
)

const title = computed(() =>
  props.mode === 'create' ? '新增这是一个用户表' : '编辑这是一个用户表'
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
      await addUserInfo({ ...form.value })
    } else {
      await updateUserInfoById({ ...form.value }, form.value.id!)
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
      <el-form-item label="用户昵称" prop="userName">
        <el-input v-model="form.userName" maxlength="32" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="商品名称" prop="productName">
        <el-input v-model="form.productName" maxlength="64" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker v-model="form.createTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
      </el-form-item>
      <el-form-item label="创建日期" prop="createDate">
        <el-date-picker v-model="form.createDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
      </el-form-item>
      <el-form-item label="是否删除" prop="isDel">
        <el-switch v-model="form.isDel" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>
