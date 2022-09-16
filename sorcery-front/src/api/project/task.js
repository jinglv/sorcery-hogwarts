import axios from '../http'
// 测试用例相关接口
const task = {
    // 测试任务管理
    // 添加测试任务
    addTask(params) {
        return axios.post('/task/', params)
    },
    // 根据taskId删除测试任务
    deleteTask(params) {
        return axios.delete('/task/' + params.id, params)
    },
    // 测试任务列表查询
    getTaskList(params) {
        return axios.get('/task/list', {params})
    },
    // 执行测试任务
    doTask(params) {
        return axios.post('/task/start', params)
    },
    // 修改测试任务信息
    editTask(params) {
        return axios.put('/task', params)
    }
}
export default task
