import { createRouter, createWebHistory } from 'vue-router'
import { tableRoutes } from './tables'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...tableRoutes,
    {
      path: '/',
      redirect: tableRoutes.length > 0 ? tableRoutes[0].path : '/welcome'
    },
    {
      path: '/welcome',
      name: 'Welcome',
      component: () => import('../views/WelcomeView.vue'),
      meta: { title: '欢迎' }
    }
  ]
})

export default router
