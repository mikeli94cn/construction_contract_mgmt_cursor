<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listContracts } from '../api/contracts'
import type { Contract } from '../types'

const router = useRouter()
const contracts = ref<Contract[]>([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    contracts.value = await listContracts()
  } catch {
    ElMessage.error('Could not load contracts')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <el-card>
    <template #header>Contracts</template>
    <el-table
      :data="contracts"
      v-loading="loading"
      stripe
      @row-click="(row: Contract) => router.push(`/contracts/${row.id}`)"
    >
      <el-table-column prop="contractNo" label="No." />
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="committedAmount" label="Committed" />
      <el-table-column prop="status" label="Status" />
    </el-table>
  </el-card>
</template>

<style scoped>
:deep(.el-table__row) {
  cursor: pointer;
}
</style>
