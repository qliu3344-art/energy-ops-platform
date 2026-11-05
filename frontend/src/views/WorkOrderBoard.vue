<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWorkOrders, createWorkOrder, transitionWorkOrder } from '../api/workOrder'
import { getDevices } from '../api/device'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const devices = ref([])

const query = reactive({
  page: 1,
  size: 10,
  status: null,
  type: null,
  keyword: ''
})

const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  deviceId: null,
  type: 1,
  assignee: '',
  description: ''
})
const rules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  type: [{ required: true, message: '请选择工单类型', trigger: 'change' }]
}

// 状态机：当前状态 -> 可执行的动作
const NEXT_ACTION = {
  0: { label: '派发', to: 1 },
  1: { label: '开始执行', to: 2 },
  2: { label: '完成', to: 3 },
  3: { label: '验收', to: 4 }
}

async function load() {
  loading.value = true
  try {
    const data = await getWorkOrders({
      page: query.page,
      size: query.size,
      status: query.status ?? undefined,
      type: query.type ?? undefined,
      keyword: query.keyword || undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function loadDevices() {
  const data = await getDevices({ page: 1, size: 200 })
  devices.value = data.list
}

function search() {
  query.page = 1
  load()
}

function resetQuery() {
  query.status = null
  query.type = null
  query.keyword = ''
  search()
}

function openCreate() {
  form.deviceId = null
  form.type = 1
  form.assignee = ''
  form.description = ''
  dialogVisible.value = true
}

async function submitCreate() {
  await formRef.value.validate()
  await createWorkOrder({ ...form })
  ElMessage.success('工单已创建')
  dialogVisible.value = false
  load()
}

async function doTransition(row) {
  const action = NEXT_ACTION[row.status]
  if (!action) return
  await ElMessageBox.confirm(
    `确定将工单「${row.code}」执行「${action.label}」吗？`,
    '状态流转',
    { type: 'warning' }
  )
  await transitionWorkOrder(row.id, {
    toStatus: action.to,
    operator: localStorage.getItem('username') || 'admin',
    remark: action.label
  })
  ElMessage.success(`${action.label}成功`)
  load()
}

function statusType(status) {
  return ['info', 'warning', 'primary', 'success', 'success'][status] || 'info'
}

function fmtTime(value) {
  return value ? String(value).replace('T', ' ') : ''
}

onMounted(() => {
  loadDevices()
  load()
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="待派发" :value="0" />
            <el-option label="已派发" :value="1" />
            <el-option label="执行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已验收" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width: 110px">
            <el-option label="巡检" :value="1" />
            <el-option label="维修" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input
            v-model="query.keyword"
            placeholder="工单号 / 设备名称"
            clearable
            style="width: 200px"
            @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="openCreate">新建工单</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="code" label="工单号" width="180" />
        <el-table-column prop="deviceName" label="设备" min-width="140" />
        <el-table-column prop="typeText" label="类型" width="90" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="负责人" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="NEXT_ACTION[row.status]"
              link
              type="primary"
              @click="doTransition(row)"
            >
              {{ NEXT_ACTION[row.status].label }}
            </el-button>
            <span v-else class="done">已结束</span>
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

    <el-dialog v-model="dialogVisible" title="新建工单" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备" prop="deviceId">
          <el-select
            v-model="form.deviceId"
            filterable
            placeholder="请选择设备"
            style="width: 100%"
          >
            <el-option
              v-for="d in devices"
              :key="d.id"
              :label="`${d.code} ${d.name}`"
              :value="d.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工单类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">巡检</el-radio>
            <el-radio :value="2">维修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.assignee" placeholder="如 张工" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="工单内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
.done {
  color: #c0c4cc;
  font-size: 13px;
}
</style>
