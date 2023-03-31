<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
            <el-col :span="1.5">
                <el-radio-group v-model="type" @change="typeChange">
                    <el-radio-button v-for="type in types" :key="type.val" :label="type.val">{{ type.txt }}
                    </el-radio-button>
                </el-radio-group>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.reply.txt.create']">新增事件
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="select_ids.length < 1" @click="handleDeleteMore"
                    v-permission="['wx.reply.txt.delete']">删除</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            @selection-change="handleSelect" :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <el-table-column type="selection" width="50" fixed="left" />
            <el-table-column label="事件类型" header-align="left" prop="type" :show-overflow-tooltip="true">
                <template #default="scope">
                    <span v-if="'follow' === scope.row.type">关注事件</span>
                    <span v-if="'keyword' === scope.row.type">关键词事件</span>
                </template>
            </el-table-column>

            <el-table-column v-if="type === 'keyword'" label="关键词" header-align="left" prop="keyword"
                :show-overflow-tooltip="true" />

            <el-table-column label="消息类型" header-align="left" prop="msgType" :show-overflow-tooltip="true" />

            <el-table-column label="回复内容" header-align="left" prop="content" :show-overflow-tooltip="true">
                <template #default="scope">
                    <span v-if="scope.row.msgType == 'txt'">
                        {{ scope.row.replyTxt.title }}
                    </span>
                    <span v-if="scope.row.msgType == 'image'">
                        <img :src="platformInfo.AppFileDomain + scope.row.replyImg.picurl" width="35" height="35">
                    </span>
                    <span v-if="scope.row.msgType == 'news'">
                        {{ scope.row.replyNews.title }}
                    </span>
                </template>
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['wx.reply.txt.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.reply.txt.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增事件" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="type" label="事件类型">
                            <span v-if="'follow' === formData.type">关注事件</span>
                            <span v-if="'keyword' === formData.type">关键词事件</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type == 'keyword'">
                        <el-form-item class="is-required" prop="keyword" label="关键词">
                            <el-input v-model="formData.keyword" maxlength="100" placeholder="关键词" auto-complete="off"
                                tabindex="1" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item class="is-required" prop="scope" label="消息类型">
                            <el-radio-group v-model="formData.msgType" @change="msgTypeChange">
                                <el-radio label="txt">文本</el-radio>
                                <el-radio label="news">图文</el-radio>
                                <el-radio label="image">图片</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'txt'">
                        <el-form-item class="is-required" label="绑定文本" prop="content">
                            <el-select v-model="formData.content" placeholder="请选择文本">
                                <el-option v-for="item in txtList" :key="item.id" :label="item.title" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'news'">
                        <el-form-item class="is-required" label="绑定图文" prop="content">
                            <el-select v-model="formData.content" placeholder="请选择图文">
                                <el-option v-for="item in newsList" :key="item.id" :label="item.title" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'image'">
                        <el-form-item class="is-required" label="绑定图片" prop="content">
                            <el-col v-for="(o, index) in imageList" :key="o.id" :span="6" :offset="index > 0 ? 3 : 0">
                                <el-card style="width: 120px;height: 120px;">
                                    <img :src="platformInfo.AppFileDomain + o.picurl" style="height: 60px;">
                                    <div style="padding: 3px;">
                                        <span v-if="formData.content == o.id" style="font-size: 12px;color: red;">已选中</span>
                                        <div class="bottom clearfix">
                                            <el-button type="text" class="button" @click="imgClassSel(o.id)">选中</el-button>
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
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

        <el-dialog title="修改事件" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="125px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="type" label="事件类型">
                            <span v-if="'follow' === formData.type">关注事件</span>
                            <span v-if="'keyword' === formData.type">关键词事件</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type == 'keyword'">
                        <el-form-item class="is-required" prop="keyword" label="关键词">
                            <el-input v-model="formData.keyword" maxlength="100" placeholder="关键词" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item class="is-required" prop="scope" label="消息类型">
                            <el-radio-group v-model="formData.msgType" @change="msgTypeChange">
                                <el-radio label="txt">文本</el-radio>
                                <el-radio label="news">图文</el-radio>
                                <el-radio label="image">图片</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'txt'">
                        <el-form-item class="is-required" label="绑定文本" prop="content">
                            <el-select v-model="formData.content" placeholder="请选择文本">
                                <el-option v-for="item in txtList" :key="item.id" :label="item.title" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'news'">
                        <el-form-item class="is-required" label="绑定图文" prop="content">
                            <el-select v-model="formData.content" placeholder="请选择图文">
                                <el-option v-for="item in newsList" :key="item.id" :label="item.title" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.msgType == 'image'">
                        <el-form-item class="is-required" label="绑定图片" prop="content">
                            <el-col v-for="(o, index) in imageList" :key="o.id" :span="6" :offset="index > 0 ? 3 : 0">
                                <el-card style="width: 120px;height: 120px;">
                                    <img :src="platformInfo.AppFileDomain + o.picurl" style="height: 60px;">
                                    <div style="padding: 3px;">
                                        <span v-if="formData.content == o.id" style="font-size: 12px;color: red;">已选中</span>
                                        <div class="bottom clearfix">
                                            <el-button plain type="primary" class="button" @click="imgClassSel(o.id)">选中</el-button>
                                        </div>
                                    </div>
                                </el-card>
                            </el-col>
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
    </div>
</template>
<script setup lang="ts" name="platform-wechat-reply-conf">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getContentList, getInfo, doCreate, doDelete, doUpdate, doDeleteMore } from '/@/api/platform/wechat/reply_conf'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'

