<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-radio-group v-model="queryParams.type" placeholder="消息类型" @change="typeChange">
                    <el-radio-button :label="''">全部消息</el-radio-button>
                    <el-radio-button :label="item.value" v-for="(item) in types" :key="item.value">{{ item.text }}
                    </el-radio-button>
                </el-radio-group>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['sys.manage.msg.create']">发送消息
                </el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="false" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过标题搜索" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            :default-sort="defaultSort">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width" :sortable="item.sortable">
                    <template v-if="item.prop == 'delFlag'" #default="scope">
                        <el-tag v-if="scope.row.delFlag" type="danger">已撤销</el-tag>
                        <el-tag v-else type="success">已发送</el-tag>
                    </template>
                    <template v-if="item.prop == 'all_num'" #default="scope">
                        <el-button link type="primary" @click="viewUserList('all', scope.row.id)">{{ scope.row.all_num
                        }}
                        </el-button>
                    </template>
                    <template v-if="item.prop == 'unread_num'" #default="scope">
                        <el-button link type="primary" @click="viewUserList('unread', scope.row.id)">{{
                                scope.row.unread_num
                        }}
                        </el-button>
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        {{ findOneValue(types, scope.row.value, 'text') }}
                    </template>
                    <template v-if="item.prop == 'sendAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" width="80" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看详情" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="撤销消息" placement="top">
                        <el-button link type="danger" icon="DocumentDelete"
                        @click="handleDelete(scope.row)" v-permission="['sys.manage.msg.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="发送消息" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item label="消息标题" prop="title">
                            <el-input v-model="formData.title" placeholder="请输入消息标题" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="URL路径" prop="url">
                            <el-input v-model="formData.url" placeholder="跳转路径" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="消息类型" prop="type">
                            <el-radio-group v-model="formData.type">
                                <el-radio v-for="obj in types" :key="obj.value" :label="obj.value">{{ obj.text }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="发送范围" prop="scope">
                            <el-radio-group v-model="formData.scope">
                                <el-radio-button v-for="obj in scopes" :key="obj.value" :label="obj.value">{{ obj.text
                                }}</el-radio-button>
                            </el-radio-group>
                            <el-tag v-if="'ALL' == formData.scope" style="margin-left:20px;">不含已被禁用用户</el-tag>

                        </el-form-item>
                    </el-col>   
                    <el-col :span="24" v-if="'SCOPE' == formData.scope" label="指定用户">
                        <el-row>
                            <el-button plain icon="Search" @click="openSelect">选择用户</el-button>
                            <el-button plain icon="Delete" type="danger" @click="clearSelect">清除选择</el-button>
                        </el-row>
                        <el-table :data="userTableData" style="width: 100%;margin-top:5px;margin-bottom: 20px;"
                            size="small" height="200">
                            <el-table-column fixed prop="loginname" label="用户名">
                                <template #default="scope">
                                    {{ scope.row.loginname }} ({{ scope.row.username }})
                                </template>
                            </el-table-column>
                            <el-table-column prop="email" label="EMail" :show-overflow-tooltip="true" />
                            <el-table-column prop="mobile" label="手机号" />
                            <el-table-column prop="unit" label="所属单位">
                                <template #default="scope">
                                    <span v-if="scope.row.unit != null">
                                        {{ scope.row.unit.name }}
                                    </span>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="消息内容" prop="note">
                            <el-input v-model="formData.note" placeholder="消息内容" type="textarea" :rows="5"
                                maxlength="500" />
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

        <user-select :multiple="true" v-model="showSelect" @selected="selectUsers"></user-select>

        <el-drawer v-model="showViewUser" direction="rtl" :title="viewUserType == 'all' ? '全部用户' : '未读用户'" size="50%">
            <template #default>
                <el-tag style="margin-bottom:10px;" type="warning">若用户数量与列表显示不一致，可能是用户已被删除造成的</el-tag>
                <el-table v-loading="tableLoading" :data="tableUserData" row-key="id" stripe
                    :default-sort="defaultSort">
                    <el-table-column fixed prop="loginname" label="用户名" width="150" />
                    <el-table-column prop="username" label="姓名" width="120" />
                    <el-table-column prop="mobile" label="手机号" />
                    <el-table-column prop="email" label="EMail" />
                    <el-table-column prop="unitname" label="所属单位" />
                </el-table>
                <el-row>
                    <pagination :total="queryUserParams.totalCount" v-model:page="queryUserParams.pageNo"
                        v-model:limit="queryUserParams.pageSize" @pagination="listUser" />
                </el-row>
            </template>
        </el-drawer>
        <el-drawer v-model="showDetail" direction="rtl" title="消息详情" size="50%">

            <template #default>
                <el-form :model="formData" label-width="80px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="发送人" prop="createdByUser">
                                <span v-if="formData.createdByUser">{{ formData.createdByUser.username}}({{ formData.createdByUser.loginname}})</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="发送时间" prop="sendAt">
                                {{ formatTime(formData.sendAt) }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="标题" prop="title">
                                {{ formData.title }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="内容" prop="note" class="content">
                                {{ formData.note }}
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </template>
        </el-drawer>
    </div>
</template>
<script setup lang="ts" name="platform-sys-msg">
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import modal from '/@/utils/modal'
import { getList, getInfo, doCreate, doDelete, getData, getViewUserList } from '/@/api/platform/sys/msg'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { addDateRange, findOneValue } from '/@/utils/common'
import UserSelect from '/@/components/UserSelect/index.vue'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()

const showSearch = ref(true)
const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const apps = ref([])
const types = ref([])
const scopes = ref([])
const showDetail = ref(false)
const userTableData = ref([])
const showSelect = ref(false)
const showViewUser = ref(false)
const viewUserType = ref('')
const tableUserData = ref([])

const defaultSort = ref({ prop: "sendAt", order: "descending" })


const data = reactive({
    formData: {
        id: '',
        title: '',
        url: '',
        type: 'USER',
        scope: 'SCOPE',
        note: '',
        users: ''
    },
    queryParams: {
        title: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'sendAt',
        pageOrderBy: 'descending'
    },
    queryUserParams: {
        type: '',
        id: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        title: [{ required: true, message: "消息标题不能为空", trigger: ["blur", "change"] }],
        type: [{ required: true, message: "消息类型不能为空", trigger: ["blur", "change"] }],
        scope: [{ required: true, message: "发送范围不能为空", trigger: ["blur", "change"] }],
        note: [{ required: true, message: "消息内容不能为空", trigger: ["blur", "change"] }],
    }
})

const { formData, formRules, queryParams, queryUserParams } = toRefs(data)

const columns = ref([
    { prop: 'title', label: `标题`, show: true, fixed: 'left' },
    { prop: 'type', label: `消息类型`, show: true, fixed: 'left', width: 100 },
    { prop: 'all_num', label: `全部用户`, show: true, fixed: false, align: 'center', width: 100 },
    { prop: 'unread_num', label: `未读用户`, show: true, fixed: false, align: 'center', width: 100 },
    { prop: 'sendAt', label: `发送时间`, show: true, fixed: false, width: 180, sortable: true },
    { prop: 'delFlag', label: `消息状态`, show: true, fixed: false, width: 120 }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        title: '',
        url: '',
        type: 'USER',
        scope: 'SCOPE',
        note: '',
        users: ''
    }
    formEl?.resetFields()
}

const handleView = (row: any) => {
    formData.value = row
    showDetail.value = true
}

// 查询消息列表
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 查询用户列表
const listUser = () => {
    tableLoading.value = true
    getViewUserList(queryUserParams.value).then((res) => {
        tableLoading.value = false
        tableUserData.value = res.data.list as never
        queryUserParams.value.totalCount = res.data.totalCount as never
    }).catch(() => {
        tableLoading.value = false
    })
}

// 显示用户列表
const viewUserList = (type: string, id: string) => {
    viewUserType.value = type
    showViewUser.value = true
    queryUserParams.value.type = type
    queryUserParams.value.id = id
    listUser()
}

// 切换消息类型
const typeChange = () => {
    handleSearch()
}

// 远程排序
const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    handleSearch()
}

// 刷新
const handleSearch = () => {
    queryParams.value.pageNo = 1
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

// 刷新&快速搜索
const quickSearch = (data: any) => {
    queryParams.value.title = data.keyword
    queryParams.value.pageNo = 1
    list()
}

// 初始化字典数据
const getInitData = () => {
    getData().then((res) => {
        apps.value = res.data.apps
        types.value = res.data.types
        scopes.value = res.data.scopes
    })
}

const openSelect = () => {
    showSelect.value = true
}

const clearSelect = () => {
    userTableData.value = []
}

const selectUsers = (row: any) => {
    userTableData.value = row
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
    modal.confirm('确定撤销 ' + row.title + '？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('撤销成功')
    }).catch(() => { })
}


// 提交新增
const create = () => {
    const users = userTableData.value.map(row => row.id)
    if (formData.value.scope === 'SCOPE' && users.length < 1) {
        modal.msgWarning('请选择发送对象')
        return
    }
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            formData.value.users = users.toString()
            doCreate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreate.value = false
                queryParams.value.pageNo = 1
                list()
            })
        }
    })
}

onMounted(() => {
    list()
    getInitData()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.content {
    white-space: normal;
    word-break: break-all;
}
</style>