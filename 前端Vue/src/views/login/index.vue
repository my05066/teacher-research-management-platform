<template>

  <div class="vue-login-container">
    <div class="container">
    
      <!-- 登录表单 -->
      <div id="loginForm" class="form-section" :class="{ active: activeForm === 'login' }">
        <div class="midTittle">
          <span>教师科研成果管理平台</span>
        </div>
        <div class="form-header">
          <h2>用户登录</h2>
          <p>请输入您的登录信息</p>
        </div>
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="loginUsername">用户名</label>
            <input
              type="text"
              id="loginUsername"
              v-model="loginForm.username"
              required
              placeholder="请输入工号或用户名"
            />
          </div>
          <div class="form-group">
            <label for="loginPassword">密码</label>
            <input
              type="password"
              id="loginPassword"
              v-model="loginForm.password"
              required
              placeholder="请输入密码"
            />
          </div>
          <button @click="loginButton" class="ceshi">登录</button>
          <div class="switch-form">
            <a @click="showForm('register')">还没有账号？立即注册</a> |
            <a @click="showForm('forgot')">忘记密码？</a>
          </div>
        </form>
      </div>

      <!-- 注册表单 -->
      <div id="registerForm" class="form-section" :class="{ active: activeForm === 'register' }">
        <div class="form-header">
          <h2>用户注册</h2>
          <p>创建您的新账户</p>
        </div>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="registerEmployeeId">工号</label>
            <input
              type="text"
              id="registerEmployeeId"
              v-model="regForm.employeeId"
              required
              placeholder="请输入工号"
            />
          </div>
          <div class="form-group">
            <label for="registerUsername">用户名</label>
            <input
              type="text"
              id="registerUsername"
              v-model="regForm.username"
              required
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-group">
            <label for="registerEmail">邮箱</label>
            <input
              type="email"
              id="registerEmail"
              v-model="regForm.email"
              required
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-group">
            <label for="registerPassword">密码</label>
            <input
              type="password"
              id="registerPassword"
              v-model="regForm.password"
              required
              placeholder="请输入密码（至少6位）"
              @input="checkPwd"
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="regForm.confirmPassword"
              required
              placeholder="请再次输入密码"
              @input="checkPwd"
            />
            <div id="passwordError" class="error-message" :style="{ display: pwdMismatch ? 'block' : 'none' }">
              两次输入的密码不一致
            </div>
          </div>
          <button @click="registerButton" :disabled="pwdMismatch" class="ceshi">注册</button>
          <div class="switch-form">
            <a @click="showForm('login')">已有账号？立即登录</a>
          </div>
        </form>
      </div>

      <!-- 找回密码表单 -->
      <div id="forgotForm" class="form-section" :class="{ active: activeForm === 'forgot' }">
        <div class="form-header">
          <h2>找回密码</h2>
          <p>重置您的密码</p>
        </div>
        <form @submit.prevent="handleForgotPassword">
          <div class="form-group">
            <label for="forgotEmployeeId">工号</label>
            <input
              type="text"
              id="forgotEmployeeId"
              v-model="forgotForm.employeeId"
              required
              placeholder="请输入您的工号"
            />
          </div>
          <div class="form-group">
            <label for="forgotEmail">注册邮箱</label>
            <input
              type="email"
              id="forgotEmail"
              v-model="forgotForm.email"
              required
              placeholder="请输入注册时的邮箱"
            />
          </div>
          <button @click="sendEmail" class="ceshi">发送重置邮件</button>
          <button @click="sendEmail" class="ceshi">发送重置邮件</button>
          <div id="resetMessage" class="success-message" :style="{ display: resetSent ? 'block' : 'none' }">
            重置邮件已发送，请查收
          </div>
          <div class="switch-form">
            <a @click="showForm('login')">想起密码了？立即登录</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { loginApi, doRegister } from '@/api/login.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
var activeForm = ref('login')
var pwdMismatch = ref(false)
var resetSent = ref(false)

const loginForm = reactive({ username: '', password: '' })

const regForm = reactive({
  employeeId: '',
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const forgotForm = reactive({ employeeId: '', email: '' })

function showForm(type) {
  activeForm.value = type
  pwdMismatch.value = false
  resetSent.value = false
}

function checkPwd() {
  pwdMismatch.value = regForm.password !== regForm.confirmPassword && regForm.confirmPassword !== ''
}

// 登录
const loginButton = async () => {
  try {
    var res = await loginApi(loginForm)
    if (res.code === 1) {
      ElMessage.success('登录成功')
      if (res.data) {
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        localStorage.setItem('userId', res.data.id)
        localStorage.setItem('token', res.data.token)
        window.dispatchEvent(new Event('storage'))
      }
      router.push('/workspace')
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    console.log(e)
    ElMessage.error('登录失败')
  }
}

// 注册
async function registerButton() {
  var res = await doRegister(regForm)
  if (res.code) {
    ElMessage.success('注册成功，请返回登录')
    // 清空表单
    regForm.employeeId = ''
    regForm.username = ''
    regForm.email = ''
    regForm.password = ''
    regForm.confirmPassword = ''
    loginForm.username = ''
    loginForm.password = ''
    showForm('login')
  } else {
    ElMessage.error(res.msg)
  }
}

// todo 邮件找回
const sendEmail = () => {}
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.vue-login-container {
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  background: #1a2a6c;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.container {
  background: #fffc;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 16px #0003;
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}
.form-header h2 {
  color: #1a2a6c;
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 600;
}
.form-header p {
  color: #2c3e50;
  font-size: 14px;
}

.form-section { display: none; }
.form-section.active { display: block; }

.midTittle {
  text-align: center;
  color: #000;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 10px;
}
.form-group { margin-bottom: 24px; }
label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-size: 14px;
  font-weight: 500;
}
input {
  width: 100%;
  padding: 14px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  transition: .3s;
  background: #fffe;
}
input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px #3498db33;
}
.ceshi { width: 100%; }
button {
  width: 100px;
  padding: 14px;
  background: #2a5298;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: .3s;
  margin-top: 10px;
}
button:hover:not(:disabled) {
  background: #1a2a6c;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.switch-form {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #2c3e50;
}
.switch-form a {
  color: #3498db;
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}
.switch-form a:hover {
  text-decoration: underline;
  color: #1a2a6c;
}
.error-message {
  color: #e74c3c;
  font-size: 13px;
  margin-top: 6px;
}
.success-message {
  color: #27ae60;
  font-size: 13px;
  margin-top: 6px;
}
</style>