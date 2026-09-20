<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, EditPen, SwitchButton, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
var userInfo = ref(null)

function loadUserInfo() {
  var str = localStorage.getItem('userInfo')
  if (!str) { userInfo.value = null; return }
  try {
    userInfo.value = JSON.parse(str)
  } catch(e) {
    userInfo.value = null
  }
}

const isAdmin = computed(() => userInfo.value?.role === 'admin')

onMounted(() => {
  loadUserInfo()
  window.addEventListener('storage', e => {
    if (e.key === 'userInfo') loadUserInfo()
  })
  // 定时同步用户信息
  setInterval(() => {
    var cur = localStorage.getItem('userInfo')
    var parsed = cur ? JSON.parse(cur) : null
    if (JSON.stringify(parsed) !== JSON.stringify(userInfo.value)) loadUserInfo()
  }, 500)
})

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.clear()
    userInfo.value = null
    ElMessage.success('退出成功')
    router.push('/login')
  }).catch(() => {})
}

function goToPassword() {
  router.push('/pc')
  setTimeout(() => window.dispatchEvent(new CustomEvent('switchToPassword')), 100)
}
</script>

<template>
  <div class="common-layout">
    <el-container>
      <!-- Header 区域 -->
      <el-header class="header">
        <span class="title">教师科研成果管理平台</span>
        <span class="right_tool">
          <a @click="goToPassword" style="cursor: pointer;">
            <el-icon><EditPen /></el-icon> 修改密码 &nbsp;&nbsp;&nbsp; |  &nbsp;&nbsp;&nbsp;
          </a>
          <a @click="handleLogout" style="cursor: pointer;">
            <el-icon><SwitchButton /></el-icon> 退出登录
          </a>
        </span>
      </el-header>
      
      <el-container>
        <!-- 左侧菜单 -->
        <el-aside width="200px" class="aside">
          <!-- 左侧菜单栏 -->
            <el-menu router>
              <!-- 工作台 -->
              <el-menu-item index="/workspace">
                <el-icon><Monitor /></el-icon> 工作台
              </el-menu-item>

              <!-- 教师端：成果管理 -->
              <template v-if="!isAdmin">
                <el-sub-menu index="/manage">
                  <template #title>
                    <el-icon><Management /></el-icon> 成果管理
                  </template>
                  <el-menu-item index="/program">
                    <el-icon><MessageBox /></el-icon> 科研项目
                  </el-menu-item>
                  <el-menu-item index="/essay">
                    <el-icon><Document /></el-icon> 发表论文
                  </el-menu-item>
                  <el-menu-item index="/patent">
                    <el-icon><Opportunity /></el-icon> 专利成果
                  </el-menu-item>
                  <el-menu-item index="/award">
                    <el-icon><Medal /></el-icon> 科研获奖
                  </el-menu-item>
                  <el-menu-item index="/meeting">
                    <el-icon><Place /></el-icon> 学术会议
                  </el-menu-item>
                  <el-menu-item index="/student">
                    <el-icon><Trophy /></el-icon> 指导学生获奖
                  </el-menu-item>
                  <el-menu-item index="/other">
                    <el-icon><Expand /></el-icon> 其他科研成果
                  </el-menu-item>
                </el-sub-menu>
                <el-menu-item index="/assistant">
                  <el-icon><ChatDotRound /></el-icon> 研觉晓 · AI助手
                </el-menu-item>
              </template>

              <!-- 管理员端：审核管理 -->
              <template v-if="isAdmin">
                <el-menu-item index="/admin">
                  <el-icon><Setting /></el-icon> 审核管理
                </el-menu-item>
              </template>
                          
              <!-- 个人中心 -->
              <el-menu-item index="/pc">
                <el-icon><InfoFilled /></el-icon>个人中心
              </el-menu-item>

              <!-- 帮助中心 -->
              <el-menu-item index="/help">
                <el-icon><InfoFilled /></el-icon>帮助中心
              </el-menu-item>

            </el-menu>
        </el-aside>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
      
    </el-container>
  </div>
</template>

<style scoped>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  .header {
    background: #1a2a6c;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    height: 70px;
    box-shadow: 0 2px 8px #0002;
    position: relative;
    z-index: 100;
  }
  .title {
    color: #fff;
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 1px;
  }
  .right_tool {
    display: flex;
    gap: 20px;
    font-size: 15px;
  }
  .right_tool a {
    color: #fffd;
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
  }
  .right_tool a:hover { color: #fff; text-decoration: underline; }
  .aside {
    width: 220px;
    height: calc(100vh - 70px);
    background: #f8f9fa;
    box-shadow: 2px 0 6px #0001;
    overflow-y: auto;
    z-index: 10;
  }
  :deep(.el-menu) { border: none; background: transparent; }
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 50px;
    line-height: 50px;
    font-size: 15px;
    color: #333;
  }
  :deep(.el-menu-item:hover),
  :deep(.el-sub-menu__title:hover) {
    background: #eef5ff !important;
    color: #1a73e8 !important;
  }
  :deep(.el-menu-item.is-active) {
    background: #1a73e8 !important;
    color: #fff !important;
    font-weight: 600;
  }
  :deep(.el-sub-menu .el-menu-item) { padding-left: 48px !important; }
  :deep(.el-sub-menu .el-menu-item.is-active) {
    background: #d0e4ff !important;
    color: #1a73e8 !important;
  }
  .el-main {
    background: #f5f7fa;
    min-height: calc(100vh - 70px);
    padding: 20px;
  }
</style>