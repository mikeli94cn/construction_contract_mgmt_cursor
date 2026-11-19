<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createPartner, listPartners } from '../api/partners'
import type { Partner } from '../types'

const rows = ref<Partner[]>([])
const loading = ref(false)
const dialog = ref(false)
const form = reactive({
  code: '',
  name: '',
  type: 'CONTRACTOR',
  contactName: '',
  contactPhone: '',
})

async function load() {
  loading.value = true
  try {
    rows.value = await listPartners()
  } catch {
    ElMessage.error('Could not load partners')
  } finally {
    loading.value = false
  }
}

async function onCreate() {
  try {
    await createPartner(form)
    ElMessage.success('Partner created')
    dialog.value = false
    await load()
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
        <span>Partners</span>
        <el-button type="primary" @click="dialog = true">New partner</el-button>
      </div>
    </template>
    <el-table :data="rows" v-loading="loading" stripe>
      <el-table-column prop="code" label="Code" />
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="type" label="Type" />
      <el-table-column prop="contactName" label="Contact" />
      <el-table-column prop="contactPhone" label="Phone" />
      <el-table-column prop="status" label="Status" />
    </el-table>
  </el-card>

  <el-dialog v-model="dialog" title="New partner">
    <el-form label-width="120px">
      <el-form-item label="Code"><el-input v-model="form.code" /></el-form-item>
      <el-form-item label="Name"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="Type">
        <el-select v-model="form.type" style="width: 100%">
          <el-option label="CLIENT" value="CLIENT" />
          <el-option label="CONTRACTOR" value="CONTRACTOR" />
          <el-option label="SUPPLIER" value="SUPPLIER" />
          <el-option label="OTHER" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item label="Contact name"><el-input v-model="form.contactName" /></el-form-item>
      <el-form-item label="Phone"><el-input v-model="form.contactPhone" /></el-form-item>
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