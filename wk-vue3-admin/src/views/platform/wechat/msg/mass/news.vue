<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.msg.mass.news.create']">新建图文
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'picurl'" #default="scope">
                        <img v-if="scope.row.picurl != ''" :src="platformInfo.AppFileDomain + scope.row.picurl" width="30"
                            height="30">
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.msg.mass.news.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>
        <el-dialog title="新增图文" v-model="showCreate" width="60%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="160px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="title" label="所属公众号" class="label-font-weight">
                            {{ formData.wxname }}
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="title" label="图文标题">
                            <el-input v-model="formData.title" maxlength="255" placeholder="图文标题" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="author" label="图文作者">
                            <el-input v-model="formData.author" maxlength="50" placeholder="图文作者" auto-complete="off"
                                tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="picurl" label="缩略图(64kb以内)" class="label-font-weight">
                            <el-upload action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false"
                                >
                                <img v-if="formData.picurl" :src="platformInfo.AppFileDomain + formData.picurl"
                                    class="_img" />
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
                        <el-form-item prop="digest" label="摘要">
                            <el-input v-model="formData.digest" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="content_source_url" label="原文链接">
                            <el-checkbox v-model="checkedSourceUrl">原文链接</el-checkbox>
                            <el-input v-if="checkedSourceUrl" v-model="formData.content_source_url" maxlength="50" placeholder="原文链接"
                                auto-complete="off" tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="show_cover_pic" label="显示封面">
                            <el-checkbox v-model="formData.show_cover_pic" true-label="1" false-label="0">显示封面</el-checkbox>
                            <el-alert v-if="formData.show_cover_pic == '1'" title="会自动使用文章中第一张图片作为封面图" type="info"
                                close-text="知道了" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="need_open_comment" label="打开评论">
                            <el-checkbox v-model="formData.need_open_comment" true-label="1"
                                false-label="0">打开评论</el-checkbox>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="only_fans_can_comment" label="粉丝评论">
                            <el-checkbox v-model="formData.only_fans_can_comment" true-label="1"
                                false-label="0">只给粉丝评论</el-checkbox>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div style="border: 1px solid #ccc;z-index: 1000;">
                            <Toolbar v-if="showCreate" style="border-bottom: 1px solid #ccc" :editor="editorRef"
                                :defaultConfig="toolbarConfig" :mode="editorMode" />
                            <Editor v-if="showCreate" style="height: 500px; overflow-y: hidden;" v-model="formData.content"
                                :defaultConfig="editorConfig" :mode="editorMode" @onCreated="handleEditorCreated" />
                        </div>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="padding-top: 5px;">
                            <el-alert style="margin-top: 5px;" title="图文中上传的图片只可在微信中查看" type="info" close-text="知道了" />
                            <el-alert style="margin-top: 5px;" title="具备微信支付权限的公众号，才可以使用a标签，其他公众号不能使用" type="info"
                                close-text="知道了" />
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
                            <el-form-item label="摘要" prop="info">
                                {{ formData.digest }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="标题图" prop="picUrl">
                                <img v-if="formData.picurl" :src="platformInfo.AppFileDomain + formData.picurl"
                                    style="width: 100px;height: 100px;" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="原文链接" prop="content_source_url">
                                {{ formData.content_source_url }}
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="显示封面" prop="show_cover_pic">
                                <span v-if="formData.show_cover_pic == 1">显示封面</span>
                                <span v-else>不显示</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="打开评论" prop="need_open_comment">
                                <span v-if="formData.need_open_comment == 1">打开评论</span>
                                <span v-else>不打开</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="评论限制" prop="only_fans_can_comment">
                                <span v-if="formData.only_fans_can_comment == 1">仅限粉丝</span>
                                <span v-else>不限制</span>
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
<script setup lang="ts" name="platform-wechat-msg-mass-news">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { IEditorConfig } from '@wangeditor/editor'
import { nextTick, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import modal from '/@/utils/modal'
import { getNewsList, doCreate, doDelete, getInfo } from '/@/api/platform/wechat/mass'
import { API_WX_FILE_UPLOAD_THUMB, API_WX_FILE_UPLOAD_IMAGE_METERIAL } from '/@/api/platform/wechat/file'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { fileUploadExt } from '/@/api/common'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { useUserInfo } from '/@/stores/userInfo'
import { useRouter } from "vue-router"

const router = useRouter()
const platformInfo = usePlatformInfo()
const userInfo = useUserInfo()


const { query } = router.currentRoute.value

console.log(query)
// 富文本编辑器
const editorRef = shallowRef()
const editorMode = ref('default')
const toolbarConfig = { modalAppendToBody: true }
type InsertFnType = (url: string, alt: string, href: string) => void
const editorConfig: Partial<IEditorConfig> = {
    MENU_CONF: {
        uploadImage: {
            server: import.meta.env.VITE_AXIOS_BASE_URL + API_WX_FILE_UPLOAD_IMAGE_METERIAL + query.wxid,
            fieldName: 'Filedata',
            headers: {
                "wk-user-token": userInfo.getToken()
            },
            // 单个文件上传成功之后
            onSuccess(file: File, res: any) {
                console.log(`${file.name} 上传成功`, res)
            },
            customInsert(res: any, insertFn: InsertFnType) {
                if (res.code == 0) {
                    insertFn(res.data.wx_picurl, res.data.filename, res.data.wx_picurl)
                }
            },
        }
    }
}

// 组件销毁时, 也及时销毁编辑器
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor) {
        editor.destroy()
    }
})

const handleEditorCreated = (editor: any) => {
    editorRef.value = editor // 记录 editor 实例，重要！
}


const createRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const wxid = ref('')
const wxname = ref('')
const checkedSourceUrl = ref(false)
const showDetail = ref(false)

const data = reactive({
    formData: {
        wxid: '',
        wxname: '',
        title: '',
        author: '',
        digest: '',
        content: '',
        picurl: '',
        content_source_url: '',
        show_cover_pic: 0,
        need_open_comment: 0,
        only_fans_can_comment: 0,
        thumb_media_id: ''
    },
    queryParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        title: [{ required: true, message: '图文标题', trigger: 'blur' }],
        picurl: [{ required: true, message: '缩略图', trigger: 'blur' }]
    }
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'picurl', label: ``, show: true, width: 80 },
    { prop: 'title', label: `标题`, show: true },
    { prop: 'author', label: `作者`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        wxid: '',
        wxname: '',
        title: '',
        author: '',
        digest: '',
        content: '',
        picurl: '',
        content_source_url: '',
        show_cover_pic: 0,
        need_open_comment: 0,
        only_fans_can_comment: 0,
        thumb_media_id: ''
    }
    formEl?.resetFields()
}

const uploadPic = (file: any) => {
    const type = file.raw.type
    if (type != 'image/jpeg' && type != 'image/png') {
        modal.notifyError('仅支持jpg、png格式图片')
        return
    }
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUploadExt(f, {}, API_WX_FILE_UPLOAD_THUMB + wxid.value, 64).then((res) => {
        if (res.code == 0) {
            formData.value.picurl = res.data.picurl
            formData.value.thumb_media_id = res.data.id
        }
    })
}

const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    list()
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getNewsList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    formData.value.wxid = wxid.value
    formData.value.wxname = wxname.value
    showCreate.value = true
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

// 预览文章
const handleView = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        showDetail.value = true
    })
}

onMounted(() => {
    wxid.value = query.wxid as never
    wxname.value = query.wxname as never
    list()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
._img {
    width: 50px;
    height: 50px;
}
</style>