<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createPayment, listPayments, submitPayment } from '../api/payments'
import { listContracts } from '../api/contracts'
import type { Contract, Payment } from '../types'

const rows = ref<Payment[]>([])
const loading = ref(false)
const contracts = ref<Contract[]>([])
const dialog = ref(false)
const form = reactive({
  contractId: '',
  paymentNo: '',
  paymentType: 'PROGRESS',
  amount: 0,
})

async function loadAll() {
  loading.value = true
  try {
    contracts.value = await listContracts()
    const payLists = await Promise.all(contracts.value.map((c) => listPayments(c.id)))
    rows.value = payLists.flat()
  } catch {
    ElMessage.error('Could not load payments')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  try {
    await createPayment(form.contractId, {
      paymentNo: form.paymentNo,
      paymentType: form.paymentType,
      amount: form.amount,
    })
    dialog.value = false
    ElMessage.success('Created')
    await loadAll()
  } catch {
    ElMessage.error('Create failed')
  }
}

async function onSubmit(row: Payment) {
  try {
    await submitPayment(row.id)
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
        <span>Payments</span>
        <el-button type="primary" @click="dialog = true">New</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe>
      <el-table-column prop="paymentNo" label="No." />
      <el-table-column prop="paymentType" label="Type" />
      <el-table-column prop="amount" label="Amount" />
      <el-table-column prop="contractId" label="Contract ID" />
      <el-table-column prop="status" label="Status" />
      <el-table-column label="Actions" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="onSubmit(row)">Submit</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New payment">
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
      <el-form-item label="Payment no."><el-input v-model="form.paymentNo" /></el-form-item>
      <el-form-item label="Type">
        <el-select v-model="form.paymentType" style="width: 100%">
          <el-option label="ADVANCE" value="ADVANCE" />
          <el-option label="PROGRESS" value="PROGRESS" />
          <el-option label="RETENTION_RELEASE" value="RETENTION_RELEASE" />
          <el-option label="FINAL" value="FINAL" />
          <el-option label="OTHER" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item label="Amount"><el-input-number v-model="form.amount" :min="0" /></el-form-item>
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
