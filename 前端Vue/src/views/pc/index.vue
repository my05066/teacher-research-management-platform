<template>
  <div class="pc-page">
    <div class="page-header">
      <h2>个人中心</h2>
      <p class="page-desc">管理您的个人信息</p>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <span class="card-title">个人信息</span>
          </template>
          <div class="user-info">
            <div class="avatar">
              <el-icon><User /></el-icon>
            </div>
            <div class="user-details">
              <div class="user-name">{{ userInfo?.realName || userInfo?.username || '未设置' }}</div>
              <div class="user-role">
                <el-tag :type="userInfo?.role === 'admin' ? 'danger' : 'primary'">
                  {{ userInfo?.role === 'admin' ? '管理员' : '教师' }}
                </el-tag>
              </div>
            </div>
          </div>
          <el-descriptions :column="1" border class="user-descriptions">
            <el-descriptions-item label="工号">{{ userInfo?.employeeId || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo?.username || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo?.email || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ userInfo?.department || '未设置' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ userInfo?.phone || '未设置' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="16">
        <el-card class="form-card" shadow="hover">
          <template #header>
            <span class="card-title">编辑信息</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="form.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="部门" prop="department">
                  <el-input v-model="form.department" placeholder="请输入部门" />
                </el-form-item>
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateInfo">保存修改</el-button>
                  <el-button @click="resetForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="pwdForm" label-width="120px" :rules="passwordRules" ref="pwdFormRef">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updatePassword">修改密码</el-button>
                  <el-button @click="resetPwdForm">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { getUserInfoApi, updateUserInfo as updateUserInfoApi, changePwd as updatePasswordApi } from '@/api/user.js'

var userInfo = ref(null)
var activeTab = ref('info')
var formRef = ref(null)
var pwdFormRef = ref(null)

var form = ref({ realName: '', department: '', phone: '' })
var pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

var rules = { realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }] }

var passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value !== pwdForm.value.newPassword) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

async function loadUserInfo() {
  try {
    var uid = localStorage.getItem('userId') || 1
    var res = await getUserInfoApi(uid)
    if (res.code === 1) {
      userInfo.value = res.data
      form.value = {
        realName: res.data.realName || '',
        department: res.data.department || '',
        phone: res.data.phone || ''
      }
    }
  } catch(e) { ElMessage.error('加载用户信息失败') }
}

async function updateInfo() {
  await formRef.value.validate()
  try {
    var uid = localStorage.getItem('userId') || 1
    var res = await updateUserInfoApi(uid, form.value)
    if (res.code === 1) { ElMessage.success('修改成功'); loadUserInfo() }
    else ElMessage.error(res.msg || '修改失败')
  } catch(e) { ElMessage.error('修改失败') }
}

async function updatePassword() {
  await pwdFormRef.value.validate()
  try {
    var uid = localStorage.getItem('userId') || 1
    var res = await updatePasswordApi(uid, pwdForm.value.oldPassword, pwdForm.value.newPassword)
    if (res.code === 1) { ElMessage.success('密码修改成功'); resetPwdForm() }
    else ElMessage.error(res.msg || '密码修改失败')
  } catch(e) { ElMessage.error('密码修改失败') }
}

function resetForm() {
  form.value = {
    realName: userInfo.value?.realName || '',
    department: userInfo.value?.department || '',
    phone: userInfo.value?.phone || ''
  }
}
function resetPwdForm() {
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  pwdFormRef.value?.resetFields()
}

onMounted(() => {
  window.addEventListener('switchToPassword', () => { activeTab.value = 'password' })
  loadUserInfo()
})
</script>

<style scoped>
.pc-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px) }
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.info-card, .form-card { border-radius: 8px }
.user-info {
  display: flex; align-items: center;
  margin-bottom: 20px; padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}
.avatar {
  width: 80px; height: 80px; border-radius: 50%;
  background: #667eea;
  display: flex; align-items: center; justify-content: center;
  margin-right: 20px;
}
.avatar .el-icon { font-size: 40px; color: #fff }
.user-details { flex: 1 }
.user-name { font-size: 20px; font-weight: 600; color: #303133; margin-bottom: 8px }
.user-role { margin-top: 8px }
.user-descriptions { margin-top: 20px }
</style>
