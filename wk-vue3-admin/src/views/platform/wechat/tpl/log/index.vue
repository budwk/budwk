<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'openid'" #default="scope">
                        <span v-if="scope.row.nickname">{{ scope.row.nickname }}</span>
                        <span v-else>{{ scope.row.openid }}</span>
                    </template>
                    <template v-if="item.prop == 'status'" #default="scope">
                        <span v-if="1===scope.row.status" style="color:green;">发送成功</span>
                        <span v-else>发送失败</span>
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>


    </div>
</template>
<script setup lang="ts" name="platform-wechat-tpl-log">
import { onMounted, reactive, ref } from 'vue'
import { getList} from '/@/api/platform/wechat/tpl_log'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'

const tableLoading = ref(false)
const tableData = ref([])
const accounts = ref([])
const wxid = ref('')
const data = reactive({
    formData: {
    },
    queryParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    }
})

const { queryParams } = toRefs(data)

const columns = ref([
    { prop: 'openid', label: `微信昵称 | openid`, show: true },
    { prop: 'content', label: `发送内容`, show: true },
    { prop: 'status', label: `发送状态`, show: true },
    { prop: 'createdAt', label: `发送时间`, show: true, sortable: true, width: 180 }
])


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

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
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