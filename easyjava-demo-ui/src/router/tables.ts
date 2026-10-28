import type { RouteRecordRaw } from 'vue-router'

export const tableRoutes: RouteRecordRaw[] = [
// <easyjava:table name="ts_order_info">
  {
    path: '/order-info',
    name: 'OrderInfo',
    component: () => import('../views/order-info/index.vue'),
    meta: { title: '订单信息表' }
  },
// </easyjava:table>
// <easyjava:table name="ts_product_info">
  {
    path: '/product-info',
    name: 'ProductInfo',
    component: () => import('../views/product-info/index.vue'),
    meta: { title: '商品信息表' }
  },
// </easyjava:table>
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
