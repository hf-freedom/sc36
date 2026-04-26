import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import MemberList from './components/MemberList.vue'
import MemberDetail from './components/MemberDetail.vue'
import CardManagement from './components/CardManagement.vue'
import Consumption from './components/Consumption.vue'
import RefundManagement from './components/RefundManagement.vue'
import Reports from './components/Reports.vue'

const routes = [
  { path: '/', redirect: '/members' },
  { path: '/members', name: 'MemberList', component: MemberList },
  { path: '/members/:id', name: 'MemberDetail', component: MemberDetail },
  { path: '/cards', name: 'CardManagement', component: CardManagement },
  { path: '/consumption', name: 'Consumption', component: Consumption },
  { path: '/refunds', name: 'RefundManagement', component: RefundManagement },
  { path: '/reports', name: 'Reports', component: Reports }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
