<template>
    <div class="app-container">
        <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="单位名称" prop="name">
                <el-input v-model="queryParams.name" placeholder="请输入单位名称" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="部门负责人" prop="leaderName">
                <el-input v-model="queryParams.leaderName" placeholder="请输入部门负责人" clearable
                    @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
                <el-button icon="Refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
        </el-form>
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" @click="handleCreate" v-permission="['sys.manage.unit.create']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button icon="Sort" @click="toggleExpandAll" v-permission="['sys.manage.unit.update']">排序</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button v-if="isExpandAll" type="info" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button v-else type="info" icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" :extendSearch="true" :columns="columns"
                @quickSearch="quickSearch" :quickSearchShow="true" quickSearchPlaceholder="通过单位名称搜索" />
        </el-row>

        <el-table v-if="refreshTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show" :show-overflow-tooltip="true">
                    <template v-if="item.prop == 'name'" #default="scope">
                        <i v-if="scope.row.type && scope.row.type.value === 'GROUP'" class="fa fa-building" />
                        <i v-if="scope.row.type && scope.row.type.value === 'COMPANY'" class="fa fa-home" />
                        {{ scope.row.name }}
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span>{{ scope.row.type?.text }}</span>
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'disabled'" #default="scope">
                        <el-tag v-if="scope.row.disabled" type="danger">禁用</el-tag>
                        <el-tag v-else type="success">启用</el-tag>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button link type="primary" icon="Plus" @click="handleCreate(scope.row)"
                        v-permission="['sys.manage.unit.create']">新增</el-button>
                    <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                        v-permission="['sys.manage.unit.update']">修改</el-button>
                    <el-button v-if="scope.row.path != '0001'" link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['sys.manage.unit.delete']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增单位" v-model="showCreate" width="50%" append-to-body>
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="120px">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="上级单位" prop="parentId">
                            <el-tree-select v-model="formData.parentId" :data="unitOptions"
                                :props="{ value: 'id', label: 'name', children: 'children' }" value-key="id"
                                placeholder="选择上级单位" check-strictly :render-after-expand="false" style="width:100%"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入单位名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位类型" prop="type">
                            <el-radio-group v-model="formData.type" @change="typeChange">
                                <el-radio v-for="item in types" :key="item.value" :label="item.value">
                                    {{ item.label }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位别名" prop="aliasName">
                            <el-input v-model="formData.aliasName" placeholder="单位别名" maxlength="100"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位编码" prop="unitcode">
                            <el-input v-model="formData.unitcode" placeholder="单位编码" maxlength="32" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="单位地址" prop="address">
                            <el-input v-model="formData.address" placeholder="单位详细地址" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="固定电话" prop="telephone">
                            <el-input v-model="formData.telephone" placeholder="单位固定电话" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="电子邮箱" prop="email">
                            <el-input v-model="formData.email" placeholder="单位电子邮箱" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="单位网站" prop="website">
                            <el-input v-model="formData.website" placeholder="单位网站" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type == 'UNIT'">
                        <el-form-item label="部门负责人" prop="leaderName">
                            <el-input v-model="formData.leaderName" placeholder="负责人姓名" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type == 'UNIT'">
                        <el-form-item label="负责人电话" prop="leaderMobile">
                            <el-input v-model="formData.leaderMobile" placeholder="负责人电话" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" >
                        <el-form-item label="单位状态" prop="disabled">
                            <el-radio-group v-model="formData.disabled">
                                <el-radio :label="false">
                                    启用
                                </el-radio>
                                <el-radio :label="true">
                                    禁用
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="note" label="备注">
                            <el-input v-model="formData.note" type="textarea" />
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

        <el-dialog title="修改单位" v-model="showUpdate" width="50%" append-to-body>
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="120px">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="单位名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入单位名称" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位类型" prop="type">
                            <el-radio-group v-model="formData.type" @change="typeChange">
                                <el-radio v-for="item in types" :key="item.value" :label="item.value">
                                    {{ item.label }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位别名" prop="aliasName">
                            <el-input v-model="formData.aliasName" placeholder="单位别名" maxlength="100"
                                auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="单位编码" prop="unitcode">
                            <el-input v-model="formData.unitcode" placeholder="单位编码" maxlength="32" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="单位地址" prop="address">
                            <el-input v-model="formData.address" placeholder="单位详细地址" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="固定电话" prop="telephone">
                            <el-input v-model="formData.telephone" placeholder="单位固定电话" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="电子邮箱" prop="email">
                            <el-input v-model="formData.email" placeholder="单位电子邮箱" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type !== 'UNIT'">
                        <el-form-item label="单位网站" prop="website">
                            <el-input v-model="formData.website" placeholder="单位网站" auto-complete="off" type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type == 'UNIT'">
                        <el-form-item label="部门负责人" prop="leaderName">
                            <el-input v-model="formData.leaderName" placeholder="负责人姓名" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="formData.type == 'UNIT'">
                        <el-form-item label="负责人电话" prop="leaderMobile">
                            <el-input v-model="formData.leaderMobile" placeholder="负责人电话" auto-complete="off"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" >
                        <el-form-item label="单位状态" prop="disabled">
                            <el-radio-group v-model="formData.disabled">
                                <el-radio :label="false">
                                    启用
                                </el-radio>
                                <el-radio :label="true">
                                    禁用
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="note" label="备注">
                            <el-input v-model="formData.note" type="textarea" />
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
<script setup lang="ts">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import { ElForm } from 'element-plus'
import { doCreate, getInfo, getList } from '/@/api/platform/sys/unit'
import { handleTree } from '/@/utils/common'
import { buildValidatorData } from '/@/utils/validate'
import modal from '/@/utils/modal'

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const isExpandAll = ref(true)
const showSearch = ref(false)
const refreshTable = ref(true)
const tableLoading = ref(false)
const tableData = ref([])
const unitOptions = ref([]);
const showCreate = ref(false)
const showUpdate = ref(false)


const data = reactive({
    formData: {
        id: '',
        parentId: '',
        name: '',
        aliasName: '',
        type: 'UNIT',
        unitcode: '',
        address: '',
        telephone: '',
        email: '',
        website: '',
        leaderName: '',
        leaderMobile: '',
        note: '',
        disabled: false
    },
    types: [
        { value: 'COMPANY', label: '分公司' },
        { value: 'UNIT', label: '部门' }
    ],
    leaders: [],
    highers: [],
    assigners: [],
    selectLeaders: [],
    selectHighers: [],
    selectAssigners: [],
    queryParams: {
        name: '',
        leaderName: '',
    },
    formRules: {
        parentId: [{ required: true, message: "上级部门不能为空", trigger: "blur" }],
        name: [{ required: true, message: "单位名称不能为空", trigger: "blur" }],
        unitcode: [{ required: true, message: "单位编码不能为空", trigger: "blur" }, buildValidatorData({ name: 'code', title:'单位编码' })],
        email: [buildValidatorData({ name: 'email', title:'电子邮箱' })],
        leaderMobile: [buildValidatorData({ name: 'mobile' })]
    },
})
const { queryParams, formData, formRules, types,leaders,highers,assigners,selectLeaders,selectHighers,selectAssigners } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `单位名称`, show: true, fixed: false },
    { prop: 'type', label: `单位类型`, show: true, fixed: false },
    { prop: 'leaderName', label: `部门负责人`, show: true, fixed: false },
    { prop: 'disabled', label: `状态`, show: true, fixed: false },
    { prop: 'createdAt', label: `创建时间`, show: true, fixed: false }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formData.value = {
        id: '',
        parentId: '',
        name: '',
        aliasName: '',
        type: 'UNIT',
        unitcode: '',
        address: '',
        telephone: '',
        email: '',
        website: '',
        leaderName: '',
        leaderMobile: '',
        note: '',
        disabled: false
    }
    formEl?.resetFields()
}


