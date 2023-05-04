
<template>
    <div class="app-container">
        <el-row>
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                <el-form-item label="设备类型" prop="typeId">
                    <el-select v-model="queryParams.typeId" clearable placeholder="请选择设备类型">
                        <el-option v-for="item in typeList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="设备厂家" prop="supplierId">
                    <el-select v-model="queryParams.supplierId" clearable placeholder="请选择设备厂家">
                        <el-option v-for="item in supplierList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="产品名称" prop="name">
                    <el-input v-model="queryParams.name" placeholder="请输入产品名称" clearable style="width: 180px"
                        @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                    <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>
        </el-row>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['devcie.manage.product.create']">新增
                </el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="true" @quickSearch="handleSearch" />
        </el-row>
        <el-row :gutter="20">
            <el-col :lg="6" :xs="24" class="product-box" v-for="(product, idx) in tableData" :key="idx">
                <el-card class="box-card" shadow="hover" @mouseover="showButton(idx)" @mouseleave="showButton(-1)"
                    :class="{ active: showButtonIdx === idx }">
                    <template #header>
                        <div class="card-header">
                            <el-row>
                                <el-col :span="12">
                                    <el-tag :color="product.deviceType?.color" effect="dark" class="type-tag">{{product.deviceType?.name}}</el-tag>
                                </el-col>
                                <el-col :span="12" style="text-align: right" v-show="showButtonIdx == idx">
                                    <el-tooltip content="修改" placement="top">
                                        <el-button link type="primary" icon="EditPen" @click="handleUpdate(product)"
                                            v-permission="['devcie.manage.product.update']"></el-button>
                                    </el-tooltip>
                                    <el-tooltip content="删除" placement="top">
                                        <el-button link type="danger" icon="Delete" @click="handleDelete(product)"
                                            v-permission="['devcie.manage.product.delete']"  style="margin-left: 5px;"></el-button>
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
        <el-row v-show="tableData.length == 0" justify="center">
            <span class="no-data">暂无数据</span>
        </el-row>
        <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize"
            @pagination="list" />

        <el-dialog title="新增产品" v-model="showCreate" width="45%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="产品名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入产品名称" />
                </el-form-item>
                <el-form-item label="设备类型" prop="typeId">
                    <el-cascader style="width: 100%;" clearable :options="typeList"
                        :props="{ expandTrigger: 'hover', value: 'id', label: 'name', children: 'children' }"
                        @change="typeChange" />
                </el-form-item>
                <el-form-item label="厂家型号" prop="supplierCode">
                    <el-cascader style="width: 100%;" clearable :options="supplierList"
                        :props="{ expandTrigger: 'hover', value: 'id', label: 'name', children: 'codeList' }"
                        @change="supplierChange" />
                </el-form-item>
                <el-form-item label="接入平台" prop="iotPlatform">
                    <el-select v-model="formData.iotPlatform" placeholder="请选择接入平台" clearable style="width: 100%;">
                        <el-option v-for="item in iotPlatform" :key="item.value" :label="item.text" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="接入协议" prop="protocolType">
                    <el-select v-model="formData.protocolType" placeholder="请选择接入协议" clearable style="width: 100%;">
                        <el-option v-for="item in protocolType" :key="item.value" :label="item.text" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="协议解析器" prop="handler">
                    <el-select v-model="formData.handler" placeholder="请选择协议解析器" clearable style="width: 100%;">
                        <el-option v-for="item in handlerList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'MQTT'" label="协议认证" prop="description"
                    class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_MQTT" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'HTTP' && formData.iotPlatform == 'AEP'" label="协议认证"
                    prop="description" class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_AEP_HTTP" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'MQ' && formData.iotPlatform == 'AEP'" label="协议认证"
                    prop="description" class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_AEP_MQ" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item label="是否有阀门" prop="enabled">
                    <el-radio-group v-model="formData.valveControl">
                        <el-radio :label="false">无</el-radio>
                        <el-radio :label="true">有</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="产品描述" prop="description">
                    <el-input v-model="formData.description" placeholder="" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改产品" v-model="showUpdate" width="45%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="产品名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入产品名称" />
                </el-form-item>
                <el-form-item label="设备类型" prop="typeId">
                    <el-cascader style="width: 100%;" clearable :options="typeList"
                        :props="{ expandTrigger: 'hover', value: 'id', label: 'name', children: 'children' }"
                        @change="typeChange" v-model="formDataType" />
                </el-form-item>
                <el-form-item label="厂家型号" prop="supplierCode">
                    <el-cascader style="width: 100%;" clearable :options="supplierList"
                        :props="{ expandTrigger: 'hover', value: 'id', label: 'name', children: 'codeList' }"
                        @change="supplierChange" v-model="formDataSupplier" />
                </el-form-item>
                <el-form-item label="接入平台" prop="iotPlatform">
                    <el-select v-model="formData.iotPlatform" placeholder="请选择接入平台" clearable style="width: 100%;">
                        <el-option v-for="item in iotPlatform" :key="item.value" :label="item.text" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="接入协议" prop="protocolType">
                    <el-select v-model="formData.protocolType" placeholder="请选择接入协议" clearable style="width: 100%;">
                        <el-option v-for="item in protocolType" :key="item.value" :label="item.text" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item label="协议解析器" prop="handler">
                    <el-select v-model="formData.handler" placeholder="请选择协议解析器" clearable style="width: 100%;">
                        <el-option v-for="item in handlerList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'MQTT'" label="协议认证" prop="description"
                    class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_MQTT" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'HTTP' && formData.iotPlatform == 'AEP'" label="协议认证"
                    prop="description" class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_AEP_HTTP" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item v-if="formData.protocolType == 'MQ' && formData.iotPlatform == 'AEP'" label="协议认证"
                    prop="description" class="label-font-weight">
                    <template v-for="(obj, idx) in ref_auth_AEP_MQ" :key="idx">
                        {{ obj.name }}: <el-input v-model="obj.value" :placeholder="obj.note" />
                    </template>
                </el-form-item>
                <el-form-item label="是否有阀门" prop="enabled">
                    <el-radio-group v-model="formData.valveControl">
                        <el-radio :label="false">无</el-radio>
                        <el-radio :label="true">有</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="产品描述" prop="description">
                    <el-input v-model="formData.description" placeholder="" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="update">确 定</el-button>
                    <el-button @click="showUpdate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-device-product">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, getInit, auth_MQTT, auth_AEP_HTTP, auth_AEP_MQ } from '/@/api/platform/device/product'
