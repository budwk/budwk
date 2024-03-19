<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Collection" @click="gotoNews" v-permission="['wx.msg.mass']">素材库
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Promotion" @click="handleSend" v-permission="['wx.msg.mass']">群发消息
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
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span v-if="scope.row.type == 'image'">图片</span>
                        <span v-if="scope.row.type == 'news'">图文</span>
                        <span v-if="scope.row.type == 'text'">文本</span>
                    </template>
                    <template v-if="item.prop == 'scope'" #default="scope">
                        <span v-if="scope.row.scope == 'some'">部分会员</span>
                        <span v-if="scope.row.scope == 'all'">全部会员</span>
                    </template>
                    <template v-if="item.prop == 'id'" #default="scope">
                        <span v-if="scope.row.massSend">
                            <span v-if="scope.row.massSend.status == 1">成功</span>
                            <span v-if="scope.row.massSend.status != 1">失败</span>
                        </span>
                    </template>
                    <template v-if="item.prop == 'createdBy'" #default="scope">
                        <span v-if="scope.row.massSend">{{ scope.row.massSend.errMsg }}</span>
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="群发消息" v-model="showSend" width="55%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="name" label="群发名称">
                            <el-input v-model="formData.name" maxlength="255" placeholder="群发名称" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item class="is-required" prop="type" label="消息类型">
                            <el-radio-group v-model="formData.type">
                                <el-radio value="text">文本</el-radio>
                                <el-radio value="image">图片</el-radio>
                                <el-radio value="news">图文</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type == 'news'">
                        <el-form-item class="is-required" prop="newsids" label="图文消息">
                            <el-row :gutter="10" style="padding-right:20px;">
                                <el-col :span="24">
                                    <el-button @click="openNews">选择图文</el-button>
                                    <el-button @click="clearNews">清空图文</el-button>
                                </el-col>
                                <el-col :span="24">
                                    <el-tree style="padding-top: 8px;" :data="newsTreeData" :allow-drop="sortAllowDrop"
                                        node-key="id" default-expand-all :expand-on-click-node="false" draggable>
                                        <template #default="{ node, data }" class="custom-tree-node">
                                            <el-row :gutter="10" justify="space-between">
                                                <el-col :span="12">
                                                    {{ node?.data.label }}
                                                </el-col>
                                                <el-col :span="12">
                                                    <el-button link type="primary" size="small" @click="remove(node)">
                                                        Delete
                                                    </el-button>
                                                </el-col>
                                            </el-row>
                                        </template>
                                    </el-tree>
                                </el-col></el-row>
                            <el-alert v-if="newsTreeData.length > 0" style="padding:10px;height: 32px;" title="图文可拖拽排序"
                                type="success" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type == 'image'">
                        <el-form-item prop="picurl" label="发送图片(1M以内)" class="label-font-weight">
                            <el-upload action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false">
                                <img v-if="formData.picurl" :src="platformInfo.AppFileDomain + formData.picurl" class="_img" />
                                <el-button v-else>
                                    选择
                                    <el-icon class="el-icon--right">
                                        <Upload />
                                    </el-icon>
                                </el-button>
                            </el-upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type == 'text'">
                        <el-form-item class="is-required" label="文本消息" prop="content">
                            <el-input v-model="formData.content" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item class="is-required" prop="scope" label="发送范围">
                            <el-radio-group v-model="formData.scope">
                                <el-radio value="all">全部会员</el-radio>
                                <el-radio value="some">部分会员</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.scope == 'some'">
                        <el-form-item class="is-required" label="接收会员" prop="receivers">
                            <el-button @click="openUser">选择会员</el-button>
                            <el-input v-model="formData.receivers" style="margin-top: 5px;" type="textarea" />
                            <el-alert style="margin-top: 5px;" title="openid 必须 >=2且 <10000，并且使用英文,符号分割" type="success" />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="send" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showSend = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="选择图文" v-model="showNews" width="45%">
            <el-table :data="newsData" row-key="id" :default-sort="{ prop: 'createdAt', order: 'descending' }"
                @selection-change="handleSelectNews">
                <el-table-column type="selection" width="50" />

                <el-table-column label="图文标题" header-align="left" prop="title" :show-overflow-tooltip="true" />

                <el-table-column sortable label="创建时间" header-align="center" align="center" prop="createdAt">
                    <template #default="scope">
                        {{ formatTime(scope.row?.createdAt) }}
                    </template>
                </el-table-column>
            </el-table>
            <el-row>
                <pagination :total="queryNewsParams.totalCount" v-model:page="queryNewsParams.pageNo"
                    v-model:limit="queryNewsParams.pageSize" @pagination="listNews" />
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="doNews">确 定</el-button>
                    <el-button @click="showNews = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="选择会员" v-model="showUser" width="60%">
            <el-table :data="userData" row-key="id" :default-sort="{ prop: 'createdAt', order: 'descending' }"
                @selection-change="handleSelectUser">
                <el-table-column type="selection" width="50" />

                <el-table-column label="微信昵称" header-align="left" prop="nickname"/>

                <el-table-column label="openid" header-align="left" prop="openid" />

                <el-table-column label="关注公众号" header-align="left" prop="subscribe"
                    width="100">
                    <template #default="scope">
                        <span v-if="!scope.row.subscribe">未关注</span>
                        <span v-if="scope.row.subscribe" style="color:green;">已关注</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-row>
                <pagination :total="queryUserParams.totalCount" v-model:page="queryUserParams.pageNo"
                    v-model:limit="queryUserParams.pageSize" @pagination="listUser" />
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="doUser">确 定</el-button>
                    <el-button @click="showUser = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-wechat-msg-mass">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getUserList, getNewsList, doCreate, doDelete, doPush } from '/@/api/platform/wechat/mass'
