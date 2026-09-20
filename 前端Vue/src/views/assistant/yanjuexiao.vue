<template>
  <div class="assistant-page">
    <div class="page-header">
      <h2>研觉晓 · 科研智能助手</h2>
      <p class="page-desc">
        面向当前登录教师，基于您的科研项目、论文、专利、获奖等数据，提供智能问答与成果分析。
      </p>
      <div class="footer-note" style="font-size: 10px; color: #999; margin-top: 8px;">
        名字取自"研习之觉，洞见于晓"——希望你在科研的清晨，总能看见光。
      </div>
    </div>

    <el-row :gutter="20" class="assistant-layout">
      <el-col :xs="24" :md="16">
        <el-card class="chat-card" shadow="hover">
          <div class="header-actions">
            <el-button type="danger" :loading="clearingHistory" @click="confirmClearHistory" size="small">
              清空历史记录
            </el-button>
          </div>
          
          <div class="chat-window" ref="chatWindowRef">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              :class="['chat-message', msg.role === 'user' ? 'from-user' : 'from-ai']"
            >
              <div class="avatar">
                <el-icon v-if="msg.role === 'user'"><User /></el-icon>
                <el-icon v-else><Cpu /></el-icon>
              </div>
              <div class="bubble">
                <div class="name">
                  {{ msg.role === 'user' ? '我' : '研觉晓' }}
                </div>
                <div class="content">
                  <div v-if="msg.loading" class="loading">
                    <el-icon class="is-loading" :size="16"><Loading /></el-icon>
                    <span>正在思考中，请稍候...</span>
                  </div>
                  <div v-else v-html="msg.html || msg.content"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="chat-input">
            <el-input
              v-model="question"
              type="textarea"
              :rows="3"
              placeholder="例如：我在2025年5月5日之前有哪些成果？ / 对我目前总体成果做个分析，告诉我哪里不足。"
              @keyup.enter.exact.prevent="handleSend"
            />
            <div class="chat-actions">
              <span class="hint">研觉晓会基于当前账号下的所有科研数据进行分析。</span>
              <el-button type="primary" :loading="loading" @click="handleSend">
                发送
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card class="side-card" shadow="hover">
          <template #header>
            <span class="card-title">快捷提问示例</span>
          </template>
          <div class="quick-questions">
            <el-tag
              v-for="item in quickQuestions"
              :key="item"
              class="quick-question-tag"
              @click="useQuickQuestion(item)"
            >
              {{ item }}
            </el-tag>
          </div>
          <div class="tips">
            <h3>使用小贴士</h3>
            <ul>
              <li>可以用自然语言描述时间范围，例如"在2025年5月5日之前"。</li>
              <li>可以让研觉晓按类型统计，比如"按项目、论文、专利分别统计数量和时间分布"。</li>
              <li>也可以咨询"未来2年科研规划建议""如何补齐短板"等开放问题。</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Cpu, Loading } from '@element-plus/icons-vue'
import { askAssistant as askAssistantApi, getChatHistory as getChatHistoryApi, clearChatHistory as clearChatHistoryApi } from '@/api/assistant.js'

let messages = ref([])
let question = ref('')
let loading = ref(false)
let clearingHistory = ref(false)
let chatWindowRef = ref(null)

const quickQuestions = [
  '我近一年发布的论文有哪些？',
  '请按类型统计我的科研成果',
  '请给我未来两年科研方向建议',
  '请分析我的科研能力的短板'
]

function showWelcomeMessage() {
  messages.value = [{
    role: 'assistant',
    content: '您好，我是科研智能助手「研觉晓」。我已经接入了您在本系统中的科研项目、论文、专利、获奖、学术会议等数据，可以帮您做统计查询和整体分析。\n\n' +
      '您可以尝试问：\n1）我在2025年5月5日之前有哪些成果？\n2）请按年份统计一下我各类成果的数量分布。\n3）对我目前总体成果做个分析，告诉我有哪些不足，未来可以重点提升哪些方向？'
  }]
}

async function loadChatHistory() {
  try {
    let res = await getChatHistoryApi()
    if (res.code === 1 && res.data.length > 0) {
      messages.value = res.data.map(item => ({ role: item.role, content: item.content, loading: false }))
    } else {
      showWelcomeMessage()
    }
  } catch(e) {
    console.log(e)
    showWelcomeMessage()
  }
}

async function scrollToBottom() {
  await nextTick()
  let el = chatWindowRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
    requestAnimationFrame(() => { el.scrollTop = el.scrollHeight })
  }
}

