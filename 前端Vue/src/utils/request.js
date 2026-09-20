import axios from 'axios'
import router from '@/router/index.js'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 600000
})

// 请求拦截 - 带上token和用户id
request.interceptors.request.use(config => {
    let token = localStorage.getItem('token')
    let uid = localStorage.getItem('userId')
    if (token) config.headers['token'] = token
    if (uid) config.headers['X-User-Id'] = uid
    return config
}, err => Promise.reject(err))

// 响应拦截
request.interceptors.response.use(
    res => res.data,
    err => {
        if (err.response && err.response.status == 401) {
            localStorage.clear()
            router.push('/login')
            ElMessage.error('登录过期，请重新登录')  
        }
        return Promise.reject(err)
    }
)

export default request