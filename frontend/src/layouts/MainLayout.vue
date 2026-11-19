<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Box,
  Coin,
  DataAnalysis,
  Document,
  Folder,
  OfficeBuilding,
  SwitchButton,
  Tickets,
  User,
  List,
} from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const active = computed(() => route.path)

const menuItems = [
  { path: '/', title: 'Dashboard', icon: DataAnalysis },
  { path: '/users', title: 'Users', icon: User },
  { path: '/projects', title: 'Projects', icon: Folder },
  { path: '/partners', title: 'Partners', icon: OfficeBuilding },
  { path: '/contracts', title: 'Contracts', icon: Document },
  { path: '/approvals', title: 'Approvals', icon: Tickets },
  { path: '/change-orders', title: 'Change Orders', icon: List },
  { path: '/payments', title: 'Payments', icon: Coin },
  { path: '/settlements', title: 'Settlements', icon: Box },
  { path: '/reports', title: 'Reports', icon: DataAnalysis },
]

async function onLogout() {
  await auth.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">CCM</div>
      <el-menu :default-active="active" router class="menu">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="title">Construction Contract Management</span>
        <div class="user-bar">
          <span v-if="auth.user">{{ auth.user.displayName || auth.user.username }}</span>
          <el-button type="danger" link :icon="SwitchButton" @click="onLogout">Logout</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
}
.aside {
  background: #1f2d3d;
  color: #fff;
}
.brand {
  padding: 20px 16px;
  font-weight: 700;
  font-size: 1.25rem;
  letter-spacing: 0.05em;
}
.menu {
  border-right: none;
  background: transparent;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--el-border-color);
  background: #fff;
}
.title {
  font-weight: 600;
}
.user-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.main {
  background: #f5f7fa;
}
</style>