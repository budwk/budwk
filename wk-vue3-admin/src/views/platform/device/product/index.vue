
<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['device.settings.handler.create']">新增
                </el-button>
            </el-col>
            <right-toolbar @quickSearch="quickSearch" />
        </el-row>
        <el-row :gutter="20">
            <el-col :lg="6" :xs="24" class="product-box" v-for="(product,idx) in tableData" :key="idx">
                <el-card class="box-card" shadow="hover" @mouseover="showButton(idx)" @mouseleave="showButton(-1)" :class="{active:showButtonIdx===idx}">
                        <template #header>
                        <div class="card-header">
                            <el-row>
                                <el-col :span="12">
                                    <el-tag color="red" effect="dark" class="type-tag">表具</el-tag>
                                </el-col>
                                <el-col :span="12" style="text-align: right" v-show="showButtonIdx==idx">
                                    <el-tooltip content="修改" placement="top">
                                        <el-button link type="primary" icon="EditPen"
                                            v-permission="['device.settings.handler.update']"></el-button>
                                        </el-tooltip>
                                    <el-tooltip content="删除" placement="top">
                                        <el-button link type="danger" icon="Delete"
                                        v-permission="['device.settings.handler.delete']"></el-button>
                                    </el-tooltip>
                                </el-col>
                            </el-row>
                            <el-row>
                                <span class="product-title">测试产品</span>
                            </el-row>
                        </div>
                        </template>
                        <el-row>
                            <el-col>
                                <el-form-item label="设备数量：" class="product-field-item">
                                200
                                </el-form-item>
                            </el-col>
                            <el-col>
                                <el-form-item label="接入平台：" class="product-field-item">
                                其他
                                </el-form-item>
                            </el-col>
                            <el-col>
                                <el-form-item label="网络协议：" class="product-field-item">
                                HTTP
                                </el-form-item>
                            </el-col>
                            <el-col>
                                <el-form-item label="协议解析：" class="product-field-item">
                                demo_http
                                </el-form-item>
                            </el-col>
                            <el-col>
                                <el-form-item label="设备厂家：" class="product-field-item">
                                金卡
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-card>    
            </el-col>
        </el-row>
        <pagination
               :total="queryParams.totalCount"
               v-model:page="queryParams.pageNo"
               v-model:limit="queryParams.pageSize"
               @pagination="list"
        />
    </div>
</template>
<script setup lang="ts" name="platform-device-product">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete } from '/@/api/platform/device/handler'
import { toRefs } from '@vueuse/core'
import { ElForm, ElUpload } from 'element-plus'
import { formatTime } from '/@/utils/common'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const showButtonIdx = ref(-1)

const data = reactive({
    formData: {
        id: '',
        name: '',
        code: '',
        fileName: '',
        filePath: '',
        classPath: '',
        description: '',
        enabled: true
    },
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
        name: [{ required: true, message: "协议名称不能为空", trigger: ["blur","change"] }],
        code: [
            { required: true, message: "协议标识不能为空", trigger: ["blur","change"] },
            { pattern: /^[a-z][a-z0-9_]+$/, message: "为小写字母或小写字母、下划线和数字的组合，并以小写字母开头", trigger: "blur" }
        ],
        classPath: [
            { required: true, message: "入口类不能为空", trigger: ["blur","change"] },
            { pattern: /^[A-Za-z][A-Za-z0-9.]+$/, message: "请输入正确的类路径", trigger: "blur" }
        ],
        filePath: [{ required: true, message: "jar文件不能为空", trigger: ["blur","change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `协议名称`, show: true, fixed: false },
    { prop: 'code', label: `协议标识`, show: true, fixed: false },
    { prop: 'description', label: `协议描述`, show: true, fixed: false },
    { prop: 'enabled', label: `启用状态`, show: true, fixed: false },
    { prop: 'updatedAt', label: `更新时间`, show: true, fixed: false }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        code: '',
        fileName: '',
        filePath: '',
        classPath: '',
        description: '',
        enabled: true
    }
    formEl?.resetFields()
}

const showButton = (val: any) => {
    console.log(val)
    showButtonIdx.value = val
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 刷新
const quickSearch = (data: any) => {
    refreshTable.value = false
    queryParams.value.pageNo = 1
    list()
    nextTick(() => {
        refreshTable.value = true
    })
}


// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    showCreate.value = true
}

// 修改按钮
const handleUpdate = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        showUpdate.value = true
    })
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除 '+ row.name + '？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}


// 提交新增
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            doCreate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreate.value = false
                queryParams.value.pageNo = 1
                list()
            })
        }
    })
}

// 提交修改
const update = () => {
    if (!updateRef.value) return
    updateRef.value.validate((valid) => {
        if (valid) {
            doUpdate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdate.value = false
                list()
            })
        }
    })
}

onMounted(()=>{
    list()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style lang='scss' scoped>
.product-box {
    margin-bottom: 20px;
}
.el-button+.el-button {
    margin-left: 2px;
}
.type-tag {
    border-style: none;
}
.active {
    cursor: pointer;
}
.product-field-item {
    margin-bottom: 2px;
}
.product-title {
    color: #2476e0;
    font-size: 18px;
    font-weight: 700;
    margin-top: 8px;
    margin-bottom: 2px;
}
</style>