import { getAccountList } from '/@/api/platform/wechat/account'
import { API_WX_FILE_UPLOAD_IMAGE_METERIAL } from '/@/api/platform/wechat/file'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import router from "/@/router"
import { fileUploadExt } from '/@/api/common'
import { usePlatformInfo } from '/@/stores/platformInfo'

const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()

const showSend = ref(false)
const showNews = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const accounts = ref([])
const wxid = ref('')
const wxname = ref('')
const btnLoading = ref(false)
const newsTreeData = ref([])
const newsData = ref([])
const selectNews = ref([])
const showUser = ref(false)
const userData = ref([])
const selectUser = ref([])

const validateText = (rule: any, value: any, callback: any) => {
    if (formData.value.type === 'text' && formData.value.content === '') {
        callback(new Error('请输入文本内容'))
    } else {
        callback()
    }
}

const validateSome = (rule: any, value: any, callback: any) => {
    if (formData.value.scope === 'some' && formData.value.receivers.length === 0) {
        callback(new Error('请选择接收会员，或输入openid'))
    } else if (formData.value.scope === 'some' && formData.value.receivers.length < 2) {
        callback(new Error('openid 数量必须 >= 2'))
    } else {
        callback()
    }
}

const validateNews = (rule: any, value: any, callback: any) => {
    if (formData.value.type === 'news' && newsTreeData.value.length === 0) {
        callback(new Error('请选择图文消息'))
    } else {
        callback()
    }
}

const validatePic = (rule: any, value: any, callback: any) => {
    if (formData.value.type === 'image' && formData.value.picurl === '') {
        callback(new Error('请上传图片'))
    } else {
        callback()
    }
}

const data = reactive({
    formData: {
        id: '',
        wxid: '',
        name: '',
        type: 'text',
        content: '',
        picurl: '',
        media_id: '',
        newsids: '',
        receivers: '',
        scope: 'some'
    },
    queryParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    queryNewsParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    queryUserParams: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        name: [{ required: true, message: '群发名称', trigger: 'blur' }],
        content: [{ validator: validateText, trigger: ['blur', 'change'] }],
        receivers: [{ validator: validateSome, trigger: ['blur', 'change'] }],
        newsids: [{ validator: validateNews, trigger: ['blur', 'change'] }],
        picurl: [{ validator: validatePic, trigger: ['blur', 'change'] }]
    }
})