onMounted(async () => {
  await loadChatHistory()
  scrollToBottom()
})

const handleSend = async () => {
  let content = question.value.trim()
  if (!content) { ElMessage.warning('请输入要咨询的问题'); return }
  if (loading.value) return

  messages.value.push({ role: 'user', content })
  question.value = ''
  loading.value = true

  let loadingMsg = { role: 'assistant', content: '', loading: true }
  messages.value.push(loadingMsg)

  try {
    let res = await askAssistantApi(content)
    if (res.code !== 1) {
      ElMessage.error(res.msg || '研觉晓服务调用失败')
      loadingMsg.loading = false
      loadingMsg.content = '抱歉，研觉晓暂时无法回答您的问题：' + (res.msg || '服务异常')
      return

    }
    loadingMsg.loading = false
    loadingMsg.content = res.data?.answer || '（未返回内容）'
  } catch(e) {
    
    console.log(e)
    loadingMsg.loading = false
    loadingMsg.content = '调用研觉晓时发生错误：' + (e?.message || '未知错误')


  } finally {
    loading.value = false
    setTimeout(scrollToBottom, 0)
  }
}

const useQuickQuestion = q => { question.value = q }

async function confirmClearHistory() {
  try {
    await ElMessageBox.confirm('确定清空所有聊天记录？不可恢复。', '确认', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    clearingHistory.value = true
    let res = await clearChatHistoryApi()
    if (res.code === 1) {
      messages.value = []
      showWelcomeMessage()
      ElMessage.success('已清空')
    } else {
      ElMessage.error(res.msg || '清空失败')
    }
  } catch(e) {
    if (e !== 'cancel') ElMessage.error('清空失败')
  } finally {
    clearingHistory.value = false
  }
}
</script>

<style scoped>
.assistant-page { padding: 20px; background: #f5f7fa; min-height: calc(100vh - 60px) }
.page-header { margin-bottom: 20px }
.page-header h2 { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: #303133 }
.page-desc { margin: 0; font-size: 14px; color: #909399 }
.assistant-layout { margin-top: 10px }
.chat-card {
  border-radius: 8px; display: flex; flex-direction: column;
  height: 560px; overflow: hidden;
}
.header-actions { padding: 10px 16px 0; text-align: right }
.chat-window {
  flex: 1; overflow-y: auto; padding: 16px;
  background: #fff; border-radius: 4px; border: 1px solid #ebeef5;
  box-sizing: border-box; max-height: calc(560px - 120px);
}
.chat-message { display: flex; margin-bottom: 16px; min-height: 40px }
.chat-message.from-user { flex-direction: row-reverse }
.chat-message .avatar {
  width: 32px; height: 32px; border-radius: 50%; background: #ecf5ff;
  display: flex; align-items: center; justify-content: center;
  margin: 0 8px; color: #409eff; flex-shrink: 0;
}
.chat-message.from-ai .avatar { background: #f0f9eb; color: #67c23a }
.chat-message .bubble { max-width: calc(100% - 60px); word-wrap: break-word }
.chat-message .name { font-size: 12px; color: #909399; margin-bottom: 4px }
.chat-message .content {
  background: #f5f7fa; padding: 10px 12px; border-radius: 6px;
  font-size: 14px; white-space: pre-wrap; line-height: 1.6;
  min-height: 20px; word-break: break-all;
}
.chat-message.from-user .content { background: #409eff; color: #fff }
.chat-input { margin-top: 12px; border-top: 1px solid #ebeef5; padding-top: 12px; flex-shrink: 0 }
.chat-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 8px }
.chat-actions .hint { font-size: 12px; color: #909399 }
.side-card { border-radius: 8px }
.card-title { font-size: 16px; font-weight: 600; color: #303133 }
.quick-questions { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px }
.quick-question-tag { cursor: pointer }
.tips h3 { font-size: 14px; margin-bottom: 8px }
.tips ul { padding-left: 18px; margin: 0; font-size: 13px; color: #606266 }
.tips li { margin-bottom: 4px }
.loading { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #909399 }
.chat-window::-webkit-scrollbar { width: 6px }
.chat-window::-webkit-scrollbar-track { background: #f1f1f1; border-radius: 3px }
.chat-window::-webkit-scrollbar-thumb { background: #dcdfe6; border-radius: 3px }
.chat-window::-webkit-scrollbar-thumb:hover { background: #c0c4cc }
</style>