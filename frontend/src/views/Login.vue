<template>
    <div class="login-container">
        <el-card class="login-card">
            <template #header>
                <h2 style="text-align:center">高校公寓管理系统</h2>
            </template>
            <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="form.username" placeholder="请输入用户名"/>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="form.password" type="password" show-password placeholder="请输入密码"/>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" style="width:100%">登 录</el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'

const router = useRouter()
const formRef = ref(null)

const form = reactive({
    username: 'admin',
    password: 'admin123'
})

const rules = {
    username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
    password: [{required: true, message: '请输入密码', trigger: 'blur'}]
}

function handleLogin() {
    formRef.value.validate(valid => {
        if (valid) {
            ElMessage.success('登录成功')
            router.push('/')
        }
    })
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
    width: 400px;
}
</style>
