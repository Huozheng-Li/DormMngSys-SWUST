<template>
    <el-container style="height:100vh">
        <el-aside width="220px">
            <el-menu
                :default-active="route.path"
                router
                style="height:100%;border-right:none"
                background-color="#1d1e1f"
                text-color="#bfcbd9"
                active-text-color="#409EFF"
            >
                <div style="height:64px;display:flex;align-items:center;justify-content:center;gap:8px;border-bottom:1px solid #2a2b2d">
                    <el-icon :size="24" color="#409EFF"><School/></el-icon>
                    <span style="color:#fff;font-size:16px;font-weight:bold">公寓管理系统</span>
                </div>

                <el-menu-item index="/dashboard">
                    <el-icon><Odometer/></el-icon>
                    <span>控制台</span>
                </el-menu-item>

                <template v-for="item in menuItems" :key="item.index">
                    <el-menu-item v-if="hasRole(item.roles)" :index="item.index">
                        <el-icon><component :is="item.icon"/></el-icon>
                        <span>{{ item.label }}</span>
                    </el-menu-item>
                </template>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header style="display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #e4e7ed;background:#fff;height:56px;padding:0 20px">
                <el-breadcrumb>
                    <el-breadcrumb-item to="/dashboard">首页</el-breadcrumb-item>
                    <el-breadcrumb-item v-if="route.meta.title">{{ route.meta.title }}</el-breadcrumb-item>
                </el-breadcrumb>
                <el-dropdown>
                    <span style="cursor:pointer;display:flex;align-items:center;gap:6px;font-size:14px">
                        <el-avatar :size="32" style="background:#409EFF">{{ auth.user?.name?.charAt(0) }}</el-avatar>
                        {{ auth.user?.name }}
                        <el-icon><ArrowDown/></el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item disabled>
                                <el-tag size="small">{{ roleName }}</el-tag>
                            </el-dropdown-item>
                            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <el-main style="background:#f0f2f5;padding:20px">
                <router-view/>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import auth, {logout} from '../../stores/auth'

const route = useRoute()
const router = useRouter()

const roleName = computed(() => {
    const map = {STUDENT: '学生', COUNSELOR: '辅导员', DORM_MANAGER: '宿管', ADMIN: '管理员'}
    return map[auth.user?.role] || '-'
})

function hasRole(roles) {
    return roles.includes(auth.user?.role)
}

const menuItems = [
    {index: '/users', label: '住户信息管理', icon: 'User', roles: ['ADMIN', 'COUNSELOR']},
    {index: '/buildings', label: '房间床位管理', icon: 'HomeFilled', roles: ['ADMIN', 'DORM_MANAGER']},
    {index: '/checkinout', label: '入住退宿管理', icon: 'SwitchButton', roles: ['ADMIN', 'DORM_MANAGER']},
    {index: '/repairs', label: '报修工单管理', icon: 'Tools', roles: ['ADMIN', 'DORM_MANAGER', 'STUDENT']},
    {index: '/fees', label: '费用收缴管理', icon: 'Money', roles: ['ADMIN', 'STUDENT']},
    {index: '/announcements', label: '公告通知管理', icon: 'Bell', roles: ['ADMIN', 'DORM_MANAGER', 'STUDENT', 'COUNSELOR']},
    {index: '/hygiene', label: '卫生检查管理', icon: 'Document', roles: ['ADMIN', 'DORM_MANAGER']},
    {index: '/late-returns', label: '晚归异常预警', icon: 'Warning', roles: ['ADMIN', 'DORM_MANAGER', 'COUNSELOR']},
    {index: '/visitors', label: '访客预约管理', icon: 'UserFilled', roles: ['ADMIN', 'DORM_MANAGER']},
]

function handleLogout() {
    logout()
    ElMessage.success('已退出')
    router.push('/login')
}
</script>
