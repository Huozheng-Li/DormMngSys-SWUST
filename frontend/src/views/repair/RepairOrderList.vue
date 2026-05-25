<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>报修工单管理</span>
                    <div style="display:flex;gap:10px">
                        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable style="width:140px" @change="fetchData">
                            <el-option label="待派单" value="PENDING"/>
                            <el-option label="维修中" value="ASSIGNED"/>
                            <el-option label="已完成" value="COMPLETED"/>
                            <el-option label="已验收" value="VERIFIED"/>
                        </el-select>
                        <el-button type="primary" @click="openCreateDialog">提交报修</el-button>
                    </div>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="title" label="报修标题" width="180" show-overflow-tooltip/>
                <el-table-column prop="student.name" label="报修人" width="90"/>
                <el-table-column label="房间" width="100">
                    <template #default="{row}">{{ row.room?.roomNumber || '-' }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{row}">
                        <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="assignee?.name" label="维修工" width="90"/>
                <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip/>
                <el-table-column prop="feedback" label="维修反馈" width="150" show-overflow-tooltip/>
                <el-table-column label="验收状态" width="90">
                    <template #default="{row}">
                        <span v-if="row.verified === true" style="color:#67c23a">已通过</span>
                        <span v-else-if="row.verified === false" style="color:#f56c6c">未通过</span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column label="申请时间" width="155">
                    <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="{row}">
                        <el-button v-if="row.status === 'PENDING'" type="warning" link size="small" @click="openAssignDialog(row)">派单</el-button>
                        <el-button v-if="row.status === 'ASSIGNED'" type="success" link size="small" @click="openCompleteDialog(row)">维修完成</el-button>
                        <el-button v-if="row.status === 'COMPLETED'" type="primary" link size="small" @click="handleVerify(row, true)">验收通过</el-button>
                        <el-button v-if="row.status === 'COMPLETED'" type="danger" link size="small" @click="handleVerify(row, false)">验收不通过</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="提交报修" width="550px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
                <el-form-item label="报修标题" prop="title">
                    <el-input v-model="createForm.title"/>
                </el-form-item>
                <el-form-item label="房间" prop="room">
                    <el-select v-model="createForm.room" value-key="id" filterable placeholder="选择房间" style="width:100%">
                        <el-option v-for="r in roomList" :key="r.id" :label="r.roomNumber" :value="r"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="问题描述" prop="description">
                    <el-input v-model="createForm.description" type="textarea" :rows="4"/>
                </el-form-item>
                <el-form-item label="图片(可选)">
                    <el-upload action="#" list-type="picture-card" :auto-upload="false" multiple>
                        <el-icon><Plus/></el-icon>
                    </el-upload>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="createDialog = false">取消</el-button>
                <el-button type="primary" @click="handleCreate">提交</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="assignDialog" title="派单" width="400px">
            <el-form ref="assignFormRef" :model="assignForm" label-width="80px">
                <el-form-item label="维修工" prop="assigneeId">
                    <el-select v-model="assignForm.assigneeId" filterable placeholder="选择维修工" style="width:100%">
                        <el-option v-for="w in workerList" :key="w.id" :label="w.name" :value="w.id"/>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="assignDialog = false">取消</el-button>
                <el-button type="primary" @click="handleAssign">派单</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="completeDialog" title="维修反馈" width="500px">
            <el-form ref="completeFormRef" :model="completeForm" label-width="100px">
                <el-form-item label="维修反馈" prop="feedback">
                    <el-input v-model="completeForm.feedback" type="textarea" :rows="4" placeholder="描述维修情况"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="completeDialog = false">取消</el-button>
                <el-button type="primary" @click="handleComplete">提交反馈</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {repairApi} from '../../api/repair'
import {userApi} from '../../api/user'
import {buildingApi} from '../../api/building'
import {ElMessage} from 'element-plus'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')
const createDialog = ref(false)
const assignDialog = ref(false)
const completeDialog = ref(false)
const editingOrderId = ref(null)
const workerList = ref([])
const roomList = ref([])

const createFormRef = ref(null)
const createForm = reactive({
    title: '', description: '', room: null
})
const createRules = {
    title: [{required: true, message: '请输入报修标题', trigger: 'blur'}],
    description: [{required: true, message: '请描述问题', trigger: 'blur'}],
    room: [{required: true, message: '请选择房间', trigger: 'change'}]
}

const assignForm = reactive({assigneeId: null})
const completeForm = reactive({feedback: ''})

onMounted(() => {
    fetchData()
    loadWorkers()
    loadRooms()
})

async function fetchData() {
    loading.value = true
    try {
        const params = {}
        if (statusFilter.value) params.status = statusFilter.value
        const res = await repairApi.list(params)
        list.value = res.data
    } finally {
        loading.value = false
    }
}

async function loadWorkers() {
    const res = await userApi.list('DORM_MANAGER')
    workerList.value = res.data
}

async function loadRooms() {
    const buildings = await buildingApi.list()
    const rooms = []
    for (const b of buildings.data) {
        const floors = await buildingApi.getFloors(b.id)
        for (const f of floors.data) {
            const r = await buildingApi.getRooms(f.id)
            rooms.push(...r.data)
        }
    }
    roomList.value = rooms
}

function openCreateDialog() {
    createForm.title = ''
    createForm.description = ''
    createForm.room = null
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        await repairApi.create({
            title: createForm.title,
            description: createForm.description,
            student: {id: 1},
            room: {id: createForm.room.id}
        })
        ElMessage.success('报修已提交')
        createDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

function openAssignDialog(row) {
    editingOrderId.value = row.id
    assignForm.assigneeId = null
    assignDialog.value = true
}

async function handleAssign() {
    if (!assignForm.assigneeId) {
        ElMessage.warning('请选择维修工')
        return
    }
    try {
        await repairApi.assign(editingOrderId.value, assignForm.assigneeId)
        ElMessage.success('派单成功')
        assignDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('派单失败')
    }
}

function openCompleteDialog(row) {
    editingOrderId.value = row.id
    completeForm.feedback = ''
    completeDialog.value = true
}

async function handleComplete() {
    if (!completeForm.feedback) {
        ElMessage.warning('请填写维修反馈')
        return
    }
    try {
        await repairApi.complete(editingOrderId.value, completeForm.feedback)
        ElMessage.success('反馈已提交')
        completeDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

async function handleVerify(row, passed) {
    try {
        await repairApi.verify(row.id, passed)
        ElMessage.success(passed ? '验收通过' : '验收不通过，退回维修')
        await fetchData()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

function statusTag(status) {
    const map = {PENDING: 'info', ASSIGNED: 'warning', COMPLETED: 'success', VERIFIED: 'primary', CLOSED: 'info'}
    return map[status] || ''
}

function statusLabel(status) {
    const map = {PENDING: '待派单', ASSIGNED: '维修中', COMPLETED: '已完成', VERIFIED: '已验收', CLOSED: '已关闭'}
    return map[status] || status
}

function formatTime(t) {
    if (!t) return '-'
    return new Date(t).toLocaleString('zh-CN')
}
</script>
