<template>
    <div class="app-container">
        <el-row>
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                    <el-form-item label="用户姓名" prop="username">
                        <el-input
v-model="queryParams.username" placeholder="请输入用户姓名" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="用户名" prop="loginname">
                        <el-input
v-model="queryParams.loginname" placeholder="请输入用户名" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="功能模块" prop="tag">
                        <el-input
v-model="queryParams.tag" placeholder="请输入功能模块" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="日志内容" prop="msg">
                        <el-input
v-model="queryParams.msg" placeholder="请输入日志内容" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="操作状态" prop="status">
                        <el-radio-group v-model="queryParams.status" placeholder="操作状态" @change="statusChange">
                            <el-radio :value="''">全部</el-radio>
                            <el-radio :value="'success'">成功</el-radio>
                            <el-radio :value="'exception'">失败</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="操作时间" prop="dateRange" style="font-weight:700;width: 325px;">
                        <el-date-picker
v-model="dateRange" type="daterange" range-separator="-"
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="x"></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
        </el-row>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-radio-group v-model="queryParams.type" placeholder="日志类型" @change="typeChange">
                            <el-radio-button :value="''">全部日志</el-radio-button>
                            <el-radio-button :value="item.value" v-for="(item) in types" :key="item.value">{{ item.text }}</el-radio-button>
                </el-radio-group>
            </el-col>
            <el-col :span="3">
                <el-select v-model="appId" class="m-2" placeholder="选择应用" @change="appChange" clearable>
                    <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-col>
                <right-toolbar
v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                        @quickSearch="handleSearch" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe  @sort-change="sortChange" :default-sort="defaultSort">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width" :sortable="item.sortable">
                    <template v-if="item.prop == 'exception'" #default="scope">
                        <el-tag v-if="scope.row.exception" :disable-transitions="true" type="danger">失败</el-tag>
                        <el-tag v-else :disable-transitions="true" type="success">成功</el-tag>
                    </template>
                    <template v-if="item.prop == 'loginname'" #default="scope">
                        <span v-if="scope.row.loginname">{{ scope.row.username }}({{ scope.row.loginname }})</span>
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'executeTime'" #default="scope">
                        <span class="time">{{ scope.row.executeTime }}ms</span>
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
                                <span v-if="formData.loginname">{{ formData.username }}({{ formData.loginname }})</span>
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
                        <el-col :span="24" v-if="formData.exception">
                            <el-form-item label="异常信息" prop="exception" class="content">
                                {{ formData.exception }}
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </template>
        </el-drawer>
    </div>
</template>
<script setup lang="ts" name="platform-sys-log">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getData } from '/@/api/platform/sys/log'
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
const showDetail = ref(false)
const appId = ref('')
const dateRange =ref([])

const defaultSort = ref({ prop: "createdAt", order: "descending" });

const data = reactive({
    formData: {},
    queryParams: {
        appId: '',
        type: '',
        status: '',
        loginname: '',
        username: '',
        tag: '',
        msg: '',
        beginTime: '',
        endTime: '',
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
    queryParams.value.appId = appId.value
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

const getInitData = () => {
    getData().then((res)=>{
        apps.value = res.data.apps
        types.value = res.data.types
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
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.time {
    font-size: 11px;
    background-color: rgb(135, 208, 104);
    color: #fff;
    display: inline-block;
    width: 38px;
    border-radius: 2px;
    padding-left: 1px;
    height: 22px;
}
.content {
    white-space:normal; 
    word-break:break-all;
}
</style>