import {createRouter, createWebHistory} from 'vue-router'
import {isLoggedIn} from '../stores/auth'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/',
        component: () => import('../views/layout/AppLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/dashboard/Dashboard.vue'),
                meta: {title: '控制台', roles: ['STUDENT', 'COUNSELOR', 'DORM_MANAGER', 'ADMIN']}
            },
            {
                path: 'users',
                name: 'UserList',
                component: () => import('../views/user/UserList.vue'),
                meta: {title: '住户信息管理', roles: ['ADMIN', 'COUNSELOR']}
            },
            {
                path: 'buildings',
                name: 'BuildingList',
                component: () => import('../views/building/BuildingList.vue'),
                meta: {title: '房间床位管理', roles: ['ADMIN', 'DORM_MANAGER']}
            },
            {
                path: 'checkinout',
                name: 'CheckInOutList',
                component: () => import('../views/checkinout/CheckInOutList.vue'),
                meta: {title: '入住退宿管理', roles: ['ADMIN', 'DORM_MANAGER']}
            },
            {
                path: 'repairs',
                name: 'RepairOrderList',
                component: () => import('../views/repair/RepairOrderList.vue'),
                meta: {title: '报修工单管理', roles: ['ADMIN', 'DORM_MANAGER', 'STUDENT']}
            },
            {
                path: 'fees',
                name: 'FeeList',
                component: () => import('../views/fee/FeeList.vue'),
                meta: {title: '费用收缴管理', roles: ['ADMIN', 'STUDENT']}
            },
            {
                path: 'announcements',
                name: 'AnnouncementList',
                component: () => import('../views/announcement/AnnouncementList.vue'),
                meta: {title: '公告通知管理', roles: ['ADMIN', 'DORM_MANAGER', 'STUDENT', 'COUNSELOR']}
            },
            {
                path: 'hygiene',
                name: 'HygieneCheckList',
                component: () => import('../views/hygiene/HygieneCheckList.vue'),
                meta: {title: '卫生检查管理', roles: ['ADMIN', 'DORM_MANAGER']}
            },
            {
                path: 'late-returns',
                name: 'LateReturnList',
                component: () => import('../views/lateReturn/LateReturnList.vue'),
                meta: {title: '晚归异常预警', roles: ['ADMIN', 'DORM_MANAGER', 'COUNSELOR']}
            },
            {
                path: 'visitors',
                name: 'VisitorList',
                component: () => import('../views/visitor/VisitorList.vue'),
                meta: {title: '访客预约管理', roles: ['ADMIN', 'DORM_MANAGER']}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.path !== '/login' && !isLoggedIn()) {
        next('/login')
    } else if (to.path === '/login' && isLoggedIn()) {
        next('/')
    } else {
        next()
    }
})

export default router
