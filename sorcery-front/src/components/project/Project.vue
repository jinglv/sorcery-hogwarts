<template>
    <div>
      <v-btn color="success" class="btn" @click="addDialog=true">创建</v-btn>
      <!--添加Jenkins-->
      <v-dialog v-model="addDialog" width="500px">
        <v-card>
          <v-card-title>新增项目</v-card-title>
          <v-card-text>
            <v-text-field v-model="projectName" label="项目名称" />
            <v-text-field v-model="gitName" label="Git项目名称" />
            <v-text-field v-model="gitAddress" label="Git项目地址" />
            <v-text-field v-model="gitCredentialsId" label="Git认证" />
            <v-text-field v-model="description" label="项目描述" />
            <v-file-input multiple v-model="image" label="图片"/>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="addDialog=false">取消</v-btn>
            <v-btn color="primary" @click="saveProject()">保存</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <!--项目卡片-->
      <template>
        <v-container
          class="spacing-playground pa-12"
          fluid
        >
        <v-row dense>
          <v-col
            v-for="project in projects"
            :key="project.projectName"
          >
          <v-card width="500px">
              <v-img
                :src="project.image"
                class="white--text align-end"
                gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
                height="200px"
              >
              <v-card-title v-text="project.projectName" />
                </v-img>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="primary" dark @click="detailProject(project.id)">详情</v-btn>
                  <v-btn color="success" dark @click="editProject(project.id)">编辑</v-btn>
                  <v-btn color="error" dark @click="deleteProject(project.id)">删除</v-btn>
                </v-card-actions>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </template>

      <!--编辑Jenkins-->
      <v-dialog v-model="detailDialog" width="500px">
        <v-card>
          <v-card-title>项目详情</v-card-title>
          <v-card-text>
            <v-text-field v-model="projectDetails.projectName" label="项目名称" />
            <v-text-field v-model="projectDetails.gitName" label="Git项目名称" />
            <v-text-field v-model="projectDetails.gitAddress" label="Git项目地址" />
            <v-text-field v-model="projectDetails.gitCredentialsId" label="Git认证" />
            <v-text-field v-model="projectDetails.description" label="项目描述" />
            <v-img :src="projectDetails.image" />
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="detailDialog=false">关闭</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <!--编辑Jenkins-->
      <v-dialog v-model="editDialog" width="500px">
        <v-card>
          <v-card-title>编辑项目</v-card-title>
          <v-card-text>
            <v-text-field v-model="projectDetails.projectName" label="项目名称" />
            <v-text-field v-model="projectDetails.gitName" label="Git项目名称" />
            <v-text-field v-model="projectDetails.gitAddress" label="Git项目地址" />
            <v-text-field v-model="projectDetails.gitCredentialsId" label="Git认证" />
            <v-text-field v-model="projectDetails.description" label="项目描述" />
            <v-file-input multiple v-model="projectDetails.image" label="图片"/>
          </v-card-text>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="editDialog=false">取消</v-btn>
            <v-btn color="primary" @click="saveEditProject()">保存</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <!--分页显示处理-->
      <v-pagination v-if="pageLength>0" v-model="currentPage" :length="pageLength" @input="getProjectList()"
                    total-visible="7"></v-pagination>
    </div>
  </template>
  <script>
  export default {
    data() {
      return {
        addDialog: false,
        projectName: '',
        gitName: '',
        gitAddress: '',
        gitCredentialsId: '',
        description: '',
        image: null,
        projects: [],
        editDialog: false,
        currentPage: 1,
        pageLength: 0,
        projectId: 0,
        projectDetails: '',
        detailDialog: false,
        editDialog: false
      }
    },
    created() {
      this.getProjectList()
    },
    methods: {
      // 添加Project
      saveProject() {
        let params = new FormData()
        params.append('projectName', this.projectName)
        params.append('gitName', this.gitName)
        params.append('gitAddress', this.gitAddress)
        params.append('gitCredentialsId', this.gitCredentialsId)
        params.append('description', this.description)
        params.append('image', this.image[0])
        this.$api.project.addProject(params).then(res => {
          if (res.data.resultCode === 1) {
            if (this.instanceNotify) {
              this.instanceNotify.close()
            }
            this.instanceNotify = this.$notify({
              title: '成功',
              message: '添加成功',
              type: 'success'
            })
            this.getProjectList()
            this.addDialog = false
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
      },
      // 编辑Jenkins
      editProject(item) {
        this.editDialog = true
        this.saveEditProject(item)
      },
      // 保存编辑Jenkins
      saveEditProject(item) {
        this.getProjectDetail(item)
        let params = new FormData()
        params.append('id', item)
        params.append('projectName', this.projectName)
        params.append('gitName', this.gitName)
        params.append('gitAddress', this.gitAddress)
        params.append('gitCredentialsId', this.gitCredentialsId)
        params.append('description', this.description)
        params.append('image', this.image[0])
        this.$api.project.editProject(params).then(res => {
          // 调用接口，返回resultCode=1，说明接口调用成功
          if (res.data.resultCode === 1) {
            if (this.instanceNotify) {
              this.instanceNotify.close()
            }
            this.instanceNotify = this.$notify({
              title: '成功',
              message: '编辑成功',
              type: 'success'
            })
            this.getProjectList()
            this.editDialog = false
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
      },
      // 删除Project
      deleteProject(item) {
        this.$api.project.deleteProject(item).then(res => {
          if (res.data.resultCode === 1) {
            if (this.instanceNotify) {
              this.instanceNotify.close()
            }
            this.instanceNotify = this.$notify({
              title: '成功',
              message: '删除成功',
              type: 'success'
            })
            this.getProjectList()
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
      },
      // 分页查询Project列表
      getProjectList() {
        let params = {
          pageNum: this.currentPage,
          pageSize: 6,
        }
        this.$api.project.getProjectList(params).then(res => {
          // 调用接口，返回resultCode=1，说明接口调用成功
          if (res.data.resultCode === 1) {
            let imagesLength = res.data.data.data.length
            for(let i = 0; i < imagesLength; i++) {
              res.data.data.data[i].image =  process.env.VUE_APP_URL + res.data.data.data[i].image
            }
            this.projects = res.data.data.data
            this.rows = res.data.data.recordsTotal
            // 每5条为一页
            this.pageLength = Math.ceil(this.rows / 5)
          }
        })
      },
      detailProject(item) {
        this.detailDialog = true
        this.getProjectDetail(item)
      },
      // 根据id查询项目详情
      getProjectDetail(item) {
        this.$api.project.projectDetail(item).then(res => {
          // 调用接口，返回resultCode=1，说明接口调用成功
          if (res.data.resultCode === 1) {
            res.data.data.image = process.env.VUE_APP_URL + res.data.data.image
            this.projectDetails = res.data.data
          }
        })
      }
    }
  }
  </script>
  <style scoped>
  .btn {
    margin: 10px;
  }
  
  .smallBtn {
    margin: 3px;
  }
  </style>
  