<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="4">
                <div class="head-container">
                    <el-input
v-model="queryUnit.name" placeholder="请输入单位名称" clearable prefix-icon="Search"
                        style="margin-bottom: 20px" />
                </div>
                <div class="head-container">
                    <el-tree
:data="unitLeftOptions" :props="{ label: 'name', children: 'children' }"
                        :expand-on-click-node="false" :filter-node-method="filterNode" ref="unitTreeRef"
                        highlight-current default-expand-all @node-click="handleNodeClick">
                        <template #default="{ node, data }">
                            <i v-if="data.type && data.type.value === 'GROUP'" class="fa fa-building" />
                            <i v-if="data.type && data.type.value === 'COMPANY'" class="fa fa-home" />
                            {{ data.name }}
                        </template>
                    </el-tree>
                </div>
            </el-col>
            <el-col :span="20">
                <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
                    <el-form-item label="用户名或姓名" prop="keyword">
                        <el-input
v-model="queryParams.keyword" placeholder="请输入用户名或姓名" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="手机号码" prop="mobile">
                        <el-input
v-model="queryParams.mobile" placeholder="请输入手机号码" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
                <el-table v-loading="tableLoading" :data="tableData" row-key="id" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50" fixed="left" />
                </el-table>
                <pagination
:total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                    v-model:limit="queryParams.pageSize" @pagination="list" />
            </el-col>
        </el-row>
    </div>
</template>
<script setup lang="ts">
import { ElForm, ElTree } from "element-plus"
import { onMounted, reactive, ref, toRefs, watch } from "vue"
import { getUnitList, getPostList, getList } from '/@/api/platform/pub/user'
import { handleTree } from '/@/utils/common'

const props = defineProps({
    multiple: {
        type: Boolean,
        default: true,
    },
})

const emits = defineEmits(['select'])

const unitTreeRef = ref<InstanceType<typeof ElTree>>()
const queryRef = ref<InstanceType<typeof ElForm>>()
    
const unitLeftOptions = ref([])
const currentUnitId = ref('')
const currentUnitPath = ref('')
const tableLoading = ref(false)
const tableData = ref([])

const data = reactive({
    queryUnit: {
        name: ''
    },
    queryParams: {
        unitPath: '',
        keyword: '',
        loginname: '',
        mobile: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    },
    posts: []
})

const { queryUnit, posts, queryParams } = toRefs(data)


// 通过条件过滤节点
const filterNode = (value: any, data: any) => {
    if (!value) return true
    return data.name.indexOf(value) !== -1
}

// 点击单位
const handleNodeClick = (data: any) => {
    queryParams.value.unitPath = data.path
    currentUnitId.value = data.id
    currentUnitPath.value = data.path
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

const handleSearch = () => {
    queryParams.value.pageNo = 1
    queryParams.value.unitPath = currentUnitPath.value
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

// 列表多选
const handleSelectionChange =(selection: any) => {
    
}

// 获取单位树
const getUnitTree = () => {
    getUnitList(queryUnit).then((res) => {
        unitLeftOptions.value = handleTree(res.data) as never
    })
}

// 获取职务
const getPost = () => {
    getPostList().then((res) => {
        posts.value = res.data as never
    })
}

// 获取职务名称
const findPostName = (id: string) => {
    const index = posts.value.findIndex((obj: any) => obj.id === id)
    return index >= 0 ? posts.value[index]['name'] : ''
}

// 筛选单位
watch(queryUnit.value, val => {
    unitTreeRef.value?.filter(val.name)
})

onMounted(()=>{
    getUnitTree()
    getPost()
    list()
})
</script>