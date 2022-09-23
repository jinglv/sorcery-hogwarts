<template>
  <div>
    <v-card>
      <v-toolbar color="indigo" dark flat>
        <v-toolbar-title>Sorcery Hogwarts Test</v-toolbar-title>

        <v-spacer></v-spacer>
        <v-btn text class="text-none">{{ username }}</v-btn>
        <v-btn text @click="getSwaggerDoc()" class="text-none" v-if="currentUsername === 'admin'">swagger文档</v-btn>
        <v-btn text @click="logout()">退出</v-btn>
      </v-toolbar>

      <v-tabs
          fixed-tabs
          background-color="indigo"
          dark
          :value="0"
      >
        <v-tab @click="$router.push('Project')">项目管理</v-tab>
        <v-tab @click="$router.push('Cases')">用例管理</v-tab>
        <v-tab @click="$router.push('Task')">任务管理</v-tab>
        <v-tab @click="$router.push('Jenkins')">Jenkins管理</v-tab>
        <v-tab @click="$router.push('Report')">报告管理</v-tab>
      </v-tabs>
    </v-card>
    <router-view></router-view>
  </div>
</template>
<script>
export default {
  data() {
    return {
      username: '',
      currentUsername: ''
    }
  },
  created() {
    this.$api.user.isLogin().then(res => {
      this.username = res.data.username
    })
    this.currentUsername = localStorage.getItem('username')
  },
  methods: {
    logout() {
      this.$api.user.logout().then(res => {
        this.$router.push('/')
      })
    },
    getSwaggerDoc() {
      window.open(process.env.VUE_APP_URL + "swagger-ui.html", "_blank")
    }
  }
}
</script>
