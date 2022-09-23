import axios from '../http'
// 项目管理相关接口
const project = {
    // 编辑项目
    editProject(params) {
        return axios.put('/project', params)
    },
    // 新增Project信息
    addProject(params) {
        return axios('/project/add', {
            method: 'post',
            data: params,
            headers: {'Content-type': 'multipart/form-data'}
        })
    },
    // 分页查询Project列表
    getProjectList(params) {
        return axios.get('/project/list', {params})
    },
    // 根据项目id查询项目详情
    projectDetail(id) {
        return axios.get('/project/' + id)
    },
    // 根据ProjectId删除Project信息
    deleteProject(id) {
        return axios.delete('/project/' + id)
    },
}
export default project