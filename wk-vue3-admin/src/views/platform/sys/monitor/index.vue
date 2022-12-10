<template>
    <div class="app-container">
        <el-row>
            <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
                <el-form-item label="服务名称" prop="serviceNameParam">
                    <el-input v-model="queryParams.serviceNameParam" placeholder="请输入服务名称" clearable
                        style="width: 180px" @keyup.enter="handleSearch" />
                </el-form-item>
                <el-form-item label="分组名称" prop="groupNameParam">
                    <el-input v-model="queryParams.groupNameParam" placeholder="请输入分组名称" clearable style="width: 180px"
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
            <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe>
                <template v-for="(item, idx) in columns" :key="idx">
                    <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                        :show-overflow-tooltip="true" :align="item.align" :width="item.width">
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
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-drawer v-model="showDetail" direction="rtl" title="服务详情" size="70%">

            <template #default>
                <el-row style="margin-bottom:10px;">
                    <el-col :span="1.5">
                        <span>集群: </span>
                        <el-radio-group v-model="queryDetailParams.clusterName" placeholder="集群"
                            @change="clusterChange">
                            <el-radio-button :label="item.name" v-for="(item) in clusters" :key="item.name">{{ item.name
                            }}
                            </el-radio-button>
                        </el-radio-group>
                    </el-col>
                </el-row>
                <el-table :data="tableDetailData" row-key="id" stripe>
                    <template v-for="(item, idx) in columnsDetail" :key="idx">
                        <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                            :show-overflow-tooltip="true" :align="item.align" :width="item.width">
                            <template v-if="item.prop == 'metadata'" #default="scope">
                                <div v-html="getInfo(scope.row.metadata)"></div>
                            </template>
                        </el-table-column>
                    </template>
                </el-table>
                <pagination :total="queryDetailParams.totalCount" v-model:page="queryDetailParams.pageNo"
                    v-model:limit="queryDetailParams.pageSize" @pagination="listDetail" />
            </template>

        </el-drawer>
    </div>
</template>
<script setup lang="ts" name="platform-sys-monitor">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { getNacosDetail, getNacosService, getNacosServices, getRedisInfo, getServerInfo } from '/@/api/platform/sys/monitor'
import { ElForm } from 'element-plus'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const tableLoading = ref(false)
const tableData = ref([])
const hasNacos = ref(false)
const showDetail = ref(false)
const tableDetailData = ref([])
const clusters = ref([])

const data = reactive({
    formData: {},
    queryParams: {
        serviceNameParam: '',
        groupNameParam: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0
    },
    queryDetailParams: {
        clusterName: '',
        serviceName: '',
        groupName: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0
    },
})

const { queryParams, queryDetailParams } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `服务名`, show: true, fixed: 'left' },
    { prop: 'groupName', label: `分组名称`, show: true, fixed: 'left', width: 160 },
    { prop: 'clusterCount', label: `集群数目`, show: true, fixed: false, align: 'center', width: 100 },
    { prop: 'ipCount', label: `实例数`, show: true, fixed: false, align: 'center', width: 100 },
    { prop: 'healthyInstanceCount', label: `健康实例数`, show: true, fixed: false, align: 'center', width: 100 },
    { prop: 'triggerFlag', label: `触发保护阈值`, show: true, fixed: false, align: 'center', width: 120 }
])

const columnsDetail = ref([
    { prop: 'ip', label: `IP`, show: true, fixed: 'left' },
    { prop: 'port', label: `端口`, show: true, fixed: 'left' },
    { prop: 'weight', label: `权重`, show: true, fixed: false, align: 'center' },
    { prop: 'ephemeral', label: `临时实例`, show: true, fixed: false, align: 'center'},
    { prop: 'healthy', label: `健康状态`, show: true, fixed: false, align: 'center' },
    { prop: 'metadata', label: `元数据`, show: true, fixed: false, align: 'left',width:450 }
])

// 查询表格
const list = () => {
    tableLoading.value = true
    getNacosServices(queryParams.value).then((res) => {
        hasNacos.value = true
        tableLoading.value = false
        tableData.value = res.data.serviceList as never
        queryParams.value.totalCount = res.data.count as never
    }).catch(() => {
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

const handleView = (row: any) => {
    showDetail.value = true
    getNacosService({ serviceName: row.name, groupName: row.groupName }).then((res) => {
        clusters.value = res.data.clusters
        queryDetailParams.value.clusterName = clusters.value[0].name
        queryDetailParams.value.serviceName = row.name
        queryDetailParams.value.groupName = row.groupName
        listDetail()
    })
}

// 查询表格
const listDetail = () => {
    getNacosDetail(queryDetailParams.value).then((res) => {
        tableDetailData.value = res.data.list as never
        queryDetailParams.value.totalCount = res.data.count as never
    })
}

const clusterChange = (val: string) => {
    queryDetailParams.value.pageNo = 1
    listDetail()
}

const getInfo = (data: any) => {
    var note = ''
    for (var key in data) {
        note += key + '=' + data[key] + '<br>'
    }
    return note
}

onMounted(() => {
    list()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>