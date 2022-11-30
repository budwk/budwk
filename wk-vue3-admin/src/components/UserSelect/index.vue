<template>
    <div class="app-container">
        <el-dialog title="选择用户" v-model="showSelectDialog" :width="multiple?'75%':'55%'" :before-close="beforeClose">
            <el-row :gutter="10">
                <el-col :span="multiple?11:24">
                    <el-input v-model="queryParams.keyword" placeholder="请输入用户名或姓名" class="input-with-select">
                        <template #prepend>
                            <el-tree-select v-model="queryParams.unitPath" :data="unitLeftOptions" filterable clearable
                                check-strictly :props="{ value: 'path', label: 'name', children: 'children' }"
                                :filter-node-method="filterNodeMethod" placeholder="选择单位" @change="handleSearch">
                                <template #default="{ data }">
                                    <i v-if="data.type && data.type.value === 'GROUP'" class="fa fa-building" />
                                    <i v-if="data.type && data.type.value === 'COMPANY'" class="fa fa-home" />
                                    {{ data.name }}
                                </template>
                            </el-tree-select>
                        </template>
                        <template #append>
                            <el-button icon="Search" @click="handleSearch" />
                        </template>
                    </el-input>

                    <el-table ref="leftTableRef" :class="multiple?'user-select-left-table':''" v-loading="tableLoading" :data="tableData" row-key="id"
                        @selection-change="handleLeftSelectChange">
                        <el-table-column type="selection" width="50" fixed="left" />
                        <el-table-column prop="username" label="姓名|用户名">
                            <template #default="scope">
                                {{ scope.row.username }} ({{ scope.row.loginname }})
                            </template>
                        </el-table-column>
                        <el-table-column prop="unit" label="所属单位">
                            <template #default="scope">
                                <span v-if="scope.row.unit">{{ scope.row.unit.name }}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                    <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                        v-model:limit="queryParams.pageSize" @pagination="list" class="user-select-page" />
                </el-col>
                <el-col v-if="multiple" :span="2" style="margin-top:100px;">
                    <el-row justify="center">
                        <el-button icon="DArrowRight" @click="selectUsers" :disabled="leftSelectData.length <= 0">
                        </el-button>
                    </el-row>
                    <el-row justify="center" style="margin-top:20px;">
                        <el-button icon="DArrowLeft" @click="removeUsers" :disabled="rightSelectData.length <= 0">
                        </el-button>
                    </el-row>
                </el-col>

                <el-col v-if="multiple" :span="11">
                    <el-row>
                        <span class="user-select-right-title">已选中用户列表</span>
                    </el-row>
                    <el-table ref="rightTableRef" :v-loading="tableLoading" :data="tableSelectData" row-key="id"
                        @selection-change="handleRightSelectChange">
                        <el-table-column type="selection" width="50" fixed="left" />
                        <el-table-column prop="username" label="姓名|用户名">
                            <template #default="scope">
                                {{ scope.row.username }} ({{ scope.row.loginname }})
                            </template>
                        </el-table-column>
                        <el-table-column prop="unit" label="所属单位">
                            <template #default="scope">
                                <span v-if="scope.row.unit">{{ scope.row.unit.name }}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-col>
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="doSelect">确 定</el-button>
                    <el-button @click="doClose">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
import { ElForm, ElTable } from "element-plus"
import { onMounted, reactive, ref, toRefs, watchEffect } from "vue"
import { getUnitList, getList } from '/@/api/platform/pub/user'
import { handleTree } from '/@/utils/common'
import modal from '/@/utils/modal'

const showSelectDialog = ref(false)

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false
    },
    multiple: {
        type: Boolean,
        default: true
    },
})

const emits = defineEmits(['update:modelValue','selected'])

const doClose = () => {
    leftSelectData.value = []
    rightSelectData.value = []
    tableSelectData.value = []
    leftTableRef.value?.clearSelection()
    rightTableRef.value?.clearSelection()
    queryParams.value.users = ''
    queryParams.value.pageNo = 1
    list()
    emits('update:modelValue', false)
}

const beforeClose = (done: any) => {
    doClose()
    done()
}

const doSelect = () => {
    if(!props.multiple){ //单选
        if(leftSelectData.value.length==0){
            modal.msgError('请选择一个用户')
            return 
        }
        if(leftSelectData.value.length>1){
            modal.msgError('仅可选择一个用户')
            return 
        }
        emits('selected', JSON.parse(JSON.stringify(leftSelectData.value)))
        doClose()
    } else { // 多选 
        if(tableSelectData.value.length==0){
            modal.msgError('请选择用户')
            return 
        }
        emits('selected', JSON.parse(JSON.stringify(tableSelectData.value)))
        doClose()
    }
}

watchEffect(() => {
    showSelectDialog.value = props.modelValue
})

const queryRef = ref<InstanceType<typeof ElForm>>()
const leftTableRef = ref<InstanceType<typeof ElTable>>()
const rightTableRef = ref<InstanceType<typeof ElTable>>()

const unitLeftOptions = ref([])
const tableLoading = ref(false)
const tableData = ref([])
const tableSelectData = ref([])
const leftSelectData = ref([])
const rightSelectData = ref([])

const data = reactive({
    queryUnit: {
        name: ''
    },
    queryParams: {
        unitPath: '',
        keyword: '',
        mobile: '',
        users: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    }
})

const { queryUnit, queryParams } = toRefs(data)

// 通过条件过滤节点
const filterNodeMethod = (value: string, data: any) => data.name.includes(value)

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
    list()
}

const handleLeftSelectChange = (row: any) => {
    leftSelectData.value = row
}

const handleRightSelectChange = (row: any) => {
    rightSelectData.value = row
}

const selectUsers = () => {
    tableSelectData.value = tableSelectData.value.concat(JSON.parse(JSON.stringify(leftSelectData.value)) as never)
    const users = tableSelectData.value.map(item => item.loginname)
    queryParams.value.users = users.toString()
    list()
}

const removeUsers = () => {
    const ids = rightSelectData.value.map(item => item.id)
    tableSelectData.value = tableSelectData.value.filter(item => ids.indexOf(item.id) < 0)
    const users = tableSelectData.value.map(item => item.loginname)
    queryParams.value.users = users.toString()
    list()
}

// 获取单位树
const getUnitTree = () => {
    getUnitList(queryUnit.value).then((res) => {
        unitLeftOptions.value = handleTree(res.data) as never
    })
}

onMounted(() => {
    getUnitTree()
    list()
})
</script>
<style>
.user-select-page .el-pagination {
    position: relative;
}

.user-select-left-table {
    margin-top: 20px;
}

.user-select-right-title {
    font-size: 16px;
    line-height: 50px;
    font-weight: 700;
}
</style>