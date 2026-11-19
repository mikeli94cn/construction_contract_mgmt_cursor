<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listChangeOrders, createChangeOrder, submitChangeOrder } from '../api/changeOrders'
import { listContracts } from '../api/contracts'
import type { ChangeOrder, Contract } from '../types'

const rows = ref<ChangeOrder[]>([])
const loading = ref(false)
const contracts = ref<Contract[]>([])
const dialog = ref(false)
const form = reactive({ contractId: '', changeNo: '', title: '', amountDelta: 0 })

async function loadAll() {
  loading.value = true
  try {
    contracts.value = await listContracts()
    const coLists = await Promise.all(contracts.value.map((c) => listChangeOrders(c.id)))
    rows.value = coLists.flat()
  } catch {
    ElMessage.error('Could not load change orders')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  if (!form.contractId) {
    ElMessage.warning('Select a contract')
    return
  }
  try {
    await createChangeOrder(form.contractId, {
      changeNo: form.changeNo,
      title: form.title,
      amountDelta: form.amountDelta,
    })
    dialog.value = false
    ElMessage.success('Created')
    await loadAll()
  } catch {
    ElMessage.error('Create failed')
  }
}

async function onSubmit(row: ChangeOrder) {
  try {
    await submitChangeOrder(row.id)
    ElMessage.success('Submitted')
    await loadAll()
  } catch {
    ElMessage.error('Submit failed')
  }
}

onMounted(loadAll)
</script>

<template>
  <el-card>
    <template #header>
      <div class="toolbar">
        <span>Change orders</span>
        <el-button type="primary" @click="dialog = true">New</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe>
      <el-table-column prop="changeNo" label="No." />
      <el-table-column prop="title" label="Title" />
      <el-table-column prop="amountDelta" label="Delta" />
      <el-table-column prop="contractId" label="Contract ID" />
      <el-table-column prop="status" label="Status" />
      <el-table-column label="Actions" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="onSubmit(row)">Submit</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New change order">
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
      <el-form-item label="Change no."><el-input v-model="form.changeNo" /></el-form-item>
      <el-form-item label="Title"><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="Amount delta"><el-input-number v-model="form.amountDelta" /></el-form-item>
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
