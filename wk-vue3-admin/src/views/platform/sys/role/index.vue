<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="4">
                <el-row>
                    <el-select v-model="unitId" style="width:100%;" @change="unitChange">
                        <el-option v-for="unit in units" :key="unit.id" :value="unit.id" :label="unit.name">
                            <span v-if="'COMPANY' === unit.type.value">&nbsp;&nbsp;&nbsp;</span>
                            {{ unit.name }}
                        </el-option>
                    </el-select>
                </el-row>
                <el-row type="flex" justify="end" v-permission="['sys.manage.role.create']" class="role-create">
                    <el-button link type="primary" @click="handleCreateRole">
                        新增角色|角色组
                    </el-button>
                </el-row>
                <el-row v-if="groups.length > 0">
                    <ul v-for="group in groups" :key="group.id" class="role-group" :index="'group_' + group.id">
                        <li class="role-group-item" @mouseover="enter(group.id)">
                            <div class="group-name">{{ group.name }}</div>
                            <div v-if="btnIndex === group.id" class="operate">
                                <el-button link type="primary" @click="openUpdateGroup(group)"><i
                                        class="fa fa-pencil-square-o" /></el-button>
                                <el-button link type="primary" style="margin-left:2px;" @click="openDeleteGroup(group)">
                                    <i class="fa fa-trash-o" />
                                </el-button>
                            </div>
                        </li>
                        <li v-for="r in group.roles" :key="r.id" class="role-group-item" :index="r.id"
                            @click="clickRole(r)" @mouseover="enter(r.id)">
                            <div :class="roleId === r.id ? 'active role' : 'role'">{{ r.name }}</div>
                            <div v-if="btnIndex === r.id" class="operate">
                                <el-button link type="primary" @click="openUpdateRole(r)"
                                    :disabled="r.code == 'public'"><i class="fa fa-pencil-square-o" />
                                </el-button>
                                <el-button link type="primary" style="margin-left:2px;" @click="openDeleteRole(r)"
                                    :disabled="r.code == 'public'"><i class="fa fa-trash-o" /></el-button>
                            </div>
                        </li>
                    </ul>
                </el-row>
                <el-row v-else style="padding-top:10px;">
                    <el-alert :closable="false">暂无数据</el-alert>
                </el-row>
            </el-col>
            <el-col :span="20">
                <el-tabs v-model="tabIndex" type="card" @tab-click="platTabClick" >
                    <el-tab-pane name="USERLIST" label="用户列表">
                        <el-row>
                        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
                            <el-table-column prop="id" label="用户">
                                <template #default="scope">
                                    {{ scope.row.loginname }}({{
                                            scope.row.username
                                    }})
                                </template>
                            </el-table-column>
                            <el-table-column prop="unitname" label="单位" />
                            <el-table-column prop="postid" label="职务">
                                <template #default="scope">
                                    {{ findOneValue(posts,scope.row.postid,'name') }}
                                </template>
                            </el-table-column>
                            <el-table-column fixed="right" header-align="center" align="center" label="操作" width="180">
                                <template #default="scope">
                                    <el-button v-permission="['sys.manage.role.delete']" link
                                        class="button-delete-color"
                                        :disabled="roleCode === 'sysadmin' && 'superadmin' === scope.row.loginname"
                                        @click="removeUser(scope.row)">
                                        移除
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                            v-model:limit="queryParams.pageSize" @pagination="list" />
                        </el-row>
                    </el-tab-pane>
                    <el-tab-pane v-for="app in apps" :key="app.id" :name="app.id" :label="app.name">

                    </el-tab-pane>
                </el-tabs>
            </el-col>
        </el-row>

        <el-dialog title="新增角色|角色组" v-model="showCreate" width="32%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item prop="type" label="分类">
                    <el-radio-group v-model="formData.type" @change="typeChange">
                        <el-radio label="role">角色</el-radio>
                        <el-radio label="group">角色组</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="groupId" label="所属角色组" :rules="funRules('groupId')">
                    <el-select v-model="formData.groupId" style="width:100%;">
                        <el-option v-for="group in groups" :key="group.id" :value="group.id" :label="group.name">
                            {{ group.name }}
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="name" label="角色名称" :rules="funRules('name')">
                    <el-input v-model="formData.name" maxlength="100" placeholder="请输入角色名" auto-complete="off"
                        tabindex="2" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'group'" prop="name" label="组名" :rules="funRules('name')">
                    <el-input v-model="formData.name" maxlength="100" placeholder="请输入组名" auto-complete="off"
                        tabindex="2" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="code" label="角色代码" :rules="funRules('code')">
                    <el-input v-model="formData.code" maxlength="100" placeholder="角色代码" auto-complete="off"
                        tabindex="3" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="disabled" label="启用状态">
                    <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                        active-color="green" inactive-color="red" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog :title="updateTitle" v-model="showUpdate" width="32%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item v-if="formData.type === 'role'" prop="groupId" label="所属角色组" :rules="funRules('groupId')">
                    <el-select v-model="formData.groupId" style="width:100%;">
                        <el-option v-for="group in groups" :key="group.id" :value="group.id" :label="group.name">
                            {{ group.name }}
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="name" label="角色名称" :rules="funRules('name')">
                    <el-input v-model="formData.name" maxlength="100" placeholder="请输入角色名" auto-complete="off"
                        tabindex="2" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'group'" prop="name" label="组名" :rules="funRules('name')">
                    <el-input v-model="formData.name" maxlength="100" placeholder="请输入组名" auto-complete="off"
                        tabindex="2" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="code" label="角色代码" :rules="funRules('code')">
                    <el-input v-model="formData.code" maxlength="100" placeholder="角色代码" auto-complete="off"
                        tabindex="3" type="text" />
                </el-form-item>
                <el-form-item v-if="formData.type === 'role'" prop="disabled" label="启用状态">
                    <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                        active-color="green" inactive-color="red" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="update" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showUpdate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, watch, toRefs } from 'vue'
