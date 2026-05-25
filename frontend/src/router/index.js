import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/',
        component: () => import('../views/layout/AppLayout.vue'),
        redirect: '/users',
        children: [
            {
                path: 'users',
                name: 'UserList',
                component: () => import('../views/user/UserList.vue'),
                meta: {title: '住户信息管理'}
            },
            {
                path: 'buildings',
                name: 'BuildingList',
                component: () => import('../views/building/BuildingList.vue'),
                meta: {title: '房间床位管理'}
            },
            {
                path: 'checkinout',
                name: 'CheckInOutList',
                component: () => import('../views/checkinout/CheckInOutList.vue'),
                meta: {title: '入住退宿管理'}
            },
            {
                path: 'repairs',
                name: 'RepairOrderList',
                component: () => import('../views/repair/RepairOrderList.vue'),
                meta: {title: '报修工单管理'}
            },
            {
                path: 'fees',
                name: 'FeeList',
                component: () => import('../views/fee/FeeList.vue'),
                meta: {title: '费用收缴管理'}
            },
            {
                path: 'announcements',
                name: 'AnnouncementList',
                component: () => import('../views/announcement/AnnouncementList.vue'),
                meta: {title: '公告通知管理'}
            },
            {
                path: 'hygiene',
                name: 'HygieneCheckList',
                component: () => import('../views/hygiene/HygieneCheckList.vue'),
                meta: {title: '卫生检查管理'}
            },
            {
                path: 'late-returns',
                name: 'LateReturnList',
                component: () => import('../views/lateReturn/LateReturnList.vue'),
                meta: {title: '晚归异常预警'}
            },
            {
                path: 'visitors',
                name: 'VisitorList',
                component: () => import('../views/visitor/VisitorList.vue'),
                meta: {title: '访客预约管理'}
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
