<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>卫生检查管理</span>
                    <el-button type="primary" @click="openCreateDialog">新增检查</el-button>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column label="房间" width="100">
                    <template #default="{row}">{{ row.room?.roomNumber || '-' }}</template>
                </el-table-column>
                <el-table-column prop="inspector.name" label="检查人" width="90"/>
                <el-table-column prop="score" label="评分" width="70">
                    <template #default="{row}">
                        <el-tag :type="row.score >= 80 ? 'success' : row.score >= 60 ? 'warning' : 'danger'">{{ row.score }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="comments" label="评语" min-width="200" show-overflow-tooltip/>
                <el-table-column prop="checkDate" label="检查日期" width="110"/>
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{row}">
                        <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="新增卫生检查" width="500px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="90px">
                <el-form-item label="房间" prop="room">
                    <el-select v-model="createForm.room" value-key="id" filterable placeholder="选择房间" style="width:100%">
                        <el-option v-for="r in roomList" :key="r.id" :label="r.roomNumber" :value="r"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="评分" prop="score">
                    <el-input-number v-model="createForm.score" :min="0" :max="100" style="width:100%"/>
                </el-form-item>
                <el-form-item label="评语" prop="comments">
                    <el-input v-model="createForm.comments" type="textarea" :rows="3"/>
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
import {hygieneApi} from '../../api/hygiene'
import {buildingApi} from '../../api/building'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const createDialog = ref(false)
const roomList = ref([])
const createFormRef = ref(null)
const createForm = reactive({room: null, score: 80, comments: ''})
const createRules = {room: [{required: true, message: '请选择房间', trigger: 'change'}]}

onMounted(() => {
    fetchData()
    loadRooms()
})

async function fetchData() {
    loading.value = true
    try {
        const res = await hygieneApi.list()
        list.value = res.data
    } finally {
        loading.value = false
    }
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
    createForm.room = null
    createForm.score = 80
    createForm.comments = ''
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        await hygieneApi.create({
            room: {id: createForm.room.id},
            inspector: {id: 1},
            score: createForm.score,
            comments: createForm.comments,
            checkDate: new Date().toISOString().split('T')[0]
        })
        ElMessage.success('检查记录已保存')
        createDialog.value = false
        await fetchData()
    } catch {
        ElMessage.error('保存失败')
    }
}

async function handleDelete(id) {
    try {
        await ElMessageBox.confirm('确认删除？', '提示', {type: 'warning'})
        await hygieneApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch {}
}
</script>