import modal from '/@/utils/modal'
import { findOneValue } from '/@/utils/common' 
import { getUnitList, getGroupList, getUserList, getAppList, doCreate, doUpdate, doDelete, getPostList } from '/@/api/platform/sys/role'
import { ElForm } from 'element-plus';

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const posts = ref([])
const apps = ref([])
const appId = ref('')
const units = ref([])
const unitId = ref('')
const roleId = ref('')
const roleCode = ref('')
const roleName = ref('')
const groups = ref([])
const btnIndex = ref('')
const tabIndex = ref('USERLIST')
const showCreate = ref(false)
const showUpdate = ref(false)
const updateTitle = ref('')
const btnLoading = ref(false)
const tableLoading = ref(false)
const tableData = ref([])

const data = reactive({
    formData: {
        id: '',
        unitId: '',
        type: 'role',
        groupId: '',
        name: '',
        code: '',
        disabled: false
    },
    formRules: {
        type: [{ required: true, message: "请选择分类", trigger: ["blur", "change"] }],
    },
    queryParams: {
        roleId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    }
})

const { queryParams, formData, formRules } = toRefs(data)

// 动态条件表单验证
const funRules = (val: string): any => {
    if (formData.value.type === 'role') {
        if ('code' == val) {
            return [{ required: true, message: "角色标识不能为空", trigger: ["blur", "change"] }]
        }
        if ('groupId' == val) {
            return [{ required: true, message: "角色组不能为空", trigger: ["blur", "change"] }]
        }
        if ('name' == val) {
            return [{ required: true, message: "角色名不能为空", trigger: ["blur", "change"] }]
        }
    } else {
        if ('name' == val) {
            return [{ required: true, message: "组名不能为空", trigger: ["blur", "change"] }]
        }
    }
}

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        unitId: '',
        type: 'role',
        groupId: '',
        name: '',
        code: '',
        disabled: false
    }
    formEl?.resetFields()
}

// 表单分类切换
const typeChange = (val: string) => {
    formData.value.name = ''
    formData.value.code = ''
    formData.value.groupId = ''
}

// 鼠标移动
const enter = (index: string) => {
    btnIndex.value = index
}

// tab切换
const platTabClick = () => {
    if (tabIndex.value === 'USERLIST') {
        return
    }
    appId.value = tabIndex.value
    loadDoMenuData()
}

// 加载应用菜单
const loadDoMenuData = () => {

}

// 切换角色
const clickRole = (role: any) => {
    roleId.value = role.id
    roleCode.value = role.code
    roleName.value = role.name
    queryParams.value.roleId = roleId.value
    list()
    platTabClick()
}

