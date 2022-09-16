import Vue from 'vue'
import './plugins/axios'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify';
import api from './api/api'
// 引入element-ui样式
import 'element-ui/lib/theme-chalk/index.css'
import {Notification} from 'element-ui'

Vue.config.productionTip = false;
// 将api/api.js挂载上
Vue.prototype.$api = api;
Vue.prototype.$notify = Notification;

new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount('#app')
