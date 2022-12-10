<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="菜单名称" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入菜单名称" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="菜单路径" prop="href">
                <el-input v-model="queryParams.href" placeholder="请输入菜单路径" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                <el-button icon="Refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" plain @click="handleCreate"
                    v-permission="['sys.manage.menu.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Sort" @click="handleSort"
                    v-permission="['sys.manage.menu.update']">排序</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain v-if="isExpandAll" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button plain v-else icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-select v-model="appId" class="m-2" placeholder="切换应用" @change="appChange">
                    <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过菜单名称搜索" />
        </el-row>

        <el-table v-if="showTreeTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width">
                    <template v-if="item.prop == 'name'" #default="scope">
                        <svg-icon v-if="scope.row&&scope.row.icon" :icon-class="scope.row.icon" />
                        {{ scope.row.name }}
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span v-if="scope.row.type === 'menu'">
                            菜单
                        </span>
                        <span v-if="scope.row.type === 'data'">
                            权限
                        </span>
                    </template>
                    <template v-if="item.prop == 'showit'" #default="scope">
                        <i v-if="scope.row.showit" class="fa fa-circle" style="color:green;" />
                        <i v-if="!scope.row.showit" class="fa fa-circle" style="color:red" />
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'disabled'" #default="scope">
                        <el-switch v-model="scope.row.disabled" :active-value="false" :inactive-value="true"
                            active-color="green" inactive-color="red" @change="disabledChange(scope.row)" />
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="right" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <div style="padding-right:30px;">
                        <el-tooltip content="新增子菜单" placement="top" v-if="scope.row.type == 'menu'">
                            <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                                v-permission="['sys.manage.menu.create']"></el-button>
                        </el-tooltip>
                        <el-tooltip v-if="scope.row.type == 'menu'" content="修改菜单" placement="top">
                            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                                v-permission="['sys.manage.menu.update']"></el-button>
                        </el-tooltip>
                        <el-tooltip v-if="scope.row.type == 'data'" content="修改权限" placement="top">
                            <el-button link type="primary" icon="Edit" @click="handleUpdateData(scope.row)"
                                v-permission="['sys.manage.menu.update']"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                                v-permission="['sys.manage.menu.delete']"></el-button>
                        </el-tooltip>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增菜单" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    <el-col :span="12">
                        <el-form-item label="上级菜单" prop="parentId">
                            <el-tree-select v-model="formData.parentId" :data="unitOptions"
                                :props="{ value: 'id', label: 'name', children: 'children' }" value-key="id"
                                placeholder="选择上级菜单" check-strictly :render-after-expand="false" style="width:100%"
                                @change="parentChange" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属应用" prop="appId">
                            <el-select v-model="appId" class="m-2" placeholder="所属应用" disabled style="width:100%">
                                <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>

                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入菜单名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="权限标识" prop="permission">
                            <el-input v-model="formData.permission" @keyup="inputChange_data" placeholder="请输入权限标识如 sys.manage" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单别名" prop="alias">
                            <el-input v-model="formData.alias" disabled placeholder="菜单别名" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="访问路径" prop="href">
                            <el-input v-model="formData.href" placeholder="后台访问路径前缀为 /platform/ " maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单图标" prop="icon">
                            <el-popover placement="bottom-start" :width="540" v-model="showChooseIcon" trigger="click"
                                @show="showSelectIcon">
                                <template #reference>
                                    <el-input v-model="formData.icon" placeholder="点击选择图标" @click="showSelectIcon"
                                        :click-outside="hideSelectIcon" readonly>
                                        <template #prefix>
                                            <svg-icon v-if="formData.icon" :icon-class="formData.icon"
                                                class="el-input__icon" style="height: 32px;width: 16px;" />
                                            <el-icon v-else style="height: 32px;width: 16px;">
                                                <search />
                                            </el-icon>
                                        </template>
                                    </el-input>
                                </template>
                                <icon-select ref="iconSelectRef" @selected="selectedIcon" />
                            </el-popover>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否显示" prop="showit">
                            <el-radio-group v-model="formData.showit">
                                <el-radio :label="true">
                                    显示
                                </el-radio>
                                <el-radio :label="false">
                                    隐藏
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单状态" prop="disabled">
                            <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item prop="type" label="有子权限">
                            <el-radio-group v-model="formData.children" @change="formRadioChange">
                                <el-radio label="false">否</el-radio>
                                <el-radio label="true">是</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-button v-if="formData.children == 'true'" icon="Plus" @click="formAddMenu">添加</el-button>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-for="(menu, index) in formData.buttons" :key="menu.key"
                            :label="'权限' + (index + 1)" :prop="'buttons[' + index + ']'" :rules="{
                                required: true,
                                validator: validateMenu,
                                trigger: ['blur', 'change']
                            }">
                            <el-row v-if="formData.children == 'true'" :gutter="2">
                                <el-col :span="7">
                                    <el-input v-model="menu.name" placeholder="权限名称" />
                                </el-col>
                                <el-col :span="15">
                                    <el-input v-model="menu.permission" placeholder="权限标识" />
                                </el-col>
                                <el-col :span="2">
                                    <el-button icon="Delete"
                                        @click.prevent="formRemoveMenu(menu)" />
                                </el-col>
                            </el-row>
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

        <el-dialog title="修改菜单" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    <el-col :span="12">
                        <el-form-item label="菜单名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入菜单名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="权限标识" prop="permission">
                            <el-input v-model="formData.permission" @keyup="inputChange_data" placeholder="请输入权限标识如 sys.manage" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单别名" prop="alias">
                            <el-input v-model="formData.alias" disabled placeholder="菜单别名" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="访问路径" prop="href">
                            <el-input v-model="formData.href" placeholder="后台访问路径前缀为 /platform/ " maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单图标" prop="icon">
                            <el-popover placement="bottom-start" :width="540" v-model="showChooseIcon" trigger="click"
                                @show="showSelectIcon">
                                <template #reference>
                                    <el-input v-model="formData.icon" placeholder="点击选择图标" @click="showSelectIcon"
                                        :click-outside="hideSelectIcon" readonly>
                                        <template #prefix>
                                            <svg-icon v-if="formData.icon" :icon-class="formData.icon"
                                                class="el-input__icon" style="height: 32px;width: 16px;" />
                                            <el-icon v-else style="height: 32px;width: 16px;">
                                                <search />
                                            </el-icon>
                                        </template>
                                    </el-input>
                                </template>
                                <icon-select ref="iconSelectRef" @selected="selectedIcon" />
                            </el-popover>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否显示" prop="showit">
                            <el-radio-group v-model="formData.showit">
                                <el-radio :label="true">
                                    显示
                                </el-radio>
                                <el-radio :label="false">
                                    隐藏
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="菜单状态" prop="disabled">
                            <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item prop="type" label="有子权限">
                            <el-radio-group v-model="formData.children" @change="formRadioChange">
                                <el-radio label="false">否</el-radio>
                                <el-radio label="true">是</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-button v-if="formData.children == 'true'" icon="Plus" @click="formAddMenu">添加</el-button>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-for="(menu, index) in formData.buttons" :key="menu.key"
                            :label="'权限' + (index + 1)" :prop="'buttons[' + index + ']'" :rules="{
                                required: true,
                                validator: validateMenu,
                                trigger: ['blur', 'change']
                            }">
                            <el-row v-if="formData.children == 'true'" :gutter="2">
                                <el-col :span="7">
                                    <el-input v-model="menu.name" placeholder="权限名称" />
                                </el-col>
                                <el-col :span="15">
                                    <el-input v-model="menu.permission" placeholder="权限标识" />
                                </el-col>
                                <el-col :span="2">
                                    <el-button icon="Delete"
                                        @click.prevent="formRemoveMenu(menu)" />
                                </el-col>
                            </el-row>
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

        <el-dialog title="修改权限" v-model="showUpdateData" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="权限名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入权限名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="权限标识" prop="permission">
                            <el-input v-model="formData.permission" @keyup="inputChange_data" placeholder="请输入权限标识如 sys.manage" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="权限别名" prop="alias">
                            <el-input v-model="formData.alias" placeholder="权限别名" disabled/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="权限状态" prop="disabled">
                            <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="updateData">确 定</el-button>
                    <el-button @click="showUpdateData = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="菜单排序" v-model="showSort" width="30%">
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
<script setup lang="ts" name="platform-sys-menu">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { getAppList, doCreate, doUpdate, doUpdateData, getInfo, getList, doDelete, doSort, doDisable } from '/@/api/platform/sys/menu'
import { handleTree } from '/@/utils/common'
import { buildValidatorData } from '/@/utils/validate'
import IconSelect from '/@/components/IconSelect/index.vue'
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
const unitOptions = ref([]);
const unitList = ref([]);
const showCreate = ref(false)
const showUpdate = ref(false)
const showSort = ref(false)
const appId = ref('')
const apps = ref([])
const showChooseIcon = ref(false)
const showUpdateData = ref(false)
const iconSelectRef = ref(null)

