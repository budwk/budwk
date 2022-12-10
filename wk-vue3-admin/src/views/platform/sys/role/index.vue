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
                                <el-button link type="danger" style="margin-left:2px;" @click="openDeleteGroup(group)">
                                    <i class="fa fa-trash-o" />
                                </el-button>
                            </div>
                        </li>
                        <li v-for="r in group.roles" :key="r.id" class="role-group-item" :index="r.id"
                            @click="clickRole(r)" @mouseover="enter(r.id)">
                            <div :class="roleId === r.id ? 'active role' : 'role'">
                                <el-tooltip :content="r.note?r.note:r.name" placement="left">{{ r.name }}</el-tooltip>
                            </div>
                            <div v-if="btnIndex === r.id" class="operate">
                                <el-button link type="primary" @click="openUpdateRole(r)"
                                    :disabled="r.code == 'public'"><i class="fa fa-pencil-square-o" />
                                </el-button>
                                <el-button link type="danger" style="margin-left:2px;" @click="openDeleteRole(r)"
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
                <el-tabs v-model="tabIndex" type="card" @tab-change="platTabClick">
                    <el-tab-pane name="USERLIST" label="用户列表">
                        <el-row class="right-user-add">
                            <el-col :span="19">
                                <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" @submit.prevent>
                                    <el-form-item label="" prop="username">
                                        <el-input v-model="queryParams.username" placeholder="请输入姓名或用户名" clearable
                                            style="width: 220px" @keyup.enter="handleSearch" />
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-col>
                            <el-col :span="5" style="text-align:right">
                                <el-button plain icon="Plus" type="primary" v-permission="['sys.manage.role.update']"
                                    @click="openUser" :disabled="roleCode == 'public'">{{ roleCode =='public'?'公共角色为默认角色':'关联用户到角色'}}</el-button>
                            </el-col>
                        </el-row>
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
                                    {{ findOneValue(posts, scope.row.postid, 'name') }}
                                </template>
                            </el-table-column>
                            <el-table-column fixed="right" header-align="center" align="center" label="操作" width="180">
                                <template #default="scope">
                                    <el-button v-permission="['sys.manage.role.delete']" link type="danger"
                                        icon="Remove" class="button-delete-color"
                                        :disabled="roleCode === 'sysadmin' && 'superadmin' === scope.row.loginname"
                                        @click="cancelLink(scope.row)">
                                        移除
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                            v-model:limit="queryParams.pageSize" @pagination="list" />
                    </el-tab-pane>
                    <el-tab-pane ref="tabPaneRef" v-for="app in apps" :key="app.id" :name="app.id" :label="app.name">
                        <template #label><img v-if="app.icon" :src="app.icon" style="width:12px;height:12px"> {{ app.name }}</template>
                        <el-row :gutter="10" style="margin-bottom: 3px;" class="mb8">
                            <el-col :span="1.5">
                                <el-button icon="Select" type="success" plain @click="menuRoleSelAll('tree_'+app.id)">全选</el-button>
                            </el-col>
                            <el-col :span="1.5">
                                <el-button icon="SemiSelect" type="danger" plain @click="menuRoleSelClear('tree_'+app.id)">清除</el-button>
                            </el-col>
                            <el-col :span="3">
                                <el-switch v-model="treeCheckStrictly" inline-prompt
                                    active-text="勾选联动"
                                    inactive-text="勾选不联动"
                                />
                            </el-col>
                            <el-col :span="16" style="text-align:right">
                                <el-button plain icon="Suitcase" type="primary" v-permission="['sys.manage.role.update']"
                                    @click="saveMenu" :loading="btnLoading">保存权限</el-button>
                            </el-col>
                        </el-row>
                        <el-tree
                            :ref="(el)=>setTreeRef(el,'tree_'+app.id)"
                            :data="doMenuTreeData"
                            :default-checked-keys="doMenuCheckedData"
                            :default-expand-all="true"
                            :highlight-current="true"
                            :check-strictly="!treeCheckStrictly"
                            show-checkbox
                            node-key="id"
                            :props="{children: 'children',label: 'name',class: customNodeClass}"
                            style="padding-top:10px;"
                        >
                            <template #default="{ node, data }">
                                <i v-if="data.type=='menu'" :class="node.expanded?'fa fa-folder-open-o':'fa fa-folder-o'"></i>{{data.name}}
                            </template>
                        </el-tree>
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
                <el-form-item v-if="formData.type === 'role'" prop="note" label="角色说明">
                    <el-input v-model="formData.note" maxlength="100" placeholder="角色说明" type="text" />
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
                <el-form-item v-if="formData.type === 'role'" prop="note" label="角色说明">
                    <el-input v-model="formData.note" maxlength="100" placeholder="角色说明" type="text" />
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

        <el-dialog title="关联用户" v-model="showUser" width="50%">
            <el-row class="link-user-tip">
                <el-alert type="success" :closable="false">* 仅列出所属公司/直属部门/子部门用户</el-alert>
            </el-row>
            <el-row>
                <el-form :model="userQueryParams" ref="queryUserRef" :inline="true" label-width="68px" @submit.prevent>
                    <el-form-item label="" prop="username">
                        <el-input v-model="userQueryParams.username" placeholder="请输入姓名或用户名" clearable
                            style="width: 220px" @keyup.enter="handleUserSearch" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleUserSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetUserSearch">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
            <el-table :data="userTableData" row-key="id" @selection-change="linkUserChange">
                <el-table-column type="selection" width="55" />
                <el-table-column prop="id" label="用户">
                    <template #default="scope">
                        {{ scope.row.loginname }}({{
                                scope.row.username
                        }})
                    </template>
                </el-table-column>
                <el-table-column prop="unitname" label="单位" />
                <el-table-column prop="postId" label="职务">
                    <template #default="scope">
                        {{ findOneValue(posts, scope.row.postid, 'name') }}
                    </template>
                </el-table-column>
            </el-table>
            <pagination :total="userQueryParams.totalCount" v-model:page="userQueryParams.pageNo"
                v-model:limit="userQueryParams.pageSize" @pagination="listUser" />

            <template #footer>

                <div class="dialog-footer">
                    <el-button type="primary" @click="linkUser" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showUser = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-sys-role">
