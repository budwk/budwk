<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['sys.manage.task.create']">新增
                </el-button>
            </el-col>
            <right-toolbar @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过任务名称搜索" />
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" @sort-change="sortChange"
            :default-sort="defaultSort">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'disabled'" #default="scope">
                        <el-switch v-model="scope.row.disabled" :active-value="false" :inactive-value="true"
                            active-color="green" inactive-color="red" @change="disabledChange(scope.row)" />
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="执行记录" placement="top">
                        <el-button link type="primary" icon="Tickets" @click="handleView(scope.row)"
                            v-permission="['sys.manage.task']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="立即执行" placement="top">
                        <el-button link type="primary" icon="VideoPlay" @click="handleRun(scope.row)"
                            v-permission="['sys.manage.task.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="修改" placement="top">
                        <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['sys.manage.task.update']"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top" v-if="scope.row.path != '0001'">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['sys.manage.task.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>

        <el-dialog title="新增任务" v-model="showCreate" width="35%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item label="任务名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入任务名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="Ioc对象名" prop="iocName">
                            <el-input v-model="formData.iocName" placeholder="请输入Ioc对象名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="方法名" prop="jobName">
                            <el-input v-model="formData.jobName" placeholder="请输入执行方法名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="Cron表达式" prop="cron">
                            <el-row>
                                <el-col :span="24">
                                    <cron-input v-model="formData.cron" v-if="showCron" />
                                    <el-input v-model="formData.cron" placeholder="Cron表达式" v-else />
                                </el-col>
                                <el-col :span="24">
                                    <el-checkbox v-model="showCron" label="使用生成器" />
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="传参" prop="params">
                            <el-input v-model="formData.params" placeholder="根据代码实现可为Json格式数据" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="启用状态" prop="disabled">
                            <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
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

        <el-dialog title="修改任务" v-model="showUpdate" width="35%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="100px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item label="任务名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入任务名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="Ioc对象名" prop="iocName">
                            <el-input v-model="formData.iocName" placeholder="请输入Ioc对象名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="方法名" prop="jobName">
                            <el-input v-model="formData.jobName" placeholder="请输入执行方法名" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="Cron表达式" prop="cron">
                            <el-row>
                                <el-col :span="24">
                                    <cron-input v-model="formData.cron" v-if="showCron" />
                                    <el-input v-model="formData.cron" placeholder="Cron表达式" v-else />
                                </el-col>
                                <el-col :span="24">
                                    <el-checkbox v-model="showCron" label="使用生成器" />
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="传参" prop="params">
                            <el-input v-model="formData.params" placeholder="根据代码实现可为Json格式数据" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="启用状态" prop="disabled">
                            <el-switch v-model="formData.disabled" :active-value="false" :inactive-value="true"
                                active-color="green" inactive-color="red" />
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

        <el-dialog title="任务执行历史" v-model="showView" width="80%">
            <el-form :model="queryHistoryParams" ref="queryHistoryRef" :inline="true" label-width="68px">
                    <el-form-item label="执行月份" prop="username">
                        <el-date-picker
                            v-model="queryHistoryParams.month"
                            type="month"
                            placeholder="选择年月"
                            value-format="YYYYMM"
                            @change="handleSearchHistory"
                            />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" icon="Search" @click="handleSearchHistory">搜索</el-button>
                        <el-button icon="Refresh" @click="resetSearchHistory">重置</el-button>
                    </el-form-item>
            </el-form>
            <el-table v-loading="tableLoading" :data="tableHistoryData" row-key="id" :default-sort="defaultSort">
                <el-table-column prop="instanceId" label="实例ID" header-align="center" width="210px" />
                <el-table-column prop="jobId" :show-overflow-tooltip="true" label="作业ID" header-align="center"
                    width="210px" />
                <el-table-column prop="success" label="是否成功" header-align="center" align="center">
                    <template #default="scope">
                        <span v-if="scope.row.success">
                            成功
                        </span>
                        <span v-if="!scope.row.success" style="color:red;">
                            失败
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="message" label="错误信息" header-align="center" align="center"/>
                <el-table-column prop="tookTime" label="耗时" header-align="center" align="center">
                    <template #default="scope">
                        {{ scope.row.tookTime }} ms
                    </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" header-align="center">
                    <template #default="scope">
                        <span v-if="scope.row.endTime">{{
                                formatTime(scope.row.endTime)
                        }}</span>
                    </template>
                </el-table-column>
            </el-table>
            <el-row>
                <pagination :total="queryHistoryParams.totalCount" v-model:page="queryHistoryParams.pageNo"
                    v-model:limit="queryHistoryParams.pageSize" @pagination="listHistory" />
            </el-row>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="showView = false">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts" name="platform-sys-task">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, getHistoryList, doDelete, doDisable, doNow } from '/@/api/platform/sys/task'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { formatDate } from '/@/utils/common'
import CronInput from './CronInput.vue'

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const queryHistoryRef =  ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const showView = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const showCron = ref(true)
const tableHistoryData = ref([])

const defaultSort = ref({ prop: "createdAt", order: "descending" });

const data = reactive({
    formData: {
        id: '',
        name: '',
        iocName: '',
        jobName: '',
        cron: '',
        params: '',
        disabled: false,
    },
    queryParams: {
        name: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    queryHistoryParams: {
        month: formatDate(new Date(), 'yyyymm'),
        taskId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
    formRules: {
        name: [{ required: true, message: "任务名称不能为空", trigger: ["blur", "change"] }],
        cron: [{ required: true, message: "Cron表达式不能为空", trigger: ["blur", "change"] }],
        iocName: [{ required: true, message: "Ioc对象名不能为空", trigger: ["blur", "change"] }],
        jobName: [{ required: true, message: "方法名不能为空", trigger: ["blur", "change"] }]
    },
})

const { queryParams, formData, formRules, queryHistoryParams } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `任务名称`, show: true },
    { prop: 'iocName', label: `Ioc对象名`, show: true },
    { prop: 'jobName', label: `方法名`, show: true, fixed: false },
    { prop: 'cron', label: `Cron`, show: true, fixed: false, align: 'center', },
    { prop: 'params', label: `传参`, show: true, fixed: false, overflow: true },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false, width: 160, sortable: true },
    { prop: 'disabled', label: `启用状态`, show: true, fixed: false, width: 100, sortable: true }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        iocName: '',
        jobName: '',
        cron: '',
        params: '',
        disabled: false,
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

// 远程排序
const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    handleSearch()
}

