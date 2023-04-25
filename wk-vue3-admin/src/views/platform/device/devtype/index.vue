<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" plain @click="handleCreate" v-permission="['device.settings.devtype.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain v-if="isExpandAll" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button plain v-else icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="false" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过名称搜索" />
        </el-row>

        <el-table v-if="showTreeTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" :width="item.width" v-if="item.show"
                    :show-overflow-tooltip="true">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'color'" #default="scope">
                        <div :style="'width: 60px;height: 20px;background-color:'+scope.row.color">
                            &nbsp;&nbsp;&nbsp;
                        </div>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="新增子类型" placement="top">
                        <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                            v-permission="['device.settings.devtype.create']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['device.settings.devtype.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                        <el-button link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['device.settings.devtype.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增设备类型" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="上级类型" prop="parentId">
                            <el-tree-select v-model="formData.parentId" :data="dataOptions" @change="parentChange($event)"
                                :props="{ value: 'id', label: 'name', children: 'children' }" value-key="id"
                                placeholder="选择上级类型" check-strictly :render-after-expand="false" style="width:100%"
                                />
                        </el-form-item>
                    </el-col>
                    <el-col>
                        <el-form-item label="业务类型" prop="type">
                            <el-select v-model="formData.type" placeholder="请选择业务类型" :disabled="formData.parentId.length>0">
                                <el-option
                                v-for="item in typeList"
                                :key="item.value"
                                :label="item.text"
                                :value="item.value"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="类型名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入类型名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="类型标识" prop="code">
                            <el-input v-model="formData.code" placeholder="请输入类型标识" maxlength="32"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="颜色标识" prop="color" v-if="formData.parentId.length==0">
                                <el-color-picker v-model="formData.color" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改设备类型" v-model="showUpdate" width="40%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row>
                    <el-col>
                        <el-form-item label="业务类型" prop="type">
                            <el-select v-model="formData.type" placeholder="请选择业务类型" :disabled="formData.parentId.length>0">
                                <el-option
                                v-for="item in typeList"
                                :key="item.value"
                                :label="item.text"
                                :value="item.value"
                                />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="类型名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入类型名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="类型标识" prop="code">
                            <el-input v-model="formData.code" placeholder="请输入类型标识" maxlength="32"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.parentId.length==0">
                        <el-form-item label="颜色标识" prop="code">
                                <el-color-picker v-model="formData.color" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="update">确 定</el-button>
                    <el-button @click="showUpdate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>
<script setup lang="ts" name="platform-device-devtype">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { doCreate, doUpdate, getInfo, getList, doDelete, getInit } from '/@/api/platform/device/devtype'
import { handleTree, findOneValue } from '/@/utils/common'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const isExpandAll = ref(true)
const showSearch = ref(false)
const showTreeTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const dataOptions = ref([])
const showCreate = ref(false)
const showUpdate = ref(false)
const typeList = ref([])


const data = reactive({
    formData: {
        id: '',
        parentId: '',
        name: '',
        code: '',
        color: '',
        type: '',
    },
    queryParams: {
        name: ''
    },
    formRules: {
        name: [{ required: true, message: "类型名称不能为空", trigger: "blur" }],
        code: [
            { required: true, message: "类型标识不能为空", trigger: "blur" },
            { pattern: /^[a-z][a-z0-9_]+$/, message: "为小写字母、下划线和数字的组合，并以字母开头", trigger: "blur" }
        ],
        type: [{ required: true, message: "业务类型不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `类型名称`, show: true, fixed: false },
    { prop: 'code', label: `类型标识`, show: true, fixed: false },
    { prop: 'color', label: `颜色`, show: true, fixed: false }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        parentId: '',
        name: '',
        code: '',
        color: '',
        type: '',
    }
    formEl?.resetFields()
}

// init
const init = () => {
    getInit().then((res) => {
        typeList.value = res.data.DeviceType
    })
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = handleTree(res.data) as never
        dataOptions.value = handleTree(res.data) as never
    })
}

onMounted(() => {
    list()
    init()
})

const parentChange = (val: any) => {
    const type = findOneValue(dataOptions.value, val, 'type', 'id')
    formData.value.type = type.value
}

// 快速搜索&刷新
const quickSearch = (data: any) => {
    queryParams.value.name = data.keyword
    showTreeTable.value = false
    list()
    nextTick(() => {
        showTreeTable.value = true
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

// 展开/折叠
const toggleExpandAll = () => {
    showTreeTable.value = false
    isExpandAll.value = !isExpandAll.value
    nextTick(() => {
        showTreeTable.value = true
    })
}


// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    if (row && row.id) {
        formData.value.parentId = row.id
        formData.value.type = row.type.value
    }
    showCreate.value = true
}

// 修改按钮
const handleUpdate = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        showUpdate.value = true
    })
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('此操作将删除 '+row.name+' 及其下级，请谨慎操作！').then(() => {
        return doDelete(row.id)
    }).then(() => {
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}

// 提交新增
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            doCreate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreate.value = false
                resetSearch()
            })
        }
    })
}

// 提交修改
const update = () => {
    if (!updateRef.value) return
    updateRef.value.validate((valid) => {
        if (valid) {
            doUpdate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdate.value = false
                resetSearch()
            })
        }
    })
}


</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>