// 查询表格
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = handleTree(res.data) as never
        unitOptions.value = handleTree(res.data) as never
    })
}


onMounted(() => {
    list()
})

// 快速搜索
const quickSearch = (data: any) => {
    queryParams.value.name = data.keyword
    queryParams.value.leaderName = ''
    list()
}

// 高级搜索
const handleSearch = () => {
    list()
}

// 重置搜索
const resetSearch = () => {
    queryRef.value?.resetFields()
    list()
}

// 展开/折叠
const toggleExpandAll = () => {
    refreshTable.value = false;
    isExpandAll.value = !isExpandAll.value
    nextTick(() => {
        refreshTable.value = true
    })
}

// 表单切换单位类型
const typeChange = () => {
    formData.value.address = ''
    formData.value.website = ''
    formData.value.telephone = ''
    formData.value.email = ''
    formData.value.leaderName = ''
    formData.value.leaderMobile = ''
}

// 查询用户
const searchUser = () =>{

}

// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    if(row && row.id){
        formData.value.parentId = row.id
    }
    showCreate.value = true
}

const handleUpdate = (row: any) => {
    getInfo(row.id).then((res:any)=>{
        formData.value = res.data
        showUpdate.value = true
    })
}

const handleDelete = (row: any) => {

}

// 提交新增
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            doCreate(formData.value).then((res: any)=>{
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
            //...
        }
    })
}

</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-sys-unit'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>