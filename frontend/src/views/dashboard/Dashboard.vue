<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="6" v-for="card in cards" :key="card.title">
                <el-card :body-style="{padding:'20px'}" shadow="hover" style="cursor:pointer;margin-bottom:20px" @click="$router.push(card.path)">
                    <div style="display:flex;align-items:center;gap:15px">
                        <div :style="{width:'50px',height:'50px',borderRadius:'12px',background:card.color,display:'flex',alignItems:'center',justifyContent:'center'}">
                            <el-icon :size="24" color="#fff"><component :is="card.icon"/></el-icon>
                        </div>
                        <div>
                            <div style="font-size:24px;font-weight:bold;color:#303133">{{ card.count }}</div>
                            <div style="font-size:13px;color:#909399">{{ card.title }}</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20">
            <el-col :span="12">
                <el-card shadow="hover">
                    <template #header><span style="font-weight:bold">快捷操作</span></template>
                    <div style="display:flex;flex-wrap:wrap;gap:12px">
                        <el-button v-for="act in quickActions" :key="act.label" :type="act.type" @click="$router.push(act.path)" plain>
                            <el-icon style="margin-right:4px"><component :is="act.icon"/></el-icon>
                            {{ act.label }}
                        </el-button>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card shadow="hover">
                    <template #header><span style="font-weight:bold">系统信息</span></template>
                    <el-descriptions :column="1" border size="small">
                        <el-descriptions-item label="系统名称">高校公寓管理系统</el-descriptions-item>
                        <el-descriptions-item label="当前用户">{{ auth.user?.name }}</el-descriptions-item>
                        <el-descriptions-item label="角色">{{ roleName }}</el-descriptions-item>
                        <el-descriptions-item label="版本">v1.0.0</el-descriptions-item>
                    </el-descriptions>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import auth from '../../stores/auth'

const router = useRouter()

const roleName = computed(() => {
    const map = {STUDENT: '学生', COUNSELOR: '辅导员', DORM_MANAGER: '宿管', ADMIN: '管理员'}
    return map[auth.user?.role] || '-'
})

const cards = [
    {title: '住户总数', count: 4, icon: 'User', color: '#409EFF', path: '/users'},
    {title: '楼栋数量', count: 1, icon: 'HomeFilled', color: '#67C23A', path: '/buildings'},
    {title: '报修工单', count: 0, icon: 'Tools', color: '#E6A23C', path: '/repairs'},
    {title: '待审申请', count: 0, icon: 'SwitchButton', color: '#F56C6C', path: '/checkinout'},
]

const quickActions = [
    {label: '住户管理', icon: 'User', type: 'primary', path: '/users'},
    {label: '房间管理', icon: 'HomeFilled', type: 'success', path: '/buildings'},
    {label: '入住申请', icon: 'SwitchButton', type: 'warning', path: '/checkinout'},
    {label: '报修提交', icon: 'Tools', type: 'danger', path: '/repairs'},
    {label: '费用管理', icon: 'Money', type: 'info', path: '/fees'},
    {label: '发布公告', icon: 'Bell', type: '', path: '/announcements'},
]
</script>
