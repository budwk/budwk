<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['sys.config.key.create']">新增
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <el-table-column prop="name" label="密钥名称" width="180">
            </el-table-column>
            <el-table-column prop="appid" label="APP ID" width="180">
            </el-table-column>
            <el-table-column prop="appkey" label="APP KEY">
                <template #default="scope">
                    <span v-if="isVisable(scope.row.appid)" class="bg-success">{{ scope.row.appkey }}</span><a v-if="!isVisable(scope.row.appid)" style="padding-left:5px;color: #06C;cursor: pointer;" @click="visiableData.push(scope.row.appid)">显示</a><a v-if="isVisable(scope.row.appid)" style="padding-left:5px;color: #06C;cursor: pointer;" @click="setNotVisable(scope.row.appid)">隐藏</a>
                </template>
            </el-table-column>
            <el-table-column prop="disabled" label="状态" align="center" width="120">
                <template #default="scope">
                    <el-switch
                        v-model="scope.row.disabled"
                        :active-value="false"
                        :inactive-value="true"
                        active-color="green"
                        inactive-color="red"
                        @change="disabledChange(scope.row)"
                        />
              </template>
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" width="120" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="删除" placement="top">
                        <el-button
link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['sys.config.key.delete']"></el-button>
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

        <el-dialog title="新增密钥" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="密钥名称" prop="name">
                    <el-input tabindex="1" v-model="formData.name" placeholder="请输入密钥名称"/>
                </el-form-item>
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
<script setup lang="ts" name="platform-sys-key">
import { onMounted, reactive, ref } from 'vue'
import { doCreate, getList, doDelete, doDisable } from '/@/api/platform/sys/key'
import { toRefs } from '@vueuse/core'
import { ElForm, ElInput } from 'element-plus'
import modal from '/@/utils/modal'

const createRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const visiableData = ref([])

const data = reactive({
    formData: {
        id: '',
        name: '',
        appid: '',
        appkey: '',
        disabled: false,
    },
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
        name: [{ required: true, message: "密钥名称不能为空", trigger: ["blur","change"] }],
    },
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        appid: '',
        appkey: '',
        disabled: false,
    }
    formEl?.resetFields()
}

// 是否显示密钥
const isVisable = (appid: string) => {
    if (visiableData.value.indexOf(appid as never) >= 0) {
        return true
    }
    return false
}

// 隐藏密钥
const setNotVisable = (appid: string) => {
    var index = visiableData.value.indexOf(appid as never)
    if (index >= 0) {
        visiableData.value.splice(index, 1)
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


// 删除按钮
const handleDelete = (row: any) => {
    modal.confirm('确定删除 '+ row.name + ' ？').then(() => {
        return doDelete(row.appid)
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

// 启用禁用
const disabledChange = (row: any) => {
    doDisable({disabled: row.disabled, appid: row.appid}).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(()=>{
        setTimeout(() => {
            row.disabled = !row.disabled
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