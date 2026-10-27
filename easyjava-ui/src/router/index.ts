import { createRouter, createWebHashHistory } from 'vue-router'
import DatasourceView from '../views/DatasourceView.vue'
import TableSelectView from '../views/TableSelectView.vue'
import ConfigView from '../views/ConfigView.vue'
import PreviewView from '../views/PreviewView.vue'
import ResultView from '../views/ResultView.vue'
import AboutView from '../views/AboutView.vue'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', redirect: '/datasource' },
    { path: '/datasource', name: 'datasource', component: DatasourceView },
    { path: '/tables', name: 'tables', component: TableSelectView },
    { path: '/config', name: 'config', component: ConfigView },
    { path: '/preview', name: 'preview', component: PreviewView },
    { path: '/result', name: 'result', component: ResultView },
    { path: '/about', name: 'about', component: AboutView },
    { path: '/:pathMatch(.*)*', redirect: '/datasource' }
  ]
})

export default router
