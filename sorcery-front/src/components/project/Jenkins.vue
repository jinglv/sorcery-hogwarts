<template>
  <div>
    <v-btn color="success" class="btn" @click="addDialog=true">添加Jenkins</v-btn>
    <!--添加Jenkins-->
    <v-dialog v-model="addDialog" width="500px">
      <v-card>
        <v-card-title>添加Jenkins</v-card-title>
        <v-card-text>
          <v-text-field v-model="jenkinsName" label="Jenkins名称"></v-text-field>
          <v-text-field v-model="jenkinsURL" label="Jenkins基础地址"></v-text-field>
          <v-text-field v-model="jenkinsUsername" label="Jenkins认证登录用户名"></v-text-field>
          <v-text-field v-model="jenkinsPassword" type="password" label="Jenkins认证登录密码"></v-text-field>
          <v-text-field v-model="jenkinsCommand" label="Jenkins执行命令"></v-text-field>
          <v-select v-model="commandRunCaseType " :items="runType" label="测试用例类型"></v-select>
          <v-text-field v-model="commandRunCaseSuffix " label="测试用例后缀名"></v-text-field>
          <v-text-field v-model="remark" label="备注"></v-text-field>
          <v-checkbox v-model="defaultJenkinsFlag" label="是否默认为指定Jenkins"></v-checkbox>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="addDialog=false">取消</v-btn>
          <v-btn color="primary" @click="saveJenkins()">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!--Jenkins信息列表-->
    <template>
      <v-data-table
          :headers="headers"
          :items="desserts"
          class="elevation-1"
          hide-default-footer
      >
        <template v-slot:item.commandRunCaseType="{item}">
          <div v-if="item.commandRunCaseType===1">文本</div>
          <div v-if="item.commandRunCaseType===2">文件</div>
        </template>
        <template v-slot:item.action="{item}">
          <v-btn color="success" small @click="editJenkins(item)" class="smallBtn">编辑</v-btn>
          <v-btn color="error" small @click="deleteJenkins(item)" class="smallBtn">删除</v-btn>
        </template>
        <template v-slot:item.defaultJenkinsFlag="{item}">
          <div v-if="item.defaultJenkinsFlag===1">是</div>
          <div v-if="item.defaultJenkinsFlag===0">否</div>
        </template>
      </v-data-table>
    </template>

    <!--编辑Jenkins-->
    <v-dialog v-model="editDialog" width="500px">
      <v-card>
        <v-card-title>编辑Jenkins</v-card-title>
        <v-card-text>
          <v-text-field v-model="jenkinsName" label="Jenkins名称"></v-text-field>
          <v-text-field v-model="jenkinsURL" label="Jenkins基础地址"></v-text-field>
          <v-text-field v-model="jenkinsUsername" label="Jenkins认证登录用户名"></v-text-field>
          <v-text-field v-model="jenkinsPassword" type="password" label="Jenkins认证登录密码"></v-text-field>
          <v-text-field v-model="jenkinsCommand" label="Jenkins执行命令"></v-text-field>
          <v-select v-model="commandRunCaseType " :items="runType" label="测试用例类型"></v-select>
          <v-text-field v-model="commandRunCaseSuffix " label="测试用例后缀名"></v-text-field>
          <v-text-field v-model="remark" label="备注"></v-text-field>
          <v-checkbox v-model="defaultJenkinsFlag" label="是否默认为指定Jenkins"></v-checkbox>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="editDialog=false">取消</v-btn>
          <v-btn color="primary" @click="saveEditJenkins()">保存</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!--分页显示处理-->
    <v-pagination v-if="pageLength>0" v-model="currentPage" :length="pageLength" @input="getJenkinsList()"
                  total-visible="7"></v-pagination>
  </div>
</template>
<script>
export default {
  data() {
    return {
      addDialog: false,
      jenkinsName: '',
      jenkinsURL: '',
      jenkinsUsername: '',
      jenkinsPassword: '',
      jenkinsCommand: '',
      commandRunCaseType: '',
      runType: [
        {text: '文本', value: 1},
        {text: '文件', value: 2}
      ],
      commandRunCaseSuffix: '',
      remark: '',
      defaultJenkinsFlag: '',
      headers: [
        {text: 'ID', value: 'id'},
        {text: 'Jenkins名称', value: 'name'},
        {text: 'Jenkins地址', value: 'url'},
        {text: 'Jenkins测试命令', value: 'testCommand'},
        {text: '测试用例类型', value: 'commandRunCaseType'},
        {text: '测试配置文件后缀', value: 'commandRunCaseSuffix'},
        {text: '是否为默认', value: 'defaultJenkinsFlag'},
        {text: '操作', value: 'action'},
      ],
      desserts: [],
      editDialog: false,
      currentPage: 1,
      pageLength: 0
    }
  },
  created() {
    this.getJenkinsList()
  },
  methods: {
    // 添加Jenkins
    saveJenkins() {
      let params = {
        commandRunCaseType: this.commandRunCaseType,
        commandRunCaseSuffix: this.commandRunCaseSuffix,
        name: this.jenkinsName,
        jenkinsPassword: this.jenkinsPassword,
        jenkinsUsername: this.jenkinsUsername,
        remark: this.remark,
        testCommand: this.jenkinsCommand,
        url: this.jenkinsURL,
        defaultJenkinsFlag: this.defaultJenkinsFlag ? 1 : 0
      }
      this.$api.jenkins.addJenkins(params).then(res => {
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '添加成功',
            type: 'success'
          })
          this.getJenkinsList()
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
    editJenkins(item) {
      this.editId = item.id
      this.jenkinsName = item.name
      this.jenkinsCommand = item.testCommand
      this.jenkinsURL = item.url
      this.jenkinsUsername = item.jenkinsUsername
      this.jenkinsPassword = item.jenkinsPassword
      this.remark = item.remark
      this.commandRunCaseType = item.commandRunCaseType
      this.commandRunCaseSuffix = item.commandRunCaseSuffix
      this.editDialog = true
    },
    // 保存编辑Jenkins
    saveEditJenkins() {
      let params = {
        id: this.editId,
        name: this.jenkinsName,
        commandRunCaseType: this.commandRunCaseType,
        commandRunCaseSuffix: this.commandRunCaseSuffix,
        jenkinsUsername: this.jenkinsUsername,
        jenkinsPassword: this.jenkinsPassword,
        remark: this.remark,
        testCommand: this.jenkinsCommand,
        url: this.jenkinsURL,
        defaultJenkinsFlag: this.defaultJenkinsFlag ? 1 : 0
      }
      this.$api.jenkins.editJenkins(params).then(res => {
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
          this.getJenkinsList()
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
    // 删除Jenkins
    deleteJenkins(item) {
      let params = {
        id: item.id
      }
      this.$api.task.deleteJenkins(params).then(res => {
        if (res.data.resultCode === 1) {
          if (this.instanceNotify) {
            this.instanceNotify.close()
          }
          this.instanceNotify = this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success'
          })
          this.getJenkinsList()
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
    // 分页查询Jenkins列表
    getJenkinsList() {
      let params = {
        pageNum: this.currentPage,
        pageSize: 5,
      }
      this.$api.jenkins.getJenkinsList(params).then(res => {
        // 调用接口，返回resultCode=1，说明接口调用成功
        if (res.data.resultCode === 1) {
          this.desserts = res.data.data.data
          this.rows = res.data.data.recordsTotal
          // 每5条为一页
          this.pageLength = Math.ceil(this.rows / 5)
        }
      })
    },
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
