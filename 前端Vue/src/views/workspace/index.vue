<template>
  <div class="workspace-page">
    <div class="page-header">
      <h2>工作台</h2>
      <p class="page-desc">欢迎回来，{{ userInfo?.realName || userInfo?.username || '老师' }}！</p>
    </div>

    <!-- 教师统计卡片 -->
    <el-row :gutter="20" class="stats-row" >
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon blue">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalAchievements || 0 }}</div>
              <div class="stat-label">科研成果总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon orange">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingReview || 0 }}</div>
              <div class="stat-label">待审核成果</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon green">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.ongoingProjects || 0 }}</div>
              <div class="stat-label">在研项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon purple">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.annualPoints || 0 }}</div>
              <div class="stat-label">年度科研积分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统管理员统计卡片（暂时不做了）
    <el-row :gutter="20" class="stats-row" >
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon blue">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ adminStats.totalUsers || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon orange">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ adminStats.totalAchievements || 0 }}</div>
              <div class="stat-label">科研成果总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon green">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ adminStats.pendingReview || 0 }}</div>
              <div class="stat-label">待审核成果</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon purple">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ adminStats.annualPoints || 0 }}</div>
              <div class="stat-label">年度科研积分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row> -->

    <!-- 快速录入 -->
    <el-card class="quick-entry-card" shadow="hover" v-if="!isAdmin">
      <template #header>
        <span class="card-title">快速录入</span>
        <span class="card-subtitle">上传证书图片，AI 自动识别并填写</span>
      </template>
      <div class="quick-entry-content" :class="{ 'is-loading': qeLoading }">
        <div v-if="qeLoading" class="quick-entry-loading">
          <el-icon class="is-loading" :size="32"><Loading /></el-icon>
          <span>正在识别中，请稍候...</span>
        </div>
        <el-upload
          v-else
          class="quick-entry-upload"
          drag
          :auto-upload="false"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.webp,.bmp"
          :disabled="qeLoading"
          :on-change="handleQuickEntryChange"
        >
          <el-icon class="upload-icon-large"><Upload /></el-icon>
          <div class="upload-text">点击或拖拽证书图片到此处</div>
          <div class="upload-hint">支持论文证书、专利证书、获奖证书、会议邀请函等</div>
        </el-upload>
      </div>
      <div class="footer-note" style="font-size: 10px; color: #999; margin-top: 8px;">
        —— 助师减负，尽己微力
      </div>
    </el-card>

    <!-- 快速入口 -->
    <el-card class="quick-actions-card" shadow="hover" v-if="!isAdmin">
      <template #header>
        <span class="card-title">快速入口</span>
      </template>
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/program')">
          <el-icon><MessageBox /></el-icon>
          <span>科研项目</span>
        </div>
        <div class="action-item" @click="$router.push('/essay')">
          <el-icon><Document /></el-icon>
          <span>发表论文</span>
        </div>
        <div class="action-item" @click="$router.push('/patent')">
          <el-icon><Opportunity /></el-icon>
          <span>专利成果</span>
        </div>
        <div class="action-item" @click="$router.push('/award')">
          <el-icon><Medal /></el-icon>
          <span>科研获奖</span>
        </div>
        <div class="action-item" @click="$router.push('/meeting')">
          <el-icon><Place /></el-icon>
          <span>学术会议</span>
        </div>
        <div class="action-item" @click="$router.push('/student')">
          <el-icon><Trophy /></el-icon>
          <span>指导学生获奖</span>
        </div>
        <div class="action-item" @click="$router.push('/other')">
          <el-icon><Expand /></el-icon>
          <span>其他科研成果</span>
        </div>
      </div>
    </el-card>

    <!-- 管理员快速入口 -->
    <el-card class="quick-actions-card" shadow="hover" v-if="isAdmin">
      <template #header>
        <span class="card-title">审核管理</span>
      </template>
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/admin')">
          <el-icon><Setting /></el-icon>
          <span>审核管理</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Folder, Clock, Document, Trophy, MessageBox, Opportunity, Medal, Place, Expand, Upload, Loading } from '@element-plus/icons-vue'
