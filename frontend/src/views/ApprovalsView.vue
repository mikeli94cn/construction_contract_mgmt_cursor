<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { approveTask, listApprovalDefinitions, listMyApprovalTasks, rejectTask } from '../api/approvals'
import type { ApprovalDefinition, ApprovalTask } from '../types'

const tasks = ref<ApprovalTask[]>([])
const definitions = ref<ApprovalDefinition[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const [t, d] = await Promise.all([listMyApprovalTasks(), listApprovalDefinitions()])
    tasks.value = t
    definitions.value = d
  } catch {
    ElMessage.error('Could not load approvals')
  } finally {
    loading.value = false
  }
}

async function handleApprove(task: ApprovalTask) {
  try {
    const { value } = await ElMessageBox.prompt('Comment (optional)', 'Approve', {
      confirmButtonText: 'Approve',
      cancelButtonText: 'Cancel',
    })
    await approveTask(task.id, value || undefined)
    ElMessage.success('Approved')
    await load()
  } catch {
  }
}

async function handleReject(task: ApprovalTask) {
  try {
    const { value } = await ElMessageBox.prompt('Comment (optional)', 'Reject', {
      confirmButtonText: 'Reject',
      cancelButtonText: 'Cancel',
    })
    await rejectTask(task.id, value || undefined)
    ElMessage.success('Rejected')
    await load()
  } catch {
  }
}

onMounted(load)
</script>

<template>
  <el-row :gutter="16">
    <el-col :span="14">
      <el-card v-loading="loading">
        <template #header>My tasks</template>
        <el-table :data="tasks" stripe>
          <el-table-column prop="targetType" label="Entity" />
          <el-table-column prop="targetId" label="Target ID" width="280" />
          <el-table-column prop="status" label="Status" />
          <el-table-column label="Actions" width="180">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="handleApprove(row)">Approve</el-button>
              <el-button size="small" type="danger" @click="handleReject(row)">Reject</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
    <el-col :span="10">
      <el-card>
        <template #header>Definitions</template>
        <el-table :data="definitions" size="small">
          <el-table-column prop="code" label="Code" />
          <el-table-column prop="name" label="Name" />
          <el-table-column prop="targetType" label="Target type" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>