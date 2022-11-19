<template>
    <div class="app-container">
      <el-row :gutter="20">
        <el-col :span="4" :xs="24">
            <div class="head-container">
               <el-input
                  v-model="queryUnit.name"
                  placeholder="请输入单位名称"
                  clearable
                  prefix-icon="Search"
                  style="margin-bottom: 20px"
               />
            </div>
            <div class="head-container">
               <el-tree
                  :data="unitOptions"
                  :props="{ label: 'name', children: 'children' }"
                  :expand-on-click-node="false"
                  :filter-node-method="filterNode"
                  ref="unitTreeRef"
                  highlight-current
                  default-expand-all
                  @node-click="handleNodeClick"
               />
            </div>
        </el-col>
        <el-col :span="20" :xs="24">
            <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                <el-form-item label="用户姓名" prop="username">
                  <el-input
                     v-model="queryParams.username"
                     placeholder="请输入用户姓名"
                     clearable
                     style="width: 180px"
                     @keyup.enter="handleSearch"
                  />
               </el-form-item>
               <el-form-item label="用户名" prop="loginname">
                  <el-input
                     v-model="queryParams.loginname"
                     placeholder="请输入用户名"
                     clearable
                     style="width: 180px"
                     @keyup.enter="handleSearch"
                  />
               </el-form-item>
               <el-form-item label="手机号码" prop="mobile">
                  <el-input
                     v-model="queryParams.mobile"
                     placeholder="请输入手机号码"
                     clearable
                     style="width: 180px"
                     @keyup.enter="handleSearch"
                  />
               </el-form-item>
               <el-form-item label="用户状态" prop="disabled">
                  <el-select
                     v-model="queryParams.disabled"
                     placeholder="用户状态"
                     clearable
                     style="width: 180px"
                  >
                    <el-option :value="false" label="启用" />
                    <el-option :value="true" label="禁用" />
                  </el-select>
               </el-form-item>
               <el-form-item label="创建时间" prop="dateRange" style="font-weight:700;width: 325px;">
                  <el-date-picker
                     v-model="dateRange"
                     value-format="YYYY-MM-DD"
                     type="daterange"
                     range-separator="-"
                     start-placeholder="开始日期"
                     end-placeholder="结束日期"
                  ></el-date-picker>
               </el-form-item>
               <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                  <el-button icon="Refresh" @click="resetSearch">重置</el-button>
               </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
               <el-col :span="1.5">
                  <el-button
                     type="primary"
                     plain
                     icon="Plus"
                     @click="handleAdd"
                     v-hasPermi="['system:user:add']"
                  >新增</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button
                     type="success"
                     plain
                     icon="Edit"
                     :disabled="single"
                     @click="handleUpdate"
                     v-hasPermi="['system:user:edit']"
                  >修改</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button
                     type="danger"
                     plain
                     icon="Delete"
                     :disabled="multiple"
                     @click="handleDelete"
                     v-hasPermi="['system:user:remove']"
                  >删除</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button
                     type="info"
                     plain
                     icon="Upload"
                     @click="handleImport"
                     v-hasPermi="['system:user:import']"
                  >导入</el-button>
               </el-col>
               <el-col :span="1.5">
                  <el-button
                     type="warning"
                     plain
                     icon="Download"
                     @click="handleExport"
                     v-hasPermi="['system:user:export']"
                  >导出</el-button>
               </el-col>
               <right-toolbar v-model:showSearch="showSearch" :extendSearch="true" :columns="columns" @quickSearch="quickSearch" />
            </el-row>

            <el-table v-if="showTable" v-loading="tableLoading" :data="tableData" row-key="id" >
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="false" :width="item.prop == 'createdAt'?'160':'110'">
                    <template v-if="item.prop == 'unit'" #default="scope">
                        <span v-if="scope.row.unit">{{ scope.row.unit.name }}</span>
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'disabled'" #default="scope">
                        <el-switch
                        v-model="scope.row.disabled"
                        :active-value="false"
                        :inactive-value="true"
                        active-color="green"
                        inactive-color="red"
                        @change="disabledChange(scope.row)"
                        />
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作" width="150" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="新增子单位" placement="top">
                        <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                            v-permission="['sys.manage.unit.create']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['sys.manage.unit.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                        <el-button link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['sys.manage.unit.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        </el-col>
      </el-row>  
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import modal from '/@/utils/modal'
import { getUnitList, doCreate, doUpdate, getInfo, getList, doDelete, doDisable } from '/@/api/platform/sys/user'
import { toRefs } from '@vueuse/core'
import { ElForm, ElTree } from 'element-plus'
import { handleTree } from '/@/utils/common'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()
const unitTreeRef = ref<InstanceType<typeof ElTree>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const showSearch = ref(true)
const showTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const unitOptions = ref([])
const dateRange = ref([])

const data = reactive({
    formData: {
        id: '',
        name: '',
        code: '',
        location: 0,
    },
    queryUnit: {
        name: ''
    },
    queryParams: {
        unitId: '',
        username: '',
        loginname: '',
        mobile: '',
        disabled: undefined,
        beginTime: undefined,
        endTime: undefined,
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
        name: [{ required: true, message: "职务名称不能为空", trigger: ["blur","change"] }],
        code: [{ required: true, message: "职务编号不能为空", trigger: ["blur","change"] }]
    },
    posts: []
})

const { queryUnit, posts, queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'username', label: `用户姓名`, show: true, fixed: 'left' },
    { prop: 'loginname', label: `用户名`, show: true, fixed: false },
    { prop: 'serialNo', label: `用户编号`, show: true, fixed: false },
    { prop: 'unit', label: `所属单位`, show: true, fixed: false },
    { prop: 'postId', label: `职务`, show: true, fixed: false },
    { prop: 'mobile', label: `手机号`, show: true, fixed: false },
    { prop: 'disabled', label: `用户状态`, show: true, fixed: false },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false }
])

// 通过条件过滤节点
const filterNode = (value: any, data: any) => {
    if (!value) return true
    return data.name.indexOf(value) !== -1
}

// 点击单位
const handleNodeClick = (data: any) =>{
    queryParams.value.unitId = data.id
    list()
}

// 获取单位树
const getUnitTree = () => {
    getUnitList(queryUnit).then((res)=>{
        unitOptions.value = handleTree(res.data) as never
    })
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

// 快速搜索&刷新
const quickSearch = () => {
    showTable.value = false
    list()
    nextTick(() => {
        showTable.value = true
    })
}

// 高级搜索
const handleSearch = () => {
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

// 启用禁用
const disabledChange = (row: any) => {
    doDisable({disabled: row.disabled, id: row.id, loginname: row.loginname}).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(()=>{
        setTimeout(() => {
            row.disabled = !row.disabled
        }, 300)
    })
}

// 筛选单位
watch(queryUnit.value, val => {
    unitTreeRef.value?.filter(val.name)
})

onMounted(()=>{
    getUnitTree()
    list()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default{
    name: 'platform-sys-user'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>