<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Plus, Delete, View, Document } from '@element-plus/icons-vue'
import { searchApi, addApi, auditProgram as auditApi, deleteApi } from '@/api/program.js'
import { uploadApi } from '@/api/file.js'

var router = useRouter()
var userInfo = ref(null)

var checkAdmin = computed(() => {
  var s = localStorage.getItem('userInfo')
  if (s) { try { userInfo.value = JSON.parse(s); return userInfo.value?.role === 'admin' } catch(e) { return false } }
  return false
})

var projectList = ref([])
var currentPage = ref(1), pageSize = ref(10), total = ref(0)

async function search() {
  try {
    var uid = localStorage.getItem('userId')
    var result = await searchApi(currentPage.value, pageSize.value, uid, null)
    if (result.code === 1) {
      projectList.value = result.data.rows || []
      total.value = result.data.total || 0
    } else {
      ElMessage.error(result.msg || '查询失败')
    }
  } catch(e) { ElMessage.error('搜索失败') }
}

function handleSizeChange(v) { pageSize.value = v; currentPage.value = 1; search() }
function handleCurrentChange(v) { currentPage.value = v; search() }

var addDialogVisible = ref(false)
var projectType = ref('纵向')

var emptyVertical = () => ({ id: Date.now(), level: '', title: '', startTime: '', completionDoc: null, completionDocPath: '', completionDocName: '', completionDocSize: '', fileList: [] })
var emptyHorizontal = () => ({ id: Date.now(), hasCompany: 0, companyName: '', signTime: '', executionPeriod: '', title: '', isCompleted: 0, expectedEndTime: '', completionDoc: null, completionDocPath: '', completionDocName: '', completionDocSize: '', fileList: [] })

var verticalProjects = ref([emptyVertical()])
var horizontalProjects = ref([emptyHorizontal()])

function openAddDialog() {
  addDialogVisible.value = true
  projectType.value = '纵向'
  verticalProjects.value = [emptyVertical()]
  horizontalProjects.value = [emptyHorizontal()]
}
function closeAddDialog() { addDialogVisible.value = false }

var verticalRules = {
  level: [{ required: true, message: '请选择项目级别', trigger: 'change' }],
  title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择下达时间', trigger: 'change' }],
  completionDoc: [{ required: true, message: '请上传结题材料', trigger: 'change' }]
}

function addVerticalProject() { verticalProjects.value.push(emptyVertical()) }
function removeVerticalProject(idx) {
  if (verticalProjects.value.length > 1) verticalProjects.value.splice(idx, 1)
  else ElMessage.warning('至少保留一个项目')
}
function addHorizontalProject() { horizontalProjects.value.push(emptyHorizontal()) }
function removeHorizontalProject(idx) {
  if (horizontalProjects.value.length > 1) horizontalProjects.value.splice(idx, 1)
  else ElMessage.warning('至少保留一个项目')
}

const handleVerticalFileChange = async (file, fileList, index) => {
  var p = verticalProjects.value[index]
  p.fileList = fileList; p.completionDoc = file.raw
  p.completionDocSize = file.raw.size; p.completionDocName = file.raw.name
  try {
    var r = await uploadApi(file.raw)
    p.completionDocPath = r.data
  } catch(e) { ElMessage.error('上传失败') }
}

const handleHorizontalFileChange = async (file, fileList, index) => {
  var p = horizontalProjects.value[index]
  p.fileList = fileList; p.completionDoc = file.raw
  p.completionDocSize = file.raw.size; p.completionDocName = file.raw.name
  try {
    var r = await uploadApi(file.raw)
    p.completionDocPath = r.data
  } catch(e) { ElMessage.error('上传失败') }
}

