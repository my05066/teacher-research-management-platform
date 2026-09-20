import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElIcons from '@element-plus/icons-vue'

var app = createApp(App)
app.use(router)
app.use(ElementPlus, {locale: zhCn})

// 注册全部图标
Object.keys(ElIcons).forEach(name => {
  app.component(name, ElIcons[name])
})

app.mount('#app')
