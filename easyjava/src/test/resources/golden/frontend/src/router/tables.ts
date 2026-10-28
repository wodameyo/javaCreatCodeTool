import type { RouteRecordRaw } from 'vue-router'

export const tableRoutes: RouteRecordRaw[] = [
// <easyjava:table name="ts_user_info">
  {
    path: '/user-info',
    name: 'UserInfo',
    component: () => import('../views/user-info/index.vue'),
    meta: { title: '这是一个用户表' }
  },
// </easyjava:table>
]

export default tableRoutes
