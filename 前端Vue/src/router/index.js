import { createRouter, createWebHistory } from 'vue-router'

import LayoutView from '@/views/layout/index.vue'
import LoginView from '@/views/login/index.vue'
import WorkspaceView from '@/views/workspace/index.vue'
import ProgramView from '@/views/program/index.vue'
import EssayView from '@/views/essay/index.vue'
import PatentView from '@/views/patent/index.vue'
import AwardView from '@/views/award/index.vue'
import MeetingView from '@/views/meeting/index.vue'
import StudentView from '@/views/student/index.vue'
import OtherView from '@/views/other/index.vue'
import AssistantView from '@/views/assistant/yanjuexiao.vue'
import HelpView from '@/views/help/index.vue'
import PcView from '@/views/pc/index.vue'
import AdminView from '@/views/admin/index.vue'

var childRoutes = [
  {path: 'workspace', name: 'workspace', component: WorkspaceView},
  {path: 'program', name: 'program', component: ProgramView},
  {path: 'essay', name: 'essay', component: EssayView},
  {path: 'patent', name: 'patent', component: PatentView},
  {path: 'award', name: 'award', component: AwardView},
  {path: 'meeting', name: 'meeting', component: MeetingView},
  {path: 'student', name: 'student', component: StudentView},
  {path: 'other', name: 'other', component: OtherView},
  {path: 'assistant', name: 'assistant', component: AssistantView},
  {path: 'pc', name: 'pc', component: PcView},
  {path: 'help', name: 'help', component: HelpView},
  {path: 'admin', name: 'admin', component: AdminView},
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: LayoutView,
      redirect: '/workspace',
      children: childRoutes
    },
    {path: '/login', name: 'login', component: LoginView}
  ]
})

// 登录拦截
router.beforeEach((to, from, next) => {
  let u = localStorage.getItem('userInfo')
  if (!u && to.path != '/login') return next('/login')
  if (u && to.path === '/login') return next('/workspace')

  // 管理员页面权限
  if (to.path === '/admin') {
    try {
      if (JSON.parse(u).role !== 'admin') return next('/workspace')
    } catch(e) { return next('/login') }
  }
  next()
})

export default router
