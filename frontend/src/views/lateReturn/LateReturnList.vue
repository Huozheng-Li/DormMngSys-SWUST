<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>晚归异常预警</span>
                    <div style="display:flex;gap:10px">
                        <el-select v-model="statusFilter" placeholder="按类型筛选" clearable style="width:140px" @change="fetchData">
                            <el-option label="晚归" value="LATE_RETURN"/>
                            <el-option label="未归" value="NO_RETURN"/>
                        </el-select>
                        <el-button type="primary" @click="openCreateDialog">新增记录</el-button>
                    </div>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="student.name" label="学生姓名" width="90"/>
                <el-table-column prop="student.studentId" label="学号" width="120"/>
                <el-table-column prop="recordDate" label="日期" width="110"/>
                <el-table-column prop="recordTime" label="时间" width="90"/>
                <el-table-column label="类型" width="80">
                    <template #default="{row}">
                        <el-tag :type="row.status === 'LATE_RETURN' ? 'warning' : 'danger'">
                            {{ row.status === 'LATE_RETURN' ? '晚归' : '未归' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                    <template #default="{row}">
                        <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="新增记录" width="400px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="80px">
                <el-form-item label="学生" prop="student">
                    <el-select v-model="createForm.student" value-key="id" filterable placeholder="搜索学生" style="width:100%">
                        <el-option v-for="s in studentList" :key="s.id" :label="s.name + ' (' + s.studentId + ')'" :value="s"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="类型" prop="status">
                    <el-select v-model="createForm.status" style="width:100%">
                        <el-option label="晚归" value="LATE_RETURN"/>
                        <el-option label="未归" value="NO_RETURN"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="日期" prop="recordDate">
                    <el-date-picker v-model="createForm.recordDate" type="date" style="width:100%"/>
                </el-form-item>
                <el-form-item label="时间" prop="recordTime">
                    <el-time-select v-model="createForm.recordTime" start="00:00" step="00:01" end="23:59" style="width:100%"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="createDialog = false">取消</el-button>
                <el-button type="primary" @click="handleCreate">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {lateReturnApi} from '../../api/lateReturn'
import {userApi} from '../../api/user'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')
const createDialog = ref(false)
const studentList = ref([])
const createFormRef = ref(null)
const createForm = reactive({student: null, status: 'LATE_RETURN', recordDate: '', recordTime: ''})
const createRules = {student: [{required: true, message: '请选择学生', trigger: 'change'}]}

onMounted(() => {
    fetchData()
    userApi.list('STUDENT').then(r => studentList.value = r.data)
})

async function fetchData() {
    loading.value = true
    try {
        const params = {}
        if (statusFilter.value) params.status = statusFilter.value
        const res = await lateReturnApi.list(params)
        list.value = res.data
    } finally {
        loading.value = false
    }
}

function openCreateDialog() {
    createForm.student = null
    createForm.status = 'LATE_RETURN'
    createForm.recordDate = ''
    createForm.recordTime = ''
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        await lateReturnApi.create({
            student: {id: createForm.student.id},
            status: createForm.status,
            recordDate: createForm.recordDate,
            recordTime: createForm.recordTime
        })
        ElMessage.success('记录已保存')
        createDialog.value = false
        await fetchData()
    } catch { ElMessage.error('保存失败') }
}

async function handleDelete(id) {
    try {
        await ElMessageBox.confirm('确认删除？', '提示', {type: 'warning'})
        await lateReturnApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch {}
}
</script>
