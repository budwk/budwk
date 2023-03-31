<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['wx.tpl.list.get']">同步模版内容
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <el-table-column type="selection" width="50" fixed="left" />
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'primary_industry'" #default="scope">
                        {{ scope.row.primary_industry }} - {{ scope.row.deputy_industry }}
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="同步模版内容" v-model="showCreate" width="30%">
            <el-form ref="createRef" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;" justify="center">
                    <el-progress type="circle" :percentage="progressNum" :status="progressStatus" />
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>
<script setup lang="ts" name="platform-wechat-tpl-list">
import { onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, doDownload } from '/@/api/platform/wechat/tpl_list'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'

const createRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const accounts = ref([])
const wxid = ref('')
const progressNum = ref(0)
const progressStatus = ref('')

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
    { prop: 'template_id', label: `模版编号(template_id)`, show: true },
    { prop: 'title', label: `模版标题`, show: true },
    { prop: 'primary_industry', label: `所属行业`, show: true },
    { prop: 'example', label: `模版示例`, show: true, overflow: true },
    { prop: 'content', label: `模版内容`, show: true },
    { prop: 'createdAt', label: `同步时间`, show: true, sortable: true, width: 180 }
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


// 新增按钮
const handleCreate = (row: any) => {
    progressNum.value = 0
    progressStatus.value = ''
    showCreate.value = true
}

// 提交新增
const create = () => {
    const t = setInterval(() => {
        progressNum.value += 10
        if (progressNum.value >= 100) {
            window.clearInterval(t)
        }
    }, 200)
    doDownload(wxid.value).then((res: any) => {
        modal.msgSuccess(res.msg)
        progressStatus.value = 'success'
        queryParams.value.pageNo = 1
        showCreate.value = false
        window.clearInterval(t)
        list()
    }).catch(() => {
        window.clearInterval(t)
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