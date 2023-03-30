<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.reply.news.create']">新增图文
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="Delete" :disabled="select_ids.length < 1" @click="handleDeleteMore"
                    v-permission="['wx.reply.news.delete']">删除</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            @selection-change="handleSelect" :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <el-table-column type="selection" width="50" fixed="left" />
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'picUrl2'" #default="scope">
                        <img v-if="scope.row.picUrl2" :src="platformInfo.AppFileDomain + scope.row.picUrl2"
                            class="_img_small" />
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width" width="180">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['wx.reply.news.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.reply.news.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增图文" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="150px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="title" label="图文标题">
                            <el-input v-model="formData.title" maxlength="120" placeholder="图文标题" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="description" label="图文描述">
                            <el-input v-model="formData.description" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="picUrl2" label="标题图(2M以内)" class="label-font-weight">
                            <el-upload action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false">
                                <img v-if="formData.picUrl2" :src="platformInfo.AppFileDomain + formData.picUrl2"
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
                        <el-form-item prop="url" label="跳转链接">
                            <el-input v-model="formData.url" placeholder="https://" auto-complete="off" tabindex="3"
                                type="text" />
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

        <el-dialog title="修改图文" v-model="showUpdate" width="40%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="150px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="title" label="图文标题">
                            <el-input v-model="formData.title" maxlength="120" placeholder="图文标题" auto-complete="off"
                                tabindex="1" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="description" label="图文描述">
                            <el-input v-model="formData.description" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="picUrl2" label="标题图(2M以内)" class="label-font-weight">
                            <el-upload action="#" :auto-upload="false" :on-change="uploadPic" :show-file-list="false">
                                <img v-if="formData.picUrl2" :src="platformInfo.AppFileDomain + formData.picUrl2"
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
                        <el-form-item prop="url" label="跳转链接">
                            <el-input v-model="formData.url" placeholder="https://" auto-complete="off" tabindex="3"
                                type="text" />
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
<script setup lang="ts" name="platform-wechat-reply-news">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getInfo, doCreate, doDelete, doUpdate, doDeleteMore } from '/@/api/platform/wechat/reply_news'
import { getAccountList } from '/@/api/platform/wechat/account'
import { API_WX_FILE_UPLOAD_IMAGE_METERIAL } from '/@/api/platform/wechat/file'
import { fileUploadExt } from '/@/api/common'
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
const btnLoading = ref(false)
const select_ids = ref([])
const select_titles = ref([])

const validateUrl = (rule: any, value: any, callback: any) => {
    if (typeof value !== 'undefined' && value !== '' && (value.indexOf('http://') === 0 || value.indexOf('https://') === 0)) {
        callback()
    } else {
        callback(new Error('链接地址不正确'))
    }
}

const data = reactive({
    formData: {
        id: '',
        wxid: '',
        picUrl: '',
        picUrl2: '',
        mediaId: '',
        description: '',
        url: ''
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
        title: [{ required: true, message: "图文标题不能为空", trigger: ["blur", "change"] }],
        description: [{ required: true, message: "图文描述不能为空", trigger: ["blur", "change"] }],
        picUrl2: [{ required: true, message: "标题图不能为空", trigger: ["blur", "change"] }],
        url: [
            { required: true, message: "跳转链接不能为空", trigger: ["blur", "change"] },
            { validator: validateUrl, trigger: ["blur", "change"] }
        ],
    }
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'title', label: `图文标题`, show: true },
    { prop: 'description', label: `图文描述`, show: true },
    { prop: 'picUrl2', label: `标题图`, show: true },
    { prop: 'url', label: `跳转链接`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        wxid: wxid.value,
        picUrl: '',
        picUrl2: '',
        mediaId: '',
        description: '',
        url: ''
    }
    formEl?.resetFields()
}


const uploadPic = (file: any) => {
    const type = file.raw.type
    if (type != 'image/jpeg' && type != 'image/png') {
        modal.notifyError('仅支持jpg、png格式图文')
        return
    }
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUploadExt(f, {}, API_WX_FILE_UPLOAD_IMAGE_METERIAL + wxid.value, 2048).then((res) => {
        if (res.code == 0) {
            formData.value.picUrl = res.data.wx_picurl
            formData.value.picUrl2 = res.data.picurl
            formData.value.mediaId = res.data.id
        }
    })
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
            list()
        }
    })
}

const handleSelect = (selection: any) => {
    select_ids.value = selection.map(item => item.id)
    select_titles.value = selection.map(item => item.title)
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
    modal.confirm('确定删除 ' + row.picurl + '？').then(() => {
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
    modal.confirm('确定删除 ' + select_titles.value.join(',') + '？').then(() => {
        return doDeleteMore(select_ids.value.toString(), select_titles.value.toString())
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
<style scoped>
._img {
    width: 50px;
    height: 50px;
}

._img_small {
    width: 30px;
    height: 30px;
}
</style>