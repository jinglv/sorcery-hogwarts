<template>
  <div>
    <v-card>
      <v-toolbar color="indigo" dark flat>
        <v-toolbar-title>Sorcery Hogwarts Test</v-toolbar-title>
      </v-toolbar>
    </v-card>

    <v-row justify="center">
      <v-col cols="12" sm="10" md="8" lg="6">
        <v-card ref="form" class="login-form">
          <h1>登录</h1>
          <v-card-text>
            <v-text-field
                v-model="username"
                label="用户名"
                placeholder="用户名"
                required
            ></v-text-field>
          </v-card-text>
          <v-card-text>
            <v-text-field
                v-model="password"
                label="密码"
                placeholder="密码"
                type="password"
                required
            ></v-text-field>
          </v-card-text>
          <v-divider class="mt-10"></v-divider>
          <v-card-actions>
            <v-btn text color="primary" @click="register()">注册</v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="login()">登录</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>
<script>
export default {
  data() {
    return {
      username: 'admin',
      password: 'admin'
    }
  },
  methods: {
    register() {
      this.$router.push({name: 'Register'});
    },
    login() {
      let post_data = {
        username: this.username,
        password: this.password
      }
      this.$api.user.login(post_data).then(res => {
        // 登录接口返回的response的resultCode=1则表示登录成功
        if (res.data.resultCode === 1) {
          // 调用登录接口，将返回的token存储
          localStorage.setItem('token', res.data.data.token)
          localStorage.setItem('username', this.username)
          // 登录成功后跳转到测试用例页面
          this.$router.push({name: 'Project'})
        }
      })
    }
  }
};
</script>
<style scoped>
.login-form {
  text-align: center;
}
</style>
