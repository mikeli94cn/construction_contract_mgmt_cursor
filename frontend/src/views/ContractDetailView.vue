<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  activateContract,
  getContract,
  submitContract,
  updateContract,
} from '../api/contracts'
import type { Contract } from '../types'

const route = useRoute()
const router = useRouter()
const contractId = route.params.id as string
const contract = ref<Contract | null>(null)
const loading = ref(false)
const edit = reactive({ title: '' })

async function load() {
  loading.value = true
  try {
    contract.value = await getContract(contractId)
    edit.title = contract.value.title
  } catch {
    ElMessage.error('Load failed')
  } finally {
    loading.value = false
  }
}

async function onSave() {
  if (!contract.value) return
  try {
    contract.value = await updateContract(contractId, {
      title: edit.title,
    })
    ElMessage.success('Saved')
  } catch {
    ElMessage.error('Save failed')
  }
}

async function onSubmit() {
  try {
    contract.value = await submitContract(contractId)
    ElMessage.success('Submitted')
  } catch {
    ElMessage.error('Submit failed')
  }
}

async function onActivate() {
  try {
    contract.value = await activateContract(contractId)
    ElMessage.success('Activated')
  } catch {
    ElMessage.error('Activate failed')
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading">
    <el-page-header @back="router.push('/contracts')">
      <template #content>
        <span v-if="contract">{{ contract.contractNo }} — {{ contract.title }}</span>
      </template>
    </el-page-header>

    <el-card class="section">
      <el-form label-width="120px">
        <el-form-item label="Title"><el-input v-model="edit.title" /></el-form-item>
        <el-form-item label="Committed">{{ contract?.committedAmount }}</el-form-item>
        <el-form-item label="Status"><el-tag>{{ contract?.status }}</el-tag></el-form-item>
        <el-button type="primary" @click="onSave">Save</el-button>
        <el-button @click="onSubmit">Submit</el-button>
        <el-button type="success" @click="onActivate">Activate</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.section {
  margin-top: 16px;
}
</style>