import axios from 'axios'
import {Message} from 'element-ui'
import router from '../router'

const root = process.env.VUE_APP_URL
const instance = axios.create({
    headers: {
        'Content-Type': 'application/json',
    },
    // 基础的url
    baseURL: root
});
// Login中的localStorage存储token
// 拦截器，拦截request请求，如果本地的localStorage存储了token，则将token存入到发送请求的token中
instance.interceptors.request.use(config => {
    if (localStorage.getItem('token')) {
        config.headers.common['token'] = localStorage.getItem('token')
    }
    return config
})
let messageInstance = null
// 拦截器，拦截response响应，对错误信息，进行弹窗提示
instance.interceptors.response.use(res => {
        // 对错误的请求结果统一处理并且有信息提示
        if (res.data.resultCode === 1) {
            return res;
        } else {
            // 如果页面已经存在提示信息，则先关闭，再提示
            if (messageInstance) {
                messageInstance.close();
            }
            // 重写Message，统一提示信息处理
            messageInstance = Message({
                type: 'error',
                message: res.data.message,
                // 提示信息，居中显示
                center: true
            })
            return res;
        }
    },
    // 接口响应异常处理
    error => {
        const {response} = error
        // 处理返回值http status code为401处理
        if (response.status === 401) {
            if (messageInstance) {
                messageInstance.close();
            }
            messageInstance = Message({
                type: 'error',
                message: response.data.message,
                center: true
            })
            // 页面错误路由跳转
            router.replace({
                path: '/',
                query: {
                    redirect: router.currentRoute.fullPath
                }
            })
        }
        return response;
    }
)
export default instance