import { nextTick, onMounted, reactive, ref, watch, toRefs } from 'vue'
import modal from '/@/utils/modal'
import { getUnitList, getGroupList, getUserList, getAppList, doCreate, doUpdate, doDelete, getPostList, getQueryUserList, doLinkUser, doUnLinkUser, getMenuList, doMenu } from '/@/api/platform/sys/role'
import { ElForm, ElTabPane } from 'element-plus';
import { handleTree } from '/@/utils/common';

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()
const queryUserRef = ref<InstanceType<typeof ElForm>>()

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
const showUser = ref(false)
const updateTitle = ref('')
const btnLoading = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const userTableData = ref([])
const linkUserIds = ref([])
const linkUserNames = ref([])
const doMenuData = ref([])
const doMenuTreeData = ref([])
const doMenuCheckedData = ref([])
const treeCheckStrictly = ref(false)

const data = reactive({
    formData: {
        id: '',
        unitId: '',
        type: 'role',
        groupId: '',
        name: '',
        note: '',
        code: '',
        disabled: false
    },
    formRules: {
        type: [{ required: true, message: "请选择分类", trigger: ["blur", "change"] }],
    },
    queryParams: {
        roleId: '',
        username: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    },
    userQueryParams: {
        roleId: '',
        unitId: '',
        username: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    }
})

const { queryParams, formData, formRules, userQueryParams } = toRefs(data)

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
        note: '',
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

// 高级搜索
const handleSearch = () => {
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

// 搜索用户
const handleUserSearch = () => {
    listUser()
}

// 重置搜索
const resetUserSearch = () => {
    queryUserRef.value?.resetFields()
    listUser()
}

// 打开查询用户
const openUser = () => {
    if (!roleId.value || roleId.value === '') {
        modal.msgError('请先选择角色！')
        return
    }
    showUser.value = true
    listUser()
}

// 查询用户
const listUser = () => {
    userQueryParams.value.roleId = roleId.value
    userQueryParams.value.unitId = unitId.value
    getQueryUserList(userQueryParams.value).then((res) => {
        userTableData.value = res.data.list as never
        userQueryParams.value.totalCount = res.data.totalCount as never
    })
}

// 用户选择
const linkUserChange = (val: any) => {
    linkUserIds.value = val.map(obj=>obj.id)
    linkUserNames.value = val.map(obj=>obj.loginname + '(' + obj.username + ')')
}

// 关联用户
const linkUser = () => {
    if(linkUserIds.value.length==0){
        modal.msgWarning('没有选择用户')
        return
    }
    doLinkUser(roleId.value,roleCode.value,linkUserIds.value.toString(),linkUserNames.value.toString()).then((res)=>{
        modal.msgSuccess(res.msg)
        queryParams.value.pageNo = 1
        list()
        showUser.value = false
    })
}

// 取消关联
const cancelLink = (row: any) => {
    const title = row.loginname + '(' + row.username + ')'
    modal.confirm('确定从角色 ' + roleName.value + ' 移除 ' + title+ ' ？').then(() => {
        return doUnLinkUser(roleId.value,roleCode.value,row.id,row.tite)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
    }).catch(() => { })
}


// 加载应用菜单
const loadDoMenuData = () => {
    doMenuCheckedData.value = []
    getMenuList(roleId.value, appId.value).then((res)=>{
        doMenuData.value = JSON.parse(JSON.stringify(res.data.menuList)) as never
        doMenuTreeData.value = handleTree(res.data.menuList) as never
        doMenuCheckedData.value = res.data.menuIds as never
        nextTick(() => {
            changeTreeClass()
        })
    })
}

// 自定义节点样式
const customNodeClass = (data: any, node: any) => {
    if (data.type == 'data') {
        return 'menu-is-data'
    }
    return null
}

// 按钮列缩进
const changeTreeClass = () => {
    const levelName = document.getElementsByClassName('menu-is-data')
    for (let i = 0; i < levelName.length; i++) {
        levelName[i].parentNode.style.cssText = 'padding-left: 30px;'
    }
}

const treeRefs = {}

// 动态树ref
const setTreeRef = (el: any, key: string) => {
    if(el){
        treeRefs[key] = el
    }
}

// 全选树节点
const menuRoleSelAll = (val: string) => {
    treeRefs[val].setCheckedNodes(doMenuData.value)
}

// 清空选择
const menuRoleSelClear = (val: string) =>{
    treeRefs[val].setCheckedNodes([])
}

// 保存权限
const saveMenu = () => {
    if (!roleId.value || roleId.value === '') {
        modal.msgError('请先选择角色！')
        return
    }
    let ids = treeRefs['tree_'+appId.value].getCheckedKeys()
    btnLoading.value = true
    doMenu({ roleId: roleId.value, roleCode: roleCode.value, appId: appId.value, menuIds: ids.toString() }).then((res)=>{
        btnLoading.value = false
        modal.msgSuccess('保存成功')
    })
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
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.link-user-tip {
    padding-bottom: 10px;
}

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
</style>
<style>

.menu-is-data {
    float: left;
}

.menu-is-data > .el-tree-node__content {
  background-color: #ffffff;
  padding-left: 0px !important;
}

</style>