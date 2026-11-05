<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAlarms, handleAlarm } from '../api/alarm'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 10,
  status: null,
  level: null
})

async function load() {
  loading.value = true
  try {
    const data = await getAlarms({
      page: query.page,
      size: query.size,
      status: query.status ?? undefined,
      level: query.level ?? undefined
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  load()
}

function resetQuery() {
  query.status = null
  query.level = null
  search()
}

async function handle(row) {
  await ElMessageBox.confirm(`确定处理告警「${row.content}」吗？`, '处理告警', {
    type: 'warning'
  })
  await handleAlarm(row.id, { remark: '已现场处理' })
  ElMessage.success('处理成功')
  load()
}

function levelType(level) {
  if (level === 3) return 'danger'
  if (level === 2) return 'warning'
  return 'info'
}

function fmtTime(value) {
  return value ? String(value).replace('T', ' ') : ''
}

onMounted(load)
</script>

<template>
  <div>
    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="未处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" placeholder="全部" clearable style="width: 130px">
            <el-option label="提示" :value="1" />
            <el-option label="警告" :value="2" />
            <el-option label="严重" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="deviceName" label="设备" min-width="140" />
        <el-table-column label="级别" width="90">
          <template #default="{ row }">
            <el-tag :type="levelType(row.level)">{{ row.levelText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="告警内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="告警时间" width="170">
          <template #default="{ row }">{{ fmtTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="处理时间" width="170">
          <template #default="{ row }">{{ fmtTime(row.handleTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              link
              type="primary"
              @click="handle(row)"
            >
              处理
            </el-button>
            <span v-else class="done">已处理</span>
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
  </div>
</template>

<style scoped>
.done {
  color: #c0c4cc;
  font-size: 13px;
}
</style>
