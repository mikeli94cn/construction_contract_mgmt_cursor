import { createRouter, createWebHistory } from 'vue-router'
import { getAccessToken } from '../api/http'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('../layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'dashboard', component: () => import('../views/DashboardView.vue') },
        { path: 'users', name: 'users', component: () => import('../views/UsersView.vue') },
        { path: 'projects', name: 'projects', component: () => import('../views/ProjectsView.vue') },
        {
          path: 'projects/:id',
          name: 'project-detail',
          component: () => import('../views/ProjectDetailView.vue'),
        },
        { path: 'partners', name: 'partners', component: () => import('../views/PartnersView.vue') },
        { path: 'contracts', name: 'contracts', component: () => import('../views/ContractsView.vue') },
        {
          path: 'contracts/:id',
          name: 'contract-detail',
          component: () => import('../views/ContractDetailView.vue'),
        },
        { path: 'approvals', name: 'approvals', component: () => import('../views/ApprovalsView.vue') },
        {
          path: 'change-orders',
          name: 'change-orders',
          component: () => import('../views/ChangeOrdersView.vue'),
        },
        { path: 'payments', name: 'payments', component: () => import('../views/PaymentsView.vue') },
        { path: 'settlements', name: 'settlements', component: () => import('../views/SettlementsView.vue') },
        { path: 'reports', name: 'reports', component: () => import('../views/ReportsView.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  if (to.meta.public) {
    if (getAccessToken() && to.name === 'login') {
      return { name: 'dashboard' }
    }
    return true
  }
  if (!getAccessToken()) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router