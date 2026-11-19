<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createContract, listContractsByProject } from '../api/contracts'
import { getProject, updateProject } from '../api/projects'
import { listPartners } from '../api/partners'
import type { Contract, Partner, Project } from '../types'

const route = useRoute()
const router = useRouter()
const projectId = route.params.id as string
const project = ref<Project | null>(null)
const contracts = ref<Contract[]>([])
const partners = ref<Partner[]>([])
const loading = ref(false)
const edit = reactive({ name: '' })
const contractForm = reactive({
  contractNo: '',
  title: '',
  partnerId: '',
  pricingType: 'LUMP_SUM',
  originalAmount: 0,
})
const contractDialog = ref(false)

async function load() {
  loading.value = true
  try {
    project.value = await getProject(projectId)
    edit.name = project.value.name
    contracts.value = await listContractsByProject(projectId)
    partners.value = await listPartners()
  } catch {
    ElMessage.error('Load failed')
  } finally {
    loading.value = false
  }
}

async function onSave() {
  if (!project.value) return
  try {
    project.value = await updateProject(projectId, { name: edit.name })
    ElMessage.success('Saved')
  } catch {
    ElMessage.error('Save failed')
  }
}

async function onCreateContract() {
  try {
    const c = await createContract(projectId, {
      contractNo: contractForm.contractNo,
      title: contractForm.title,
      partnerId: contractForm.partnerId,
      pricingType: contractForm.pricingType,
      originalAmount: contractForm.originalAmount,
    })
    contractDialog.value = false
    router.push(`/contracts/${c.id}`)
  } catch {
    ElMessage.error('Create contract failed')
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading">
    <el-page-header @back="router.push('/projects')">
      <template #content>
        <span v-if="project">{{ project.code }} — {{ project.name }}</span>
      </template>
    </el-page-header>

    <el-card class="section">
      <el-form label-width="120px">
        <el-form-item label="Name"><el-input v-model="edit.name" /></el-form-item>
        <el-form-item label="Status"><el-tag>{{ project?.status }}</el-tag></el-form-item>
        <el-button type="primary" @click="onSave">Save</el-button>
      </el-form>
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="toolbar">
          <span>Contracts</span>
          <el-button type="primary" @click="contractDialog = true">New contract</el-button>
        </div>
      </template>
      <el-table :data="contracts" @row-click="(row: Contract) => router.push(`/contracts/${row.id}`)">
        <el-table-column prop="contractNo" label="No." />
        <el-table-column prop="title" label="Title" />
        <el-table-column prop="committedAmount" label="Committed" />
        <el-table-column prop="status" label="Status" />
      </el-table>
    </el-card>
  </div>

  <el-dialog v-model="contractDialog" title="New contract">
    <el-form label-width="120px">
      <el-form-item label="Contract no."><el-input v-model="contractForm.contractNo" /></el-form-item>
      <el-form-item label="Title"><el-input v-model="contractForm.title" /></el-form-item>
      <el-form-item label="Partner">
        <el-select v-model="contractForm.partnerId" filterable style="width: 100%">
          <el-option v-for="p in partners" :key="p.id" :label="`${p.code} — ${p.name}`" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="Pricing">
        <el-select v-model="contractForm.pricingType" style="width: 100%">
          <el-option label="LUMP_SUM" value="LUMP_SUM" />
          <el-option label="UNIT_PRICE" value="UNIT_PRICE" />
          <el-option label="COST_PLUS" value="COST_PLUS" />
        </el-select>
      </el-form-item>
      <el-form-item label="Amount">
        <el-input-number v-model="contractForm.originalAmount" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="contractDialog = false">Cancel</el-button>
      <el-button type="primary" @click="onCreateContract">Create</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.section {
  margin-top: 16px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
