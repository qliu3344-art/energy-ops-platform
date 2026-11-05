<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDevices, createDevice, updateDevice, deleteDevice } from '../api/device'
import { getDeviceTypes } from '../api/dict'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const types = ref([])

const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  typeId: null,
  status: null
})

const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({
  code: '',
  name: '',
  typeId: null,
  station: '',
  status: 1,
  installDate: ''
})

const rules = {
  code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  station: [{ required: true, message: '请输入所属场站', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const data = await getDevices({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      typeId: query.typeId || undefined,
      status: query.status ?? undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function loadTypes() {
  types.value = await getDeviceTypes()
}

function search() {
  query.page = 1
  load()
}

function resetQuery() {
  query.keyword = ''
  query.typeId = null
  query.status = null
  search()
}

function resetForm() {
  form.code = ''
  form.name = ''
  form.typeId = null
  form.station = ''
  form.status = 1
  form.installDate = ''
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  form.code = row.code
  form.name = row.name
  form.typeId = row.typeId
  form.station = row.station
  form.status = row.status
  form.installDate = row.installDate || ''
  dialogVisible.value = true
}

async function submitForm() {
  await formRef.value.validate()
  if (editingId.value) {
    await updateDevice(editingId.value, { ...form })
    ElMessage.success('修改成功')
  } else {
    await createDevice({ ...form })
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除设备「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteDevice(row.id)
  ElMessage.success('删除成功')
  load()
}

function statusType(status) {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

onMounted(() => {
  loadTypes()
  load()
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="query.keyword"
            placeholder="设备名称 / 编码"
            clearable
            style="width: 200px"
            @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.typeId" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="运行" :value="1" />
            <el-option label="停用" :value="0" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="openCreate">新增设备</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="code" label="设备编码" width="130" />
        <el-table-column prop="name" label="设备名称" min-width="150" />
        <el-table-column prop="typeName" label="类型" width="100" />
        <el-table-column prop="station" label="所属场站" min-width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="installDate" label="投运日期" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="load"
        @size-change="search"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑设备' : '新增设备'"
      width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备编码" prop="code">
          <el-input v-model="form.code" placeholder="如 DEV-011" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="设备类型" prop="typeId">
          <el-select v-model="form.typeId" placeholder="请选择" style="width: 100%">
            <el-option v-for="t in types" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属场站" prop="station">
          <el-input v-model="form.station" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="运行" :value="1" />
            <el-option label="停用" :value="0" />
            <el-option label="故障" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="投运日期">
          <el-date-picker
            v-model="form.installDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
