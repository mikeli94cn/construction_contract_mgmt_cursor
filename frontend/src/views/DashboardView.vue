<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listMyApprovalTasks } from '../api/approvals'
import { listProjects } from '../api/projects'
import type { ApprovalTask, Project } from '../types'

const projects = ref<Project[]>([])
const tasks = ref<ApprovalTask[]>([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const [p, t] = await Promise.allSettled([listProjects(), listMyApprovalTasks()])
    if (p.status === 'fulfilled') projects.value = p.value
    if (t.status === 'fulfilled') tasks.value = t.value
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <el-row :gutter="16" v-loading="loading">
    <el-col :span="12">
      <el-card>
        <template #header>Projects</template>
        <el-statistic title="Total" :value="projects.length" />
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <template #header>My approval tasks</template>
        <el-statistic title="Pending" :value="tasks.filter((t) => t.status !== 'COMPLETED').length" />
      </el-card>
    </el-col>
  </el-row>
</template>