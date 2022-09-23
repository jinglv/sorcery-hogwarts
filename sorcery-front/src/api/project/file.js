import axios from '../http'
// 文件相关接口
const file = {
    uploadFile(params) {
        return axios('/file/uploads', {
            method: 'post',
            data: params,
            headers: {'Content-type': 'multipart/form-data'}
        })
    }
}
export default file