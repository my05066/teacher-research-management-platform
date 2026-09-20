<template>
  <div class="essay-page">
    <div class="page-header">
      <h2>发表论文</h2>
      <p class="page-desc">管理您的发表论文信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Document /></el-icon>
            已提交论文
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加论文
          </el-button>
        </div>
      </template>

      <el-table :data="essayList" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="论文题目" min-width="300" show-overflow-tooltip />
        <el-table-column prop="level" label="论文等级" width="150" align="center" />
        <el-table-column prop="authorType" label="作者信息" width="120" align="center" />
        <el-table-column prop="journalName" label="期刊名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="publishTime" label="发表时间" width="120" align="center" />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deleteEssay(row.id)">
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

    <!-- 添加论文对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增发表论文" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="论文题目" prop="title">
          <el-input v-model="form.title" placeholder="请输入论文题目" />
        </el-form-item>
        <el-form-item label="发表论文等级" prop="level">
          <el-select v-model="form.level" placeholder="请选择等级" style="width: 100%">
            <el-option label="Science" value="Science" />
            <el-option label="Nature" value="Nature" />
            <el-option label="SCI一区" value="SCI一区" />
            <el-option label="SCI二区" value="SCI二区" />
            <el-option label="SCI三区" value="SCI三区" />
            <el-option label="SCI四区" value="SCI四区" />
            <el-option label="EI收录论文" value="EI收录论文" />
            <el-option label="CSCD" value="CSCD" />
            <el-option label="CSSCI" value="CSSCI" />
            <el-option label="CSCD-E" value="CSCD-E" />
            <el-option label="中文核心期刊" value="中文核心期刊" />
          </el-select>
        </el-form-item>
        <el-form-item label="作者信息" prop="authorType">
          <el-radio-group v-model="form.authorType">
            <el-radio label="第一作者">第一作者</el-radio>
            <el-radio label="通讯作者">通讯作者</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="期刊名称">
          <el-input v-model="form.journalName" placeholder="请输入期刊名称" />
        </el-form-item>
        <el-form-item label="发表时间">
          <el-date-picker
            v-model="form.publishTime"
            type="date"
            placeholder="选择发表时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="证书照片" :required="!prefillCert">
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
            accept=".jpg,.jpeg,.png,.webp"
            :limit="1"
            drag
          >
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="el-upload__text">点击或拖拽证书照片到此处上传</div>
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
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, Document, Upload, CircleCheck } from '@element-plus/icons-vue'
import { getEssayList as searchApi, addEssay as addApi, removeEssay as deleteApi } from '@/api/essay.js'
import { uploadApi } from '@/api/file.js'
import { check_Item } from '@/common/quick_fill.js'

const route = useRoute()
const router = useRouter()

var essayList = ref([])
var currentPage = ref(1)
var pageSize = ref(10)
var total = ref(0)
var addDialogVisible = ref(false)
var formRef = ref(null)
var fileList = ref([])
var certFile = ref(null)
var prefillCert = ref(null)
var filePath = ref('')
var fileName = ref('')
var fileSize = ref(0)

var form = ref({
  title: '', level: '', authorType: '',
  journalName: '', publishTime: ''
})

var rules = {
  title: [{ required: true, message: '请输入论文题目', trigger: 'blur' }],
  level: [{ required: true, message: '请选择论文等级', trigger: 'change' }],
  authorType: [{ required: true, message: '请选择作者信息', trigger: 'change' }]
}

async function search() {
  try {
    var res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) {
      essayList.value = res.data.rows
      total.value = res.data.total
    }
  } catch(e) {
    ElMessage.error('查询失败')
  }
}

const handleFileChange = async (file, files) => {
  fileList.value = files
  certFile.value = file.raw
  fileName.value = file.raw.name
  fileSize.value = file.raw.size
  try {
    var res = await uploadApi(file.raw)
    if (res.code == 1) filePath.value = res.data
  } catch(e) {
    ElMessage.error('上传失败')
  }
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
  search()
}
function handleCurrentChange(val) {
  currentPage.value = val
  search()
}

function openAddDialog(prefillData) {
  addDialogVisible.value = true
  form.value = {
    title: prefillData?.title ?? '',
    level: prefillData?.level ?? '',
    authorType: prefillData?.authorType ?? '',
    journalName: prefillData?.journalName ?? '',
    publishTime: prefillData?.publishTime ?? ''
  }
  fileList.value = []
  certFile.value = null
  prefillCert.value = null
  if (prefillData?.certificatePath) {
    prefillCert.value = {
      path: prefillData.certificatePath,
      name: prefillData.certificateName,
      size: prefillData.certificateSize
    }
  }
}

async function submitForm() {
  await formRef.value.validate()
  var certPath = null, certName = null, certSize = null
  if (prefillCert.value) {
    certPath = prefillCert.value.path
    certName = prefillCert.value.name
    certSize = prefillCert.value.size
  } else if (certFile.value) {
    certPath = filePath.value
    certName = fileName.value
    certSize = fileSize.value
  }
  if (!certPath) { ElMessage.warning('请上传证书照片'); return }
  try {
    await addApi({ ...form.value, certificatePath: certPath, certificateName: certName, certificateSize: certSize })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    currentPage.value = 1
    search()
  } catch(e) {
    ElMessage.error('添加失败: ' + e.message)
  }
}

function deleteEssay(id) {
  ElMessageBox.confirm('确定删除该论文吗？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id)
    ElMessage.success('删除成功')
    search()
  }).catch(() => {})
}

onMounted(() => {
  search()
  check_Item(route, router, 'essay', openAddDialog)
})
</script>

<style scoped>
.essay-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 24px; font-weight: bold; color: #333; }
.page-desc { margin: 8px 0 0; font-size: 14px; color: #999; }
.table-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; }
.pagination-wrapper { margin-top: 20px; text-align: center; padding: 16px 0; }
.upload-icon { font-size: 40px; color: #c0c4cc; margin-bottom: 8px; }
.prefill-cert-tip {
  display: flex; align-items: center; gap: 8px;
  color: #67c23a; padding: 12px; background: #f0f9eb; border-radius: 8px;
}
</style>
