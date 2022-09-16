import axios from '../http'
// 测试用例相关接口
const report = {
    // 测试报告管理
    // 根据任务id获取Allure报告
    getAllure(params) {
        return axios.get('/report/' + params.id)
    },
    // 任务中用例的数量统计信息 - 折线图
    getCaseCount(params) {
        return axios.get('/report/count/')
    },
    // 根据任务类型获取任务统计信息 - 饼状图
    getStatus(params) {
        return axios.get('/report/type/')
    },
}
export default report
