<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.conf.mina.create']">新增
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
                            v-permission="['wx.conf.mina.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.conf.mina.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增小程序" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="appname" label="小程序名称">
                            <el-input v-model="formData.appname" maxlength="100" placeholder="小程序名称" auto-complete="off"
                                tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appid" label="appid">
                            <el-input v-model="formData.appid" maxlength="100" placeholder="appid" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appsecret" label="appsecret">
                            <el-input v-model="formData.appsecret" maxlength="100" placeholder="appsecret"
                                auto-complete="off" tabindex="4" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="wxid" label="关联公众号">
                            <el-select v-model="formData.wxid" clearable placeholder="关联公众号">
                                <el-option
                                v-for="item in accounts"
                                :key="item.id"
                                :label="item.appname"
                                :value="item.id"
                                />
                            </el-select>
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

        <el-dialog title="修改小程序" v-model="showUpdate" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="125px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="appname" label="小程序名称">
                            <el-input v-model="formData.appname" maxlength="100" placeholder="小程序名称" auto-complete="off"
                                tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appid" label="appid">
                            <el-input v-model="formData.appid" maxlength="100" placeholder="appid" auto-complete="off"
                                tabindex="3" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="appsecret" label="appsecret">
                            <el-input v-model="formData.appsecret" maxlength="100" placeholder="appsecret"
                                auto-complete="off" tabindex="4" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="wxid" label="关联公众号">
                            <el-select v-model="formData.wxid" clearable placeholder="关联公众号">
                                <el-option
                                v-for="item in accounts"
                                :key="item.id"
                                :label="item.appname"
                                :value="item.id"
                                />
                            </el-select>
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
<script setup lang="ts" name="platform-wechat-conf-mina">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, getPayList } from '/@/api/platform/wechat/mina'
import { getAccountList } from '/@/api/platform/wechat/account'
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
const accounts = ref([])

const data = reactive({
    formData: {
        id: '',
        appname: '',
        appid: '',
        appsecret: '',
        wxid: '',
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
        appname: [{ required: true, message: "小程序名称不能为空", trigger: ["blur", "change"] }],
        appid: [{ required: true, message: "appid不能为空", trigger: ["blur", "change"] }],
        appsecret: [{ required: true, message: "appsecret不能为空", trigger: ["blur", "change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'appname', label: `小程序名称`, show: true },
    { prop: 'appid', label: `appid`, show: true },
    { prop: 'appsecret', label: `appsecret`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true },
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        appname: '',
        appid: '',
        appsecret: '',
        wxid: '',
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

const listAccount = () => {
    getAccountList().then((res) => {
        accounts.value = res.data as never
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
    listAccount()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>