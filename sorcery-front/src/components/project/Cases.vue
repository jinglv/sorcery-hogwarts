<template>
  <div>
    <v-btn color="success" class="btn" @click="createCase()">添加测试用例</v-btn>
    <v-btn color="primary" class="btn" @click="createTask()">生成测试任务</v-btn>
    <!--添加测试用例-->
    <v-dialog v-model="addDialog" width="400">
      <v-card>
        <v-card-title>添加测试用例</v-card-title>
        <v-card-text>
          <v-text-field v-model="caseName" label="测试用例名称"/>
          <v-select v-model="projectValue" :items="projectOption" label="请选择所属项目"/>
          <v-select v-model="selectType" :items="selectItem" label="选择用例类型"/>
          <v-textarea v-if="selectType===1" v-model="caseData" label="输入文本信息"/>
          <v-file-input v-if="selectType===2" v-model="file" label="选择文件"/>
          <v-text-field v-model="remark" label="备注"/>
        </v-card-text>
        <v-card-actions>
          <!--按钮放到右边-->
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="addDialog=false">取消</v-btn>
          <v-btn color="primary" @click="addCase()">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--生成测试任务测试-->
    <v-dialog v-model="taskDialog" width="400px">
      <v-card>
        <v-card-title>生成测试任务</v-card-title>
        <v-card-text>
          <v-text-field label="测试任务名称" v-model="taskName"/>
          <v-select v-model="jenkinsId" :items="jenkinsOption" label="请选择Jenkins"/>
          <v-text-field label="备注" v-model="taskRemark"/>
        </v-card-text>
        <v-card-actions>
          <v-spacer>
            <v-btn text color="primary" @click="taskDialog=false">取消</v-btn>
            <v-btn color="primary" @click="addTask()">确认</v-btn>
          </v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--测试用例列表展示-->
    <template>
      <v-data-table
          v-model="selected"
          :headers="headers"
          :items="desserts"
          :single-select="singleSelect"
          show-select
          hide-default-footer
          class="elevation-1"
      >
        <template v-slot:item.action="{item}">
          <v-btn small color="primary" @click="editItem(item)" class="smallBtn">编辑</v-btn>
          <v-btn small color="error" @click="deleteCases(item)" class="smallBtn">删除</v-btn>
        </template>

        <template v-slot:top>
          <v-switch
              v-model="singleSelect"
              label="Single select"
              class="pa-3"
          ></v-switch>
        </template>
      </v-data-table>
    </template>

    <!--编辑测试用例-->
    <v-dialog v-model="editDialog" width="400">
      <v-card>
        <v-card-title>编辑</v-card-title>
        <v-card-text>
          <v-text-field v-model="caseName" label="用例名称"/>
          <v-select v-model="projectValue" :items="projectOption" label="请选择所属项目"/>
          <v-select v-model="selectType" :items="selectItem" label="选择用例类型"/>
          <v-textarea v-if="selectType===1" v-model="caseData" label="输入文本信息"/>
          <v-file-input v-if="selectType===2" v-model="file" label="选择文件"/>
          <v-text-field label="备注" v-model="remark" />
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="editDialog=false">取消</v-btn>
          <v-btn color="primary" @click="editCases()">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--分页显示处理-->
    <v-pagination v-if="pageLength>0" v-model="currentPage" :length="pageLength" @input="getCaseList()"
                  total-visible="7"/>
  </div>
</template>
<script>
export default {
  data() {
    return {
      addDialog: false,
      singleSelect: false,
      projectValue: 0,
      projectOption: [],
      jenkinsId: 0,
      jenkinsOption: [],
      selected: [],
      headers: [
        {
          text: 'ID',
          align: 'start',
          sortable: false,
          value: 'id',
        },
        {text: '测试用例名称', value: 'caseName'},
        {text: '所属项目名称', value: 'projectName'},
        {text: '测试用例执行', value: 'caseData'},
        {text: '备注', value: 'remark'},
        {text: '操作', value: 'action'}
      ],
      desserts: [],
      addDialog: false,
      caseName: '',
      selectType: 1,
      selectItem: [
        {text: '文本', value: 1},
        {text: '文件', value: 2}
      ],
      caseData: '',
      file: null,
      remark: '',
      editDialog: false,
      editId: '',
      taskDialog: false,
      taskName: '',
      taskRemark: '',
      currentPage: 1,
      pageLength: 0
    }
  },
  mounted() {
    this.getCaseList()
  },
  methods: {
    createCase() {
      this.addDialog = true
      this.getProjectList()
    },
    // 新增测试用例
    addCase() {
      if (this.selectType === 1) {
        let params = {
          caseName: this.caseName,
          caseData: this.caseData,
          remark: this.remark,
          projectId: this.projectValue
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
        params.append('projectId', this.projectValue)
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
    // 测试用例列表
    getCaseList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: 10,
      }
      this.$api.cases.getCaseList(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          var result = res.data.data.data
          this.desserts = result
          this.rows = res.data.data.recordsTotal
          // 每10条为一页
          this.pageLength = Math.ceil(this.rows / 10)
        }
      })
    },
    editItem(item) {
      this.editId = item.id
      this.caseName = item.caseName
      this.projectValue = item.projectId
      this.caseData = item.caseData
      this.remark = item.remark
      this.getProjectList()
      this.editDialog = true
    },
    // 修改测试用例信息
    editCases() {
      let params = {
        id: this.editId,
        caseName: this.caseName,
        caseData: this.caseData,
        remark: this.remark,
        projectId: this.projectValue 
      }
      this.$api.cases.editCase(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '修改成功',
            type: 'success'
          })
          // 修改用例添加成功，关闭弹窗
          this.editDialog = false
          // 修改用例添加成功，刷新列表
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
    },
    // 删除测试用例
    deleteCases(item) {
      let params = {
        id: item.id
      }
      this.$api.cases.deleteCase(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success'
          })
          // 删除用例添加成功，刷新列表
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
    },
    createTask() {
      this.taskDialog = true
      this.getJenkinsList()
    },
    // 添加测试任务
    addTask() {
      let casesId = []
      for (let i = 0; i < this.selected.length; i++) {
        casesId.push(this.selected[i].id)
      }
      let params = {
        caseIdList: casesId,
        testTask: {
          name: this.taskName,
          jenkinsId: this.jenkinsId,
          remark: this.taskRemark
        }
      }
      this.$api.task.addTask(params).then(res => {
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '添加成功',
            type: 'success'
          })
          // 添加测试任务成功，关闭弹窗
          this.taskDialog = false
          // 路由跳转Task页面
          this.$router.push({name: 'Task'})
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
        pageSize: 10,
      }
      this.$api.project.getProjectList(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          let imagesLength = res.data.data.data.length
          for(let i = 0; i < imagesLength; i++) {
            this.projectOption.push(
              {
                value: res.data.data.data[i].id,
                text: res.data.data.data[i].projectName
              }
            )
          }
          this.rows = res.data.data.recordsTotal
          // 每10条为一页
          this.pageLength = Math.ceil(this.rows / 10)
        }
      })
    },
    // 分页查询Project列表
    getJenkinsList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: 10,
      }
      this.$api.jenkins.getJenkinsList(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          let imagesLength = res.data.data.data.length
          for(let i = 0; i < imagesLength; i++) {
            this.jenkinsOption.push(
              {
                value: res.data.data.data[i].id,
                text: res.data.data.data[i].name
              }
            )
          }
          this.rows = res.data.data.recordsTotal
          // 每10条为一页
          this.pageLength = Math.ceil(this.rows / 10)
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
