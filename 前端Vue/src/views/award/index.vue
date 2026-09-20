<template>
  <div class="award-page">
    <div class="page-header">
      <h2>科研获奖</h2>
      <p class="page-desc">管理您的科研获奖信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Medal /></el-icon>
            已提交获奖
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加获奖
          </el-button>
        </div>
      </template>

      <el-table :data="awardList" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="awardName" label="获奖名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="level" label="获奖级别" width="150" align="center" />
        <el-table-column prop="awardTime" label="获奖时间" width="120" align="center" />
        <el-table-column prop="awardOrg" label="颁奖机构" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deleteAward(row.id)">
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
    <el-dialog v-model="showDlg" title="新增科研获奖" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="获奖名称" prop="awardName">
          <el-input v-model="form.awardName" placeholder="请输入获奖名称" />
        </el-form-item>
        <el-form-item label="获奖级别" prop="level">
          <el-input v-model="form.level" placeholder="请输入获奖级别" />
        </el-form-item>
        <el-form-item label="获奖时间" prop="awardTime">
          <el-date-picker
            v-model="form.awardTime"
            type="date"
            placeholder="选择获奖时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="颁奖机构">
          <el-input v-model="form.awardOrg" placeholder="请输入颁奖机构" />
        </el-form-item>
        <el-form-item label="证书文件">
          <div v-if="prefillCert" class="prefill-cert-tip">
            <el-icon><CircleCheck /></el-icon>
            <span>证书已通过快速录入上传</span>
          </div>
          <el-upload
            v-else
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
        <el-button @click="showDlg = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Medal, Upload, CircleCheck } from '@element-plus/icons-vue'
import { fetchAwardPage as searchApi, addAward as addApi, deleteAward as deleteApi } from '@/api/award.js'
import { uploadApi } from '@/api/file.js'
import { check_Item } from '@/common/quick_fill.js'

let awardList = ref([])
let currentPage = ref(1)
let pageSize = ref(10)
let total = ref(0)
let showDlg = ref(false)
let formRef = ref(null)
let fileList = ref([])
let certFile = ref(null)
let prefillCert = ref(null)
let filePath = ref(''), fileName = ref(''), fileSize = ref(0)

let form = ref({ awardName: '', level: '', awardTime: '', awardOrg: '' })

const rules = {
  awardName: [{ required: true, message: '请输入获奖名称', trigger: 'blur' }],
  level: [{ required: true, message: '请输入获奖级别', trigger: 'blur' }],
  awardTime: [{ required: true, message: '请选择获奖时间', trigger: 'change' }]
}

const handleFileChange = async (file, files) => {
  fileList.value = files
  certFile.value = file.raw
  fileName.value = file.raw.name
  fileSize.value = file.raw.size
  try {
    let r = await uploadApi(file.raw)
    if (r.code == 1) filePath.value = r.data
  } catch(e) {
    console.log(e)
    ElMessage.error('上传失败')
  }
}

async function search() {
  try {
    let res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) {
      awardList.value = res.data.rows
      total.value = res.data.total
    }
  } catch(err) {
    ElMessage.error('查询失败')
  }
}

const handleSizeChange = (v) => { pageSize.value = v; currentPage.value = 1; search() }
const handleCurrentChange = (v) => { currentPage.value = v; search() }

function openAddDialog(prefillData) {
  showDlg.value = true
  form.value = {
    awardName: prefillData?.awardName ?? '',
    level: prefillData?.level ?? '',
    awardTime: prefillData?.awardTime ?? '',
    awardOrg: prefillData?.awardOrg ?? ''
  }
  fileList.value = []
  certFile.value = null
  prefillCert.value = null
  if (prefillData?.certificatePath) {
    prefillCert.value = { path: prefillData.certificatePath, name: prefillData.certificateName, size: prefillData.certificateSize }
  }
}

async function submitForm() {
  await formRef.value.validate()
  let cp = null, cn = null, cs = null
  if (prefillCert.value) {
    cp = prefillCert.value.path; cn = prefillCert.value.name; cs = prefillCert.value.size
  } else if (certFile.value) {
    cp = filePath.value; cn = fileName.value; cs = fileSize.value
  }
  try {
    await addApi({ ...form.value, certificatePath: cp, certificateName: cn, certificateSize: cs })
    ElMessage.success('添加成功')
    showDlg.value = false
    currentPage.value = 1
    search()
  } catch(e) {
    ElMessage.error('添加失败')
  }
}

function deleteAward(id) {
  ElMessageBox.confirm('确定删除该获奖吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id)
    ElMessage.success('已删除')
    search()
  }).catch(() => {})
}

const route = useRoute()
const router = useRouter()
onMounted(() => {
  search()
  check_Item(route, router, 'award', openAddDialog)
})
</script>

<style scoped>
.award-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.page-header { margin-bottom: 20px; }
.page-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #303133;
}
.page-desc { color: #909399; font-size: 14px; }
.table-card { border-radius: 8px; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 16px 0;
}
.upload-icon { font-size: 40px; color: #ccc; margin-bottom: 8px; }
.prefill-cert-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #67c23a;
  padding: 12px;
  background: #f0f9eb;
  border-radius: 8px;
}
</style>
