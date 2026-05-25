<template>
    <div>
        <el-card>
            <template #header>
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <span>费用收缴管理</span>
                    <div style="display:flex;gap:10px">
                        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable style="width:140px" @change="fetchData">
                            <el-option label="未缴费" value="UNPAID"/>
                            <el-option label="已缴费" value="PAID"/>
                            <el-option label="已逾期" value="OVERDUE"/>
                        </el-select>
                        <el-button type="success" @click="openCreateDialog">生成账单</el-button>
                        <el-button>导出明细</el-button>
                    </div>
                </div>
            </template>
            <el-table :data="list" border stripe v-loading="loading">
                <el-table-column prop="student.name" label="学生" width="90"/>
                <el-table-column prop="student.studentId" label="学号" width="120"/>
                <el-table-column label="费用类型" width="100">
                    <template #default="{row}">
                        <el-tag :type="feeTypeTag(row.feeType)">{{ feeTypeLabel(row.feeType) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="amount" label="金额(元)" width="100"/>
                <el-table-column prop="period" label="账期" width="100"/>
                <el-table-column prop="dueDate" label="截止日期" width="110"/>
                <el-table-column label="状态" width="90">
                    <template #default="{row}">
                        <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="paidAt" label="缴费日期" width="110"/>
                <el-table-column prop="paymentMethod" label="缴费方式" width="100"/>
                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{row}">
                        <el-button v-if="row.status === 'UNPAID' || row.status === 'OVERDUE'" type="success" link size="small" @click="openPayDialog(row)">缴费</el-button>
                        <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <el-dialog v-model="createDialog" title="生成账单" width="500px">
            <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
                <el-form-item label="学生" prop="student">
                    <el-select v-model="createForm.student" value-key="id" filterable placeholder="搜索学生" style="width:100%">
                        <el-option v-for="s in studentList" :key="s.id" :label="s.name + ' (' + s.studentId + ')'" :value="s"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="费用类型" prop="feeType">
                    <el-select v-model="createForm.feeType" style="width:100%">
                        <el-option label="水费" value="WATER"/>
                        <el-option label="电费" value="ELECTRICITY"/>
                        <el-option label="住宿费" value="ACCOMMODATION"/>
                    </el-select>
                </el-form-item>
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="金额" prop="amount">
                            <el-input-number v-model="createForm.amount" :min="0" :precision="2" style="width:100%"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账期" prop="period">
                            <el-input v-model="createForm.period" placeholder="如: 2026年5月"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="截止日期" prop="dueDate">
                    <el-date-picker v-model="createForm.dueDate" type="date" style="width:100%" placeholder="选择截止日期"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="createDialog = false">取消</el-button>
                <el-button type="primary" @click="handleCreate">生成</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="payDialog" title="线上缴费" width="400px">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="学生">{{ payItem?.student?.name }}</el-descriptions-item>
                <el-descriptions-item label="费用类型">{{ payItem ? feeTypeLabel(payItem.feeType) : '' }}</el-descriptions-item>
                <el-descriptions-item label="金额">{{ payItem?.amount }} 元</el-descriptions-item>
            </el-descriptions>
            <el-form ref="payFormRef" :model="payForm" style="margin-top:15px">
                <el-form-item label="缴费方式" prop="method">
                    <el-select v-model="payForm.method" style="width:100%">
                        <el-option label="微信支付" value="WECHAT"/>
                        <el-option label="支付宝" value="ALIPAY"/>
                        <el-option label="银行转账" value="BANK"/>
                    </el-select>
                </el-form-item>
                <el-form-item label="交易编号" prop="transactionId">
                    <el-input v-model="payForm.transactionId" placeholder="可选"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="payDialog = false">取消</el-button>
                <el-button type="primary" @click="handlePay">确认缴费</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {ref, reactive, onMounted} from 'vue'
import {feeApi} from '../../api/fee'
import {userApi} from '../../api/user'
import {ElMessage, ElMessageBox} from 'element-plus'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('')
const createDialog = ref(false)
const payDialog = ref(false)
const payItem = ref(null)
const studentList = ref([])
const createFormRef = ref(null)

const createForm = reactive({
    student: null, feeType: 'WATER', amount: 0, period: '', dueDate: null
})
const createRules = {
    student: [{required: true, message: '请选择学生', trigger: 'change'}],
    amount: [{required: true, message: '请输入金额', trigger: 'blur'}],
    dueDate: [{required: true, message: '请选择截止日期', trigger: 'change'}]
}
const payForm = reactive({method: 'WECHAT', transactionId: ''})

onMounted(() => {
    fetchData()
    loadStudents()
})

async function fetchData() {
    loading.value = true
    try {
        const params = {}
        if (statusFilter.value) params.status = statusFilter.value
        const res = await feeApi.list(params)
        list.value = res.data
    } finally {
        loading.value = false
    }
}

async function loadStudents() {
    const res = await userApi.list('STUDENT')
    studentList.value = res.data
}

function openCreateDialog() {
    createForm.student = null
    createForm.feeType = 'WATER'
    createForm.amount = 0
    createForm.period = ''
    createForm.dueDate = null
    createDialog.value = true
}

async function handleCreate() {
    const valid = await createFormRef.value.validate()
    if (!valid) return
    try {
        await feeApi.create({
            student: {id: createForm.student.id},
            feeType: createForm.feeType,
            amount: createForm.amount,
            period: createForm.period,
            dueDate: createForm.dueDate
        })
        ElMessage.success('账单已生成')
        createDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error('生成失败')
    }
}

function openPayDialog(row) {
    payItem.value = row
    payForm.method = 'WECHAT'
    payForm.transactionId = ''
    payDialog.value = true
}

async function handlePay() {
    try {
        await feeApi.pay(payItem.value.id, payForm.method, payForm.transactionId)
        ElMessage.success('缴费成功')
        payDialog.value = false
        await fetchData()
    } catch (e) {
        ElMessage.error(e.response?.data?.message || '缴费失败')
    }
}

async function handleDelete(id) {
    try {
        await ElMessageBox.confirm('确认删除该账单？', '提示', {type: 'warning'})
        await feeApi.delete(id)
        ElMessage.success('删除成功')
        await fetchData()
    } catch {
    }
}

function feeTypeLabel(type) {
    const map = {WATER: '水费', ELECTRICITY: '电费', ACCOMMODATION: '住宿费'}
    return map[type] || type
}

function feeTypeTag(type) {
    const map = {WATER: 'primary', ELECTRICITY: 'warning', ACCOMMODATION: 'success'}
    return map[type] || ''
}

function statusTag(status) {
    const map = {UNPAID: 'danger', PAID: 'success', OVERDUE: 'warning'}
    return map[status] || ''
}

function statusLabel(status) {
    const map = {UNPAID: '未缴费', PAID: '已缴费', OVERDUE: '逾期'}
    return map[status] || status
}
