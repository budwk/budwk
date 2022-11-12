<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="单位名称" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入单位名称" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="部门负责人" prop="leaderName">
                <el-input
v-model="queryParams.leaderName" placeholder="请输入部门负责人" clearable
                    @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                <el-button icon="Refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button
type="primary" @click="handleCreate"
                    v-permission="['sys.manage.unit.create']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="info" @click="toggleExpandAll">{{isExpandAll?'折叠':'展开'}}</el-button>
            </el-col>
            <right-toolbar
v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过单位名称搜索" />
        </el-row>

        <el-table
            v-if="refreshTable" 
            v-loading="tableLoading" 
            :data="tableData" 
            row-key="id"
            :default-expand-all="isExpandAll" 
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item,idx) in columns" :key="idx">
                <el-table-column
:prop="item.prop" :label="item.label" :fixed="item.fixed"
                v-if="item.show" :show-overflow-tooltip="true">
                    <template v-if="item.prop=='type'" #default="scope">
                        <span>{{ scope.row.type?.text }}</span>
                    </template>
                    <template v-if="item.prop=='createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button
link type="primary" @click="handleUpdate(scope.row)"
                        v-permission="['sys.manage.unit.update']">修改</el-button>
                    <el-button
link type="primary" icon="Plus" @click="handleCreate(scope.row)"
                        v-permission="['sys.manage.unit.create']">新增</el-button>
                    <el-button
v-if="scope.row.path != '0001'" link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                        v-permission="['sys.manage.unit.delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { getList } from '/@/api/platform/sys/unit'
import { handleTree } from '/@/utils/common'

const isExpandAll = ref(true)
const showSearch = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])

const data = reactive({
    formData: {},
    queryParams: {
        name: '',
        leaderName: '',
    },
    formRules: {
        parentId: [{ required: true, message: "上级部门不能为空", trigger: "blur" }],
        deptName: [{ required: true, message: "部门名称不能为空", trigger: "blur" }],
        orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
        email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
        phone: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `单位名称`, show: true, fixed: false },
    { prop: 'type', label: `单位类型`, show: true, fixed: false },
    { prop: 'leaderName', label: `部门负责人`, show: true, fixed: false },
    { prop: 'disabled', label: `状态`, show: true, fixed: false },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false }
])

const listPage = () => {
    tableLoading.value = true
    getList(queryParams.value.name, queryParams.value.leaderName).then((res)=>{
        tableLoading.value = false
        tableData.value = handleTree(res.data) as never
    })
} 

onMounted(()=>{
    listPage()
})

const quickSearch = (data: any) => {
    queryParams.value.name = data.keyword
    queryParams.value.leaderName = ''
    listPage()
}

const handleSearch = () => {

}

const resetSearch = () => {
    
}

const toggleExpandAll = () => {
    refreshTable.value = false;
    isExpandAll.value = !isExpandAll.value
    nextTick(() => {
        refreshTable.value = true
    })
}

const handleCreate = (row: any) => {

}

const handleUpdate = (row: any) => {

}

const handleDelete = (row: any) => {

}

</script>
<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
    name: 'platform-sys-unit'
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>