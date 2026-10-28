<#assign pk = table.primaryColumn>
<#assign temporalCount = 0>
<#assign booleanCount = 0>
<#list table.formColumns as column>
<#if column.temporal><#assign temporalCount = temporalCount + 1></#if>
<#if column.booleanType><#assign booleanCount = booleanCount + 1></#if>
</#list>
<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { ${table.beanName} } from '../../../types/${table.moduleName}'
import { add${table.beanName}<#if pk??>, update${table.beanName}By${pk.upperPropertyName}</#if> } from '../../../api/${table.moduleName}'

const props = defineProps<{
  visible: boolean
  mode: 'create' | 'edit'
  model: ${table.beanName}
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  saved: []
}>()

const formRef = ref<FormInstance>()
const form = ref<${table.beanName}>({})
const saving = ref(false)

const rules: FormRules = {
<#list table.formColumns as column>
<#if column.required || (column.stringType && column.length??)>
  ${column.propertyName}: [
<#if column.required>    { required: true, message: '请输入${column.displayName?js_string}', trigger: 'blur' },
</#if><#if column.stringType && column.length??>    { max: ${column.length}, message: '长度不能超过 ${column.length}', trigger: 'blur' },
</#if>  ],
</#if>
</#list>
}

<#if temporalCount gt 0>
function pad(value: number) {
  return value < 10 ? '0' + value : String(value)
}
</#if>

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      form.value = { ...props.model }
<#if (temporalCount > 0) || (booleanCount > 0)>
      if (props.mode === 'create') {
<#if temporalCount gt 0>
        const now = new Date()
<#list table.formColumns as column><#if column.temporal>
<#switch column.frontendControl>
<#case "datetime">
        form.value.${column.propertyName} = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + ' ' + pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds())
<#break>
<#case "date">
        form.value.${column.propertyName} = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate())
<#break>
<#case "time">
        form.value.${column.propertyName} = pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds())
<#break>
</#switch>
</#if></#list>
</#if>
<#if booleanCount gt 0>
<#list table.formColumns as column><#if column.booleanType>
        if (form.value.${column.propertyName} === undefined) {
          form.value.${column.propertyName} = false
        }
</#if></#list>
</#if>
      }
</#if>
    }
  }
)

const title = computed(() =>
  props.mode === 'create' ? '新增${table.menuTitle?js_string}' : '编辑${table.menuTitle?js_string}'
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
<#if pk??>
    if (props.mode === 'create') {
      await add${table.beanName}({ ...form.value })
    } else {
      await update${table.beanName}By${pk.upperPropertyName}({ ...form.value }, form.value.${pk.propertyName}!)
    }
<#else>
    await add${table.beanName}({ ...form.value })
</#if>
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
<#list table.formColumns as column>
      <el-form-item label="${column.displayName?html}" prop="${column.propertyName}">
<#switch column.frontendControl>
<#case "switch">
        <el-switch v-model="form.${column.propertyName}" />
<#break>
<#case "select">
        <el-select v-model="form.${column.propertyName}" placeholder="请选择${column.displayName?html}" clearable style="width: 100%">
<#list column.enumOptions as option>
          <el-option label="${option?html}" value="${option?js_string}" />
</#list>
        </el-select>
<#break>
<#case "number">
        <el-input-number v-model="form.${column.propertyName}" :controls="false" style="width: 100%" />
<#break>
<#case "textarea">
        <el-input v-model="form.${column.propertyName}" type="textarea" :rows="3"<#if column.length??> maxlength="${column.length}" show-word-limit</#if> />
<#break>
<#case "date">
        <el-date-picker v-model="form.${column.propertyName}" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%" />
<#break>
<#case "datetime">
        <el-date-picker v-model="form.${column.propertyName}" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
<#break>
<#case "time">
        <el-date-picker v-model="form.${column.propertyName}" type="time" value-format="HH:mm:ss" placeholder="请选择时间" style="width: 100%" />
<#break>
<#default>
        <el-input v-model="form.${column.propertyName}"<#if column.length??> maxlength="${column.length}" show-word-limit</#if> clearable />
</#switch>
      </el-form-item>
</#list>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>
