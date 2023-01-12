<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="4">
                <div class="head-container">
                    <el-select v-model="siteId" class="m-2" placeholder="切换站点" @change="siteChange">
                    <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
                </div>
                <div class="head-container">
                    <el-tree
                        style="margin-top:10px;"
:data="channelOptions" :props="{ label: 'name', children: 'children' }"
                        :expand-on-click-node="false" ref="channelTreeRef"
                        highlight-current default-expand-all @node-click="handleNodeClick">
                        <template #default="{ node, data }">
                            <i v-if="data.type && data.type.value === 'GROUP'" class="fa fa-building" />
                            <i v-if="data.type && data.type.value === 'COMPANY'" class="fa fa-home" />
                            {{ data.name }}
                        </template>
                    </el-tree>
                </div>
            </el-col>
            <el-col :span="20">
                <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
                    <el-form-item label="文章标题" prop="title">
                        <el-input
v-model="queryParams.title" placeholder="请输入文章标题" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>

                <el-row :gutter="10" class="mb8">
                    <el-col :span="1.5">
                        <el-button
type="primary" plain icon="Plus" @click="handleCreate"
                            v-permission="['cms.content.article.create']">新增</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
type="success" plain icon="Edit" :disabled="single" @click="handleUpdateMore"
                            v-permission="['cms.content.article.update']">修改</el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
type="danger" plain icon="Delete" :disabled="multiple" @click="handleDeleteMore"
                            v-permission="['cms.content.article.delete']">删除</el-button>
                    </el-col>
                    <right-toolbar
v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                        @quickSearch="quickSearch" />
                </el-row>

                <el-table v-loading="tableLoading" :data="tableData" row-key="id" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="50" fixed="left" />
                    <el-table-column type="expand" fixed="left">
                        <template #default="scope">
                            <el-row class="expand-row" :gutter="5">
                                <el-col :span="5">
                                    职务: {{ findPostName(scope.row.postId) }}
                                </el-col>
                                <el-col :span="5">
                                    性别:
                                    <span v-if="scope.row.sex == 0">未知</span>
                                    <span v-if="scope.row.sex == 1">男</span>
                                    <span v-if="scope.row.sex == 2">女</span>
                                </el-col>
                                <el-col :span="5">
                                    Email: {{ scope.row.email }}
                                </el-col>
                                <el-col :span="5">
                                    登录时间: {{ formatTime(scope.row.loginAt) }}
                                </el-col>
                                <el-col :span="5">
                                    登录IP: {{ scope.row.loginIp }}
                                </el-col>
                                <el-col :span="5">
                                    创建人: <span v-if="scope.row.createdByUser">{{
                                            scope.row.createdByUser.loginname
                                    }}({{
        scope.row.createdByUser.username
}})</span>
                                </el-col>
                                <el-col :span="5">
                                    创建时间: {{ formatTime(scope.row.createdAt) }}
                                </el-col>
                                <el-col :span="5">
                                    修改人: <span v-if="scope.row.updatedByUser">{{
                                            scope.row.updatedByUser.loginname
                                    }}({{
        scope.row.updatedByUser.username
}})</span>
                                </el-col>
                                <el-col :span="6">
                                    修改时间: {{ formatTime(scope.row.updatedAt) }}
                                </el-col>
                            </el-row>
                        </template>
                    </el-table-column>
                    <template v-for="(item, idx) in columns" :key="idx">
                        <el-table-column
:prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                            :show-overflow-tooltip="false" :align="item.align" :width="item.width">
                            <template v-if="item.prop == 'unit'" #default="scope">
                                <span v-if="scope.row.unit">{{ scope.row.unit.name }}</span>
                            </template>
                            <template v-if="item.prop == 'createdAt'" #default="scope">
                                <span>{{ formatTime(scope.row.createdAt) }}</span>
                            </template>
                            <template v-if="item.prop == 'disabled'" #default="scope">
                                <el-switch
v-model="scope.row.disabled" :active-value="false" :inactive-value="true"
                                    active-color="green" inactive-color="red" @change="disabledChange(scope.row)" />
                            </template>
                        </el-table-column>
                    </template>
                    <el-table-column
fixed="right" header-align="center" align="center" label="操作" width="120"
                        class-name="small-padding fixed-width">
                        <template #default="scope">
                            <el-tooltip content="重置密码" placement="top">
                                <el-button
link type="primary" icon="RefreshRight" @click="handleReset(scope.row)"
                                    v-permission="['sys.manage.unit.update']"></el-button>
                            </el-tooltip>
                            <el-tooltip content="修改" placement="top">
                                <el-button
link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                                    v-permission="['sys.manage.unit.update']"></el-button>
                            </el-tooltip>
                            <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                                <el-button
