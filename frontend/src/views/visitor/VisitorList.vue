<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>访客预约管理</span>
                    <el-button type="primary" @click="openCreateDialog">新增预约</el-button>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="visitorName" label="访客姓名" width="100"/>
                <el-table-column prop="visitorPhone" label="联系方式" width="120"/>
                <el-table-column prop="student.name" label="被访学生" width="90"/>
                <el-table-column prop="reason" label="来访事由" min-width="160" show-overflow-tooltip/>
                <el-table-column label="预约时间" width="155">
                    <template #default="{row}">{{ formatTime(row.visitTime) }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{row}">
                        <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="进/出时间" width="180">
                    <template #default="{row}">
                        {{ row.entryTime ? formatTime(row.entryTime) : '-' }} / {{ row.exitTime ? formatTime(row.exitTime) : '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{row}">
                        <el-button v-if="row.status === 'PENDING'" type="success" link size="small" @click="handleConfirm(row)">学生确认</el-button>
                        <el-button v-if="row.status === 'CONFIRMED'" type="primary" link size="small" @click="handleApprove(row)">宿管通过</el-button>
                        <el-button v-if="row.status === 'CONFIRMED'" type="danger" link size="small" @click="handleReject(row)">驳回</el-button>
                        <el-button v-if="row.status === 'APPROVED' && !row.entryTime" type="info" link size="small" @click="handleEntry(row)">登记进入</el-button>
                        <el-button v-if="row.entryTime && !row.exitTime" type="warning" link size="small" @click="handleExit(row)">登记离开</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="访客预约" width="500px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
                <el-form-item label="访客姓名" prop="visitorName">
                    <el-input v-model="createForm.visitorName"/>
                </el-form-item>
                <el-form-item label="联系方式" prop="visitorPhone">
                    <el-input v-model="createForm.visitorPhone"/>
                </el-form-item>
                <el-form-item label="被访学生" prop="student">
                    <el-select v-model="createForm.student" value-key="id" filterable placeholder="搜索学生" style="width:100%">
                        <el-option v-for="s in studentList" :key="s.id" :label="s.name + ' (' + s.studentId + ')'" :value="s"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="预约时间" prop="visitTime">
                    <el-date-picker v-model="createForm.visitTime" type="datetime" style="width:100%" placeholder="选择预约时间"/>
                </el-form-item>
                <el-form-item label="来访事由" prop="reason">
                    <el-input v-model="createForm.reason" type="textarea" :rows="2"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="createDialog = false">取消</el-button>
                <el-button type="primary" @click="handleCreate">提交预约</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {visitorApi} from '../../api/visitor'
import {userApi} from '../../api/user'
import {ElMessage} from 'element-plus'

const loading = ref(false)
const list = ref([])
const createDialog = ref(false)
const studentList = ref([])
const createFormRef = ref(null)
const createForm = reactive({visitorName: '', visitorPhone: '', student: null, visitTime: '', reason: ''})
const createRules = {
    visitorName: [{required: true, message: '请输入访客姓名', trigger: 'blur'}],
    visitorPhone: [{required: true, message: '请输入联系方式', trigger: 'blur'}],
    student: [{required: true, message: '请选择被访学生', trigger: 'change'}],
    visitTime: [{required: true, message: '请选择预约时间', trigger: 'change'}]
}

onMounted(() => {
    fetchData()
    userApi.list('STUDENT').then(r => studentList.value = r.data)
})

async function fetchData() {
    loading.value = true
    try {
        const res = await visitorApi.list()
        list.value = res.data
    } finally {
        loading.value = false
    }
}

function openCreateDialog() {
    createForm.visitorName = ''
    createForm.visitorPhone = ''
    createForm.student = null
    createForm.visitTime = ''
    createForm.reason = ''
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        await visitorApi.create({
            visitorName: createForm.visitorName,
            visitorPhone: createForm.visitorPhone,
            student: {id: createForm.student.id},
            visitTime: createForm.visitTime,
            reason: createForm.reason
        })
        ElMessage.success('预约已提交')
        createDialog.value = false
        await fetchData()
    } catch { ElMessage.error('提交失败') }
}

async function handleConfirm(row) {
    await visitorApi.confirm(row.id)
    ElMessage.success('学生已确认')
    await fetchData()
}

async function handleApprove(row) {
    await visitorApi.approve(row.id)
    ElMessage.success('预约已通过')
    await fetchData()
}

async function handleReject(row) {
    await visitorApi.reject(row.id)
    ElMessage.success('预约已驳回')
    await fetchData()
}

async function handleEntry(row) {
    await visitorApi.recordEntry(row.id)
    ElMessage.success('已登记进入')
    await fetchData()
}

async function handleExit(row) {
    await visitorApi.recordExit(row.id)
    ElMessage.success('已登记离开')
    await fetchData()
}

function statusTag(s) {
    const map = {PENDING: 'info', CONFIRMED: 'primary', APPROVED: 'success', REJECTED: 'danger', COMPLETED: ''}
    return map[s] || ''
}

function statusLabel(s) {
    const map = {PENDING: '待确认', CONFIRMED: '已确认', APPROVED: '已通过', REJECTED: '已驳回', COMPLETED: '已完成'}
    return map[s] || s
}

function formatTime(t) {
    if (!t) return '-'
    return new Date(t).toLocaleString('zh-CN')
}
</script>
