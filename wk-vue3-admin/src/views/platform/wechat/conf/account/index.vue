<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.conf.account.create']">新增
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['wx.conf.account.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.conf.account.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增公众号" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item label="所属分类" prop="siteId">
                            <el-select v-model="classId" class="m-2" placeholder="所属分类" disabled style="width:100%">
                                <el-option v-for="item in linkClass" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="公众号名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入公众号名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="公众号地址" prop="url">
                            <el-input v-model="formData.url" placeholder="请输入公众号地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="打开方式" prop="target">
                            <el-radio-group v-model="formData.target">
                                <el-radio :label="'_self'">
                                  本页面
                                </el-radio>
                                <el-radio :label="'_blank'">
                                  新页面
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="公众号类型" prop="type">
                            <el-radio-group v-model="formData.type" @change="typeChange">
                                <el-radio :label="'txt'">
                                  文本公众号
                                </el-radio>
                                <el-radio :label="'img'">
                                  图片公众号
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type=='img'">
                        <el-form-item label="上传图片" prop="picUrl" class="label_font">
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
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改公众号" v-model="showUpdate" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item label="公众号名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入公众号名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="公众号地址" prop="url">
                            <el-input v-model="formData.url" placeholder="请输入公众号地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="打开方式" prop="target">
                            <el-radio-group v-model="formData.target">
                                <el-radio :label="'_self'">
                                  本页面
                                </el-radio>
                                <el-radio :label="'_blank'">
                                  新页面
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="公众号类型" prop="type">
                            <el-radio-group v-model="formData.type" @change="typeChange">
                                <el-radio :label="'txt'">
                                  文本公众号
                                </el-radio>
                                <el-radio :label="'img'">
                                  图片公众号
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type=='img'">
                        <el-form-item label="上传图片" prop="picUrl" class="label_font">
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
<script setup lang="ts" name="platform-cms-links-link">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { fileUpload } from '/@/api/common'

const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()


const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const linkClass = ref([])
const classId = ref('')

const data = reactive({
    formData: {
        id: '',
        classId: '',
        name: '',
        url: '',
        target: '',
        type: '',
        picUrl: ''
    },
    queryParams: {
        classId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        name: [{ required: true, message: "公众号名称不能为空", trigger: ["blur", "change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'appname', label: `公众号名称`, show: true },
    { prop: 'id', label: `公众号类型`, show: true },
    { prop: 'appid', label: `AppID`, show: true },
    { prop: 'mchid', label: `支付商户号`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true },
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        classId: classId.value,
        name: '',
        url: 'javascript:void(0);',
        target: '_blank',
        type: 'txt',
        picUrl: ''
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

const classChange = (val: string) => {
    queryParams.value.classId = val
    list()
}

const typeChange = (val: string) => {
    if('txt'==val) {
        formData.value.picUrl = ''
    }
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

onMounted(()=>{
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
.label_font {
    font-weight: 700;
}
</style>