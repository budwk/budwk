<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['wx.conf.pay.create']">新增
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看V3证书记录" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"
                            ></el-button>
                    </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['wx.conf.pay.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.conf.pay.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增支付信息" v-model="showCreate" width="45%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="name" label="支付名称">
                            <el-input v-model="formData.name" maxlength="100" placeholder="支付名称" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="mchid" label="商户号(mchid)">
                            <el-input v-model="formData.mchid" maxlength="100" placeholder="mchid" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3key" label="V3密钥">
                            <el-input v-model="formData.v3key" maxlength="100" placeholder="V3密钥" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>

                        <el-form-item prop="v3keyPath" label="V3密钥文件路径">
                            <el-input v-model="formData.v3keyPath" maxlength="100" placeholder="apiclient_key.pem"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3certPath" label="V3证书文件路径">
                            <el-input v-model="formData.v3certPath" maxlength="100" placeholder="apiclient_cert.pem"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3certP12Path" label="V3 p12文件路径">
                            <el-input v-model="formData.v3certP12Path" maxlength="100" placeholder="apiclient_cert.p12"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改支付信息" v-model="showUpdate" width="45%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="125px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="name" label="支付名称">
                            <el-input v-model="formData.name" maxlength="100" placeholder="支付名称" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="mchid" label="商户号(mchid)">
                            <el-input v-model="formData.mchid" maxlength="100" placeholder="mchid" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3key" label="V3密钥">
                            <el-input v-model="formData.v3key" maxlength="100" placeholder="V3密钥" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>

                        <el-form-item prop="v3keyPath" label="V3密钥文件路径">
                            <el-input v-model="formData.v3keyPath" maxlength="100" placeholder="apiclient_key.pem"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3certPath" label="V3证书文件路径">
                            <el-input v-model="formData.v3certPath" maxlength="100" placeholder="apiclient_cert.pem"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="v3certP12Path" label="V3 p12文件路径">
                            <el-input v-model="formData.v3certP12Path" maxlength="100" placeholder="apiclient_cert.p12"
                                auto-complete="off" tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="update">确 定</el-button>
                    <el-button @click="showUpdate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="V3证书记录" v-model="showView" width="60%">
            <el-table :data="tableData" row-key="id">
                <el-table-column
                    prop="mchid"
                    label="商户号(mchid)"
                />
                <el-table-column
                    prop="serial_no"
                    label="序列号"
                />
                <el-table-column
                    prop="effective_at"
                    label="起始有效时间"
                >
                    <template #default="scope">
                        <span>{{ formatTime(scope.row.effective_at) }}</span>
                    </template>
                </el-table-column>
                <el-table-column
                    prop="expire_at"
                    sortable
                    label="失效时间"
                >
                    <template #default="scope">
                        <span>{{ formatTime(scope.row.expire_at) }}</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-row>
                <pagination :total="queryCertParams.totalCount" v-model:page="queryCertParams.pageNo"
                v-model:limit="queryCertParams.pageSize" @pagination="listCert" />
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="showView = false">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-wechat-conf-pay">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, getCertList } from '/@/api/platform/wechat/pay'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'

const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const showView = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const certData = ref([])

const data = reactive({
    formData: {
        id: '',
        name: '',
        mchid: '',
        v3key: '',
        v3keyPath: '',
        v3certPath: '',
        v3certP12Path: ''
    },
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    queryCertParams: {
        mchid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        name: [{ required: true, message: "支付名称不能为空", trigger: ["blur", "change"] }],
        mchid: [{ required: true, message: "商户号不能为空", trigger: ["blur", "change"] }],
    },
})

const { queryParams, formData, formRules, queryCertParams } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `支付名称`, show: true },
    { prop: 'mchid', label: `商户号`, show: true },
    { prop: 'v3key', label: `V3密钥`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true },
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        mchid: '',
        v3key: '',
        v3keyPath: '',
        v3certPath: '',
        v3certP12Path: ''
    }
    formEl?.resetFields()
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

const listCert = () => {
    getCertList(queryCertParams.value).then((res) => {
        certData.value = res.data.list as never
        queryCertParams.value.totalCount = res.data.totalCount as never
    })
}

const handleView = (row: any) => {
    showView.value = true
    queryCertParams.value.mchid =  row.mchid
    listCert()
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
    modal.confirm('确定删除 ' + row.name + '？').then(() => {
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

onMounted(() => {
    list()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>