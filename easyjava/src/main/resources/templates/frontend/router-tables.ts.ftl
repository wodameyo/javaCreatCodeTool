import type { RouteRecordRaw } from 'vue-router'

export const tableRoutes: RouteRecordRaw[] = [
<#if business><#list tables as table>// <easyjava:table name="${table.tableName}">
  {
    path: '${table.routePath}',
    name: '${table.beanName}',
    component: () => import('../views/${table.moduleName}/index.vue'),
    meta: { title: '${table.menuTitle?js_string}' }
  },
// </easyjava:table>
</#list></#if>]

export default tableRoutes
