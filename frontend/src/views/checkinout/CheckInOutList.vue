<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>入住退宿管理</span>
                    <div style="display:flex;gap:10px">
                        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable style="width:140px" @change="fetchData">
                            <el-option label="待审核" value="PENDING"/>
                            <el-option label="已通过" value="APPROVED"/>
                            <el-option label="已驳回" value="REJECTED"/>
                        </el-select>
                        <el-button type="primary" @click="openApplyDialog('CHECK_IN')">办理入住</el-button>
                        <el-button type="warning" @click="openApplyDialog('CHECK_OUT')">办理退宿</el-button>
                    </div>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="student.name" label="学生姓名" width="100"/>
                <el-table-column prop="student.studentId" label="学号" width="120"/>
                <el-table-column label="类型" width="80">
                    <template #default="{row}">
                        <el-tag :type="row.type === 'CHECK_IN' ? 'success' : 'info'">
                            {{ row.type === 'CHECK_IN' ? '入住' : '退宿' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="申请房间" width="100">
                    <template #default="{row}">{{ row.room?.roomNumber || '-' }}</template>
                </el-table-column>
                <el-table-column label="状态" width="100">
                    <template #default="{row}">
                        <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="reason" label="申请原因" min-width="200" show-overflow-tooltip/>
                <el-table-column prop="reviewComment" label="审核意见" width="150" show-overflow-tooltip/>
                <el-table-column label="申请时间" width="160">
                    <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="{row}">
                        <el-button v-if="row.status === 'PENDING'" type="success" link size="small" @click="openReviewDialog(row)">审核</el-button>
                        <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="applyDialog" :title="applyType === 'CHECK_IN' ? '入住申请' : '退宿申请'" width="500px">
            <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="100px">
                <el-form-item label="学生" prop="student">
                    <el-select v-model="applyForm.student" value-key="id" filterable placeholder="搜索学生" style="width:100%">
                        <el-option v-for="s in studentList" :key="s.id" :label="s.name + ' (' + s.studentId + ')'" :value="s"/>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="applyType === 'CHECK_IN'" label="选择房间" prop="room">
                    <el-cascader v-model="selectedRoomId" :options="roomTree" :props="roomCascaderProps" placeholder="楼栋/楼层/房间" style="width:100%" clearable/>
                </el-form-item>
                <el-form-item label="申请原因" prop="reason">
                    <el-input v-model="applyForm.reason" type="textarea" :rows="3"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="applyDialog = false">取消</el-button>
                <el-button type="primary" @click="handleSubmitApply">提交申请</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="reviewDialog" title="审核申请" width="500px">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="学生">{{ reviewItem?.student?.name }}</el-descriptions-item>
                <el-descriptions-item label="类型">{{ reviewItem?.type === 'CHECK_IN' ? '入住' : '退宿' }}</el-descriptions-item>
                <el-descriptions-item label="房间">{{ reviewItem?.room?.roomNumber || '-' }}</el-descriptions-item>
                <el-descriptions-item label="申请原因" :span="2">{{ reviewItem?.reason }}</el-descriptions-item>
            </el-descriptions>
            <el-form ref="reviewFormRef" :model="reviewForm" style="margin-top:15px">
                <el-form-item v-if="reviewItem?.type === 'CHECK_IN'" label="分配床位" prop="bedId">
                    <el-select v-model="reviewForm.bedId" filterable placeholder="选择空闲床位" style="width:100%">
                        <el-option v-for="bed in freeBeds" :key="bed.id" :label="bed.bedNumber + '号床'" :value="bed.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="驳回原因" prop="comment">
                    <el-input v-model="reviewForm.comment" type="textarea" :rows="2" placeholder="通过审核无需填写"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="reviewDialog = false">取消</el-button>
                <el-button type="danger" @click="handleReject">驳回</el-button>
                <el-button type="success" @click="handleApprove">通过</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted, watch} from 'vue'
import {checkinoutApi} from '../../api/checkinout'
import {userApi} from '../../api/user'
import {buildingApi} from '../../api/building'
import {ElMessage} from 'element-plus'
import auth from '../../stores/auth'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')
const applyDialog = ref(false)
const applyType = ref('CHECK_IN')
const studentList = ref([])
const selectedRoomId = ref(null)
const roomTree = ref([])
const roomCascaderProps = {value: 'id', label: 'label', children: 'children', checkStrictly: true}
const applyFormRef = ref(null)
const reviewDialog = ref(false)
const reviewItem = ref(null)
const freeBeds = ref([])
const reviewFormRef = ref(null)

const applyForm = reactive({
    student: null, room: null, reason: '', type: 'CHECK_IN'
})

const applyRules = {
    student: [{required: true, message: '请选择学生', trigger: 'change'}],
    reason: [{required: true, message: '请填写申请原因', trigger: 'blur'}]
}

const reviewForm = reactive({
    bedId: null, comment: ''
})

onMounted(() => {
    fetchData()
    loadStudents()
    loadRoomTree()
})

async function fetchData() {
    loading.value = true
    try {
        const params = {}
        if (statusFilter.value) params.status = statusFilter.value
        const res = await checkinoutApi.list(params)
        list.value = res.data
    } finally {
        loading.value = false
    }
}

async function loadStudents() {
    const res = await userApi.list('STUDENT')
    studentList.value = res.data
}

async function loadRoomTree() {
    const buildings = await buildingApi.list()
    const tree = []
    for (const b of buildings.data) {
        const buildingNode = {id: b.id, label: b.name, children: []}
        const floors = await buildingApi.getFloors(b.id)
        for (const f of floors.data) {
            const floorNode = {id: `floor-${f.id}`, label: `${f.floorNumber}楼`, children: []}
            const rooms = await buildingApi.getRooms(f.id)
            for (const r of rooms.data) {
                floorNode.children.push({id: r.id, label: `${r.roomNumber}(${r.capacity}人间)`})
            }
            buildingNode.children.push(floorNode)
        }
        tree.push(buildingNode)
    }
    roomTree.value = tree
}

function openApplyDialog(type) {
    applyType.value = type
    applyForm.student = null
    applyForm.room = null
    applyForm.reason = ''
    selectedRoomId.value = null
    applyDialog.value = true
}

async function handleSubmitApply() {
    const valid = await applyFormRef.value.validate()
    if (!valid) return
    try {
        const data = {
            student: {id: applyForm.student.id},
            type: applyType.value
        }
        if (applyType.value === 'CHECK_IN') {
            data.room = {id: selectedRoomId.value[selectedRoomId.value.length - 1]}
        }
        if (applyForm.reason) data.reason = applyForm.reason
        await checkinoutApi.create(data)
        ElMessage.success('申请已提交')
        applyDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error(e.response?.data?.message || '提交失败')
    }
}

async function openReviewDialog(row) {
    reviewItem.value = row
    reviewForm.bedId = null
    reviewForm.comment = ''
    if (row.type === 'CHECK_IN' && row.room) {
        try {
            const beds = await buildingApi.getBeds(row.room.id)
            freeBeds.value = beds.data.filter(b => b.status === 'FREE')
        } catch {
            freeBeds.value = []
        }
    }
    reviewDialog.value = true
}

async function handleApprove() {
    if (reviewItem.value.type === 'CHECK_IN' && !reviewForm.bedId) {
        ElMessage.warning('入住申请必须分配床位')
        return
    }
    try {
        await checkinoutApi.approve(reviewItem.value.id, auth.user.id, reviewForm.bedId)
        ElMessage.success('审核通过')
        reviewDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error(e.response?.data?.message || '操作失败')
    }
}

async function handleReject() {
    try {
        await checkinoutApi.reject(reviewItem.value.id, auth.user.id, reviewForm.comment || '驳回')
        ElMessage.success('已驳回')
        reviewDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

function viewDetail(row) {
    ElMessage.info('查看申请详情: ID=' + row.id)
}

function statusTag(status) {
    const map = {PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger'}
    return map[status] || ''
}

function statusLabel(status) {
    const map = {PENDING: '待审核', APPROVED: '已通过', REJECTED: '已驳回'}
    return map[status] || status
}

function formatTime(t) {
    if (!t) return '-'
    return new Date(t).toLocaleString('zh-CN')
}
</script>
