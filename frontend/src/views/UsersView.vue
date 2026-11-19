<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createUser, listUsers } from '../api/users'
import type { User } from '../types'

const rows = ref<User[]>([])
const loading = ref(false)
const dialog = ref(false)
const form = reactive({
  username: '',
  password: '',
  displayName: '',
  email: '',
  roleCodes: ['VIEWER'] as string[],
})

async function load() {
  loading.value = true
  try {
    rows.value = await listUsers()
  } catch {
    ElMessage.error('Could not load users')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  try {
    await createUser({
      username: form.username,
      password: form.password,
      displayName: form.displayName,
      email: form.email || undefined,
      roleCodes: form.roleCodes,
    })
    ElMessage.success('User created')
    dialog.value = false
    await load()
  } catch {
    ElMessage.error('Create failed')
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>Users</span>
        <el-button type="primary" @click="dialog = true">New user</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe>
      <el-table-column prop="username" label="Username" />
      <el-table-column prop="displayName" label="Name" />
      <el-table-column prop="email" label="Email" />
      <el-table-column prop="status" label="Status" />
      <el-table-column label="Roles">
        <template #default="{ row }">
          <el-tag v-for="r in row.roles" :key="r" size="small" class="tag">{{ r }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New user" width="420px">
    <el-form label-width="100px">
      <el-form-item label="Username"><el-input v-model="form.username" /></el-form-item>
      <el-form-item label="Password"><el-input v-model="form.password" type="password" show-password /></el-form-item>
      <el-form-item label="Display name"><el-input v-model="form.displayName" /></el-form-item>
      <el-form-item label="Email"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="Roles">
        <el-select v-model="form.roleCodes" multiple style="width: 100%">
          <el-option label="SYSTEM_ADMIN" value="SYSTEM_ADMIN" />
          <el-option label="PROJECT_ADMIN" value="PROJECT_ADMIN" />
          <el-option label="CONTRACT_MANAGER" value="CONTRACT_MANAGER" />
          <el-option label="FINANCE" value="FINANCE" />
          <el-option label="APPROVER" value="APPROVER" />
          <el-option label="VIEWER" value="VIEWER" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialog = false">Cancel</el-button>
      <el-button type="primary" @click="onCreate">Create</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tag {
  margin-right: 4px;
}
</style>