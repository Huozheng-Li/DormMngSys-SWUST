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
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
