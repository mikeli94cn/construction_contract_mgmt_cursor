<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createProject, listProjects } from '../api/projects'
import type { Project } from '../types'

const router = useRouter()
const rows = ref<Project[]>([])
const loading = ref(false)
const dialog = ref(false)
const form = reactive({ code: '', name: '', description: '' })

async function load() {
  loading.value = true
  try {
    rows.value = await listProjects()
  } catch {
    ElMessage.error('Could not load projects')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  try {
    const p = await createProject(form)
    ElMessage.success('Project created')
    dialog.value = false
    router.push({ name: 'project-detail', params: { id: p.id } })
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
        <span>Projects</span>
        <el-button type="primary" @click="dialog = true">New project</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe @row-click="(row: Project) => router.push(`/projects/${row.id}`)">
      <el-table-column prop="code" label="Code" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="status" label="Status" />
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New project">
    <el-form label-width="100px">
      <el-form-item label="Code"><el-input v-model="form.code" /></el-form-item>
      <el-form-item label="Name"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="Description"><el-input v-model="form.description" type="textarea" /></el-form-item>
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
:deep(.el-table__row) {
  cursor: pointer;
}
</style>