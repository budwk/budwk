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
                    <template v-if="item.prop == 'loginname'" #default="scope">
                        {{ scope.row.username }}({{ scope.row.loginname }})
                    </template>
                    <template v-if="item.prop == 'sendAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" width="70" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看详情" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"></el-button>
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
                                maxlength="30" />
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

        <user-select multiple v-if="showSelect" :show="showSelect"></user-select>

        <el-drawer v-model="showDetail" direction="rtl" title="日志详情" size="50%">

            <template #default>
                <el-form :model="formData" label-width="80px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="操作人" prop="loginname">
                                {{ formData.username }}({{ formData.loginname }})
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="操作时间" prop="createdAt">
                                {{ formatTime(formData.createdAt) }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="操作IP" prop="ip">
                                {{ formData.ip }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="请求路径" prop="url">
                                {{ formData.url }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="执行方法" prop="method">
                                {{ formData.method }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="操作系统" prop="os">
                                {{ formData.os }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="浏览器" prop="browser" class="content">
                                {{ formData.browser }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="请求参数" prop="params" class="content">
                                {{ formData.params }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="响应结果" prop="result" class="content">
                                {{ formData.result }}
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </template>
        </el-drawer>
    </div>
</template>
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getInfo, doCreate, doDelete, getData } from '/@/api/platform/sys/msg'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { addDateRange } from '/@/utils/common'
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

const defaultSort = ref({ prop: "sendAt", order: "descending" });

const data = reactive({
    formData: {
        id: '',
        title: '',
        url: '',
        type: 'USER',
        scope: 'SCOPE',
        note: ''
    },
    queryParams: {
        title: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'sendAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        title: [{ required: true, message: "消息标题不能为空", trigger: ["blur", "change"] }],
        type: [{ required: true, message: "消息类型不能为空", trigger: ["blur", "change"] }],
        scope: [{ required: true, message: "发送范围不能为空", trigger: ["blur", "change"] }],
    }
})

const { formData, formRules, queryParams } = toRefs(data)

const columns = ref([
    { prop: 'title', label: `标题`, show: true, fixed: 'left' },
    { prop: 'type', label: `消息类型`, show: true, fixed: 'left', width: 100 },
    { prop: 'all_num', label: `全部用户数`, show: true, fixed: false },
    { prop: 'unread_num', label: `未读用户数`, show: true, fixed: false, align: 'center' },
    { prop: 'sendAt', label: `发送时间`, show: true, fixed: false, width: 160, sortable: true },
    { prop: 'delFlag', label: `消息状态`, show: true, fixed: false, width: 100 }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        title: '',
        url: '',
        type: 'USER',
        scope: 'SCOPE',
        note: ''
    }
    formEl?.resetFields()
}

const handleView = (row: any) => {
    formData.value = row
    showDetail.value = true
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

const typeChange = () => {
    handleSearch()
}

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
    modal.confirm('确定删除 ' + row.name + '？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
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
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-msg'
}
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