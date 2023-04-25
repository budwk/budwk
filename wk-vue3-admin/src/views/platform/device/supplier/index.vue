<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" plain @click="handleCreate"
                    v-permission="['device.settings.supplier.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain v-if="isExpandAll" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button plain v-else icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="false" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="false" quickSearchPlaceholder="通过名称搜索" />
        </el-row>

        <el-table v-if="showTreeTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" :width="item.width"
                    v-if="item.show" :show-overflow-tooltip="true">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip v-if="scope.row.isSupplier" content="新增设备型号" placement="top">
                        <el-button link type="primary" icon="CirclePlus" @click="handleCreateCode(scope.row)"
                            v-permission="['device.settings.supplier.create']"></el-button>
                    </el-tooltip>
                    <el-tooltip v-if="scope.row.isSupplier" content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['device.settings.supplier.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip v-if="scope.row.isSupplier" content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['device.settings.supplier.delete']"></el-button>
                    </el-tooltip>
                    <el-tooltip v-if="!scope.row.isSupplier" content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdateCode(scope.row)"
                            v-permission="['device.settings.supplier.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip v-if="!scope.row.isSupplier" content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDeleteCode(scope.row)"
                            v-permission="['device.settings.supplier.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增厂家" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="厂家名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入厂家名称" maxlength="100" />
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

        <el-dialog title="修改厂家" v-model="showUpdate" width="40%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="厂家名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入厂家名称" maxlength="100" />
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

        <el-dialog title="新增设备型号" v-model="showCreateCode" width="40%">
            <el-form ref="createCodeRef" :model="formDataCode" :rules="formCodeRules" label-width="100px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="设备型号" prop="name">
                            <el-input v-model="formDataCode.name" placeholder="请输入设备型号" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="型号标识" prop="code">
                            <el-input v-model="formDataCode.code" placeholder="请输入型号标识" maxlength="5" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="createCode">确 定</el-button>
                    <el-button @click="showCreateCode = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改设备型号" v-model="showUpdateCode" width="40%">
            <el-form ref="updateCodeRef" :model="formDataCode" :rules="formCodeRules" label-width="100px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="设备型号" prop="name">
                            <el-input v-model="formDataCode.name" placeholder="请输入设备型号" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="型号标识" prop="code">
                            <el-input v-model="formDataCode.code" placeholder="请输入型号标识" maxlength="5" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="updateCode">确 定</el-button>
                    <el-button @click="showUpdateCode = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-device-supplier">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { doCreate, doUpdate, getInfo, getList, doDelete, getCodeInfo, doCodeDelete, doCodeUpdate, doCodeCreate } from '/@/api/platform/device/supplier'
import { handleTree } from '/@/utils/common'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const createCodeRef = ref<InstanceType<typeof ElForm>>()
const updateCodeRef = ref<InstanceType<typeof ElForm>>()
const isExpandAll = ref(true)
const showSearch = ref(false)
const showTreeTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const showCreate = ref(false)
const showUpdate = ref(false)
const showCreateCode = ref(false)
const showUpdateCode = ref(false)


const data = reactive({
    formData: {
        id: '',
        name: ''
    },
    formDataCode: {
        id: '',
        name: '',
        code: '',
        supplierId: ''
    },
    queryParams: {
        name: ''
    },
    formRules: {
        name: [{ required: true, message: "厂家名称不能为空", trigger: "blur" }]
    },
    formCodeRules: {
        name: [{ required: true, message: "设备型号不能为空", trigger: "blur" }],
        code: [{ required: true, message: "型号标识不能为空", trigger: "blur" },
            { pattern: /^[A-Z][A-Z]+$/, message: "为2-5位大写字母，且不可重复", trigger: "blur" }]
    },
})
const { queryParams, formData, formRules, formDataCode, formCodeRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `厂家名称 | 设备型号`, show: true, fixed: false },
    { prop: 'code', label: `型号标识`, show: true, fixed: false },
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
    }
    formEl?.resetFields()
}

const resetFormCode = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formDataCode.value = {
        id: '',
        name: '',
        code: '',
        supplierId: ''
    }
    formEl?.resetFields()
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data as never
    })
}

onMounted(() => {
    list()
})

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
    modal.confirm('此操作将删除 ' + row.name + ' 及其设备型号，请谨慎操作！').then(() => {
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

// 新增按钮
const handleCreateCode = (row: any) => {
    resetFormCode(createCodeRef.value)
    if (row && row.id) {
        formDataCode.value.supplierId = row.id
    }
    showCreateCode.value = true
}

// 修改按钮
const handleUpdateCode = (row: any) => {
    getCodeInfo(row.id).then((res: any) => {
        formDataCode.value = res.data
        showUpdateCode.value = true
    })
}

// 提交新增
const createCode = () => {
    if (!createCodeRef.value) return
    createCodeRef.value.validate((valid) => {
        if (valid) {
            doCodeCreate(formDataCode.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreateCode.value = false
                resetSearch()
            })
        }
    })
}

// 提交修改
const updateCode = () => {
    if (!updateCodeRef.value) return
    updateCodeRef.value.validate((valid) => {
        if (valid) {
            doCodeUpdate(formDataCode.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdateCode.value = false
                resetSearch()
            })
        }
    })
}

// 删除按钮
const handleDeleteCode = (row: any) => {
    modal.confirm('此操作将删除设备型号 ' + row.name + '，请谨慎操作！').then(() => {
        return doCodeDelete(row.id)
    }).then(() => {
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}

</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>