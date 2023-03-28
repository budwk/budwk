<template>
    <div class="app-container">
        <el-row>
            <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
                <el-form-item label="公众号" prop="wxid">
                    <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
                    </el-form-item>
                    <el-form-item label="openid" prop="openid">
                        <el-input
v-model="queryParams.openid" placeholder="请输入openid" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item label="消息内容" prop="content">
                        <el-input
v-model="queryParams.content" placeholder="请输入消息内容" clearable style="width: 180px"
                            @keyup.enter="handleSearch" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearch">重置</el-button>
                    </el-form-item>
                </el-form>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <el-table-column type="expand">
                <template #default="scope">
                    <el-form label-position="left" inline class="el-table-expand" style="padding-left: 60px;">
                        <el-form-item label="回复内容">
                            <span v-if="scope.row.replyId && scope.row.replyId != ''">{{ scope.row.reply.content }}</span>
                        </el-form-item>
                        <el-form-item label="回复时间">
                            <span v-if="scope.row.replyId && scope.row.replyId != ''">
                                {{ formatTime(scope.row.reply.createdAt) }}</span>
                        </el-form-item>
                    </el-form>
                </template>
            </el-table-column>
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'openid'" #default="scope">
                        <span v-if="scope.row.nickname">{{ scope.row.nickname }}</span>
                        <span v-else>{{ scope.row.openid }}</span>
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span v-if="scope.row.type === 'txt'">
                            文本
                        </span>
                        <span v-if="scope.row.type === 'image'">
                            图片
                        </span>
                        <span v-if="scope.row.type === 'video'">
                            视频
                        </span>
                    </template>
                    <template v-if="item.prop == 'content'" #default="scope">
                        <span v-if="scope.row.type === 'txt'">
                            {{ scope.row.content }}
                        </span>
                        <span v-if="scope.row.type === 'image'">
                            <a :href="platformInfo.AppFileDomain + scope.row.content" target="_blank">
                                <img :src="platformInfo.AppFileDomain + scope.row.content" width="30" height="30">
                            </a>
                        </span>
                        <span v-if="scope.row.type === 'video'">
                            <a :href="platformInfo.AppFileDomain + scope.row.content" target="_blank">
                                <i class="fa fa-film" style="font-size: 2em;" />
                            </a>
                        </span>
                    </template>
                    <template v-if="item.prop == 'replyId'" #default="scope">
                        <span v-if="!scope.row.replyId || scope.row.replyId == ''">未回复
                            <el-button v-permission="['wx.msg.user.reply']" size="small" round
                                @click="openReply(scope.row)">回复</el-button>
                        </span>
                        <span v-if="scope.row.replyId && scope.row.replyId != ''">已回复</span>
                    </template>
                </el-table-column>
            </template>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="回复消息" v-model="showReply" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" size="small" label-width="80px">
                <el-form-item label="微信昵称 ">
                    <span v-if="formData.nickname">{{ formData.nickname }}</span>
                    <span v-else>未获取</span>
                </el-form-item>
                <el-form-item label="openid">
                    {{ formData.openid }}
                </el-form-item>
                <el-form-item label="消息内容">
                    <span v-if="formData.type === 'txt'">
                        {{ formData.content }}
                    </span>
                    <span v-if="formData.type === 'image'">
                        <a :href="platformInfo.AppFileDomain + formData.msgContent" target="_blank">
                            <img :src="platformInfo.AppFileDomain + formData.msgContent" width="30" height="30">
                        </a>
                    </span>
                    <span v-if="formData.type === 'video'">
                        <a :href="platformInfo.AppFileDomain + formData.msgContent" target="_blank">
                            <i class="fa fa-film" style="font-size: 2em;" />
                        </a>
                    </span>
                </el-form-item>
                <el-form-item label="回复内容" prop="replyContent">
                    <el-input v-model="formData.replyContent" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="reply" :loading="btnLoading">确 定</el-button>
                    <el-button @click="showReply = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>
<script setup lang="ts" name="platform-wechat-msg-user">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, doReply } from '/@/api/platform/wechat/msg'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'

const platformInfo = usePlatformInfo()
const showReply = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const progressNum = ref(0)
const progressStatus = ref('')
const accounts = ref([])
const wxid = ref('')
const btnLoading = ref(false)
const createRef = ref<InstanceType<typeof ElForm>>()
const queryRef = ref<InstanceType<typeof ElForm>>()

const data = reactive({
    formData: {
        id: '',
        msgid: '',
        content: '',
        openid: '',
        nickname: '',
        type: '',
        replyContent: ''
    },
    queryParams: {
        wxid: '',
        openid: '',
        content: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        replyContent: [{ required: true, message: "回复内容不能为空", trigger: ["blur", "change"] }]
    }
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'openid', label: `微信昵称 | openid`, show: true },
    { prop: 'type', label: `消息类型`, show: true, width: 100 },
    { prop: 'content', label: `消息内容`, show: true },
    { prop: 'replyId', label: `消息状态`, show: true, width: 150 },
    { prop: 'createdAt', label: `接收时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        msgid: '',
        content: '',
        openid: '',
        nickname: '',
        type: '',
        replyContent: ''
    }
    formEl?.resetFields()
}


const accountChange = (val: string) => {
    wxid.value = val
    queryParams.value.wxid = val
    queryParams.value.pageNo = 1
    list()
}

const listAccount = () => {
    getAccountList().then((res) => {
        accounts.value = res.data as never
        if (accounts.value.length > 0) {
            wxid.value = accounts.value[0].id
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

const handleSearch = () => {
    queryParams.value.pageNo = 1
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
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

const openReply = (row: any) => {
    resetForm(createRef.value)
    showReply.value = true
    formData.value = row
    formData.value.msgid = row.id
}

const reply = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            doReply(wxid.value, formData.value).then((res: any) => {
                modal.msgSuccess('回复成功')
                showReply.value = false
                queryParams.value.pageNo = 1
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