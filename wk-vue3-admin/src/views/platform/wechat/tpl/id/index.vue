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
                    v-permission="['wx.tpl.id.create']">新增编号
                </el-button>
            </el-col>
            <el-col :span="1.5">
                        <el-button
type="danger" plain icon="Delete" :disabled="select_ids.length<1" @click="handleDeleteMore"
                            v-permission="['wx.tpl.id.delete']">删除</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe @sort-change="sortChange"
            @selection-change="handleSelect"
            :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <el-table-column type="selection" width="50" fixed="left" />
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
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.tpl.id.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增编号" v-model="showCreate" width="40%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="id" label="模板库ID">
                            <el-input v-model="formData.id" maxlength="120" placeholder="模板库ID" auto-complete="off"
                                tabindex="1" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-alert
                        title="微信公众号后台：模块消息->模板库 中的编号，如 TM00303"
                        type="warning"
                        effect="dark"
                        />
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

    </div>
</template>
<script setup lang="ts" name="platform-wechat-tpl-id">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, doCreate, doDelete, doDeleteMore } from '/@/api/platform/wechat/tpl_id'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'

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
const select_template_ids = ref([])

const data = reactive({
    formData: {
        id: '',
        wxid: '',
        title: '',
        content: ''
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
        id: [{ required: true, message: "模版库ID", trigger: ["blur", "change"] }]
    }
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'id', label: `模版库ID`, show: true },
    { prop: 'template_id', label: `模版编号(template_id)`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        wxid: wxid.value,
        title: '',
        content: ''
    }
    formEl?.resetFields()
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
    select_template_ids.value = selection.map(item => item.template_id)
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

// 批量删除
const handleDeleteMore = () => {
    if (select_ids.value.length == 0) {
        modal.msgError('请选择要删除的数据')
        return
    }
    modal.confirm('确定删除 ' + select_template_ids.value.join(',') + '？').then(() => {
        return doDeleteMore(select_ids.value.toString(), select_template_ids.value.toString())
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

onMounted(() => {
    listAccount()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>