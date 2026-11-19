<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const form = reactive({ username: '', password: '' })
const submitting = ref(false)

async function onSubmit() {
  submitting.value = true
  try {
    await auth.login(form.username, form.password)
    ElMessage.success('Logged in')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch {
    ElMessage.error('Login failed')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="card">
      <h2>CCM Sign In</h2>
      <p class="hint">Default admin: <code>admin</code> / <code>Admin@123</code></p>
      <el-form label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="Username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" native-type="submit" :loading="submitting" style="width: 100%">
          Login
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eef2f8;
}
.card {
  width: 380px;
}
.hint {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 16px;
}
</style>