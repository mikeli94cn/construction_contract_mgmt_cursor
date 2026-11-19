<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAuditEvents, getContractSummary } from '../api/reports'
import type { AuditEvent, ContractSummaryRow } from '../types'

const summary = ref<ContractSummaryRow[]>([])
const events = ref<AuditEvent[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const [s, e] = await Promise.all([getContractSummary(), getAuditEvents()])
    summary.value = s
    events.value = e
  } catch {
    ElMessage.error('Could not load reports')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <el-row :gutter="16" v-loading="loading">
    <el-col :span="12">
      <el-card>
        <template #header>Contract summary</template>
        <el-table :data="summary" stripe size="small">
          <el-table-column prop="contractNo" label="Contract" />
          <el-table-column prop="status" label="Status" />
          <el-table-column prop="committedAmount" label="Committed" />
          <el-table-column prop="paidAmount" label="Paid" />
          <el-table-column prop="remainingAmount" label="Remaining" />
        </el-table>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <template #header>Audit events</template>
        <el-table :data="events" stripe size="small">
          <el-table-column prop="createdAt" label="When" width="180" />
          <el-table-column prop="actorUserId" label="Actor" />
          <el-table-column prop="action" label="Action" />
          <el-table-column prop="entityType" label="Entity" />
          <el-table-column prop="entityId" label="Entity ID" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>