// 查询角色下用户列表
const list = () => {
    tableLoading.value = true
    getUserList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 新建角色/角色组
const handleCreateRole = () => {
    resetForm(createRef.value)
    formData.value.type = groups.value.length > 0 ? 'role' : 'group'
    showCreate.value = true
}

// 切换单位
const unitChange = (val: string) => {
    listGroup(unitId.value)
}

const openUpdateGroup = (group: any) => {
    updateTitle.value = '修改角色组'
    formData.value = JSON.parse(JSON.stringify(group))
    formData.value.type = 'group'
    showUpdate.value = true
}

const openDeleteGroup = (group: any) => {
    modal.confirm('确定删除角色组 ' + group.name.toString() + '？').then(() => {
        return doDelete('group', group.id, group.name)
    }).then(() => {
        modal.msgSuccess('删除成功')
        listGroup(unitId.value)
    }).catch(() => { })
}

const openUpdateRole = (role: any) => {
    updateTitle.value = '修改角色组'
    formData.value = JSON.parse(JSON.stringify(role))
    formData.value.type = 'role'
    showUpdate.value = true
}

const openDeleteRole = (role: any) => {
    modal.confirm('确定删除角色 ' + role.name.toString() + '？').then(() => {
        return doDelete('role', role.id, role.name)
    }).then(() => {
        modal.msgSuccess('删除成功')
        listGroup(unitId.value)
    }).catch(() => { })
}

// 提交角色/角色组
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            btnLoading.value = true
            formData.value.unitId = unitId.value
            doCreate(formData.value).then((res: any) => {
                btnLoading.value = false
                modal.msgSuccess(res.msg)
                showCreate.value = false
                listGroup(unitId.value)
            })
        }
    })
}

// 提交角色/角色组
const update = () => {
    if (!updateRef.value) return
    updateRef.value.validate((valid) => {
        if (valid) {
            btnLoading.value = true
            doUpdate(formData.value).then((res: any) => {
                btnLoading.value = false
                modal.msgSuccess(res.msg)
                showUpdate.value = false
                listGroup(unitId.value)
            })
        }
    })
}

// 加载角色组和角色
const listGroup = (unitId: string) => {
    getGroupList(unitId).then((res) => {
        groups.value = res.data
        if (groups.value.length > 0 && groups.value[0].roles.length > 0) {
            roleId.value = groups.value[0].roles[0].id
            roleCode.value = groups.value[0].roles[0].code
            roleName.value = groups.value[0].roles[0].name
            queryParams.value.roleId = roleId.value
            list()
            platTabClick()
        } else {
            roleId.value = ''
            roleCode.value = ''
            roleName.value = ''
            queryParams.value.roleId = ''
            list()
            platTabClick()
        }
    })
}

// 加载职务列表
const listPost = () => {
    getPostList().then((res) => {
        posts.value = res.data
    })
}

// 加载应用列表
const listApp = () => {
    getAppList().then((res) => {
        apps.value = res.data
        if (apps.value.length > 0) {
            appId.value = apps.value[0].id
        }
    })
}

// 列出集团和分公司
const listUnit = () => {
    getUnitList().then((res) => {
        units.value = res.data
        if (units.value.length > 0) {
            unitId.value = units.value[0].id
            listGroup(unitId.value)
        }
    })
}

onMounted(() => {
    listPost()
    listUnit()
    listApp()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-role'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.role-create {
    padding-top: 5px;
}

.role-group {
    list-style: none;
    padding: 0;
    margin: 0;
    width: 100%;
    font-size: var(--el-font-size-base);
    color: var(--el-input-text-color, var(--el-text-color-regular));
}

.role-group li {
    line-height: 35px;
}

.role-group .role {
    padding-left: 20px;
    cursor: pointer;
    font-weight: 400;
}

.active {
    background-color: rgba(36, 118, 224, .1);
    color: #2476e0;
    font-weight: 600;
}

.role-group-item {
    position: relative;
    min-height: 42px;
    line-height: 42px;
}

.group-name {
    color: #999;
    margin-top: 10px;
    padding-left: 5px;
}

.operate {
    position: absolute;
    right: 10px;
    top: 0;
}

.especially {
    width: 100px;
}
</style>