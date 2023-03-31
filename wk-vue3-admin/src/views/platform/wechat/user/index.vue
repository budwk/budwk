<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Download" @click="handleDownload"
                    v-permission="['wx.user.list.sync']">同步会员资料
                </el-button>
            </el-col>
            <right-toolbar @quickSearch="quickSearch" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe  @sort-change="sortChange" :default-sort="{ prop: 'subscribeAt', order: 'descending' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'subscribeAt'" #default="scope">
                        <span>{{ formatTime(scope.row.subscribeAt*1000) }}</span>
                    </template>
                    <template v-if="item.prop == 'subscribe'" #default="scope">
                        <span v-if="!scope.row.subscribe">未关注</span>
                        <span v-if="scope.row.subscribe" style="color:green;">已关注</span>
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="同步会员资料" v-model="showDownload" width="30%">
            <el-row>
                <el-alert
                title="微信公众号后台配置好接入URL后，同步操作只用手动执行一次（用户关注公众号会自动创建）"
                type="success"
                />
            </el-row>
            <el-row style="padding-top:5px;" justify="center">
                <el-progress type="circle" :percentage="progressNum" :status="progressStatus" />
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="download" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showDownload = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>
<script setup lang="ts" name="platform-wechat-user">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, doDownload } from '/@/api/platform/wechat/user'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'

const showDownload = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const progressNum =  ref(0)
const progressStatus = ref('')
const accounts = ref([])
const wxid = ref('')
const btnLoading = ref(false)

const data = reactive({
    formData: {
    },
    queryParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'subscribeAt',
        pageOrderBy: 'descending'
    }
})

const { queryParams, formData } = toRefs(data)

const columns = ref([
    { prop: 'openid', label: `openid`, show: true },
    { prop: 'unionid', label: `unionid`, show: true },
    { prop: 'nickname', label: `昵称`, show: true },
    { prop: 'subscribe', label: `是否关注`, show: true, width: 100 },
    { prop: 'subscribeAt', label: `关注时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
    }
    formEl?.resetFields()
}


const accountChange = (val: string) => {
    wxid.value = val
    queryParams.value.wxid = val
    list()
}

const listAccount = () => {
    getAccountList().then((res) => {
        accounts.value = res.data as never
        if (accounts.value.length > 0) {
            wxid.value = accounts.value[0].id
            queryParams.value.wxid = accounts.value[0].id
            list()
        }
    })
}

const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    list()
}

// 快速搜索&刷新
const quickSearch = () => {
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

// 新增按钮
const handleDownload = () => {
    showDownload.value = true
}

const download = () => {
    var t = setInterval(() => {
        progressNum.value += 10
        if (progressNum.value >= 100) {
            window.clearInterval(t)
        }
    }, 400)
    btnLoading.value = true
    doDownload(wxid.value).then((res)=>{
        btnLoading.value = false
        progressNum.value = 100
        progressStatus.value = 'success'
        list()
        showDownload.value = false
    }).catch(() => {
        progressStatus.value = 'exception'
    })
}

onMounted(() => {
    listAccount()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>