<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['sys.config.param.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-select v-model="appId" class="m-2" placeholder="切换应用" @change="appChange">
                    <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-col>
            <right-toolbar :columns="columns" @quickSearch="quickSearch" :quickSearchShow="true"
                quickSearchPlaceholder="通过参数Key搜索" />
        </el-row>

        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width">
                    <template v-if="item.prop == 'opened'" #default="scope">
                        <i v-if="scope.row.opened" class="fa fa-circle" style="color:green;" />
                        <i v-if="!scope.row.opened" class="fa fa-circle" style="color:red" />
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span v-if="scope.row.type">{{ scope.row.type.text }}</span>
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width" width="150">
                <template #default="scope">
                    <div>
                        <el-tooltip content="修改" placement="top">
                            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                                v-permission="['sys.config.param.update']"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="top">
                            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                                v-permission="['sys.config.param.delete']"></el-button>
                        </el-tooltip>
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>
        <el-dialog title="新增参数" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    <el-col :span="12">
                        <el-form-item label="参数类型" prop="name">
                            <el-radio-group v-model="formData.type">
                                <el-radio :label="item.value" v-for="(item) in types" :key="item.value">
                                    {{ item.text }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属应用" prop="appId">
                            <el-select v-model="formData.appId" class="m-2" placeholder="所属应用" disabled style="width:100%">
                                <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="参数Key" prop="configKey">
                            <el-input v-model="formData.configKey" placeholder="参数Key" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'TEXT'">
                        <el-form-item prop="configValue" label="参数值">
                            <el-input v-model="formData.configValue" maxlength="100" placeholder="参数值"
                                auto-complete="off" tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'BOOL'">
                        <el-form-item prop="configValue" label="参数值">
                            <el-radio-group v-model="formData.configValue">
                                <el-radio label="true">是</el-radio>
                                <el-radio label="false">否</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'IMAGE'">
                        <el-form-item prop="configValue" label="参数值" style="font-weight:700">
                            <el-upload 
                            ref="uploadRef"
                            :limit="1"
                            accept=".png, .jpg, .svg"
                            :show-file-list="false"
                            @change="imgUpload"
                            :auto-upload="false"
                            list-type="picture-card"
                                >
                                <img v-if="formData.configValue" :src="platformInfo.AppFileDomain + formData.configValue"
                                    class="avatar">
                                <i v-else class="fa fa-plus avatar-uploader-icon" />
                            </el-upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="参数备注" prop="note">
                            <el-input v-model="formData.note" placeholder="备注说明" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="是否开放" prop="opened">
                            <el-radio-group v-model="formData.opened">
                                <el-radio :label="true">
                                    是
                                </el-radio>
                                <el-radio :label="false">
                                    否
                                </el-radio>
                            </el-radio-group>
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

        <el-dialog title="修改参数" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    <el-col :span="12">
                        <el-form-item label="参数类型" prop="name">
                            <el-radio-group v-model="formData.type">
                                <el-radio :label="item.value" v-for="(item) in types" :key="item.value">
                                    {{ item.text }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属应用" prop="appId">
                            <el-select v-model="formData.appId" class="m-2" placeholder="所属应用" disabled style="width:100%">
                                <el-option v-for="item in apps" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="参数Key" prop="configKey">
                            <el-input v-model="formData.configKey" placeholder="参数Key" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'TEXT'">
                        <el-form-item prop="configValue" label="参数值">
                            <el-input v-model="formData.configValue" maxlength="100" placeholder="参数值"
                                auto-complete="off" tabindex="2" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'BOOL'">
                        <el-form-item prop="configValue" label="参数值">
                            <el-radio-group v-model="formData.configValue">
                                <el-radio label="true">是</el-radio>
                                <el-radio label="false">否</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.type === 'IMAGE'">
                        <el-form-item prop="configValue" label="参数值" style="font-weight:700">
                            <el-upload 
                            ref="uploadRef"
                            :limit="1"
                            accept=".png, .jpg, .svg"
                            :show-file-list="false"
                            @change="imgUpload"
                            :auto-upload="false"
                            list-type="picture-card"
                                >
                                <img v-if="formData.configValue" :src="platformInfo.AppFileDomain + formData.configValue"
                                    class="avatar">
                                <i v-else class="fa fa-plus avatar-uploader-icon" />
                            </el-upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="参数备注" prop="note">
                            <el-input v-model="formData.note" placeholder="备注说明" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="是否开放" prop="opened">
                            <el-radio-group v-model="formData.opened">
                                <el-radio :label="true">
                                    是
                                </el-radio>
                                <el-radio :label="false">
                                    否
                                </el-radio>
                            </el-radio-group>
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
<script setup lang="ts" name="platform-sys-param">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { getData, doCreate, doUpdate, getInfo, getList, doDelete, } from '/@/api/platform/sys/param'
import { findOneValue, handleTree } from '/@/utils/common'
import { buildValidatorData } from '/@/utils/validate'
import { usePlatformInfo } from "/@/stores/platformInfo"
import { fileUpload } from '/@/api/common'
import modal from '/@/utils/modal'

const platformInfo = usePlatformInfo()

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const tableLoading = ref(false)
const tableData = ref([])
const showCreate = ref(false)
const showUpdate = ref(false)
const appId = ref('')
const apps = ref([])
const types = ref([])

const data = reactive({
    formData: {
        id: '',
        appId: '',
        configKey: '',
        configValue: '',
        type: 'TEXT',
        note: '',
        opened: true,
    },
    queryParams: {
        appId: '',
        configKey: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        configKey: [{ required: true, message: "参数Key不能为空", trigger: "blur" }],
        configValue: [{ required: true, message: "参数值不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'configKey', label: `参数Key`, show: true, fixed: false },
    { prop: 'configValue', label: `参数值`, show: true, fixed: false },
    { prop: 'type', label: `参数类型`, show: true, fixed: false },
    { prop: 'note', label: `备注`, show: true, fixed: false },
    { prop: 'opened', label: `是否开放`, show: true, fixed: false, width: '80', align: 'left' }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        appId: '',
        configKey: '',
        configValue: '',
        type: 'TEXT',
        note: '',
        opened: true,
    }
    formEl?.resetFields()
}

const appList = () => {
    getData().then((res) => {
        apps.value = res.data.apps
        types.value = res.data.types
        appId.value = apps.value[0].id
        queryParams.value.appId = apps.value[0].id
        formData.value.appId = apps.value[0].id
        list()
    })
}

const appChange = (val: string) => {
    queryParams.value.appId = val
    list()
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount
    })
}

const imgUpload = (file: any) => {
    let fd = new FormData()
    fd.append('Filedata', file.raw)
    fileUpload(fd,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.configValue = res.data.url
        }
    })
}

onMounted(() => {
    appList()
})

// 快速搜索&刷新
const quickSearch = (data: any) => {
    queryParams.value.configKey = data.keyword
    queryParams.value.pageNo = 1
    list()
}

// 重置查询
const resetSearch = () => {
    queryParams.value.configKey = ''
    queryParams.value.pageNo = 1
    list()
}


// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    formData.value.appId = appId.value
    showCreate.value = true
}

// 修改按钮
const handleUpdate = (row: any) => {
    getInfo(row.id).then((res: any) => {
        formData.value = res.data
        formData.value.type = formData.value.type.value
        showUpdate.value = true
    })
}

// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除 ' + row.configKey + ' ?').then(() => {
        return doDelete(row.id)
    }).then(() => {
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
                resetSearch()
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
                resetSearch()
            })
        }
    })
}


</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.table-col-center {
    text-align: center;
}
.avatar {
    width: 145px;
    height: 145px;
    display: block;
    padding: 2px 2px;
    border-radius: 6px;
}
</style>
