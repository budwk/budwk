<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="单位名称" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入单位名称" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="部门负责人" prop="leaderName">
                <el-input v-model="queryParams.leaderName" placeholder="请输入部门负责人" clearable
                    @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                <el-button icon="Refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="Plus" @click="handleCreate"
                    v-permission="['sys.manage.unit.create']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过单位名称搜索" />
        </el-row>

        <el-table v-if="refreshTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <el-table-column :prop="columns[0].prop" :label="columns[0].label" :fixed="columns[0].fixed"
                v-if="columns[0].show" width="260"></el-table-column>
            <el-table-column :prop="columns[1].prop" :label="columns[1].label" :fixed="columns[1].fixed"
                v-if="columns[1].show" width="260"></el-table-column>
            <el-table-column :prop="columns[2].prop" :label="columns[2].label" :fixed="columns[2].fixed"
                v-if="columns[2].show" width="260">
                <template #default="scope">
                    <span>{{ formatTime(scope.row.createdAt) }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button type="text" icon="Edit" @click="handleUpdate(scope.row)"
                        v-permission="['sys.manage.unit.update']">修改</el-button>
                    <el-button type="text" icon="Plus" @click="handleCreate(scope.row)"
                        v-permission="['sys.manage.unit.create']">新增</el-button>
                    <el-button v-if="scope.row.parentId != 0" type="text" icon="Delete" @click="handleDelete(scope.row)"
                        v-permission="['sys.manage.unit.delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script setup lang="ts">
import { reactive, ref, toRefs } from 'vue'

const isExpandAll = ref(true)
const showSearch = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])

const data = reactive({
    formData: {},
    queryParams: {
        name: undefined,
        leaderName: undefined,
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
    { prop: 'name', label: `单位名称`, show: true, fixed: 'left' },
    { prop: 'disabled', label: `状态`, show: true, fixed: false },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false }
])

const quickSearch = (data: any) => {
    console.log('quickSearch')
    console.log(data)
}

const handleSearch = () => {

}

const resetSearch = () => {
    
}

const toggleExpandAll = () => {

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