link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                                    v-permission="['sys.manage.unit.delete']"></el-button>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                </el-table>
                <pagination
:total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                    v-model:limit="queryParams.pageSize" @pagination="list" />
            </el-col>
        </el-row>        

        <el-dialog title="新增文章" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    
                    <el-col :span="12">
                        <el-form-item label="所属站点" prop="siteId">
                            <el-select v-model="siteId" class="m-2" placeholder="所属站点" disabled style="width:100%">
                                <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>

                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入栏目名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div style="border: 1px solid #ccc">
                            <Toolbar
                                style="border-bottom: 1px solid #ccc"
                                :editor="editorRef"
                                :defaultConfig="toolbarConfig"
                                :mode="editorMode"
                            />
                            <Editor
                                style="height: 500px; overflow-y: hidden;"
                                v-model="formData.note"
                                :defaultConfig="editorConfig"
                                :mode="editorMode"
                                @onCreated="handleEditorCreated"
                            />
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目状态" prop="disabled">
                            <el-switch
v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
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
    </div>
</template>

<script setup lang="ts" name="platform-cms-article">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef, toRefs } from 'vue'
import modal from '/@/utils/modal'
import { getChannelList, doCreate, doUpdate, getList, doDelete } from '/@/api/platform/cms/article'
import { getSiteList } from '/@/api/platform/cms/channel'
import { ElForm } from 'element-plus'
import { handleTree } from '/@/utils/common'

// 富文本编辑器
const editorRef = shallowRef()
const editorMode = ref('default')
const toolbarConfig = {}
const editorConfig = { placeholder: '请输入内容...' }
// 组件销毁时, 也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
})

const handleEditorCreated = (editor: any) => {
    editorRef.value = editor // 记录 editor 实例，重要！
}

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const showCreate = ref(false)
const showUpdate = ref(false)
const channelOptions = ref([])
const single = ref(true)
const multiple = ref(true)
const sites = ref([])
const siteId = ref('')
const tableLoading = ref(false)
const tableData = ref([])
const dateRange = ref([])
const showSearch = ref(true)
const ids = ref([])
const names = ref([])

const data = reactive({
    formData: {
        id: '',
        siteId: '',
        channelId: '',
        disabled: false,
        note: ''
    },
    queryParams: {
        siteId: '',
        channelId: '',
        title: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'updatedAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        title: [{ required: true, message: "文章标题不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'username', label: `用户姓名`, show: true, fixed: 'left' },
    { prop: 'loginname', label: `用户名`, show: true, fixed: false },
    { prop: 'serialNo', label: `用户编号`, show: true, fixed: false, align: 'center' },
    { prop: 'unit', label: `所属单位`, show: true, fixed: false },
    { prop: 'mobile', label: `手机号`, show: true, fixed: false, width: 120 },
    { prop: 'disabled', label: `用户状态`, show: true, fixed: false, align: 'center' },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false, width: 160, align: 'center' }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        siteId: '',
        channelId: '',
        disabled: false,
        note: ''
    }
    formEl?.resetFields()
}

// 列表多选
const handleSelectionChange =(selection: any) => {
    ids.value = selection.map(item => item.id)
    names.value = selection.map(item => item.username)
    single.value = selection.length != 1
    multiple.value = !selection.length
}

const siteChange = (val: string) => {
    queryParams.value.siteId = val
    channelList()
}

const handleNodeClick = (data: any) => {
    queryParams.value.channelId = data.id
    formData.value.channelId = data.id
    list()
}   

// 快速搜索&刷新
const quickSearch = () => {
    list()
}

// 高级搜索
const handleSearch = () => {
    list()
}

// 重置搜索
const resetSearch = () => {
    dateRange.value = []
    queryRef.value?.resetFields()
    list()
}

const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

const siteList = () => {
    getSiteList().then((res) => {
        sites.value = res.data
        siteId.value = sites.value[0].id
        queryParams.value.siteId = sites.value[0].id
        formData.value.siteId = sites.value[0].id
        channelList()
    })
}

const channelList = () => {
    getChannelList(siteId.value).then((res) => {
        channelOptions.value = handleTree(res.data) as never
    })
}


// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    showCreate.value = true
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除 ' + row.title + '？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}


// 修改按钮
const handleUpdate = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        showUpdate.value = true
    })
}

// 批量修改
const handleUpdateMore = () => {
    handleUpdate({id: ids.value.toString()})
}

// 批量删除
const handleDeleteMore = () => {
    modal.confirm('确定删除 ' + names.value.toString() + '？').then(() => {
        return doDeleteMore(ids.value.toString(), names.value.toString())
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => {
        list()
    })
}

const create = () => {
    
}

onMounted(() => {
    siteList()
})
</script>

<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>