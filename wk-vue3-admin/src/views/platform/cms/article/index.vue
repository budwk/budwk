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
                        style="margin-top: 10px;"
:data="channelOptions" :props="{ label: 'name', children: 'children' }"
                        :expand-on-click-node="false" ref="channelTreeRef"
                        highlight-current default-expand-all @node-click="handleNodeClick">
                        <template #default="{ node, data }">
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
                    <template v-for="(item, idx) in columns" :key="idx">
                        <el-table-column
:prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                            :show-overflow-tooltip="false" :align="item.align" :width="item.width">
                            <template v-if="item.prop == 'publishAt'" #default="scope">
                                <span>{{ formatTime(scope.row.publishAt) }}</span> ~ 
                                <span>{{ formatTime(scope.row.endAt) }}</span>
                            </template>
                            <template v-if="item.prop == 'title'" #default="scope">
                                <el-button link type="primary" @click="view(scope.row.id)">{{ scope.row.title }}</el-button>
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

        <el-dialog title="新增文章" v-model="showCreate" width="70%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    
                    <el-col :span="12">
                        <el-form-item label="文章标题" prop="title">
                            <el-input v-model="formData.title" placeholder="请输入文章标题" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属站点" prop="siteId">
                            <el-select v-model="siteId" class="m-2" placeholder="所属站点" disabled style="width:100%">
                                <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="外链地址" prop="url">
                            <el-input v-model="formData.url" placeholder="外链地址，有则填写" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属栏目" prop="channelName" class="label_font">
                            {{ formData.channelName }}
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="文章作者" prop="author">
                            <el-input v-model="formData.author" placeholder="请输入文章作者" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="发布状态" prop="disabled">
                            <el-switch
v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="发布时间" prop="publishAt">
                            <el-date-picker v-model="formData.publishAt" type="date" 
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="x"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="截至时间" prop="endAt">
                            <el-date-picker v-model="formData.endAt" type="date" 
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="x"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="文章简介" prop="info">
                            <el-input v-model="formData.info" :rows="2" type="textarea" placeholder="请输入文章简介" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="标题图" prop="picUrl" class="label_font">
                            <el-upload
action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.picUrl" :src="platformInfo.AppFileDomain + formData.picUrl" class="_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div style="border: 1px solid #ccc">
                            <Toolbar
                                v-if="showCreate"
                                style="border-bottom: 1px solid #ccc"
                                :editor="editorRef"
                                :defaultConfig="toolbarConfig"
                                :mode="editorMode"
                            />
                            <Editor
                                v-if="showCreate"
                                style="height: 500px; overflow-y: hidden;"
                                v-model="formData.content"
                                :defaultConfig="editorConfig"
                                :mode="editorMode"
                                @onCreated="handleEditorCreated"
                            />
                        </div>
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

        <el-dialog title="修改文章" v-model="showUpdate" width="70%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    
                    <el-col :span="12">
                        <el-form-item label="文章标题" prop="title">
                            <el-input v-model="formData.title" placeholder="请输入文章标题" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属站点" prop="siteId">
                            <el-select v-model="siteId" class="m-2" placeholder="所属站点" disabled style="width:100%">
                                <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="外链地址" prop="url">
                            <el-input v-model="formData.url" placeholder="外链地址，有则填写" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属栏目" prop="channelName" class="label_font">
                            {{ formData.channelName }}
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="文章作者" prop="author">
                            <el-input v-model="formData.author" placeholder="请输入文章作者" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="发布状态" prop="disabled">
                            <el-switch
v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="发布时间" prop="publishAt">
                            <el-date-picker v-model="formData.publishAt" type="date" 
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="x"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="截至时间" prop="endAt">
                            <el-date-picker v-model="formData.endAt" type="date" 
                            start-placeholder="开始日期" end-placeholder="结束日期" value-format="x"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="文章简介" prop="info">
                            <el-input v-model="formData.info" :rows="2" type="textarea" placeholder="请输入文章简介" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="标题图" prop="picUrl" class="label_font">
                            <el-upload
