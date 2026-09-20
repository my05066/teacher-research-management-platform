<template>
  <div class="other-page">
    <div class="page-header">
      <h2>其他科研成果</h2>
      <p class="page-desc">管理您的其他科研成果信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Expand /></el-icon>
            已提交成果
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加成果
          </el-button>
        </div>
      </template>

      <el-table :data="otherList" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="成果标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="description" label="成果描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deleteOther(row.id)">
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

    <!-- 添加成果对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增其他科研成果" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="成果标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入成果标题" />
        </el-form-item>
        <el-form-item label="成果描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入成果描述"
          />
        </el-form-item>
        <el-form-item label="证明文件">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            accept=".pdf,.doc,.docx,.jpg,.png"
            :limit="1"
            drag
          >
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="el-upload__text">点击或拖拽文件到此处上传</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Expand, Upload } from '@element-plus/icons-vue'
import { getOtherPage as searchApi, saveOther as addApi, deleteOther as deleteApi } from '@/api/other.js'
import { uploadApi } from '@/api/file.js'

var otherList = ref([])
var currentPage = ref(1), pageSize = ref(10), total = ref(0)
var addDialogVisible = ref(false)
var formRef = ref(null)
var fileList = ref([]), evidenceFile = ref(null)
var filePath = ref(''), fileName = ref(''), fileSize = ref(0)
var form = ref({ title: '', description: '' })
var rules = { title: [{ required: true, message: '请输入成果标题', trigger: 'blur' }] }

const search = async () => {
  try {
    var res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) { otherList.value = res.data.rows; total.value = res.data.total }
  } catch(e) { ElMessage.error('查询失败') }
}

const handleFileChange = async (file, files) => {
  fileList.value = files
  evidenceFile.value = file.raw
  fileName.value = file.raw.name
  fileSize.value = file.raw.size
  try {
    var r = await uploadApi(file.raw)
    if (r.code == 1) filePath.value = r.data
  } catch(e) { ElMessage.error('上传失败') }
}

const handleSizeChange = v => { pageSize.value = v; currentPage.value = 1; search() }
const handleCurrentChange = v => { currentPage.value = v; search() }

function openAddDialog() {
  addDialogVisible.value = true
  form.value = { title: '', description: '' }
  fileList.value = []; evidenceFile.value = null
}

async function submitForm() {
  await formRef.value.validate()
  var ep = null, en = null, es = null
  if (evidenceFile.value) {
    ep = filePath.value; en = fileName.value; es = fileSize.value
  }
  try {
    await addApi({ ...form.value, evidencePath: ep, evidenceName: en, evidenceSize: es })
    ElMessage.success('添加成功')
    addDialogVisible.value = false; currentPage.value = 1; search()
  } catch(e) { ElMessage.error('添加失败') }
}

function deleteOther(id) {
  ElMessageBox.confirm('确定删除该成果？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id)
    ElMessage.success('已删除')
    search()
  }).catch(() => {})
}

onMounted(search)
</script>

<style scoped>
.other-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #333 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.table-card { border-radius: 8px }
.card-header {
  display: flex; justify-content: space-between; align-items: center;
}
.card-title {
  display: flex; align-items: center; gap: 8px;
  font-size: 16px; font-weight: 600; color: #333;
}
.pagination-wrapper {
  margin-top: 20px; display: flex;
  justify-content: center; padding: 16px 0;
}
.upload-icon { font-size: 40px; color: #c0c4cc; margin-bottom: 8px }
</style>
