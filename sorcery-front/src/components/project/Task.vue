<template>
  <div>
    <!--Task信息列表-->
    <v-data-table
        :headers="headers"
        :items="desserts"
        class="elevation-1"
        hide-default-footer
    >
      <template v-slot:item.status="{item}">
        <div v-if="item.status===0">无效</div>
        <div v-else-if="item.status===1">新建</div>
        <div v-else-if="item.status===2">执行中</div>
        <div v-else-if="item.status===3"><a @click="getAllure(item)">执行完成</a></div>
      </template>

      <template v-slot:item.shortTestCommand="{item}">
        <v-hover v-slot:default="{hover}">
          <div>
            <span class="short-test-command">{{ item.testCommand }}</span>
            <div v-if="hover" class=" transition-fast-in-fast-out grey v-card--reveal test-command">
              {{ item.testCommand }}
            </div>
          </div>
        </v-hover>
      </template>

      <template v-slot:item.action="{item}">
        <!--执行状态为1（新建）则可以点击执行-->
        <v-btn v-if="item.status===1" color="primary" small @click="doTask(item)" class="btn">执行任务</v-btn>
        <v-btn small v-else disabled>执行任务</v-btn>
        <v-btn color="success" small @click="editTask(item)" class="btn">编辑</v-btn>
        <v-btn color="error" small @click="deleteTask(item)" class="btn">删除</v-btn>
      </template>
    </v-data-table>

    <!--编辑测试任务-->
    <v-dialog v-model="editDialog" width="400px">
      <v-card>
        <v-card-title>编辑任务</v-card-title>
        <v-card-text>
          <v-text-field label="任务名称" v-model="taskName"></v-text-field>
          <v-text-field label="备注" v-model="remark"></v-text-field>
        </v-card-text>
        <v-card-actions>
          <v-spacer>
            <v-btn color="primary" text @click="editDialog=false">取消</v-btn>
            <v-btn color="primary" @click="saveEdit()">确认</v-btn>
          </v-spacer>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!--分页显示处理-->
    <v-pagination v-if="pageLength>0" v-model="currentPage" :length="pageLength" @input="getTaskList()"
                  total-visible="7"></v-pagination>
  </div>
</template>
<script>
export default {
  data() {
    return {
      editDialog: false,
      taskName: '',
      remark: '',
      headers: [
        {text: 'ID', value: 'id'},
        {text: '测试任务名称', value: 'name'},
        {text: 'JenkinsId', value: 'testJenkinsId'},
        {text: '测试用例数量', value: 'caseCount'},
        {text: '执行脚本', value: 'testCommand'},
        {text: '执行状态', value: 'status'},
        {text: '操作', value: 'action'},
      ],
      desserts: [],
      currentPage: 1,
      pageLength: 0
    }
  },
  created() {
    this.getTaskList()
  },
  methods: {
    // 编辑测试任务
    editTask(item) {
      this.taskName = item.name
      this.remark = item.remark
      this.editId = item.id
      this.editDialog = true
    },
    // 保存编辑的测试任务
    saveEdit() {
      let params = {
        name: this.taskName,
        remark: this.remark,
        id: this.editId
      }
      this.$api.task.editTask(params).then(res => {
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
          this.getTaskList()
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
    // 删除测试任务
    deleteTask(item) {
      let params = {
        id: item.id
      }
      this.$api.task.deleteTask(params).then(res => {
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
          this.getTaskList()
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
    // 测试任务列表
    getTaskList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: 10,
      }
      this.$api.task.getTaskList(params).then(res => {
        this.desserts = res.data.data.data
        this.rows = res.data.data.recordsTotal
        this.pageLength = Math.ceil(this.rows / 10)
      })
    },
    // 执行测试任务
    doTask(item) {
      let params = {
        taskId: item.id
      }
      this.$api.task.doTask(params).then(res => {
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '测试任务执行',
            type: 'success'
          })
          this.getTaskList()
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
    // 获取测试报告
    getAllure(item) {
      let params = {
        id: item.id
      }
      this.$api.report.getAllure(params).then(res => {
        // _blank打开新的窗口，浏览器窗口跳转
        window.open(res.data.data.allureReportUrl, "_blank")
      })
    },
  }
}
</script>
<style scoped>
.btn {
  margin: 3px;
}
</style>
