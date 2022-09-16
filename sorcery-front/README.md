# sorcery-front

## Project setup

```
npm install
```

### Compiles and hot-reloads for development

```
npm run serve
```

### Compiles and minifies for production

```
npm run build
```

### Customize configuration

See [Configuration Reference](https://cli.vuejs.org/config/).

## 双环境配置

1. 在工程目录下，新建两个文件
    - `.env.development`    (配置开发环境下的地址)
    - `.env.production`     (配置生产环境下的地址)

   <img src="https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151543016.png" alt="image-20220915154331918" style="zoom:50%;" />

2. 文件内容如下
    - `.env.development`中：
      ```
      NODE_ENV = development
      VUE_APP_URL = http://localhost:8887/（开发环境下地址）
      ```

      ![image-20220915154421753](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151544786.png)

    - `.env.production`中：

      ```
      NODE_ENV = production
      VUE_APP_URL = http://product.sorcery.com/ （生产环境下地址）
      ```

      ![image-20220915154448131](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151544163.png)

3. 修改src/api/http.js中的部分内容

    - 在 import axios from 'axios' 下新增语句： const root = process.env.VUE_APP_URL
    - 修改 baseURL:`http://localhost:8887/` 为：baseURL: root

   ![image-20220915154519816](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151545847.png)

## 提示信息统一处理

信息提示使用element-ui中的Message组件

1. 安装element-ui
   ```shell
   npm install element-ui -S
   ```

2. src/main.js引入对应element-ui样式

   ![image-20220915154547761](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151545797.png)

3. src/api/http.js引入element-ui的Message模块，并编写提示信息的统一处理代码

   ```js
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
   
   ```

4. 展示效果

   ![image-20220915154634288](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151546318.png)

![image-20220915154708836](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151547868.png)

## 组件消息提示

组件消息提示使用element-ui中的Notification的组件

1. 上面已经安装了element-ui，现在需要引入Notification的组件，src/main.js引入Notification

   ![image-20220915154732687](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151547722.png)

2. 在各个组件中使用对应的地方使用Notification，例如：增加测试用例，代码如下

   ```javascript
       // 新增测试用例
       addCase() {
         if (this.selectType === 1) {
           let params = {
             caseName: this.caseName,
             caseData: this.caseData,
             remark: this.remark
           }
           // 通过文本，新增测试用例
           this.$api.cases.addCaseText(params).then(res => {
             // 调用接口，返回resultCode=1，说明接口调用成功
             if (res.data.resultCode === 1) {
               if (this.instanceNotify) {
                 this.instanceNotify.close()
               }
               this.instanceNotify = this.$notify({
                 title: '成功',
                 message: '添加成功',
                 type: 'success'
               })
               // 测试用例添加成功，关闭弹窗
               this.addDialog = false
               // 测试用例添加成功，刷新列表
               this.getCaseList()
             } else {
               if (this.notifyInstance) {
                 this.notifyInstance.close()
               }
               this.notifyInstance = this.$notify({
                 title: '错误',
                 message: res.data.message,
                 type: 'error'
               })
             }
           })
         } else {
           let params = new FormData()
           params.append('caseFile', this.file)
           params.append('caseData', this.caseData)
           params.append('caseName', this.caseName)
           params.append('remark', this.remark)
           // 通过文件，新增测试用例
           this.$api.cases.addCaseFile(params).then(res => {
             // 调用接口，返回resultCode=1，说明接口调用成功
             if (res.data.resultCode === 1) {
               if (this.instanceNotify) {
                 this.instanceNotify.close()
               }
               this.instanceNotify = this.$notify({
                 title: '成功',
                 message: '添加成功',
                 type: 'success'
               })
               // 测试用例添加成功，关闭弹窗
               this.addDialog = false
               // 测试用例添加成功，刷新列表
               this.getCaseList()
             } else {
               if (this.notifyInstance) {
                 this.notifyInstance.close()
               }
               this.notifyInstance = this.$notify({
                 title: '错误',
                 message: res.data.message,
                 type: 'error'
               })
             }
           })
         }
       },
   ```


3. 展示效果

   ![image-20220915154829713](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151548751.png)

## 分页处理

1. 后台已提供分页查询的接口
2. 使用vuetify提供的分页组件
3. 组件中处理分页

```vue

<template>
  <div>
    <!--分页显示处理-->
    <v-pagination v-if="pageLength>0" v-model="currentPage" :length="pageLength" @input="getCaseList()"
                  total-visible="7"></v-pagination>
  </div>
</template>
<script>
export default {
  data() {
    return {
      currentPage: 1,
      pageLength: 0
    }
  },
  created() {
    this.getCaseList()
  },
  methods: {
    // 测试用例列表
    getCaseList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: 5,
      }
      this.$api.cases.getCaseList(params).then(res => {
        this.desserts = res.data.data.data
        this.rows = res.data.data.recordsTotal
        // 每5条为一页
        this.pageLength = Math.ceil(this.rows / 5)
      })
    }
  }
}
</script>
<style scoped>
</style>

```

## echarts使用

[echarts中文官方文档](https://echarts.apache.org/zh/index.html)

1. 安装element-ui

```shell
npm install echarts -S
```

2. 在report.vue的组件中引入并使用echarts

![image-20220915154903208](https://jing-images.oss-cn-beijing.aliyuncs.com/img/202209151549244.png)