import { toRefs } from '@vueuse/core'
import { handleTree } from '/@/utils/common'
import { ElForm, ElUpload } from 'element-plus'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()

const showSearch = ref(true)
const showCreate = ref(false)
const showUpdate = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const showButtonIdx = ref(-1)
const iotPlatform = ref([])
const typeList = ref([])
const supplierList = ref([])
const protocolType = ref([])
const handlerList = ref([])
const formDataType = ref([])
const formDataSupplier = ref([])
const ref_auth_MQTT = ref(auth_MQTT)
const ref_auth_AEP_HTTP = ref(auth_AEP_HTTP)
const ref_auth_AEP_MQ = ref(auth_AEP_MQ)

const data = reactive({
    formData: {
        id: '',
        name: '',
        typeId: '',
        subTypeId: '',
        supplierId: '',
        supplierCode: '',
        iotPlatform: '',
        protocolType: '',
        handler: '',
        valveControl: false,
        authJson: '',
        description: ''
    },
    queryParams: {
        typeId: '',
        name: '',
        supplierId: '',
        pageNo: 1,
        pageSize: 8,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: '',
    },
    formRules: {
        name: [{ required: true, message: "协议名称不能为空", trigger: ["blur", "change"] }],
        typeId: [{ required: true, message: "设备类型不能为空", trigger: ["blur", "change"] }],
        supplierId: [{ required: true, message: "所属厂家不能为空", trigger: ["blur", "change"] }],
        supplierCode: [{ required: true, message: "厂家型号不能为空", trigger: ["blur", "change"] }],
        iotPlatform: [{ required: true, message: "接入平台不能为空", trigger: ["blur", "change"] }],
        protocolType: [{ required: true, message: "接入协议不能为空", trigger: ["blur", "change"] }],
        handler: [{ required: true, message: "协议解析器不能为空", trigger: ["blur", "change"] }],
        code: [
            { required: true, message: "协议标识不能为空", trigger: ["blur", "change"] },
            { pattern: /^[a-z][a-z0-9_]+$/, message: "为小写字母或小写字母、下划线和数字的组合，并以小写字母开头", trigger: "blur" }
        ],
        classPath: [
            { required: true, message: "入口类不能为空", trigger: ["blur", "change"] },
            { pattern: /^[A-Za-z][A-Za-z0-9.]+$/, message: "请输入正确的类路径", trigger: "blur" }
        ],
        filePath: [{ required: true, message: "jar文件不能为空", trigger: ["blur", "change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        typeId: '',
        subTypeId: '',
        supplierId: '',
        supplierCode: '',
        iotPlatform: 'DIRECT',
        protocolType: '',
        handler: '',
        valveControl: false,
        authJson: '',
        description: ''
    }
    formEl?.resetFields()
}

const showButton = (val: any) => {
    showButtonIdx.value = val
}

// 刷新
const handleSearch = () => {
    queryParams.value.pageNo = 1
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
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

const init = () => {
    getInit().then((res) => {
        iotPlatform.value = res.data.iotPlatform
        typeList.value = handleTree(res.data.typeList) as never
        supplierList.value = res.data.supplierList
        handlerList.value = res.data.handlerList
        protocolType.value = res.data.protocolType
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

// 设备类型改变
const typeChange = (val: any) => {
    if (val.length > 1) {
        formData.value.typeId = val[0]
        formData.value.subTypeId = val[1]
    } else if (val.length == 1) {
        formData.value.typeId = val[0]
        formData.value.subTypeId = ''
    } else {
        formData.value.typeId = ''
        formData.value.subTypeId = ''
    }
}

// 厂家型号改变
const supplierChange = (val: any) => {
    if (val.length > 1) {
        formData.value.supplierId = val[0]
        formData.value.supplierCode = val[1]
    } else if (val.length == 1) {
        formData.value.supplierId = val[0]
        formData.value.supplierCode = ''
    } else {
        formData.value.supplierId = ''
        formData.value.supplierCode = ''
    }
}

// 获取auth数据
const bindAuth = () => {
    if (formData.value.protocolType == 'MQ' && formData.value.iotPlatform == 'AEP') {
        formData.value.authJson = JSON.stringify(ref_auth_AEP_MQ.value)
    }
    if (formData.value.protocolType == 'HTTP' && formData.value.iotPlatform == 'AEP') {
        formData.value.authJson = JSON.stringify(ref_auth_AEP_HTTP.value)
    }
    if (formData.value.protocolType == 'MQTT') {
        formData.value.authJson = JSON.stringify(ref_auth_MQTT.value)
    }
}

// 获取auth数据
const parseAuth = () => {
    if (formData.value.protocolType == 'MQ' && formData.value.iotPlatform == 'AEP') {
        ref_auth_AEP_MQ.value = JSON.parse(formData.value.authJson)
    }
    if (formData.value.protocolType == 'HTTP' && formData.value.iotPlatform == 'AEP') {
        ref_auth_AEP_HTTP.value = JSON.parse(formData.value.authJson)
    }
    if (formData.value.protocolType == 'MQTT') {
        ref_auth_MQTT.value = JSON.parse(formData.value.authJson)
    }
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
        formData.value.iotPlatform = res.data.iotPlatform.value
        formData.value.protocolType = res.data.protocolType.value
        formDataSupplier.value = [res.data.supplierId, res.data.supplierCode] as never
        formDataType.value = [res.data.typeId, res.data.subTypeId] as never
        parseAuth()
        showUpdate.value = true
    })
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除产品 ' + row.name + '？').then(() => {
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
            bindAuth()
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
            bindAuth()
            doUpdate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdate.value = false
                list()
            })
        }
    })
}

onMounted(() => {
    list()
    init()
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

.no-data {
    text-align: center;
    font-size: 14px;
    color: #999;
    height: 50px;
}
</style>