<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>住户信息列表</span>
                    <div style="display:flex;gap:10px">
                        <el-select v-model="roleFilter" placeholder="按角色筛选" clearable style="width:140px" @change="fetchData">
                            <el-option label="学生" value="STUDENT"/>
                            <el-option label="辅导员" value="COUNSELOR"/>
                            <el-option label="宿管" value="DORM_MANAGER"/>
                            <el-option label="管理员" value="ADMIN"/>
                        </el-select>
                        <el-input v-model="searchKeyword" placeholder="搜索学号/姓名" clearable style="width:200px" @clear="fetchData" @keyup.enter="handleSearch"/>
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button type="success" @click="openDialog()">新增住户</el-button>
                        <el-button>批量导入</el-button>
                    </div>
                </div>
            </template>
            <el-table :data="userList" border stripe style="width:100%" v-loading="loading">
                <el-table-column prop="studentId" label="学号" width="130"/>
                <el-table-column prop="name" label="姓名" width="100"/>
                <el-table-column prop="gender" label="性别" width="60"/>
                <el-table-column prop="className" label="班级" width="150"/>
                <el-table-column prop="major" label="专业" width="150"/>
                <el-table-column prop="phone" label="联系方式" width="130"/>
                <el-table-column prop="role" label="角色" width="100">
                    <template #default="{row}">
                        <el-tag :type="roleTagType(row.role)">{{ roleLabel(row.role) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{row}">
                        <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
                        <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑住户' : '新增住户'" width="600px">
            <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用户名" prop="username">
                            <el-input v-model="form.username" :disabled="isEdit"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="姓名" prop="name">
                            <el-input v-model="form.name"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="角色" prop="role">
                            <el-select v-model="form.role" style="width:100%">
                                <el-option label="学生" value="STUDENT"/>
                                <el-option label="辅导员" value="COUNSELOR"/>
                                <el-option label="宿管" value="DORM_MANAGER"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="性别" prop="gender">
                            <el-select v-model="form.gender" style="width:100%">
                                <el-option label="男" value="男"/>
                                <el-option label="女" value="女"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="学号" prop="studentId">
                            <el-input v-model="form.studentId"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="班级" prop="className">
                            <el-input v-model="form.className"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="专业" prop="major">
                    <el-input v-model="form.major"/>
                </el-form-item>
                <el-form-item label="联系方式" prop="phone">
                    <el-input v-model="form.phone"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {userApi} from '../../api/user'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const userList = ref([])
const roleFilter = ref('')
const searchKeyword = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const defaultForm = {
    username: '', password: '123456', name: '', role: 'STUDENT',
    gender: '男', studentId: '', className: '', major: '', phone: ''
}

const form = reactive({...defaultForm})

const rules = {
    username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
    name: [{required: true, message: '请输入姓名', trigger: 'blur'}],
    role: [{required: true, message: '请选择角色', trigger: 'change'}]
}

onMounted(() => fetchData())

function roleLabel(role) {
    const map = {STUDENT: '学生', COUNSELOR: '辅导员', DORM_MANAGER: '宿管', ADMIN: '管理员'}
    return map[role] || role
}

function roleTagType(role) {
    const map = {STUDENT: '', COUNSELOR: 'success', DORM_MANAGER: 'warning', ADMIN: 'danger'}
    return map[role] || ''
}

async function fetchData() {
    loading.value = true
    try {
        const res = await userApi.list(roleFilter.value || undefined)
        userList.value = res.data
    } finally {
        loading.value = false
    }
}

async function handleSearch() {
    if (!searchKeyword.value) {
        return fetchData()
    }
    loading.value = true
    try {
        const res = await userApi.search(searchKeyword.value)
        userList.value = res.data
    } finally {
        loading.value = false
    }
}

function openDialog(row) {
    if (row) {
        isEdit.value = true
        editingId.value = row.id
        Object.assign(form, row)
    } else {
        isEdit.value = false
        editingId.value = null
        Object.assign(form, {...defaultForm})
    }
    dialogVisible.value = true
}

async function handleSave() {
    const valid = await formRef.value.validate()
    if (!valid) return
    try {
        if (isEdit.value) {
            await userApi.update(editingId.value, form)
            ElMessage.success('更新成功')
        } else {
            await userApi.create(form)
            ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error(e.response?.data?.message || '操作失败')
    }
}

async function handleDelete(id) {
    try {
        await ElMessageBox.confirm('确认删除该住户？', '提示', {type: 'warning'})
        await userApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch {
    }
}
</script>
