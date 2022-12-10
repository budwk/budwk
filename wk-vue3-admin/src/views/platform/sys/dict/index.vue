<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" plain @click="handleCreate" v-permission="['sys.config.dict.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Sort" @click="handleSort" v-permission="['sys.config.dict.update']">排序</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain v-if="isExpandAll" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button plain v-else icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="false" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过字典名称搜索" />
        </el-row>

        <el-table v-if="showTreeTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" :width="item.width" v-if="item.show"
                    :show-overflow-tooltip="true">
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
            <el-table-column fixed="right" header-align="center" align="center" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="新增子项目" placement="top">
                        <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                            v-permission="['sys.config.dict.create']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['sys.config.dict.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                        <el-button link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['sys.config.dict.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增字典" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="120px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="上级字典" prop="parentId">
                            <el-tree-select v-model="formData.parentId" :data="dictOptions"
                                :props="{ value: 'id', label: 'name', children: 'children' }" value-key="id"
                                placeholder="选择上级字典" check-strictly :render-after-expand="false" style="width:100%"
                                />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="字典名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入字典名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="字典编码" prop="code">
                            <el-input v-model="formData.code" placeholder="字典编码" maxlength="32"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="disabled" label="字典状态">
                            <el-switch
                                v-model="formData.disabled"
                                :active-value="false"
                                :inactive-value="true"
                                active-color="green"
                                inactive-color="red"
                            />
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

        <el-dialog title="修改字典" v-model="showUpdate" width="40%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="120px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="字典名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入字典名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="字典编码" prop="code">
                            <el-input v-model="formData.code" placeholder="字典编码" maxlength="32"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="disabled" label="字典状态">
                            <el-switch
                                v-model="formData.disabled"
                                :active-value="false"
                                :inactive-value="true"
                                active-color="green"
                                inactive-color="red"
                            />
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

        <el-dialog title="字典排序" v-model="showSort" width="30%">
            <el-tree ref="sortTree" :data="sortData" draggable :allow-drop="sortAllowDrop" node-key="id"
                :props="{ label: 'name', children: 'children' }">
            </el-tree>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="sort">保 存</el-button>
                    <el-button @click="showSort = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-sys-dict">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { doCreate, doUpdate, getInfo, getList, doDelete, doSort, doDisable } from '/@/api/platform/sys/dict'
import { handleTree } from '/@/utils/common'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const isExpandAll = ref(true)
const showSearch = ref(false)
const showTreeTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const sortData = ref([])
const dictOptions = ref([]);
const dictList = ref([]);
const showCreate = ref(false)
const showUpdate = ref(false)
const showSort = ref(false)


const data = reactive({
    formData: {
        id: '',
        parentId: '',
        name: '',
        code: '',
        disabled: false,
    },
    queryParams: {
        name: '',
        leaderName: '',
    },
    formRules: {
        name: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
        code: [{ required: true, message: "字典编码不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `字典名称`, show: true, fixed: false },
    { prop: 'code', label: `字典编码`, show: true, fixed: false },
    { prop: 'disabled', label: `状态`, show: true, fixed: false },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false, width: 160 }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        parentId: '',
        name: '',
        code: '',
        disabled: false,
    }
    formEl?.resetFields()
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        dictList.value = res.data
        tableData.value = handleTree(res.data) as never
        dictOptions.value = handleTree(res.data) as never
        sortData.value = handleTree(JSON.parse(JSON.stringify(res.data))) as never
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

// 启用禁用
const disabledChange = (row: any) => {
    doDisable({disabled: row.disabled, id: row.id}).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(()=>{
        setTimeout(() => {
            row.disabled = !row.disabled
        }, 300)
    })
}

// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    if (row && row.id) {
        formData.value.parentId = row.id
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

// 拖拽是否允许跨级
const sortAllowDrop = (moveNode: any, inNode: any, type: string) => {
    if (moveNode.data.parentId === inNode.data.parentId) {
        return type === 'prev'
    }
}

// 排序
const handleSort = () => {
    showSort.value = true
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

// 获取ids
const getTreeIds = (ids: string[], data: any[]) => {
    data.forEach((obj) => {
        ids.push(obj.id)
        if (obj.children && obj.children.length > 0) {
            getTreeIds(ids, obj.children)
        }
    })
}
// 提交排序
const sort = () => {
    let ids: string[] = []
    getTreeIds(ids, sortData.value)
    doSort(ids.toString()).then((res: any) => {
        modal.msgSuccess(res.msg)
        showSort.value = false
        resetSearch()
    })
}

</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>