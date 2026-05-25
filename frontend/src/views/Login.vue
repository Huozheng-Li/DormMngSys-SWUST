<template>
    <div class="login-container">
        <div class="login-bg"></div>
        <el-card class="login-card" shadow="xl">
            <template #header>
                <div style="text-align:center">
                    <el-icon :size="40" color="#409EFF"><School/></el-icon>
                    <h2 style="margin:8px 0 0;color:#303133">高校公寓管理系统</h2>
                    <p style="color:#909399;margin:4px 0 0;font-size:13px">Dormitory Management System</p>
                </div>
            </template>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
                <el-form-item prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"/>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock"/>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%;height:44px;font-size:16px">登 录</el-button>
                </el-form-item>
            </el-form>
            <div style="text-align:center;color:#909399;font-size:12px">
                <p>测试账号：admin / admin123</p>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {User, Lock} from '@element-plus/icons-vue'
import {loginApi} from '../api/auth'
import {login} from '../stores/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
    username: 'admin',
    password: 'admin123'
})

const rules = {
    username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
    password: [{required: true, message: '请输入密码', trigger: 'blur'}]
}

async function handleLogin() {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
    loading.value = true
    try {
        const res = await loginApi(form.username, form.password)
        login(res.data.token, res.data.user)
        ElMessage.success(`欢迎回来，${res.data.user.name}`)
        router.push('/')
    } catch (e) {
        ElMessage.error(e.response?.data?.message || '登录失败')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #1a2a6c, #b21f1f, #fdbb2d);
    background-size: 400% 400%;
    animation: gradient 15s ease infinite;
    position: relative;
    overflow: hidden;
}

@keyframes gradient {
    0% { background-position: 0% 50%; }
    50% { background-position: 100% 50%; }
    100% { background-position: 0% 50%; }
}

.login-bg {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.05) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;
}

.login-card {
    width: 420px;
    border-radius: 12px;
    backdrop-filter: blur(10px);
    background: rgba(255, 255, 255, 0.95);
    z-index: 1;
}

.login-card :deep(.el-card__header) {
    padding: 30px 20px 20px;
    border-bottom: none;
}
</style>
