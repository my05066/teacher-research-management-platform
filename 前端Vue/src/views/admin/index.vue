<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>管理员审核</h2>
      <p class="page-desc">审核教师提交的科研成果</p>
    </div>

    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="模块类型">
          <el-select v-model="filterForm.moduleType" @change="handleModuleChange" style="width: 200px">
            <el-option label="科研项目" value="program" />
            <el-option label="发表论文" value="essay" />
            <el-option label="专利成果" value="patent" />
            <el-option label="科研获奖" value="award" />
            <el-option label="学术会议" value="meeting" />
            <el-option label="指导学生获奖" value="student-award" />
            <el-option label="其他科研成果" value="other-achievement" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" @change="search" style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <el-table :data="auditList" border style="width: 100%" stripe v-loading="loading">
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题/名称" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ getTitle(row) }}
          </template>
        </el-table-column>
        <el-table-column label="提交人信息" min-width="200" align="center">
          <template #default="{ row }">
            <div style="text-align: left; line-height: 1.8;">
              <div><strong>姓名：</strong>{{ row.userName || '未知' }}</div>
              <div><strong>工号：</strong>{{ row.employeeId || '未知' }}</div>
              <div><strong>手机：</strong>{{ row.phone || '未填写' }}</div>
              <div><strong>部门：</strong>{{ row.department || '未填写' }}</div>
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
        <el-table-column prop="createTime" label="提交时间" width="180" align="center" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleAudit(row.id, '已通过')" v-if="row.status === '待审核'">
              通过
            </el-button>
            <el-button size="small" type="danger" @click="handleAudit(row.id, '已驳回')" v-if="row.status === '待审核'">
              驳回
            </el-button>
            <el-button size="small" type="primary" @click="viewDetail(row)">
              详情
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

    <!-- 审核对话框 -->
    <el-dialog v-model="auditDialogVisible" :title="auditStatus === '已通过' ? '审核通过' : '审核驳回'" width="500px">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAudit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      :title="`详情 - ${getTitle(currentDetail)}`" 
      width="800px"
      top="5vh"
      class="detail-dialog"
    >
      <div class="detail-content" v-loading="detailLoading">
        <div v-if="currentDetail">
          <!-- 基础信息 -->
          <div class="basic-info">
            <h3>基础信息</h3>
            <div class="info-row">
              <div class="info-item">
                <label>标题/名称：</label>
                <span>{{ currentDetail.title || currentDetail.projectName || currentDetail.name || currentDetail.awardName || currentDetail.meetingName || currentDetail.studentName || '无' }}</span>
              </div>
              <div class="info-item">
                <label>提交人：</label>
                <span>{{ currentDetail.userName || '未知' }}</span>
              </div>
              <div class="info-item">
                <label>工号：</label>
                <span>{{ currentDetail.employeeId || '未知' }}</span>
              </div>
              <div class="info-item">
                <label>手机号：</label>
                <span>{{ currentDetail.phone || '未填写' }}</span>
              </div>
              <div class="info-item">
                <label>部门：</label>
                <span>{{ currentDetail.department || '未填写' }}</span>
              </div>
              <div class="info-item">
                <label>提交时间：</label>
                <span>{{ currentDetail.createTime || '未知' }}</span>
              </div>
              <div class="info-item">
                <label>审核状态：</label>
                <el-tag :type="currentDetail.status === '已通过' ? 'success' : currentDetail.status === '待审核' ? 'warning' : 'danger'">
                  {{ currentDetail.status }}
                </el-tag>
              </div>
            </div>
          </div>

          <!-- 模块特有信息 -->
          <div class="module-specific-info" v-if="moduleSpecificInfo.length > 0">
            <h3>详细信息</h3>
            <div class="info-row">
              <div v-for="(item, index) in moduleSpecificInfo" :key="index" class="info-item">
                <label>{{ item.label }}：</label>
                <span v-if="item.type === 'link'">
                  <a :href="item.value" target="_blank">{{ item.value }}</a>
                </span>
                <span v-else-if="item.type === 'text'">{{ item.value }}</span>
                <span v-else>{{ item.value || '无' }}</span>
              </div>
            </div>
          </div>

          <!-- 附件/图片 -->
          <div class="attachments-section" v-if="attachments.length > 0">
            <h3>附件/证明材料</h3>
            <div class="attachments-grid">
              <div 
                v-for="(attachment, index) in attachments" 
                :key="'att-' + index" 
                class="attachment-item"
              >
                <div class="attachment-preview">
                  <img 
                    v-if="isImage(attachment)" 
                    :src="attachment.url" 
                    :alt="attachment.name"
                    @click="previewImage(attachment)"
                  />
                  <div v-else class="file-placeholder">
                    <el-icon :size="32"><Document /></el-icon>
                    <span>{{ attachment.name }}</span>
                  </div>
                </div>
                <div class="attachment-info">
                  <a :href="attachment.url" target="_blank" download>{{ attachment.name }}</a>
                  <small>大小: {{ formatFileSize(attachment.size) }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-data">暂无数据</div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" title="图片预览" width="800px" append-to-body>
      <img :src="currentPreviewImage" alt="预览图片" style="width: 100%; height: auto;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { isAdmin } from '@/utils/auth.js'
import { searchApi, auditProgram as auditApi } from '@/api/program.js'
import { getEssayList as essaySearchApi, auditEssay as essayAuditApi } from '@/api/essay.js'
import { queryPatentList as patentSearchApi, auditPatent as patentAuditApi } from '@/api/patent.js'
import { fetchAwardPage as awardSearchApi, doAuditAward as awardAuditApi } from '@/api/award.js'
import { getMeetingPage as meetingSearchApi, auditMeeting as meetingAuditApi } from '@/api/meeting.js'
import { listStudentAward as studentSearchApi, auditStudentAward as studentAuditApi } from '@/api/student.js'
import { getOtherPage as otherSearchApi, auditOther as otherAuditApi } from '@/api/other.js'

let auditList = ref([])
let currentPage = ref(1), pageSize = ref(10), total = ref(0)
let loading = ref(false)
let filterForm = ref({ moduleType: 'program', status: '' })
let auditDialogVisible = ref(false)
let currentAuditId = ref(null)
let auditStatus = ref('已通过')
let detailDialogVisible = ref(false)
let detailLoading = ref(false)
let currentDetail = ref({})
let imagePreviewVisible = ref(false)
let currentPreviewImage = ref('')
let auditForm = ref({ auditRemark: '' })

const apiMap = {
  program: { search: searchApi, audit: auditApi },
  essay: { search: essaySearchApi, audit: essayAuditApi },
  patent: { search: patentSearchApi, audit: patentAuditApi },
  award: { search: awardSearchApi, audit: awardAuditApi },
  meeting: { search: meetingSearchApi, audit: meetingAuditApi },
  'student-award': { search: studentSearchApi, audit: studentAuditApi },
  'other-achievement': { search: otherSearchApi, audit: otherAuditApi }
}

let moduleSpecificInfo = ref([])
let attachments = ref([])

function getTitle(row) {
  return row.title || row.projectName || row.name || row.awardName || row.meetingName || row.studentName || '未知'
}

async function search() {
  loading.value = true
  try {
    var api = apiMap[filterForm.value.moduleType].search
    var result = await api(currentPage.value, pageSize.value, null, filterForm.value.status)
    if (result.code === 1) {
      auditList.value = result.data.rows || []
      total.value = result.data.total || 0
    } else {
      ElMessage.error(result.msg || '查询失败')
    }
  } catch(e) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

function handleModuleChange() { currentPage.value = 1; search() }
const handleSizeChange = v => { pageSize.value = v; currentPage.value = 1; search() }
const handleCurrentChange = v => { currentPage.value = v; search() }


function resetFilter() {
  filterForm.value = { moduleType: 'program', status: '' }
  currentPage.value = 1
  search()
}

function handleAudit(id, status) {
  currentAuditId.value = id
  auditStatus.value = status
  auditForm.value.auditRemark = ''
  auditDialogVisible.value = true
}

async function confirmAudit() {
  try {
    var api = apiMap[filterForm.value.moduleType].audit
    var uid = localStorage.getItem('userId') || 1
    var res = await api(currentAuditId.value, auditStatus.value, uid, auditForm.value.auditRemark)
    if (res.code === 1) {
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      search()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch(e) { ElMessage.error('审核失败') }
}

function viewDetail(row) {
  currentDetail.value = row
  detailDialogVisible.value = true
  parseModuleInfo(row)
  parseAttachments(row)
}

function parseModuleInfo(row) {
  moduleSpecificInfo.value = []
  var mod = filterForm.value.moduleType
  if (mod === 'program') {
    moduleSpecificInfo.value = [
      { label: '项目名称', value: row.title },
      { label: '项目类型', value: row.type },
      { label: '项目级别', value: row.level },
      { label: '下达时间', value: row.startTime },
      { label: '是否签约企业', value: row.hasCompany === 1 ? '是' : '否' },
      { label: '企业名称', value: row.companyName },
      { label: '签约时间', value: row.signTime },
      { label: '执行周期', value: row.executionPeriod },
      { label: '是否完成', value: row.isCompleted === 1 ? '是' : '否' },
      { label: '完成文档', value: row.completionDocName || row.completionDoc },
      { label: '状态', value: row.status }
    ]
  } else if (mod === 'essay') {
    moduleSpecificInfo.value = [
      { label: '论文题目', value: row.title },
      { label: '论文等级', value: row.level },
      { label: '作者信息', value: row.authorType },
      { label: '期刊名称', value: row.journalName },
      { label: '发表时间', value: row.publishTime },
      { label: '状态', value: row.status }
    ]
  } else if (mod === 'patent') {
    moduleSpecificInfo.value = [
      { label: '专利号', value: row.patentNumber },
      { label: '专利名称', value: row.name },
      { label: '获批时间', value: row.approvalTime },
      { label: '专利类型', value: row.type },
      { label: '状态', value: row.status }
    ]
  } else if (mod === 'award') {
    moduleSpecificInfo.value = [
      { label: '获奖名称', value: row.awardName },
      { label: '获奖级别', value: row.level },
      { label: '获奖时间', value: row.awardTime },
      { label: '颁奖机构', value: row.awardOrg }
    ]
  } else if (mod === 'meeting') {
    moduleSpecificInfo.value = [
      { label: '会议名称', value: row.meetingName },
      { label: '会议类别', value: row.category },
      { label: '是否做报告', value: row.hasReport === 1 ? '是' : '否' },
      { label: '报告时间', value: row.reportTime },
      { label: '会议时间', value: row.meetingTime },
      { label: '会议地点', value: row.meetingLocation }
    ]
  } else if (mod === 'student-award') {
    moduleSpecificInfo.value = [
      { label: '学生负责人姓名', value: row.studentName },
      { label: '获奖项目名称', value: row.awardName },
      { label: '获奖类型', value: row.awardType },
      { label: '获奖时间', value: row.awardTime }
    ]
  } else if (mod === 'other-achievement') {
    moduleSpecificInfo.value = [{ label: '描述', value: row.description }]
  }
}

function parseAttachments(row) {
  attachments.value = []
  var mod = filterForm.value.moduleType
  if (mod === 'program' && row.completionDocPath) {
    attachments.value.push({ name: row.completionDocName || row.completionDoc || '结题文档', url: row.completionDocPath, size: row.completionDocSize })
  } else if ((mod === 'essay' || mod === 'patent' || mod === 'award' || mod === 'student-award') && row.certificatePath) {
    attachments.value.push({ name: row.certificateName || '证书文件', url: row.certificatePath, size: row.certificateSize })
  } else if ((mod === 'meeting' || mod === 'other-achievement') && row.evidencePath) {
    attachments.value.push({ name: row.evidenceName || '证明文件', url: row.evidencePath, size: row.evidenceSize })
  }
}

function isImage(att) {
  return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(att.url)
}

function formatFileSize(size) {
  if (!size) return '未知'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

function previewImage(att) {
  if (isImage(att)) { currentPreviewImage.value = att.url; imagePreviewVisible.value = true }
}

onMounted(() => {
  if (!isAdmin()) { ElMessage.warning('您没有权限访问此页面'); return }
  search()
})
</script>

<style scoped>
.admin-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px) }
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.filter-card, .table-card { border-radius: 8px; margin-bottom: 20px }
.pagination-wrapper { margin-top: 20px; display: flex; justify-content: center; padding: 16px 0 }
.detail-content { max-height: 60vh; overflow-y: auto }
.basic-info, .module-specific-info { margin-bottom: 20px }
.basic-info h3, .module-specific-info h3, .attachments-section h3 {
  margin: 0 0 15px; padding-bottom: 8px;
  border-bottom: 1px solid #eee; color: #303133; font-size: 16px;
}
.info-row { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 12px }
.info-item { display: flex; flex-direction: column; margin-bottom: 10px }
.info-item label { font-weight: bold; color: #606266; margin-bottom: 4px; font-size: 14px }
.info-item span, .info-item a { color: #303133; word-break: break-word }
.attachments-section { margin-top: 20px }
.attachments-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); gap: 15px }
.attachment-item { border: 1px solid #dcdfe6; border-radius: 4px; padding: 10px; text-align: center }
.attachment-preview { margin-bottom: 8px }
.attachment-preview img { width: 100%; height: 120px; object-fit: cover; border-radius: 4px; cursor: pointer }
.file-placeholder {
  height: 120px; display: flex; flex-direction: column;
  justify-content: center; align-items: center;
  background: #f5f7fa; border-radius: 4px; padding: 10px;
}
.file-placeholder i { font-size: 24px; color: #909399; margin-bottom: 5px }
.file-placeholder span { font-size: 12px; color: #909399; word-break: break-all }
.attachment-info { text-align: left }
.attachment-info a {
  display: block; white-space: nowrap; overflow: hidden;
  text-overflow: ellipsis; color: #409eff; margin-bottom: 4px; font-size: 13px;
}
.attachment-info small { color: #909399; font-size: 12px }
.no-data { text-align: center; color: #909399; padding: 20px }
.detail-dialog :deep(.el-dialog__body) { padding: 20px }
</style>