async function submitVerticalProjects() {
  var valid = true, errs = []
  verticalProjects.value.forEach((p, i) => {
    if (!p.level) { errs.push(`项目${i+1}：请选择项目级别`); valid = false }
    if (!p.title) { errs.push(`项目${i+1}：请输入项目名称`); valid = false }
    if (!p.startTime) { errs.push(`项目${i+1}：请选择下达时间`); valid = false }
    if (!p.completionDoc) { errs.push(`项目${i+1}：请上传结题材料`); valid = false }
  })
  if (!valid) { ElMessage.error(errs[0]); return }
  for (var p of verticalProjects.value) {
    try {
      await addApi({
        title: p.title, type: '纵向', level: p.level, startTime: p.startTime,
        endTime: '', status: '待审核', hasCompany: null, isCompleted: 1,
        completionDocName: p.completionDocName, completionDocSize: p.completionDocSize,
        completionDocPath: p.completionDocPath, fileList: p.fileList
      })
    } catch(e) { ElMessage.error(`项目 "${p.title}" 提交失败`); return }
  }
  ElMessage.success(`成功提交${verticalProjects.value.length}个项目`)
  closeAddDialog(); currentPage.value = 1; search()
}

async function submitHorizontalProjects() {
  var valid = true, errs = []
  horizontalProjects.value.forEach((p, i) => {
    if (p.hasCompany == null) { errs.push(`项目${i+1}：请选择是否签约企业`); valid = false }
    if (p.hasCompany === 1) {
      if (!p.companyName) { errs.push(`项目${i+1}：请输入企业名称`); valid = false }
      if (!p.signTime) { errs.push(`项目${i+1}：请选择签约时间`); valid = false }
    }
    if (!p.executionPeriod) { errs.push(`项目${i+1}：请输入执行周期`); valid = false }
    if (!p.title) { errs.push(`项目${i+1}：请输入项目名称`); valid = false }
    if (!p.isCompleted == 1 && !p.expectedEndTime) { errs.push(`项目${i+1}：请选择预计完成时间`); valid = false }
    if (p.isCompleted == 1 && !p.completionDoc) { errs.push(`项目${i+1}：请上传结题材料`); valid = false }
  })
  if (!valid) { ElMessage.error(errs[0]); return }
  for (var p of horizontalProjects.value) {
    try {
      await addApi({
        title: p.title, type: '横向', hasCompany: p.hasCompany, companyName: p.companyName,
        signTime: p.signTime, isCompleted: p.isCompleted, executionPeriod: p.executionPeriod,
        expectedEndTime: p.expectedEndTime, completionDoc: p.completionDoc, status: '待审核',
        completionDocName: p.completionDocName, completionDocSize: p.completionDocSize,
        completionDocPath: p.completionDocPath, fileList: p.fileList
      })
    } catch(e) { ElMessage.error(`项目 "${p.title}" 提交失败`); return }
  }
  ElMessage.success(`成功提交${horizontalProjects.value.length}个项目`)
  closeAddDialog(); currentPage.value = 1; search()
}

var completionDialogVisible = ref(false)
var currentProject = ref(null)
var completionFileList = ref([])
var completionDoc = ref(null)

function submitCompletionDoc(row) {
  if (row.type !== '横向' || row.isCompleted == 1) { ElMessage.warning('该项目已完成或不是横向项目'); return }
  currentProject.value = row; completionFileList.value = []; completionDoc.value = null
  completionDialogVisible.value = true
}

const handleCompletionFileChange = (file, fl) => { completionFileList.value = fl; completionDoc.value = file.raw }

function confirmSubmitCompletion() {
  if (!completionDoc.value) { ElMessage.warning('请先上传结题材料'); return }
  var idx = projectList.value.findIndex(item => item.id === currentProject.value.id)
  if (idx !== -1) {
    projectList.value[idx].isCompleted = 1
    projectList.value[idx].status = '待审核'
    ElMessage.success('结题材料提交成功')
    completionDialogVisible.value = false
  }
}