const data = reactive({
    formData: {
        id: '',
        appId: '',
        name: '',
        alias: '',
        permission: '',
        href: '',
        icon: undefined,
        parentId: '',
        parentName: '',
        type: 'menu',
        showit: true,
        disabled: false,
        children: 'false',
        buttons: []
    },
    queryParams: {
        appId: '',
        name: '',
        href: '',
    },
    formRules: {
        permission: [{ required: true, message: "权限标识不能为空", trigger: "blur" }],
        name: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `菜单名称`, show: true, fixed: false },
    { prop: 'href', label: `URL`, show: true, fixed: false },
    { prop: 'type', label: `菜单类型`, show: true, fixed: false },
    { prop: 'permission', label: `权限标识`, show: true, fixed: false, width: '200' },
    { prop: 'showit', label: `是否展示`, show: true, fixed: false, align: 'center' },
    { prop: 'disabled', label: `状态`, show: true, fixed: false, align: 'center' }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        appId: '',
        name: '',
        alias: '',
        permission: '',
        href: '',
        icon: undefined,
        parentId: '',
        parentName: '',
        type: 'menu',
        showit: true,
        disabled: false,
        children: 'false',
        buttons: []
    }
    formEl?.resetFields()
}

const appList = () => {
    getAppList().then((res) => {
        apps.value = res.data.apps
        appId.value = apps.value[0].id
        queryParams.value.appId = apps.value[0].id
        formData.value.appId = apps.value[0].id
        list()
    })
}

