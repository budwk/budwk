<template>
    <div class="app-container">
        <el-row>
            <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
                    <el-form-item label="服务名称" prop="serviceNameParam">
                        <el-input
v-model="queryParams.serviceNameParam" placeholder="请输入服务名称" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="分组名称" prop="groupNameParam">
                        <el-input
v-model="queryParams.groupNameParam" placeholder="请输入分组名称" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                    </el-form-item>    
             </el-form>
        </el-row>
        <el-row :gutter="10" class="mb8">
            <el-col :span="24" v-if="!hasNacos">
                <el-tag type="info">非 Nacos + Dubbo 分布式部署方式，无法获取服务列表</el-tag><br><br>
            </el-col>
            <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe  @sort-change="sortChange" :default-sort="defaultSort">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width" :sortable="item.sortable">
                    <template v-if="item.prop == 'exception'" #default="scope">
                        <el-tag v-if="scope.row.exception" :disable-transitions="true" type="danger">失败</el-tag>
                        <el-tag v-else :disable-transitions="true" type="success">成功</el-tag>
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
        </el-row>
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { getNacosDetail, getNacosService, getNacosServices, getRedisInfo, getServerInfo } from '/@/api/platform/sys/monitor'
import { ElForm } from 'element-plus'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const tableLoading = ref(false)
const tableData = ref([])
const hasNacos = ref(false)

const data = reactive({
    formData: {},
    queryParams: {
        serviceNameParam: '',
        groupNameParam: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0
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

// 查询表格
const list = () => {
    tableLoading.value = true
    getNacosServices(queryParams.value).then((res) => {
        hasNacos.value = true
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    }).catch(()=>{
        tableLoading.value = false
        hasNacos.value = false
    })
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

const handleView = () => {
    
}


onMounted(()=>{
    list()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-monitor'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>