const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const accounts = ref([])
const wxid = ref('')
const select_ids = ref([])
const select_types = ref([])
const types = ref([
    { val: 'follow', txt: '关注事件' },
    { val: 'keyword', txt: '关键词事件' }
])
const type = ref("follow")
const txtList = ref([])
const newsList = ref([])
const imageList = ref([])

const validateKeyword = (rule: any, value: any, callback: any) => {
    if (formData.value.type === 'keyword' && (typeof (formData.value.keyword) === 'undefined' || formData.value.keyword === '')) {
        callback(new Error('请输入关键词'))
    } else if (formData.value.type === 'keyword' && formData.value.keyword.length > 20) {
        callback(new Error('关键词最大长度为20个字符'))
    } else {
        callback()
    }
}

const data = reactive({
    formData: {
        id: '',
        wxid: '',
        type: '',
        keyword: '',
        msgType: '',
        content: ''
    },
    queryParams: {
        wxid: '',
        type: 'follow',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        keyword: [
            { required: false, message: "关键词", trigger: ["blur", "change"] },
            { validator: validateKeyword, trigger: ['blur', 'change'] }
        ],
        content: [{ required: true, message: "回复内容", trigger: ["blur", "change"] }]
    }
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        wxid: wxid.value,
        type: type.value,
        keyword: '',
        msgType: 'txt',
        content: ''
    }
    formEl?.resetFields()
}

const typeChange = (val: string) => {
    queryParams.value.type = val
    queryParams.value.wxid = wxid.value
    list()
}

const msgTypeChange = (val: string) => {
    formData.value.content = ''
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
            queryParams.value.wxid = accounts.value[0].id
            initSelectContent()
            list()
        }
    })
}

const initSelectContent = () => {
    getContentList(wxid.value, 'txt').then((res) => {
        txtList.value = res.data as never
    })
    getContentList(wxid.value, 'news').then((res) => {
        newsList.value = res.data as never
    })
    getContentList(wxid.value, 'image').then((res) => {
        imageList.value = res.data as never
    })
}

const imgClassSel = (val: string) => {
    formData.value.content = val
}

const handleSelect = (selection: any) => {
    select_ids.value = selection.map(item => item.id)
    select_types.value = selection.map(item => item.type)
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
    modal.confirm('确定删除 ？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}

// 批量删除
const handleDeleteMore = () => {
    if (select_ids.value.length == 0) {
        modal.msgError('请选择要删除的数据')
        return
    }
    modal.confirm('确定删除 ？').then(() => {
        return doDeleteMore(select_ids.value.toString(), select_types.value.toString())
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
    listAccount()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>