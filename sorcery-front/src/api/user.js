import axios from './http'
// 用户需要的接口
const user = {
    // 登录接口
    login(params) {
        return axios.post('/user/login', params)
    },
    // 注册接口
    register(params) {
        return axios.post('/user/register', params)
    },
    // 用户是否登录
    isLogin(params) {
        return axios.get('/user/isLogin', {params})
    },
    // 用户登出
    logout(params) {
        return axios.delete('/user/logout', {params})
    }
}
export default user