function viewDetail(row) {
  var detail = `项目名称：${row.title}\n项目类型：${row.type}\n`
  if (row.type === '纵向') {
    detail += `项目级别：${row.level}\n下达时间：${row.startTime}\n`
  } else {
    detail += `是否签约企业：${row.hasCompany}\n执行周期：${row.executionPeriod || '未填写'}\n`
    if (row.hasCompany === 1) detail += `企业名称：${row.companyName || '未填写'}\n签约时间：${row.signTime || '未填写'}\n`
    detail += `完成状态：${row.isCompleted==1 ? '已完成' : '未完成'}\n`
    if (!row.isCompleted == 1 && row.endTime) detail += `预计完成时间：${row.endTime}\n`
  }
  detail += `审核状态：${row.status}`
  ElMessageBox.alert(detail, '项目详情', { confirmButtonText: '确定' })
}

function deleteProject(id) {
  ElMessageBox.confirm('确定删除该项目？', '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    projectList.value = projectList.value.filter(item => item.id !== id)
    total.value = projectList.value.length
    try {
      var result = await deleteApi(id)
      if (result.code == 1) { ElMessage.success('删除成功'); search() }
      else ElMessage.error('删除失败: ' + result.msg)
    } catch(e) { ElMessage.error('删除失败') }
  }).catch(() => { ElMessage.info('已取消') })
}

onMounted(() => {
  var s = localStorage.getItem('userInfo')
  if (s) {
    try {
      var u = JSON.parse(s)
      if (u.role === 'admin') { router.push('/admin'); return }
    } catch(e) {}
  }
  search()
})
</script>