// 搜索
const handleSearch = () => {
    queryParams.value.pageNo = 1
    list()
}

// 刷新
const quickSearch = (data: any) => {
    queryParams.value.name = data.keyword
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
    modal.confirm('确定删除 ' + row.name + '？').then(() => {
        return doDelete(row.id)
    }).then(() => {
        queryParams.value.pageNo = 1
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}

// 立即执行
const handleRun = (row: any) => {
    modal.confirm('确定立即执行 ' + row.name + ' 一次？').then(() => {
        return doNow(row.id)
    }).then(() => {
        modal.msgSuccess('执行成功')
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
const disabledChange = (row: any) => {
    doDisable({ disabled: row.disabled, id: row.id, path: row.path }).then((res: any) => {
        modal.msgSuccess(res.msg)
        list()
    }).catch(() => {
        setTimeout(() => {
            row.disabled = !row.disabled
        }, 300)
    })
}

const listHistory = () => {
    tableLoading.value = true
    getHistoryList(queryHistoryParams.value).then((res) => {
        tableLoading.value = false
        tableHistoryData.value = res.data.list as never
        queryHistoryParams.value.totalCount = res.data.totalCount as never
    }).catch(()=>{
        tableLoading.value = false
    })
}

// 显示执行记录
const handleView = (row: any) => {
    showView.value = true
    queryHistoryParams.value.taskId = row.id
    listHistory()
}

const handleSearchHistory = () =>{
    queryHistoryParams.value.pageNo = 1
    listHistory()
}

const resetSearchHistory = () => {
    queryHistoryRef.value?.resetFields()
    queryHistoryParams.value.pageNo = 1
    listHistory()
}

onMounted(() => {
    list()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>