action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.picUrl" :src="platformInfo.AppFileDomain + formData.picUrl" class="_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div style="border: 1px solid #ccc">
                            <Toolbar
                                v-if="showUpdate"
                                style="border-bottom: 1px solid #ccc"
                                :editor="editorRef2"
                                :defaultConfig="toolbarConfig"
                                :mode="editorMode"
                            />
                            <Editor
                                v-if="showUpdate"
                                style="height: 500px; overflow-y: hidden;"
                                v-model="formData.content"
                                :defaultConfig="editorConfig"
                                :mode="editorMode"
                                @onCreated="handleEditor2Created"
                            />
                        </div>
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


        <el-drawer v-model="showDetail" direction="rtl" title="预览文章" size="50%">

            <template #default>
                <el-form :model="formData" label-width="80px">
                    <el-row>
                        <el-col :span="24">
                            <el-form-item label="文章标题" prop="title">
                                <span>{{ formData.title }}</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="文章作者" prop="author">
                                <span>{{ formData.author }}</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="发布时间" prop="publishAt">
                                <span>{{ formatTime(formData.publishAt) }}</span>
                                <span style="padding-left: 10px;padding-right: 10px;">~</span>  
                                <span>{{ formatTime(formData.endAt) }}</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="文章简介" prop="info">
                                {{ formData.info }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="标题图" prop="picUrl">
                                <img v-if="formData.picUrl" :src="platformInfo.AppFileDomain +formData.picUrl" style="width: 100px;height: 100px;"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="文章内容" prop="content">
                                <div v-html="formData.content"></div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </template>
        </el-drawer>
    </div>
</template>

<script setup lang="ts" name="platform-cms-article">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { IEditorConfig } from '@wangeditor/editor'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef, toRefs } from 'vue'
import modal from '/@/utils/modal'
import { fileUpload } from '/@/api/common'
import { getChannelList, getInfo, doCreate, doUpdate, getList, doDelete } from '/@/api/platform/cms/article'
import { getSiteList } from '/@/api/platform/cms/channel'
import { ElForm, ElTree } from 'element-plus'
import { handleTree } from '/@/utils/common'
import { useUserInfo } from '/@/stores/userInfo'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { platformUploadImageUrl } from '/@/api/common'

const platformInfo = usePlatformInfo()
const userInfo = useUserInfo()
// 富文本编辑器
const editorRef = shallowRef()
const editorRef2 = shallowRef()
const editorMode = ref('default')
const editorMode2 = ref('default')
const toolbarConfig = {modalAppendToBody: true}
type InsertFnType = (url: string, alt: string, href: string) => void
const editorConfig: Partial<IEditorConfig> = { 
    MENU_CONF: {
        uploadImage: {
            server: import.meta.env.VITE_AXIOS_BASE_URL + platformUploadImageUrl,
            fieldName: 'Filedata',
            headers: {
                "wk-user-token": userInfo.getToken()
            },
            // 单个文件上传成功之后
            onSuccess(file: File, res: any) {
                console.log(`${file.name} 上传成功`, res)
            },
            customInsert(res: any, insertFn: InsertFnType) { 
                if(res.code == 0 ) {
                    insertFn(platformInfo.AppFileDomain + res.data.url, res.data.filename, res.data.url)
                }
            },
        }
    }
}

// 组件销毁时, 也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor){
        editor.destroy()
    }
    const editor2 = editorRef2.value
    if (editor2){
        editor2.destroy()
    }
})

const handleEditorCreated = (editor: any) => {
    editorRef.value = editor // 记录 editor 实例，重要！
}
const handleEditor2Created = (editor: any) => {
    editorRef2.value = editor // 记录 editor 实例，重要！
}

const channelTreeRef = ref<InstanceType<typeof ElTree>>()
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
const channelId = ref('')
const channelName = ref('')
const showDetail = ref(false)

const data = reactive({
    formData: {
        id: '',
        siteId: '',
        title: '',
        channelId: '',
        channelName: '',
        info: '',
        content: '',
        author: '',
        disabled: false,
        publishAt: 0,
        endAt: 0,
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
        publishAt: [{ required: true, message: "请选择发布时间", trigger: "blur" }],
        endAt: [{ required: true, message: "请选择截至时间", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'title', label: `文章标题`, show: true, fixed: false },
    { prop: 'author', label: `文章作者`, show: true, fixed: false },
    { prop: 'publishAt', label: `发布时间`, show: true, fixed: false, width: 200, align: 'center' },
    { prop: 'disabled', label: `文章状态`, show: true, fixed: false, align: 'center' }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        siteId: '',
        channelId: channelId.value,
        channelName: channelName.value,
        info: '',
        content: '',
        title: '',
        author: userInfo.user.username,
        disabled: false,
        publishAt: new Date().getTime(),
        endAt: 4102415999000,
        note: ''
    }
    formEl?.resetFields()
}

const beforeUpload = (file: any) => {
    if (file.type.indexOf("image/") == -1) {
        modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。")
    }
}

const uploadPic = (file: any) => {
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUpload(f,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.picUrl = res.data.url
        }
    })
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
    channelId.value = data.id
    channelName.value = data.name
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
        if(res.data && res.data.length > 0){
            channelId.value = res.data[0].id
            channelName.value = res.data[0].name
            queryParams.value.channelId = 'root'
        }
        const treeData = handleTree(res.data)
        const rootData = [{
            name: '所有栏目',
            id: 'root'
        }]
        channelOptions.value = rootData.concat(treeData) as never
        list()
    })
}


// 新增按钮
const handleCreate = (row: any) => {
    if(channelId.value == 'root') {
        modal.msgWarning('请选择栏目')
        return
    }
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

// 预览文章
const view = (id: string) => {
    getInfo(id).then((res: any) => {
        formData.value = res.data
        showDetail.value = true
    })
}

// 提交新增
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            formData.value.siteId = siteId.value
            doCreate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreate.value = false
                list()
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
                list()
            })
        }
    })
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

<style scoped>
._img {
    width: 50px;
    height: 50px;   
}
.label_font {
    font-weight: 700;
}
</style>