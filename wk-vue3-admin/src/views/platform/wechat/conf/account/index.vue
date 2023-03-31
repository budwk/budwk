<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.conf.account.create']">新增
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
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['wx.conf.account.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.conf.account.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增公众号" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="id" label="唯一标识">
                            <el-input v-model="formData.id" maxlength="100" placeholder="唯一标识" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="配置URL" class="label-font-weight">
                            {{ platformInfo.AppDomain }}/api/wechat/open/weixin/msg/{{ formData.id }}
                            <el-alert title="微信后台配置的URL" type="success" style="height:32px;" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appname" label="公众号名称">
                            <el-input v-model="formData.appname" maxlength="100" placeholder="公众号名称" auto-complete="off"
                                tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appid" label="AppId">
                            <el-input v-model="formData.appid" maxlength="100" placeholder="AppId" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appsecret" label="AppSecret">
                            <el-input v-model="formData.appsecret" maxlength="100" placeholder="AppSecret"
                                auto-complete="off" tabindex="4" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="token" label="Token">
                            <el-input v-model="formData.token" maxlength="100" placeholder="Token" auto-complete="off"
                                tabindex="5" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="encodingAESKey" label="EncodingAESKey">
                            <el-input v-model="formData.encodingAESKey" maxlength="100" placeholder="EncodingAESKey"
                                auto-complete="off" tabindex="6" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="mchid" label="支付商户">
                            <el-select v-model="formData.mchid" clearable placeholder="微信支付商户绑定">
                                <el-option v-for="item in payData" :key="item.mchid" :label="item.name"
                                    :value="item.mchid" />
                            </el-select>
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

        <el-dialog title="修改公众号" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="125px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="id" label="唯一标识">
                            <el-input v-model="formData.id" maxlength="100" placeholder="唯一标识" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="配置URL">
                            {{ platformInfo.AppDomain }}/api/wechat/open/weixin/msg/{{ formData.id }}
                            <el-alert title="微信后台配置的URL" type="success" style="height:32px;" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appname" label="公众号名称">
                            <el-input v-model="formData.appname" maxlength="100" placeholder="公众号名称" auto-complete="off"
                                tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appid" label="AppId">
                            <el-input v-model="formData.appid" maxlength="100" placeholder="AppId" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appsecret" label="AppSecret">
                            <el-input v-model="formData.appsecret" maxlength="100" placeholder="AppSecret"
                                auto-complete="off" tabindex="4" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="token" label="Token">
                            <el-input v-model="formData.token" maxlength="100" placeholder="Token" auto-complete="off"
                                tabindex="5" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="encodingAESKey" label="EncodingAESKey">
                            <el-input v-model="formData.encodingAESKey" maxlength="100" placeholder="EncodingAESKey"
                                auto-complete="off" tabindex="6" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="mchid" label="支付商户">
                            <el-select v-model="formData.mchid" clearable placeholder="微信支付商户绑定">
                                <el-option v-for="item in payData" :key="item.mchid" :label="item.name"
                                    :value="item.mchid" />
                            </el-select>
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

    </div>
</template>
<script setup lang="ts" name="platform-wechat-conf-account">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, getPayList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'

const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()


const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const payData = ref([])

const data = reactive({
    formData: {
        id: '',
        appname: '',
        appid: '',
        appsecret: '',
        token: '',
        encodingAESKey: '',
        mchid: ''
    },
    queryParams: {
        classId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        id: [{ required: true, message: "公众号标识不能为空", trigger: ["blur", "change"] }],
        appname: [{ required: true, message: "公众号名称不能为空", trigger: ["blur", "change"] }],
        appid: [{ required: true, message: "AppID不能为空", trigger: ["blur", "change"] }],
        appsecret: [{ required: true, message: "AppSecret不能为空", trigger: ["blur", "change"] }],
        token: [{ required: true, message: "Token不能为空", trigger: ["blur", "change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'id', label: `标识`, show: true },
    { prop: 'appname', label: `公众号名称`, show: true },
    { prop: 'appid', label: `AppID`, show: true },
    { prop: 'mchid', label: `商户号`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true },
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        appname: '',
        appid: '',
        appsecret: '',
        token: '',
        encodingAESKey: '',
        mchid: ''
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

const listPay = () => {
    getPayList().then((res) => {
        payData.value = res.data as never
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
    modal.confirm('确定删除 ' + row.appname + '？').then(() => {
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
    listPay()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>