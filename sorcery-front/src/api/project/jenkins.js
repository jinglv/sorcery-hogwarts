import axios from '../http'
// 测试用例相关接口
const jenkins = {
    // Jenkins管理
    // 修改Jenkins信息
    editJenkins(params) {
        return axios.put('/jenkins', params)
    },
    // 新增Jenkins信息
    addJenkins(params) {
        return axios.post('/jenkins', params)
    },
    // 分页查询Jenkins列表
    getJenkinsList(params) {
        return axios.get('/jenkins/list', {params})
    },
    // 根据JenkinsId删除Jenkins信息
    deleteJenkins(params) {
        return axios.delete('/jenkins/' + params.id)
    },
}
export default jenkins