const { queryParams, formData, formRules, queryNewsParams, queryUserParams } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `群发名称`, show: true },
    { prop: 'type', label: `消息类型`, show: true },
    { prop: 'scope', label: `发送范围`, show: true },
    { prop: 'id', label: `发送状态`, show: true, width: 100 },
    { prop: 'createdBy', label: `发送结果`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        wxid: wxid.value,
        name: '',
        type: 'text',
        content: '',
        picurl: '',
        media_id: '',
        newsids: '',
        receivers: '',
        scope: 'some'
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
    fileUploadExt(f, {}, API_WX_FILE_UPLOAD_IMAGE_METERIAL + wxid.value, 64).then((res) => {
        if (res.code == 0) {
            formData.value.picurl = res.data.picurl
            formData.value.media_id = res.data.id
        }
    })
}

const sortAllowDrop = (moveNode: any, inNode: any, type: any) => {
    if (moveNode.data.parentId === inNode.data.parentId) {
        return type === 'prev'
    }
}

const openNews = () => {
    showNews.value = true
    listNews()
}

const clearNews = () => {
    newsTreeData.value = []
}

const remove = (node: any) => {
    const data = node.data
    // 注意:对数组对象进行深度复制,否则原始对象不能进行删除操作
    const temp = JSON.parse(JSON.stringify(newsTreeData.value))
    const index = temp.findIndex(function (o: any) {
        return o.id === data.id
    })
    temp.splice(index, 1)
    newsTreeData.value = temp
    formData.value.newsids = ''
    const newsids: any[] = []
    newsTreeData.value.forEach((o) => {
        newsids.push(o.id)
    })
    formData.value.newsids = newsids.join(',')
}

const doNews = () => {
    showNews.value = false
    if (selectNews.value && selectNews.value.length > 0) {
        selectNews.value.forEach((o: any) => {
            formData.value.newsids += o.id + ','
            newsTreeData.value.push({ id: o.id, label: o.title })
        })
    }
}

const handleSelectNews = (selection: any) => {
    selectNews.value = selection
}

const accountChange = (val: string) => {
    wxid.value = val
    queryParams.value.wxid = val
    list()
}

const listAccount = () => {
    getAccountList().then((res) => {
        accounts.value = res.data as never
        if (accounts.value.length > 0) {
            wxid.value = accounts.value[0].id
            wxname.value = accounts.value[0].appname
            queryParams.value.wxid = accounts.value[0].id
            list()
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
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 查询图文
const listNews = () => {
    queryNewsParams.value.wxid = wxid.value
    getNewsList(queryNewsParams.value).then((res) => {
        newsData.value = res.data.list as never
        queryNewsParams.value.totalCount = res.data.totalCount as never
    })
}

const openUser = () => {
    showUser.value = true
    listUser()
}

const doUser = () => {
    showUser.value = false
    if (selectUser.value && selectUser.value.length > 0) {
        selectUser.value.forEach((o: any) => {
            formData.value.receivers += o.openid + ','
        })
    }
}

const handleSelectUser = (selection: any) => {
    selectUser.value = selection
}

// 查询用户
const listUser = () => {
    queryUserParams.value.wxid = wxid.value
    getUserList(queryUserParams.value).then((res) => {
        userData.value = res.data.list as never
        queryUserParams.value.totalCount = res.data.totalCount as never
    })
}


// 新增按钮
const handleSend = () => {
    showSend.value = true
    resetForm(createRef.value)
}

const send = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            doPush(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showSend.value = false
                queryParams.value.pageNo = 1
                list()
            })
        }
    })
}

const gotoNews = () => {
    router.push({
        path: '/platform/wechat/msg/mass/news',
        query: {
            wxid: wxid.value,
            wxname: wxname.value
        }
    })
}

onMounted(() => {
    listAccount()
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