<template>
  <div class="project-page">
    <!-- 标题 -->
    <div class="page-header">
      <h2>科研项目</h2>
      <p class="page-desc">管理您的科研项目信息，支持纵向和横向项目的录入与管理</p>
    </div>

    <!-- 表格区域：展示已提交的项目 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Document /></el-icon>
            已提交项目
          </span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">
            添加项目
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="projectList"  <!-- 改为 projectList -->
        border 
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="250" show-overflow-tooltip />
        <el-table-column prop="type" label="项目类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '纵向' ? 'primary' : 'success'" effect="plain">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="项目信息" min-width="200">
          <template #default="{ row }">
            <div v-if="row.type === '纵向'">
              <div><strong>级别：</strong>{{ row.level }}</div>
              <div><strong>下达时间：</strong>{{ row.startTime }}</div>
            </div>
            <div v-else>
              <div><strong>执行周期：</strong>{{ row.executionPeriod || '未填写' }}</div>
              <div v-if="row.hasCompany === 1">
                <strong>签约企业：</strong>{{ row.companyName || '未填写' }}
              </div>
              <div><strong>完成状态：</strong>
                <el-tag :type="row.isCompleted == 1 ? 'success' : 'warning'" size="small">
                  {{ row.isCompleted == 1 ? '已完成' : '未完成' }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已通过' ? 'success' : row.status === '待审核' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              :icon="View" 
              @click="viewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button 
              v-if="row.type === '横向' && row.isCompleted != 1"
              size="small" 
              type="success" 
              @click="submitCompletionDoc(row)"
            >
              提交结题
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              :icon="Delete" 
              @click="deleteProject(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
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

    <!-- 添加项目对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增科研项目"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <!-- 项目类型选择 -->
      <div class="type-selector">
        <el-radio-group v-model="projectType" size="large">
          <el-radio-button label="纵向">纵向项目</el-radio-button>
          <el-radio-button label="横向">横向项目</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 纵向项目表单 -->
      <div v-if="projectType === '纵向'" class="vertical-form">
        <div 
          v-for="(project, index) in verticalProjects" 
          :key="project.id" 
          class="project-item"
        >
          <div class="item-header">
            <span class="item-title">项目 {{ index + 1 }}</span>
            <el-button 
              v-if="verticalProjects.length > 1"
              type="danger" 
              :icon="Delete" 
              size="small" 
              circle
              @click="removeVerticalProject(index)"
            />
          </div>
          
          <el-form :model="project" label-width="120px" class="project-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="项目级别">
                  <el-select v-model="project.level" placeholder="请选择级别" style="width: 100%">
                    <el-option label="国家级" value="国家级" />
                    <el-option label="省部级" value="省部级" />
                    <el-option label="地市级" value="地市级" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="项目名称">
                  <el-input v-model="project.title" placeholder="请输入项目名称" />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="下达时间">
                  <el-date-picker
                    v-model="project.startTime"
                    type="date"
                    placeholder="选择下达时间"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="结题材料">
                  <el-upload
                    action="#"
                    :auto-upload="false"
                    :on-change="(file, fileList) => handleVerticalFileChange(file, fileList, index)"
                    :file-list="project.fileList"
                    accept=".pdf,.doc,.docx,.txt"
                    :limit="1"
                    drag
                  >
                    <el-icon class="upload-icon"><Upload /></el-icon>
                    <div class="el-upload__text">点击或拖拽文件到此处上传</div>
                    <template #tip>
                      <div class="el-upload__tip">支持 PDF、DOC、DOCX、TXT 格式，单个文件</div>
                    </template>
                  </el-upload>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        
        <div class="form-actions">
          <el-button type="primary" :icon="Plus" @click="addVerticalProject">  
            添加项目
          </el-button>
        </div>
      </div>

      <!-- 横向项目表单 -->  
      <div v-if="projectType === '横向'" class="horizontal-form">
        <div 
          v-for="(project, index) in horizontalProjects" 
          :key="project.id" 
          class="project-item"
        >
          <div class="item-header">
            <span class="item-title">项目 {{ index + 1 }}</span>
            <el-button 
              v-if="horizontalProjects.length > 1"
              type="danger" 
              :icon="Delete" 
              size="small" 
              circle
              @click="removeHorizontalProject(index)"
            />
          </div>
          
          <el-form :model="project" label-width="140px" class="project-form">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="是否签约企业">
                  <el-radio-group v-model="project.hasCompany">
                    <el-radio :label="1">是</el-radio>
                    <el-radio :label="0">否</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12" v-if="project.hasCompany === 1">
                <el-form-item label="签约企业">
                  <el-input v-model="project.companyName" placeholder="请输入企业名称" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" v-if="project.hasCompany === 1">
              <el-col :span="12">
                <el-form-item label="签约时间">
                  <el-date-picker
                    v-model="project.signTime"
                    type="date"
                    placeholder="选择签约日期"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="执行周期">
                  <el-input v-model="project.executionPeriod" placeholder="例如：12个月" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="项目名称">
                  <el-input v-model="project.title" placeholder="请输入项目名称" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="项目完成状态">
              <el-radio-group v-model="project.isCompleted">
                <el-radio :label="1">已完成</el-radio>
                <el-radio :label="0">未完成</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item 
              v-if="!project.isCompleted == 1" 
              label="预计完成时间"
            >
              <el-date-picker
                v-model="project.expectedEndTime"
                type="date"
                placeholder="选择预计完成时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 300px"
              />
            </el-form-item>

            <el-form-item 
              v-if="project.isCompleted == 1" 
              label="结题材料"
            >
              <el-upload
                action="#"
                :auto-upload="false"
                :on-change="(file, fileList) => handleHorizontalFileChange(file, fileList, index)"
                :file-list="project.fileList"
                accept=".pdf,.doc,.docx,.txt"
                :limit="1"
                drag
              >
                <el-icon class="upload-icon"><Upload /></el-icon>
                <div class="el-upload__text">点击或拖拽文件到此处上传</div>
                <template #tip>
                  <div class="el-upload__tip">支持 PDF、DOC、DOCX、TXT 格式，单个文件</div>
                </template>
              </el-upload>
            </el-form-item>
          </el-form>
        </div>
        
        <div class="form-actions">
          <el-button type="primary" :icon="Plus" @click="addHorizontalProject">
            添加项目
          </el-button>
        </div>
      </div>

      <template #footer>
        <el-button @click="closeAddDialog">取消</el-button>
        <el-button 
          type="primary" 
          @click="projectType === '纵向' ? submitVerticalProjects() : submitHorizontalProjects()"
        >
          批量提交 ({{ projectType === '纵向' ? verticalProjects.length : horizontalProjects.length }})
        </el-button>
      </template>
    </el-dialog>

    <!-- 提交结题材料对话框 -->
    <el-dialog
      v-model="completionDialogVisible"
      title="提交结题材料"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentProject">
        <p style="margin-bottom: 16px; color: #606266;">
          项目名称：<strong>{{ currentProject.title }}</strong>
        </p>
        <el-upload
          action="#"
          :auto-upload="false"
          :on-change="handleCompletionFileChange"
          :file-list="completionFileList"
          accept=".pdf,.doc,.docx,.txt"
          :limit="1"
          drag
        >
          <el-icon class="upload-icon"><Upload /></el-icon>
          <div class="el-upload__text">点击或拖拽文件到此处上传</div>
          <template #tip>
            <div class="el-upload__tip">支持 PDF、DOC、DOCX、TXT 格式，单个文件</div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <el-button @click="completionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubmitCompletion">确定提交</el-button>
      </template>
    </el-dialog>
  </div>

  

</template>

<style scoped>
.project-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px) }
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.table-card { border-radius: 8px }
.card-header { display: flex; justify-content: space-between; align-items: center }
.card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; color: #303133 }
.pagination-wrapper {
  margin-top: 20px; display: flex; justify-content: center;
  padding: 16px 0; background: #fff; border-top: 1px solid #e4e7ed;
}
.type-selector {
  margin-bottom: 20px; padding: 16px; background: #f5f7fa;
  border-radius: 6px; display: flex; justify-content: center;
}
.vertical-form, .horizontal-form { max-height: 500px; overflow-y: auto; padding-right: 8px }
.project-item { margin-bottom: 20px; padding: 16px; background: #fafafa; border-radius: 6px; border: 1px solid #e4e7ed }
.item-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #e4e7ed;
}
.item-title { font-size: 15px; font-weight: 600; color: #409eff }
.project-form { margin-top: 12px }
.form-actions { margin-top: 20px; padding-top: 16px; border-top: 1px solid #e4e7ed; display: flex; justify-content: center }
.el-form-item { margin-bottom: 18px }
.el-upload { width: 100% }
.upload-icon { font-size: 40px; color: #c0c4cc; margin-bottom: 8px }
.el-upload__text { color: #606266; font-size: 14px }
.el-upload__tip { color: #909399; font-size: 12px; margin-top: 8px }
:deep(.el-card__header) { background: #fafafa; border-bottom: 1px solid #e4e7ed; padding: 16px 20px }
:deep(.el-card__body) { padding: 20px }
:deep(.el-table) { font-size: 14px }
:deep(.el-table th) { font-weight: 600 }
:deep(.el-button + .el-button) { margin-left: 8px }
:deep(.el-upload-dragger) { width: 100%; padding: 30px 20px }
:deep(.el-dialog__body) { padding: 20px; max-height: 70vh; overflow-y: auto }
:deep(.el-radio-group) { display: flex; gap: 12px }
.vertical-form::-webkit-scrollbar, .horizontal-form::-webkit-scrollbar { width: 6px }
.vertical-form::-webkit-scrollbar-track, .horizontal-form::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 3px }
.vertical-form::-webkit-scrollbar-thumb, .horizontal-form::-webkit-scrollbar-thumb { background: #c1c1c1; border-radius: 3px }
.vertical-form::-webkit-scrollbar-thumb:hover, .horizontal-form::-webkit-scrollbar-thumb:hover { background: #a8a8a8 }
</style>