<template>
  <div>
    <v-card>
      <v-toolbar color="indigo" dark flat>
        <v-toolbar-title>Sorcery Test</v-toolbar-title>
      </v-toolbar>
    </v-card>

    <v-row justify="center">
      <v-col cols="12" sm="10" md="8" lg="6">
        <v-card ref="form" class="register">
          <h1>注册</h1>
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
          <v-card-text>
            <v-text-field
                v-model="email"
                label="邮箱"
                placeholder="邮箱"
                required
            ></v-text-field>
          </v-card-text>
          <v-divider class="mt-10"></v-divider>
          <v-card-actions>
            <v-btn color="primary" @click="register()">
              确认
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="login()">
              返回登录
            </v-btn>
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
      username: '',
      password: '',
      email: ''
    }
  },
  methods: {
    login() {
      this.$router.push({name: 'Login'});
    },
    register() {
      let post_data = {
        username: this.username,
        password: this.password,
        email: this.email
      }
      this.$api.user.register(post_data);
    }
  }
};
</script>
<style scoped>
.register {
  text-align: center;
}
</style>
