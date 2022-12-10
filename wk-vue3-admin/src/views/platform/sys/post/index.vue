<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['sys.manage.post.create']">新增
                </el-button>
            </el-col>
            <right-toolbar @quickSearch="quickSearch" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <el-table-column prop="name" label="职务名称">
            </el-table-column>
            <el-table-column prop="code" label="职务编号">
            </el-table-column>
            <el-table-column prop="location" label="排序" align="center" width="150">
                <template #default="scope">
                    <el-input-number
                        v-model="scope.row.location"
                        controls-position="right"
                        :min="1"
                        :max="100"
                        @change="locationChange(scope.row)"
                        style="width:100px;"
                    />
              </template>
            </el-table-column>
            <el-table-column fixed="right" header-align="center" align="center" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['sys.manage.post.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                        <el-button link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['sys.manage.post.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination
               :total="queryParams.totalCount"
               v-model:page="queryParams.pageNo"
               v-model:limit="queryParams.pageSize"
               @pagination="list"
            />
        </el-row>

        <el-dialog title="新增职务" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="职务名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入职务名称"/>
                </el-form-item>
                <el-form-item label="职务编号" prop="code">
                    <el-input v-model="formData.code" placeholder="请输入职务编号"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改职务" v-model="showUpdate" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-form-item label="职务名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入职务名称"/>
                </el-form-item>
                <el-form-item label="职务编号" prop="code">
                    <el-input v-model="formData.code" placeholder="请输入职务编号"/>
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
<script setup lang="ts" name="platform-sys-post">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, doLocation } from '/@/api/platform/sys/post'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])

const data = reactive({
    formData: {
        id: '',
        name: '',
        code: '',
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
        name: [{ required: true, message: "职务名称不能为空", trigger: ["blur","change"] }],
        code: [{ required: true, message: "职务编号不能为空", trigger: ["blur","change"] }]
    },
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        code: '',
        location: 0,
    }
    formEl?.resetFields()
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
    queryParams.value.pageNo = 1
    list()
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

// 排序
const locationChange = (row: any) => {
    doLocation({location: row.location, id: row.id}).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
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