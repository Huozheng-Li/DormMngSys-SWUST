<template>
    <el-container style="height:100vh">
        <el-aside width="220px">
            <el-menu
                :default-active="route.path"
                router
                style="height:100%"
                background-color="#304156"
                text-color="#bfcbd9"
                active-text-color="#409EFF"
            >
                <div style="height:60px;display:flex;align-items:center;justify-content:center;color:#fff;font-size:18px;font-weight:bold;border-bottom:1px solid #4a5a6e">
                    公寓管理系统
                </div>
                <el-menu-item index="/users">
                    <el-icon><User/></el-icon>
                    <span>住户信息管理</span>
                </el-menu-item>
                <el-menu-item index="/buildings">
                    <el-icon><HomeFilled/></el-icon>
                    <span>房间床位管理</span>
                </el-menu-item>
                <el-menu-item index="/checkinout">
                    <el-icon><SwitchButton/></el-icon>
                    <span>入住退宿管理</span>
                </el-menu-item>
                <el-menu-item index="/repairs">
                    <el-icon><Tools/></el-icon>
                    <span>报修工单管理</span>
                </el-menu-item>
                <el-menu-item index="/fees">
                    <el-icon><Money/></el-icon>
                    <span>费用收缴管理</span>
                </el-menu-item>
                <el-menu-item index="/announcements">
                    <el-icon><Bell/></el-icon>
                    <span>公告通知管理</span>
                </el-menu-item>
                <el-menu-item index="/hygiene">
                    <el-icon><Document/></el-icon>
                    <span>卫生检查管理</span>
                </el-menu-item>
                <el-menu-item index="/late-returns">
                    <el-icon><Warning/></el-icon>
                    <span>晚归异常预警</span>
                </el-menu-item>
                <el-menu-item index="/visitors">
                    <el-icon><UserFilled/></el-icon>
                    <span>访客预约管理</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header style="display:flex;align-items:center;justify-content:space-between;border-bottom:1px solid #dcdfe6;background:#fff">
                <span style="font-size:16px;font-weight:bold">{{ route.meta.title }}</span>
                <el-dropdown>
                    <span style="cursor:pointer;display:flex;align-items:center;gap:5px">
                        admin <el-icon><ArrowDown/></el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <el-main style="background:#f0f2f5">
                <router-view/>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'

const route = useRoute()
const router = useRouter()

function handleLogout() {
    ElMessage.success('已退出')
    router.push('/login')
}
</script>
