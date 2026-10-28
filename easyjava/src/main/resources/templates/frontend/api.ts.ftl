import { request } from '../utils/request'
import type { ${table.beanName}, ${table.queryName}, ${table.beanName}Page } from '../types/${table.moduleName}'

const BASE = '/${table.requestMapping}'

export function load${table.beanName}List(params: ${table.queryName}) {
  return request<${table.beanName}Page>({ url: BASE + '/loadDataList', method: 'get', params })
}

export function add${table.beanName}(data: ${table.beanName}) {
  return request<null>({ url: BASE + '/add', method: 'post', data })
}
<#if table.primaryColumn??>
<#assign pk = table.primaryColumn>

export function get${table.beanName}By${pk.upperPropertyName}(${pk.propertyName}: ${pk.tsType}) {
  return request<${table.beanName} | null>({
    url: BASE + '/get${table.beanName}By${pk.upperPropertyName}',
    method: 'get',
    params: { ${pk.propertyName} }
  })
}

export function update${table.beanName}By${pk.upperPropertyName}(data: ${table.beanName}, ${pk.propertyName}: ${pk.tsType}) {
  return request<null>({
    url: BASE + '/update${table.beanName}By${pk.upperPropertyName}',
    method: 'post',
    data,
    params: { ${pk.propertyName} }
  })
}

export function delete${table.beanName}By${pk.upperPropertyName}(${pk.propertyName}: ${pk.tsType}) {
  return request<null>({
    url: BASE + '/delete${table.beanName}By${pk.upperPropertyName}',
    method: 'post',
    params: { ${pk.propertyName} }
  })
}
</#if>
