<template>
  <div class="meeting-page">
    <div class="page-header">
      <h2>学术会议</h2>
      <p class="page-desc">管理您的学术会议信息</p>
    </div>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Place /></el-icon>
            已提交会议
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加会议
          </el-button>
        </div>
      </template>

      <el-table :data="meetingList" border style="width: 100%" stripe>
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="meetingName" label="会议名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="category" label="会议类别" width="150" align="center" />
        <el-table-column prop="hasReport" label="是否做报告" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.hasReport === 1 ? 'success' : 'info'">
              {{ row.hasReport === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="meetingTime" label="会议时间" width="120" align="center" />
        <el-table-column prop="meetingLocation" label="会议地点" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="danger" :icon="Delete" @click="deleteMeeting(row.id)">
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

    <!-- 添加会议对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增学术会议" width="700px" :close-on-click-modal="false">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="会议名称" prop="meetingName">
          <el-input v-model="form.meetingName" placeholder="请输入会议名称" />
        </el-form-item>
        <el-form-item label="会议类别" prop="category">
          <el-select v-model="form.category" placeholder="请选择类别" style="width: 100%">
            <el-option label="国际学术会议" value="国际学术会议" />
            <el-option label="国内学术会议" value="国内学术会议" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否做报告" prop="hasReport">
          <el-radio-group v-model="form.hasReport">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.hasReport === 1" label="报告时间">
          <el-date-picker
            v-model="form.reportTime"
            type="date"
            placeholder="选择报告时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="会议时间">
          <el-date-picker
            v-model="form.meetingTime"
            type="date"
            placeholder="选择会议时间"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="会议地点">
          <el-input v-model="form.meetingLocation" placeholder="请输入会议地点" />
        </el-form-item>
        <el-form-item label="报告邀请函照片" :required="!prefillCert">
          <div v-if="prefillCert" class="prefill-cert-tip">
            <el-icon><CircleCheck /></el-icon>
            <span>邀请函已通过快速录入上传</span>
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
            <div class="el-upload__text">点击或拖拽报告邀请函照片到此处上传</div>
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
import { Plus, Delete, Place, Upload, CircleCheck } from '@element-plus/icons-vue'
import { getMeetingPage as searchApi, addMeeting as addApi, removeMeeting as deleteApi } from '@/api/meeting.js'
import { uploadApi } from '@/api/file.js'
import { check_Item } from '@/common/quick_fill.js'

var meetingList = ref([])
var currentPage = ref(1)
var pageSize = ref(10)
var total = ref(0)
var addDialogVisible = ref(false)
var formRef = ref(null)
var fileList = ref([])
var evidenceFile = ref(null)
var prefillCert = ref(null)
var filePath = ref(''), fileName = ref(''), fileSize = ref(0)

var form = ref({
  meetingName: '', category: '', hasReport: 0,
  reportTime: '', meetingTime: '', meetingLocation: ''
})

var rules = {
  meetingName: [{ required: true, message: '请输入会议名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择会议类别', trigger: 'change' }],
  hasReport: [{ required: true, message: '请选择是否做报告', trigger: 'change' }]
}

async function doSearch() {
  try {
    var res = await searchApi(currentPage.value, pageSize.value, localStorage.getItem('userId'))
    if (res.code === 1) {
      meetingList.value = res.data.rows
      total.value = res.data.total
    }
  } catch(e) {
    ElMessage.error('查询失败')
  }
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

function handleSizeChange(v) { pageSize.value = v; currentPage.value = 1; doSearch() }
function handleCurrentChange(v) { currentPage.value = v; doSearch() }

function openAddDialog(pf) {
  addDialogVisible.value = true
  var hr = 0
  if (pf?.hasReport !== undefined) hr = Number(pf.hasReport) || 0
  form.value = {
    meetingName: pf?.meetingName ?? '', category: pf?.category ?? '',
    hasReport: hr, reportTime: pf?.reportTime ?? '',
    meetingTime: pf?.meetingTime ?? '', meetingLocation: pf?.meetingLocation ?? ''
  }
  fileList.value = []; evidenceFile.value = null; prefillCert.value = null
  if (pf?.certificatePath) {
    prefillCert.value = { path: pf.certificatePath, name: pf.certificateName, size: pf.certificateSize }
  }
}

async function submitForm() {
  await formRef.value.validate()
  var ep = null, en = null, es = null
  if (prefillCert.value) {
    ep = prefillCert.value.path; en = prefillCert.value.name; es = prefillCert.value.size
  } else if (evidenceFile.value) {
    ep = filePath.value; en = fileName.value; es = fileSize.value
  }
  if (!ep) { ElMessage.warning('请上传报告邀请函照片'); return }
  try {
    await addApi({ ...form.value, evidencePath: ep, evidenceName: en, evidenceSize: es })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    currentPage.value = 1
    doSearch()
  } catch(e) { ElMessage.error('添加失败') }
}

function deleteMeeting(id) {
  ElMessageBox.confirm('确定删除该会议？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    await deleteApi(id)
    ElMessage.success('删除成功')
    doSearch()
  }).catch(() => {})
}

var route = useRoute()
var router = useRouter()
onMounted(() => { doSearch(); check_Item(route, router, 'meeting', openAddDialog) })
</script>

<style scoped>
.meeting-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}
.page-header {
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
}
.page-desc {
  font-size: 14px;
  color: #909399;
}
.table-card { border-radius: 8px }
.card-header { display:flex; justify-content:space-between; align-items:center }
.card-title { display:flex; align-items:center; gap:8px; font-size:16px; font-weight:600 }
.pagination-wrapper { margin-top:20px; display:flex; justify-content:center; padding:16px 0 }
.upload-icon { font-size:40px; color:#c0c4cc; margin-bottom:8px }
.prefill-cert-tip {
  display:flex; align-items:center; gap:8px;
  color:#67c23a; padding:12px; background:#f0f9eb; border-radius:8px
}
</style>
