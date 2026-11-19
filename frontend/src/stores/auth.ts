import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '../api/auth'
import { fetchMe } from '../api/users'
import { clearTokens, getAccessToken } from '../api/http'
import type { User } from '../types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const loading = ref(false)

  const isAuthenticated = () => !!getAccessToken()

  async function login(username: string, password: string) {
    loading.value = true
    try {
      await authApi.login(username, password)
      user.value = await fetchMe()
    } finally {
      loading.value = false
    }
  }

  async function loadMe() {
    if (!getAccessToken()) {
      user.value = null
      return
    }
    loading.value = true
    try {
      user.value = await fetchMe()
    } catch {
      clearTokens()
      user.value = null
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    await authApi.logout()
    user.value = null
  }

  return { user, loading, isAuthenticated, login, loadMe, logout }
})