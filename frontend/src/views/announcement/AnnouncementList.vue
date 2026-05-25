<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>公告通知管理</span>
                    <el-button type="primary" @click="openCreateDialog">发布公告</el-button>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="title" label="标题" min-width="200"/>
                <el-table-column prop="publisher.name" label="发布人" width="90"/>
                <el-table-column label="推送范围" width="100">
                    <template #default="{row}">
                        <el-tag :type="row.scope === 'ALL' ? 'primary' : 'success'">
                            {{ row.scope === 'ALL' ? '全体' : '指定楼栋' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="building?.name" label="目标楼栋" width="120"/>
                <el-table-column label="发布时间" width="155">
                    <template #default="{row}">{{ formatTime(row.createdAt) }}</template>
                </el-table-column>
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{row}">
                        <el-button type="primary" link size="small" @click="viewDetail(row)">查看</el-button>
                        <el-button type="info" link size="small" @click="viewReadStatus(row)">已读情况</el-button>
                        <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="发布公告" width="600px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="createForm.title"/>
                </el-form-item>
                <el-form-item label="推送范围" prop="scope">
                    <el-radio-group v-model="createForm.scope">
                        <el-radio value="ALL">全体推送</el-radio>
                        <el-radio value="BUILDING">指定楼栋</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item v-if="createForm.scope === 'BUILDING'" label="目标楼栋" prop="building">
                    <el-select v-model="createForm.building" value-key="id" placeholder="选择楼栋" style="width:100%">
                        <el-option v-for="b in buildingList" :key="b.id" :label="b.name" :value="b"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <el-input v-model="createForm.content" type="textarea" :rows="6"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="createDialog = false">取消</el-button>
                <el-button type="primary" @click="handleCreate">发布</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="detailDialog" title="公告详情" width="600px">
            <h3>{{ detail?.title }}</h3>
            <div style="color:#909399;margin:10px 0">
                发布人: {{ detail?.publisher?.name }} | {{ formatTime(detail?.createdAt) }}
            </div>
            <el-divider/>
            <div style="white-space:pre-wrap;line-height:1.8">{{ detail?.content }}</div>
            <template #footer>
                <el-button @click="detailDialog = false">关闭</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="readStatusDialog" :title="'已读情况 - ' + readStatusItem?.title" width="500px">
            <el-table :data="readUsers" border stripe>
                <el-table-column prop="userName" label="用户姓名" width="120"/>
                <el-table-column label="阅读时间" width="180">
                    <template #default="{row}">{{ formatTime(row.readAt) }}</template>
                </el-table-column>
            </el-table>
            <div style="margin-top:10px;color:#909399">共 {{ readUsers.length }} 人已读</div>
            <template #footer>
                <el-button @click="readStatusDialog = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {announcementApi} from '../../api/announcement'
import {buildingApi} from '../../api/building'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const createDialog = ref(false)
const detailDialog = ref(false)
const detail = ref(null)
const readStatusDialog = ref(false)
const readStatusItem = ref(null)
const readUsers = ref([])
const buildingList = ref([])
const createFormRef = ref(null)

const createForm = reactive({
    title: '', content: '', scope: 'ALL', building: null
})
const createRules = {
    title: [{required: true, message: '请输入标题', trigger: 'blur'}],
    content: [{required: true, message: '请输入内容', trigger: 'blur'}]
}

onMounted(() => {
    fetchData()
    buildingApi.list().then(r => buildingList.value = r.data)
})

async function fetchData() {
    loading.value = true
    try {
        const res = await announcementApi.list()
        list.value = res.data
    } finally {
        loading.value = false
    }
}

function openCreateDialog() {
    createForm.title = ''
    createForm.content = ''
    createForm.scope = 'ALL'
    createForm.building = null
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        const data = {
            title: createForm.title,
            content: createForm.content,
            scope: createForm.scope,
            publisher: {id: 1}
        }
        if (createForm.scope === 'BUILDING' && createForm.building) {
            data.building = {id: createForm.building.id}
        }
        await announcementApi.create(data)
        ElMessage.success('公告已发布')
        createDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('发布失败')
    }
}

function viewDetail(row) {
    detail.value = row
    announcementApi.markRead(row.id, 1)
    detailDialog.value = true
}

async function viewReadStatus(row) {
    readStatusItem.value = row
    try {
        const res = await announcementApi.getReadStatus(row.id)
        readUsers.value = res.data.users || []
        readStatusDialog.value = true
    } catch {
        ElMessage.error('获取失败')
    }
}

async function handleDelete(id) {
    try {
        await ElMessageBox.confirm('确认删除该公告？', '提示', {type: 'warning'})
        await announcementApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch {
    }
}

function formatTime(t) {
    if (!t) return '-'
    return new Date(t).toLocaleString('zh-CN')
}
</script>
