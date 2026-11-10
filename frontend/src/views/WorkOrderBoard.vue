<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWorkOrders, createWorkOrder, transitionWorkOrder } from '../api/workOrder'
import { getDevices } from '../api/device'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const devices = ref([])

// 查询条件
const query = reactive({
  page: 1,
  size: 10,
  status: null,
  type: null,
  keyword: ''
})

// 新建工单弹窗
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

// 令牌过期会整页跳回登录页，Vue 状态全部丢失；把填写中的表单存到 sessionStorage 兜底
const DRAFT_KEY = 'draft:work-order'

watch(
  form,
  () => {
    if (dialogVisible.value) {
      sessionStorage.setItem(DRAFT_KEY, JSON.stringify(form))
    }
  },
  { deep: true }
)

function clearDraft() {
  sessionStorage.removeItem(DRAFT_KEY)
}

/** 重新登录回到本页时，把上次没提交完的草稿恢复出来 */
function restoreDraft() {
  const raw = sessionStorage.getItem(DRAFT_KEY)
  if (!raw) return
  try {
    Object.assign(form, JSON.parse(raw))
    dialogVisible.value = true
    ElMessage.info('已恢复上次未提交的工单草稿')
  } catch (e) {
    clearDraft()
  }
}

// 状态机：当前状态 -> 可执行的动作
// 注意和后端 WorkOrderService.canTransition 保持一致：
// 前端只决定「按钮显示什么」，真正的合法性校验在后端
const NEXT_ACTION = {
  0: { label: '派发', to: 1 },
  1: { label: '开始执行', to: 2 },
  2: { label: '完成', to: 3 },
  3: { label: '验收', to: 4 }
}

/** 加载工单列表 */
async function load() {
  loading.value = true
  try {
    const data = await getWorkOrders({
      page: query.page,
      size: query.size,
      status: query.status ?? undefined,   // ?? 保证状态 0（待派发）不被当成空
      type: query.type ?? undefined,
      keyword: query.keyword || undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 设备下拉选项：一次取 200 条，够演示用 */
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
  clearDraft()
  form.deviceId = null
  form.type = 1
  form.assignee = ''
  form.description = ''
  dialogVisible.value = true
}

/**
 * 新建工单。注意只传业务字段，status 由后端固定为「待派发」，
 * 前端传了也不会生效——状态只能通过流转接口推进。
 */
async function submitCreate() {
  await formRef.value.validate()
  await createWorkOrder({ ...form })
  ElMessage.success('工单已创建')
  dialogVisible.value = false
  load()
}

/**
 * 状态流转：根据当前状态查表得到「下一个动作」，确认后调接口。
 * 如果当前已经是 4（已验收），NEXT_ACTION 里没有对应项，直接返回。
 */
async function doTransition(row) {
  const action = NEXT_ACTION[row.status]
  if (!action) return
  let assignee
  if (row.status === 0) {
    // 派发即指定执行人：负责人必填
    const { value } = await ElMessageBox.prompt(
      `确定将工单「${row.code}」派发吗？请填写负责人：`,
      '派发工单',
      {
        inputPlaceholder: '如 张工',
        inputValidator: (v) => (v && v.trim() ? true : '请填写负责人')
      }
    )
    assignee = value.trim()
  } else {
    await ElMessageBox.confirm(
      `确定将工单「${row.code}」执行「${action.label}」吗？`,
      '状态流转',
      { type: 'warning' }
    )
  }
  await transitionWorkOrder(row.id, {
    toStatus: action.to,
    operator: localStorage.getItem('username') || 'admin',
    remark: action.label,
    assignee
  })
  ElMessage.success(`${action.label}成功`)
  load()
}

/** 用数组下标当映射：0~4 分别对应不同的标签颜色 */
function statusType(status) {
  return ['info', 'warning', 'primary', 'success', 'success'][status] || 'info'
}

/** 后端返回的时间带 T，展示时换成空格更好看 */
function fmtTime(value) {
  return value ? String(value).replace('T', ' ') : ''
}

onMounted(() => {
  loadDevices()
  load()
  restoreDraft()
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
        <!-- deviceName 由后端 JOIN 得到 -->
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
            <!-- 有下一步动作才显示按钮；已验收则显示「已结束」 -->
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

    <el-dialog v-model="dialogVisible" title="新建工单" width="520px" @closed="clearDraft">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="设备" prop="deviceId">
          <!-- filterable：设备多了可以直接输关键字筛选 -->
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
