<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createSettlement, getSettlement, sealSettlement, submitSettlement } from '../api/settlements'
import { listContracts } from '../api/contracts'
import type { Contract, Settlement } from '../types'

const rows = ref<Settlement[]>([])
const loading = ref(false)
const contracts = ref<Contract[]>([])
const dialog = ref(false)
const form = reactive({ contractId: '', settlementNo: '', finalAmount: 0 })

async function loadAll() {
  loading.value = true
  try {
    contracts.value = await listContracts()
    const settlements = await Promise.all(contracts.value.map((c) => getSettlement(c.id)))
    rows.value = settlements.filter((s): s is Settlement => s != null)
  } catch {
    ElMessage.error('Could not load settlements')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  try {
    await createSettlement(form.contractId, {
      settlementNo: form.settlementNo,
      finalAmount: form.finalAmount,
    })
    dialog.value = false
    ElMessage.success('Created')
    await loadAll()
  } catch {
    ElMessage.error('Create failed')
  }
}

async function onSubmit(row: Settlement) {
  try {
    await submitSettlement(row.id)
    ElMessage.success('Submitted')
    await loadAll()
  } catch {
    ElMessage.error('Submit failed')
  }
}

async function onSeal(row: Settlement) {
  try {
    await sealSettlement(row.id)
    ElMessage.success('Sealed')
    await loadAll()
  } catch {
    ElMessage.error('Seal failed')
  }
}

onMounted(loadAll)
</script>

<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>Settlements</span>
        <el-button type="primary" @click="dialog = true">New</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe>
      <el-table-column prop="settlementNo" label="No." />
      <el-table-column prop="finalAmount" label="Final amount" />
      <el-table-column prop="contractId" label="Contract ID" />
      <el-table-column prop="status" label="Status" />
      <el-table-column label="Actions" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="onSubmit(row)">Submit</el-button>
          <el-button size="small" type="success" @click="onSeal(row)">Seal</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New settlement">
    <el-form label-width="120px">
      <el-form-item label="Contract">
        <el-select v-model="form.contractId" filterable style="width: 100%">
          <el-option
            v-for="c in contracts"
            :key="c.id"
            :label="`${c.contractNo} — ${c.title}`"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Settlement no."><el-input v-model="form.settlementNo" /></el-form-item>
      <el-form-item label="Final amount"><el-input-number v-model="form.finalAmount" :min="0" /></el-form-item>
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
</style>
