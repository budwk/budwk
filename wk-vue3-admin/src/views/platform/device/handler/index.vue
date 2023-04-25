<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['device.settings.handler.create']">新增
                </el-button>
            </el-col>
            <right-toolbar @quickSearch="quickSearch" />
        </el-row>
        <el-table v-if="refreshTable" v-loading="tableLoading" :data="tableData" row-key="id">
            <el-table-column prop="name" label="协议名称">
            </el-table-column>
            <el-table-column prop="code" label="协议标识">
            </el-table-column>
            <el-table-column prop="description" label="协议描述">
            </el-table-column>
            <el-table-column prop="enabled" label="状态" align="center" width="150">
                <template #default="scope">
                    <el-switch
                        v-model="scope.row.enabled"
                        :active-value="true"
                        :inactive-value="false"
                        active-color="green"
                        inactive-color="red"
                        @change="enabledChange(scope.row)"
                        />
              </template>
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['device.settings.handler.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['device.settings.handler.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <pagination
               :total="queryParams.totalCount"
               v-model:page="queryParams.pageNo"
               v-model:limit="queryParams.pageSize"
               @pagination="list"
        />

        <el-dialog title="新增协议" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="协议名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入协议名称"/>
                </el-form-item>
                <el-form-item label="协议标识" prop="code">
                    <el-input v-model="formData.id" placeholder="请输入协议标识"/>
                </el-form-item>
                <el-form-item
                    prop="icon"
                    label="协议图标"
                    >
                    <el-upload
                        ref="uploadRef"
                        :limit="1"
                        accept=".png, .jpg, .svg"
                        :show-file-list="false"
                        @change="imgUpload"
                        :auto-upload="false"
                        list-type="picture-card"
                    >
                        <img
                        v-if="formData.icon"
                        :src="formData.icon"
                        class="avatar"
                        >
                        <div v-else>
                            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                        </div>
                        <template #tip>
                                <div class="el-upload__tip text-center">
                                    <div class="el-upload__tip">
                                        仅允许上传图片格式文件，推荐上传svg文件
                                    </div>
                                </div>
                            </template>
                    </el-upload>
                </el-form-item>      
                <el-form-item prop="enabled" label="协议状态">
                    <el-switch
                        v-model="formData.enabled"
                        :active-value="true"
                        :inactive-value="false"
                        active-color="green"
                        inactive-color="red"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改协议" v-model="showUpdate" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="协议名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入协议名称"/>
                </el-form-item>
                <el-form-item label="协议标识" prop="code">
                    <el-input v-model="formData.id" placeholder="请输入协议标识"/>
                </el-form-item>
                <el-form-item label="默认路径" prop="path">
                    <el-input v-model="formData.path" placeholder="请输入默认路径"/>
                </el-form-item>
                <el-form-item
                    prop="icon"
                    label="协议图标"
                    >
                    <el-upload
                        ref="uploadRef"
                        :limit="1"
                        accept=".png, .jpg, .svg"
                        :show-file-list="false"
                        @change="imgUpload"
                        :auto-upload="false"
                        list-type="picture-card"
                    >
                        <img
                        v-if="formData.icon"
                        :src="formData.icon"
                        class="avatar"
                        >
                        <div v-else>
                            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                        </div>
                        <template #tip>
                                <div class="el-upload__tip text-center">
                                    <div class="el-upload__tip">
                                        仅允许上传图片格式文件，推荐上传svg文件
                                    </div>
                                </div>
                            </template>
                    </el-upload>
                </el-form-item>    
                <el-form-item prop="enabled" label="协议状态">
                    <el-switch
                        v-model="formData.enabled"
                        :active-value="true"
                        :inactive-value="false"
                        active-color="green"
                        inactive-color="red"
                    />
                </el-form-item>
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
<script setup lang="ts" name="platform-device-handler">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, doEnabled } from '/@/api/platform/device/handler'
import { toRefs } from '@vueuse/core'
import { ElForm, ElUpload } from 'element-plus'
import { useUserInfo } from '/@/stores/userInfo'

const userInfo = useUserInfo()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const uploadRef = ref<InstanceType<typeof ElUpload>>()    

const showCreate = ref(false)
const showUpdate = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])

const upload = reactive({
    // 是否禁用上传
    isUploading: false,
    // 是否更新已经存在的用户数据
    updateSupport: 0,
    // 新用户默认密码
    pwd: '',
    // 设置上传的请求头部
    headers: { "wk-user-token": userInfo.getToken() },
    // 上传的地址
    url: '' + import.meta.env.VITE_AXIOS_BASE_URL
})

const data = reactive({
    formData: {
        id: '',
        name: '',
        path: '',
        enabled: true,
        location: 0,
    },
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
        name: [{ required: true, message: "协议名称不能为空", trigger: ["blur","change"] }],
        code: [
            { required: true, message: "协议标识不能为空", trigger: ["blur","change"] },
            { pattern: /^[a-z][a-z0-9_]+$/, message: "为小写字母或小写字母、下划线和数字的组合，并以小写字母开头", trigger: "blur" }
        ]
    },
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        path: '',
        enabled: true,
        location: 0,
    }
    formEl?.resetFields()
}

const imgUpload = (file: any) => {
    // let fd = new FormData()
    // fd.append('Filedata', file.raw)
    // fileUpload(fd,{},'image').then((res) => {
    //     if (res.code == 0) {
  
    //     }
    // })
    let reader = new FileReader()
    // 转base64
    reader.onload = ((e) => {
        formData.value.icon = e.target.result
        uploadRef.value?.clearFiles()
    })
    reader.readAsDataURL(file.raw)
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

// 刷新
const quickSearch = (data: any) => {
    refreshTable.value = false
    queryParams.value.pageNo = 1
    list()
    nextTick(() => {
        refreshTable.value = true
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
    modal.confirm('确定删除 '+ row.name + '？').then(() => {
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

// 启用禁用
const enabledChange = (row: any) => {
    doEnabled(row.id, row.enabled).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(()=>{
        setTimeout(() => {
            row.enabled = !row.enabled
        }, 300)
    })
}

onMounted(()=>{
    list()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.avatar {
    width: 98%;
    height: 98%;
    border-radius: 6px;
}
</style>