import { getDashboardStats } from '@/api/statistics.js'
import { quickEntryApi } from '@/api/file.js'

const router = useRouter()
var userInfo = ref(null)
var isAdmin = ref(false)
var qeLoading = ref(false)

var stats = ref({
  totalAchievements: 0,
  pendingReview: 0,
  ongoingProjects: 0,
  annualPoints: 0
})

// 快速录入路由映射
var QE_ROUTES = { essay: '/essay', patent: '/patent', award: '/award', meeting: '/meeting' }

function loadUserInfo() {
  var str = localStorage.getItem('userInfo')
  if (!str) return
  try {
    userInfo.value = JSON.parse(str)
    isAdmin.value = userInfo.value?.role === 'admin'
  } catch(e) { /* ignore */ }
}

async function loadStats() {
  try {
    var uid = localStorage.getItem('userId')
    var res = await getDashboardStats(uid)
    if (res.code === 1) stats.value = res.data
  } catch(e) {
    console.log(e)
    ElMessage.error('加载统计失败')
  }
}

const handleQuickEntryChange = async (uploadFile) => {
  var file = uploadFile?.raw
  if (!file || !file.type?.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  qeLoading.value = true
  try {
    var res = await quickEntryApi(file)
    if (res.code !== 1) { ElMessage.error(res.msg || '识别失败'); return }
    var { type, data = {}, certificatePath, certificateName, certificateSize } = res.data || {}
    var route = QE_ROUTES[type]
    if (!route) { ElMessage.warning('未能识别证书类型'); return }
    sessionStorage.setItem('quickEntryPrefill', JSON.stringify({
      type,
      data: { ...data, certificatePath, certificateName, certificateSize }
    }))
    ElMessage.success('识别成功，正在跳转...')
    await router.push(route + '?prefill=1')
  } catch(err) {
    ElMessage.error('快速录入失败')
  } finally {
    qeLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
  if (!isAdmin.value) loadStats()
})
</script>

<style scoped>
.workspace-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px); }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133; }
.page-desc { margin: 0; font-size: 14px; color: #909399; }
.stats-row { margin-bottom: 20px; }
.stat-card { border-radius: 8px; }
.stat-content { display: flex; align-items: center; }
.stat-icon {
  width: 60px; height: 60px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  margin-right: 16px;
}
.stat-icon .el-icon { font-size: 28px; color: #fff; }
.stat-icon.blue { background: #667eea; }
.stat-icon.orange { background: #f5576c; }
.stat-icon.green { background: #4facfe; }
.stat-icon.purple { background: #43e97b; }
.stat-info { flex: 1; }
.stat-number { font-size: 28px; font-weight: 700; color: #303133; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
.quick-entry-card { border-radius: 8px; margin-bottom: 20px; }
.quick-entry-card .card-subtitle { font-size: 12px; color: #909399; margin-left: 8px; }
.quick-entry-content { position: relative; min-height: 160px; }
.quick-entry-loading {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 40px; color: #999; gap: 12px;
}
.quick-entry-upload { width: 100%; }
.quick-entry-upload :deep(.el-upload-dragger) { width: 100%; padding: 24px; }
.upload-icon-large { font-size: 48px; color: #409eff; margin-bottom: 12px; }
.upload-text { font-size: 16px; color: #303133; margin-bottom: 8px; }
.upload-hint { font-size: 12px; color: #909399; }
.quick-actions-card { border-radius: 8px; }
.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}
.action-item {
  display: flex; flex-direction: column; align-items: center;
  justify-content: center; padding: 24px;
  background: #fafafa; border-radius: 8px; cursor: pointer;
}
.action-item:hover { background: #eee; }
.action-item .el-icon { font-size: 32px; color: #409eff; margin-bottom: 8px; }
.action-item span { font-size: 14px; color: #606266; }
</style>
