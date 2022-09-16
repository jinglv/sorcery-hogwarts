import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from "@/components/user/Login";
import Register from "@/components/user/Register";
import Jenkins from "@/components/project/Jenkins";
import Task from "@/components/project/Task";
import Report from "@/components/project/Report";
import Cases from "@/components/project/Cases";
import Navigation from "@/components/Navigation";


Vue.use(VueRouter)
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}

const routes = [
    {
        path: '/',
        name: "Login",
        component: Login
    },
    {
        path: '/register',
        name: "Register",
        component: Register
    },
    {
        path: '/home',
        component: Navigation,
        children: [
            {
                path: 'jenkins',
                name: 'Jenkins',
                component: Jenkins
            },
            {
                path: 'cases',
                name: 'Cases',
                component: Cases
            },
            {
                path: 'task',
                name: 'Task',
                component: Task
            },
            {
                path: 'report',
                name: 'Report',
                component: Report
            }
        ]
    }

]

const router = new VueRouter({
    routes
})
export default router
