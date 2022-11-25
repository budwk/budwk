<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <right-toolbar @quickSearch="quickSearch" />
        </el-row>
        <el-table v-if="showTable" v-loading="tableLoading" :data="tableData" row-key="id" stripe>
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width">
                    <template v-if="item.prop == 'exception'" #default="scope">
                        <el-tag v-if="scope.row.exception" :disable-transitions="true" type="danger">失败</el-tag>
                        <el-tag v-else :disable-transitions="true" type="success">成功</el-tag>
                    </template>
                    <template v-if="item.prop == 'loginname'" #default="scope">
                        {{ scope.row.username }}({{ scope.row.loginname }})
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
import { getList, getData } from '/@/api/platform/sys/log'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const showTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const showDetail = ref(false)

const data = reactive({
    formData: {},
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'desc'
    },
})

const { formData, queryParams } = toRefs(data)

const columns = ref([
    { prop: 'url', label: `请求路径`, show: true, fixed: 'left', width: 200 },
    { prop: 'tag', label: `功能模块`, show: true, fixed: 'left', width: 100 },
    { prop: 'msg', label: `日志内容`, show: true, fixed: false },
    { prop: 'exception', label: `操作状态`, show: true, fixed: false, align: 'center', width: 80 },
    { prop: 'loginname', label: `操作人`, show: true, fixed: false },
    { prop: 'createdAt', label: `操作时间`, show: true, fixed: false, width: 160 },
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
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 刷新
const quickSearch = (data: any) => {
    showTable.value = false
    queryParams.value.pageNo = 1
    list()
    nextTick(() => {
        showTable.value = true
    })
}


onMounted(() => {
    list()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-log'
}
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