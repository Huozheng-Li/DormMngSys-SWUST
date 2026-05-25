<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>楼栋管理</span>
                    <el-button type="success" @click="openBuildingDialog()">新增楼栋</el-button>
                </div>
            </template>
            <el-table :data="buildingList" border stripe v-loading="loading">
                <el-table-column prop="name" label="楼栋名称" width="150"/>
                <el-table-column prop="address" label="地址" width="200"/>
                <el-table-column prop="totalFloors" label="楼层数" width="80"/>
                <el-table-column prop="description" label="描述" min-width="200"/>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{row}">
                        <el-button type="primary" link size="small" @click="viewBuilding(row)">查看楼层</el-button>
                        <el-button type="danger" link size="small" @click="handleDeleteBuilding(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="buildingDialog" :title="'楼栋详情 - ' + currentBuilding?.name" width="90%" v-if="currentBuilding">
            <el-tabs v-model="activeFloorTab">
                <el-tab-pane v-for="floor in floorList" :key="floor.id" :label="floor.floorNumber + '楼'" :name="String(floor.id)">
                    <div style="margin-bottom:10px">
                        <el-button type="success" size="small" @click="openRoomDialog(floor.id)">新增房间</el-button>
                        <span style="margin-left:10px;color:#909399">已绑定楼层 ID: {{ floor.id }}</span>
                    </div>
                    <el-table :data="roomList" border stripe>
                        <el-table-column prop="roomNumber" label="房间号" width="100"/>
                        <el-table-column prop="roomType" label="类型" width="100"/>
                        <el-table-column prop="capacity" label="容量" width="70"/>
                        <el-table-column prop="status" label="状态" width="100">
                            <template #default="{row}">
                                <el-tag :type="roomStatusTag(row.status)">{{ roomStatusLabel(row.status) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column label="床位" min-width="200">
                            <template #default="{row}">
                                <el-tag v-for="bed in row.beds" :key="bed.id"
                                        :type="bed.status === 'FREE' ? 'success' : bed.status === 'OCCUPIED' ? 'danger' : 'warning'"
                                        style="margin:2px" size="small">
                                    {{ bed.bedNumber }}号床({{ bedStatusLabel(bed.status) }})
                                </el-tag>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
            <template #footer>
                <el-button @click="buildingDialog = false">关闭</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="buildingFormDialog" :title="isEditBuilding ? '编辑楼栋' : '新增楼栋'" width="500px">
            <el-form ref="buildingFormRef" :model="buildingForm" :rules="buildingRules" label-width="100px">
                <el-form-item label="楼栋名称" prop="name">
                    <el-input v-model="buildingForm.name"/>
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    <el-input v-model="buildingForm.address"/>
                </el-form-item>
                <el-form-item label="楼层数" prop="totalFloors">
                    <el-input-number v-model="buildingForm.totalFloors" :min="1" :max="30"/>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input v-model="buildingForm.description" type="textarea"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="buildingFormDialog = false">取消</el-button>
                <el-button type="primary" @click="handleSaveBuilding">保存</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="roomDialog" title="新增房间" width="400px">
            <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="100px">
                <el-form-item label="房间号" prop="roomNumber">
                    <el-input v-model="roomForm.roomNumber"/>
                </el-form-item>
                <el-form-item label="类型" prop="roomType">
                    <el-input v-model="roomForm.roomType"/>
                </el-form-item>
                <el-form-item label="容量" prop="capacity">
                    <el-input-number v-model="roomForm.capacity" :min="1" :max="8"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="roomDialog = false">取消</el-button>
                <el-button type="primary" @click="handleSaveRoom">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted, watch} from 'vue'
import {buildingApi} from '../../api/building'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const buildingList = ref([])
const buildingDialog = ref(false)
const buildingFormDialog = ref(false)
const isEditBuilding = ref(false)
const currentBuilding = ref(null)
const activeFloorTab = ref('')
const floorList = ref([])
const roomList = ref([])
const roomDialog = ref(false)
const addingFloorId = ref(null)
const buildingFormRef = ref(null)
const roomFormRef = ref(null)

const buildingForm = reactive({
    name: '', address: '', totalFloors: 5, description: ''
})

const buildingRules = {
    name: [{required: true, message: '请输入楼栋名称', trigger: 'blur'}],
    address: [{required: true, message: '请输入地址', trigger: 'blur'}]
}

const roomForm = reactive({
    roomNumber: '', roomType: '标准间', capacity: 4
})

const roomRules = {
    roomNumber: [{required: true, message: '请输入房间号', trigger: 'blur'}]
}

onMounted(() => fetchBuildings())

async function fetchBuildings() {
    loading.value = true
    try {
        const res = await buildingApi.list()
        buildingList.value = res.data
    } finally {
        loading.value = false
    }
}

function openBuildingDialog() {
    isEditBuilding.value = false
    buildingForm.name = ''
    buildingForm.address = ''
    buildingForm.totalFloors = 5
    buildingForm.description = ''
    buildingFormDialog.value = true
}

async function handleSaveBuilding() {
    const valid = await buildingFormRef.value.validate()
    if (!valid) return
    try {
        const building = await buildingApi.create(buildingForm)
        for (let i = 1; i <= buildingForm.totalFloors; i++) {
            await buildingApi.addFloor(building.data.id, {floorNumber: i})
        }
        ElMessage.success('创建成功')
        buildingFormDialog.value = false
        await fetchBuildings()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

async function viewBuilding(building) {
    currentBuilding.value = building
    const res = await buildingApi.getFloors(building.id)
    floorList.value = res.data
    if (res.data.length > 0) {
        activeFloorTab.value = String(res.data[0].id)
        await loadRooms(res.data[0].id)
    }
    buildingDialog.value = true
}

watch(activeFloorTab, async (val) => {
    if (val) await loadRooms(Number(val))
})

async function loadRooms(floorId) {
    const res = await buildingApi.getRooms(floorId)
    for (const room of res.data) {
        const bedRes = await buildingApi.getBeds(room.id)
        room.beds = bedRes.data
    }
    roomList.value = res.data
}

function openRoomDialog(floorId) {
    addingFloorId.value = floorId
    roomForm.roomNumber = ''
    roomForm.roomType = '标准间'
    roomForm.capacity = 4
    roomDialog.value = true
}

async function handleSaveRoom() {
    const valid = await roomFormRef.value.validate()
    if (!valid) return
    try {
        await buildingApi.addRoom(addingFloorId.value, roomForm)
        ElMessage.success('房间创建成功')
        roomDialog.value = false
        await loadRooms(addingFloorId.value)
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

async function handleDeleteBuilding(id) {
    try {
        await ElMessageBox.confirm('确认删除该楼栋？', '提示', {type: 'warning'})
        await buildingApi.delete(id)
        ElMessage.success('删除成功')
        await fetchBuildings()
    } catch {
    }
}

function roomStatusLabel(status) {
    const map = {AVAILABLE: '空闲', PARTIAL: '部分入住', FULL: '已满', MAINTENANCE: '维修'}
    return map[status] || status
}

function roomStatusTag(status) {
    const map = {AVAILABLE: 'success', PARTIAL: 'warning', FULL: 'danger', MAINTENANCE: 'info'}
    return map[status] || ''
}

function bedStatusLabel(status) {
    const map = {FREE: '空闲', OCCUPIED: '已入住', MAINTENANCE: '维修'}
    return map[status] || status
}
</script>