const appChange = (val: string) => {
    queryParams.value.appId = val
    list()
}

const showSelectIcon = () => {
    iconSelectRef.value?.reset()
    showChooseIcon.value = true
}

const hideSelectIcon = (event: any) => {
    let elem = event.relatedTarget || event.srcElement || event.target || event.currentTarget
    var className = elem.className
    if (className !== "el-input__inner") {
        showChooseIcon.value = false;
    }
}

const selectedIcon = (val: string) => {
    formData.value.icon = val
    showChooseIcon.value = false
}

// 是否有权限的操作
const formRadioChange = (val: string) => {
    formData.value.children = val
    if (val === 'true') {
        formData.value.buttons = [{name: '',permission: '',key: Date.now()}]
    } else {
        formData.value.buttons = []
    }
}

// 动态添加行的删除
const formRemoveMenu = (menu: any) => {
    let index = formData.value.buttons.indexOf(menu)
    if (index !== -1) {
        formData.value.buttons.splice(index, 1)
    }
}

// 动态添加一行
const formAddMenu = () => {
    formData.value.buttons.push({name: '',permission: '',key: Date.now()})
}

const inputChange_data = () => {
    formData.value.alias = formData.value.permission
}

const validateMenu = (rule: any, row: any, callback: any) => {
      if (typeof row.name === undefined || row.name === '') {
        callback(new Error('权限名称不能为空'))
      } else if (typeof row.permission === undefined || row.permission === '') {
        callback(new Error('权限标识不能为空'))
      } else {
        callback()
      }
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        unitList.value = res.data
        tableData.value = handleTree(res.data) as never
        unitOptions.value = handleTree(res.data) as never
        sortData.value = handleTree(JSON.parse(JSON.stringify(res.data))) as never
    })
}

onMounted(() => {
    appList()
})

// 快速搜索&刷新
const quickSearch = (data: any) => {
    queryParams.value.name = data.keyword
    queryParams.value.href = ''
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

// 上级菜单改变
const parentChange = (id: any) => {
    const idx = unitList.value.findIndex((o) => {
        return o.id == id
    })
    formData.value.parentType = unitList.value[idx]?.type
}

// 启用禁用
const disabledChange = (row: any) => {
    doDisable({ disabled: row.disabled, id: row.id, path: row.path }).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(() => {
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

// 修改按钮
const handleUpdateData = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        showUpdateData.value = true
    })
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除 ' + row.name +' ?').then(() => {
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
            doCreate(JSON.stringify(formData.value),JSON.stringify(formData.value.buttons),appId.value).then((res: any) => {
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
            doUpdate(JSON.stringify(formData.value),JSON.stringify(formData.value.buttons)).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdate.value = false
                resetSearch()
            })
        }
    })
}

// 提交修改
const updateData = () => {
    if (!updateRef.value) return
    updateRef.value.validate((valid) => {
        if (valid) {
            doUpdateData(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showUpdateData.value = false
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
    doSort(ids.toString(), appId.value).then((res: any) => {
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
<style scoped>
.table-col-center {
    text-align: center;
}
</style>