<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-radio-group v-model="queryParams.type" placeholder="日志类型" @change="typeChange">
                            <el-radio-button :label="''">全部日志</el-radio-button>
                            <el-radio-button :label="item.value" v-for="(item) in types" :key="item.value">{{ item.text }}</el-radio-button>
                </el-radio-group>
            </el-col>
                <right-toolbar
v-model:showSearch="showSearch" :extendSearch="false" :columns="columns"
                        @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过标题搜索" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe  @sort-change="sortChange" :default-sort="defaultSort">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width" :sortable="item.sortable">
                    <template v-if="item.prop == 'loginname'" #default="scope">
                        {{ scope.row.username }}({{ scope.row.loginname }})
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" width="70" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看详情" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-drawer v-model="showDetail" direction="rtl" title="日志详情" size="50%">

            <template #default>
                <el-form :model="formData" label-width="80px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="操作人" prop="loginname">
                                {{ formData.username }}({{ formData.loginname }})
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="操作时间" prop="createdAt">
                                {{ formatTime(formData.createdAt) }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="操作IP" prop="ip">
                                {{ formData.ip }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="请求路径" prop="url">
                                {{ formData.url }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="执行方法" prop="method">
                                {{ formData.method }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="操作系统" prop="os">
                                {{ formData.os }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="浏览器" prop="browser" class="content">
                                {{ formData.browser }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="请求参数" prop="params" class="content">
                                {{ formData.params }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="响应结果" prop="result" class="content">
                                {{ formData.result }}
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </template>
        </el-drawer>
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getType, doCreate, doDelete, getData } from '/@/api/platform/sys/msg'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { addDateRange } from '/@/utils/common'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()

const showSearch = ref(true)    
const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const apps = ref([])
const types = ref([])
const scopes = ref([])
const showDetail = ref(false)
const appId = ref('')
const dateRange =ref([])

const defaultSort = ref({ prop: "createdAt", order: "descending" });

const data = reactive({
    formData: {},
    queryParams: {
        title: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
})

const { formData, queryParams } = toRefs(data)

const columns = ref([
    { prop: 'url', label: `请求路径`, show: true, fixed: 'left', width: 200 },
    { prop: 'tag', label: `功能模块`, show: true, fixed: 'left', width: 100 },
    { prop: 'msg', label: `日志内容`, show: true, fixed: false },
    { prop: 'exception', label: `操作状态`, show: true, fixed: false, align: 'center', width: 80 },
    { prop: 'loginname', label: `操作人`, show: true, fixed: false },
    { prop: 'createdAt', label: `操作时间`, show: true, fixed: false, width: 160, sortable: true },
    { prop: 'ip', label: `IP`, show: true, fixed: false, width: 100 },
    { prop: 'executeTime', label: `执行耗时`, show: true, fixed: false, align: 'center', width: 80 }
])

const handleView = (row: any) => {
    formData.value = row
    showDetail.value = true
}

// 查询表格
const list = () => {
    tableLoading.value = true
    addDateRange(queryParams.value, dateRange.value)
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

const statusChange = () => {
    handleSearch()
}

const typeChange = () => {
    handleSearch()
}

const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    handleSearch()
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

// 刷新&快速搜索
const quickSearch = (data: any) => {
    queryParams.value.title = data.keyword
    queryParams.value.pageNo = 1
    list()
}

const getInitData = () => {
    getData().then((res)=>{
        apps.value = res.data.apps
        types.value = res.data.types
        scopes.value = res.data.scopes
    })
}

const appChange = (val: string) => {
    handleSearch()
}

onMounted(() => {
    list()
    getInitData()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-msg'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.content {
    white-space:normal; 
    word-break:break-all;
}
</style>