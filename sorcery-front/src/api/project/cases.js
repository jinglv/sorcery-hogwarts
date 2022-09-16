import axios from '../http'
// 测试用例相关接口
const cases = {
    // 通过文本数据，新增测试用例
    addCaseText(params) {
        return axios.post('/cases/add', params)
    },
    // 通过上传文件，新增测试用例
    addCaseFile(params) {
        return axios('/cases/file', {
            method: 'post',
            data: params,
            headers: {'Content-type': 'multipart/form-data'}
        })
    },
    // 分页查询测试用例列表
    getCaseList(params) {
        return axios.get('/cases/list', {params})
    },
    // 修改测试用例
    editCase(params) {
        return axios.put('/cases/', params)
    },
    // 删除测试用例
    deleteCase(params) {
        return axios.delete('/cases/' + params.id)
    }
}
export default cases
