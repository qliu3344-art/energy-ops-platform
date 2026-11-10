<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDevices, createDevice, updateDevice, deleteDevice } from '../api/device'
import { getDeviceTypes } from '../api/dict'

// ---------- 响应式数据 ----------
// 单个值用 ref：加载中、表格数据、总数、类型下拉选项
const loading = ref(false)
const list = ref([])
const total = ref(0)
const types = ref([])

// 一组相关字段用 reactive：查询条件
const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  typeId: null,
  status: null
})

// 弹窗相关：是否显示、当前编辑的 id（null 表示新增）、表单组件引用
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

// Element Plus 表单校验规则：required + 提示语 + 触发时机
const rules = {
  code: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  typeId: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  station: [{ required: true, message: '请输入所属场站', trigger: 'blur' }]
}

/**
 * 加载列表。把查询条件发给后端，拿到 { total, list, page, size }。
 * 赋给响应式变量后，表格和分页组件会自动刷新，不需要操作 DOM。
 */
async function load() {
  loading.value = true
  try {
    const data = await getDevices({
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,   // 空串转 undefined，后端就不会拼这个条件
      typeId: query.typeId || undefined,
      status: query.status ?? undefined      // 用 ??：状态 0 是有效值，不能被当成空
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 类型下拉选项（走后端 Redis 缓存） */
async function loadTypes() {
  types.value = await getDeviceTypes()
}

/** 点击查询：回到第一页再加载，避免停在越界的页码上 */
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

/** 把表单恢复成新增时的默认值 */
function resetForm() {
  form.code = ''
  form.name = ''
  form.typeId = null
  form.station = ''
  form.status = 1
  form.installDate = ''
}

/** 打开新增弹窗：编辑 id 置空，表单清空 */
function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

/** 打开编辑弹窗：把当前行的值回填到表单 */
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

/**
 * 提交表单：先本地校验，再根据 editingId 决定新增还是修改。
 * { ...form } 是浅拷贝，避免把 reactive 对象直接传给 axios。
 */
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

/** 删除：先二次确认，用户点取消会 reject，后续代码不执行 */
async function remove(row) {
  await ElMessageBox.confirm(`确定删除设备「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteDevice(row.id)
  ElMessage.success('删除成功')
  load()
}

/** 状态码映射成标签颜色：运行绿、故障红、其它灰 */
function statusType(status) {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

// 页面挂载后初始化：先拉类型字典，再拉列表
onMounted(() => {
  loadTypes()
  load()
})
</script>

<template>
  <div>
    <el-card shadow="never">
      <!-- 查询条件区。@submit.prevent 阻止回车触发浏览器默认提交 -->
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
          <!-- v-for 渲染下拉项，:key 必须是唯一值 -->
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

      <!-- v-loading 绑定 loading，请求期间表格显示遮罩；:data 绑定列表 -->
      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="code" label="设备编码" width="130" />
        <el-table-column prop="name" label="设备名称" min-width="150" />
        <!-- typeName 是后端 LEFT JOIN 带出来的，前端不用再查类型表 -->
        <el-table-column prop="typeName" label="类型" width="100" />
        <el-table-column prop="station" label="所属场站" min-width="160" />
        <el-table-column label="状态" width="90">
          <!-- 作用域插槽：拿到当前行的数据 row，自定义这一格的渲染 -->
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

      <!-- v-model:xxx 是 Vue 3 的「带参数双向绑定」，分页组件通过它把页码同步回来 -->
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

    <!-- 新增 / 编辑共用同一个弹窗，靠 editingId 区分标题和提交行为 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑设备' : '新增设备'"
      width="520px"
    >
      <!-- ref 拿到表单实例，用来调 validate()；:rules 绑定校验规则 -->
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
          <!-- value-format 让日期以 YYYY-MM-DD 字符串提交，和后端 LocalDate 对齐 -->
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
