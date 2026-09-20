<template>
  <div class="student-page">
    <div class="page-header">
      <h2>指导学生获奖</h2>
      <p class="page-desc">管理您的指导学生获奖信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Trophy /></el-icon>
            已提交获奖
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加获奖
          </el-button>
        </div>
      </template>

      <el-table :data="list" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生负责人" width="120" align="center" />
        <el-table-column prop="awardName" label="获奖项目名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="awardType" label="获奖类型" min-width="200" show-overflow-tooltip />
        <el-table-column prop="awardTime" label="获奖时间" width="120" align="center" />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deleteStudentAward(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加获奖对话框 -->
    <el-dialog v-model="dlgVisible" title="新增指导学生获奖" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="学生负责人名字" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入学生负责人名字" />
        </el-form-item>
        <el-form-item label="获奖项目名称" prop="awardName">
          <el-input v-model="form.awardName" placeholder="请输入获奖项目名称" />
        </el-form-item>
        <el-form-item label="获奖类型" prop="awardType">
          <el-input v-model="form.awardType" placeholder="请输入获奖类型" />
        </el-form-item>
        <el-form-item label="获奖时间">
          <el-date-picker
            v-model="form.awardTime"
            type="date"
            placeholder="选择获奖时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlgVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Trophy } from '@element-plus/icons-vue'
import { listStudentAward as searchApi, addStudentAward as addApi, delStudentAward as deleteApi } from '@/api/student.js'

let list = ref([])
let currentPage = ref(1)
let pageSize = ref(10)
let total = ref(0)
let dlgVisible = ref(false)
let formRef = ref(null)
let form = ref({ studentName: '', awardName: '', awardType: '', awardTime: '' })

let rules = {
  studentName: [{ required: true, message: '请输入学生负责人名字', trigger: 'blur' }],
  awardName: [{ required: true, message: '请输入获奖项目名称', trigger: 'blur' }],
  awardType: [{ required: true, message: '请输入获奖类型', trigger: 'blur' }]
}

async function loadData() {
  try {
    let res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) { list.value = res.data.rows; total.value = res.data.total }
  } catch(e) { ElMessage.error('查询失败') }
}

function handleSizeChange(v) { pageSize.value = v; currentPage.value = 1; loadData() }
function handleCurrentChange(v) { currentPage.value = v; loadData() }

function openAddDialog() {
  dlgVisible.value = true
  form.value = { studentName: '', awardName: '', awardType: '', awardTime: '' }
}

async function submitForm() {
  await formRef.value.validate()
  try {
    await addApi(form.value)
    ElMessage.success('添加成功')
    dlgVisible.value = false; currentPage.value = 1; loadData()
  } catch(e) { ElMessage.error('添加失败') }
}

function deleteStudentAward(id) {
  ElMessageBox.confirm('确定删除该获奖？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id); ElMessage.success('删除成功'); loadData()
  }).catch(() => {})
}

onMounted(() => loadData())
</script>

<style scoped>
.student-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px) }
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0; font-size: 24px; font-weight: bold }
.page-desc { margin: 8px 0 0; font-size: 14px; color: #999 }
.table-card { border-radius: 8px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600 }
.pagination-wrapper { margin-top: 20px; text-align: center; padding: 16px 0 }
</style>
