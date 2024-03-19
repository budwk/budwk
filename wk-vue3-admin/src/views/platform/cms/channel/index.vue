<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" icon="Plus" plain @click="handleCreate"
                    v-permission="['cms.content.channel.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Sort" @click="handleSort"
                    v-permission="['cms.content.channel.update']">排序</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain v-if="isExpandAll" icon="FolderOpened" @click="toggleExpandAll">展开</el-button>
                <el-button plain v-else icon="Folder" @click="toggleExpandAll">折叠</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-select v-model="siteId" class="m-2" placeholder="切换站点" @change="siteChange">
                    <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-col>
      </el-row>
      <el-table v-if="showTreeTable" v-loading="tableLoading" :data="tableData" row-key="id"
            :default-expand-all="isExpandAll" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="true" :align="item.align" :width="item.width">
                    <template v-if="item.prop == 'name'" #default="scope">
                        <svg-icon v-if="scope.row&&scope.row.icon" :icon-class="scope.row.icon" />
                        {{ scope.row.name }}
                    </template>
                    <template v-if="item.prop == 'type'" #default="scope">
                        <span v-if="scope.row.type === 'ARTICLE'">
                            文章
                        </span>
                        <span v-if="scope.row.type === 'PHOTO'">
                            相册
                        </span>
                    </template>
                    <template v-if="item.prop == 'showit'" #default="scope">
                        <i v-if="scope.row.showit" class="fa fa-circle" style="color:green;" />
                        <i v-if="!scope.row.showit" class="fa fa-circle" style="color:red" />
                    </template>
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'disabled'" #default="scope">
                        <el-switch v-model="scope.row.disabled" :active-value="false" :inactive-value="true"
                            active-color="green" inactive-color="red" @change="disabledChange(scope.row)" />
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="right" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <div style="padding-right:30px;">
                        <el-tooltip content="新增子栏目" placement="top">
                            <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                                v-permission="['cms.content.channel.create']"></el-button>
                        </el-tooltip>
                        <el-tooltip content="修改" placement="top">
                            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                                v-permission="['cms.content.channel.update']"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="top">
                            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                                v-permission="['cms.content.channel.delete']"></el-button>
                        </el-tooltip>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="新增栏目" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                    <el-col :span="12">
                        <el-form-item label="上级栏目" prop="parentId">
                            <el-tree-select v-model="formData.parentId" :data="unitOptions"
                                :props="{ value: 'id', label: 'name', children: 'children' }" value-key="id"
                                placeholder="选择上级栏目" check-strictly :render-after-expand="false" style="width:100%"
                                />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属站点" prop="siteId">
                            <el-select v-model="siteId" class="m-2" placeholder="所属站点" disabled style="width:100%">
                                <el-option v-for="item in sites" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>

                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入栏目名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目标识" prop="code">
                            <el-input v-model="formData.code" placeholder="栏目标识" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目类型" prop="type">
                          <el-radio-group v-model="formData.type">
                                <el-radio :value="'ARTICLE'">
                                  文章
                                </el-radio>
                                <el-radio :value="'PHOTO'">
                                  相册
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="外链URL" prop="href">
                            <el-input v-model="formData.url" placeholder="外链地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="打开方式" prop="href">
                          <el-radio-group v-model="formData.target">
                                <el-radio :value="'_blank'">
                                    新页面
                                </el-radio>
                                <el-radio :value="'_self'">
                                    本页面
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否显示" prop="showit">
                            <el-radio-group v-model="formData.showit">
                                <el-radio :value="true">
                                    显示
                                </el-radio>
                                <el-radio :value="false">
                                    隐藏
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目状态" prop="disabled">
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

        <el-dialog title="修改栏目" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="80px">
                <el-row :gutter="10">
                  <el-col :span="12">
                        <el-form-item label="栏目名称" prop="name">
                            <el-input v-model="formData.name" placeholder="请输入栏目名称" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目标识" prop="code">
                            <el-input v-model="formData.code" placeholder="栏目标识" maxlength="100" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目类型" prop="type">
                          <el-radio-group v-model="formData.type">
                                <el-radio :value="'ARTICLE'">
                                  文章
                                </el-radio>
                                <el-radio :value="'PHOTO'">
                                  相册
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="外链URL" prop="href">
                            <el-input v-model="formData.url" placeholder="外链地址" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="打开方式" prop="href">
                          <el-radio-group v-model="formData.target">
                                <el-radio :value="'_blank'">
                                    新页面
                                </el-radio>
                                <el-radio :value="'_self'">
                                    本页面
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否显示" prop="showit">
                            <el-radio-group v-model="formData.showit">
                                <el-radio :value="true">
                                    显示
                                </el-radio>
                                <el-radio :value="false">
                                    隐藏
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="栏目状态" prop="disabled">
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

        <el-dialog title="栏目排序" v-model="showSort" width="30%">
            <el-tree ref="sortTree" :data="sortData" draggable :allow-drop="sortAllowDrop" node-key="id"
                :props="{ label: 'name', children: 'children' }">
            </el-tree>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="sort">保 存</el-button>
                    <el-button @click="showSort = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>
  </div>
</template>
<script setup lang="ts" name="platform-cms-channel">
import { nextTick, onMounted, reactive, ref, toRefs } from 'vue'
import modal from '/@/utils/modal'
import { getSiteList, doCreate, doUpdate, getInfo, getList, doDelete, doSort } from '/@/api/platform/cms/channel'
import { ElForm } from 'element-plus'
import { handleTree } from '/@/utils/common'

const queryRef = ref<InstanceType<typeof ElForm>>()
const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()
const isExpandAll = ref(true)
const showCreate = ref(false)
const showUpdate = ref(false)
const showSort = ref(false)

const tableLoading = ref(false)
const tableData = ref([])
const sortData = ref([])
const unitOptions = ref([])
const unitList = ref([])
const showTreeTable = ref(true)
const sites = ref([])
const siteId = ref('')

const data = reactive({
    formData: {
        id: '',
        siteId: '',
        parentId: '',
        name: '',
        code: '',
        disabled: false,
        showit: true,
        type: 'ARTICLE',
        target: '_blank'
    },
    queryParams: {
        siteId: '',
        name: '',
    },
    formRules: {
        code: [{ required: true, message: "栏目标识不能为空", trigger: "blur" }],
        name: [{ required: true, message: "栏目名称不能为空", trigger: "blur" }],
    },
})
const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'name', label: `栏目名称`, show: true, fixed: false },
    { prop: 'code', label: `栏目标识`, show: true, fixed: false },
    { prop: 'type', label: `栏目类型`, show: true, fixed: false },
    { prop: 'url', label: `外链URL`, show: true, fixed: false },
    { prop: 'showit', label: `是否展示`, show: true, fixed: false, align: 'center' },
    { prop: 'disabled', label: `状态`, show: true, fixed: false, align: 'center' }
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        siteId: '',
        parentId: '',
        name: '',
        code: '',
        disabled: false,
        showit: true,
        type: 'ARTICLE',
        target: '_blank'
    }
    formEl?.resetFields()
}

// 展开/折叠
const toggleExpandAll = () => {
    showTreeTable.value = false
    isExpandAll.value = !isExpandAll.value
    nextTick(() => {
        showTreeTable.value = true
    })
}

const siteChange = (val: string) => {
    queryParams.value.siteId = val
    list()
}

const siteList = () => {
    getSiteList().then((res) => {
        sites.value = res.data
        siteId.value = sites.value[0].id
        queryParams.value.siteId = sites.value[0].id
        formData.value.siteId = sites.value[0].id
        list()
    })
}

const list = () => {
    tableLoading.value = true
    getList(queryParams.value.siteId).then((res) => {
        tableLoading.value = false
        unitList.value = res.data
        tableData.value = handleTree(res.data) as never
        unitOptions.value = handleTree(res.data) as never
        sortData.value = handleTree(JSON.parse(JSON.stringify(res.data))) as never
    })
}

// 新增按钮
const handleCreate = (row: any) => {
    resetForm(createRef.value)
    if (row && row.id) {
        formData.value.parentId = row.id
    }
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
    modal.confirm('确定删除 ' + row.name +' ?').then(() => {
        return doDelete(row.id)
    }).then(() => {
        list()
        modal.msgSuccess('删除成功')
    }).catch(() => { })
}

// 拖拽是否允许跨级
const sortAllowDrop = (moveNode: any, inNode: any, type: string) => {
    if (moveNode.data.parentId === inNode.data.parentId) {
        return type === 'prev'
    }
}

// 排序
const handleSort = () => {
    showSort.value = true
}

// 提交新增
const create = () => {
    if (!createRef.value) return
    createRef.value.validate((valid) => {
        if (valid) {
            formData.value.siteId = siteId.value
            doCreate(formData.value).then((res: any) => {
                modal.msgSuccess(res.msg)
                showCreate.value = false
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

// 获取ids
const getTreeIds = (ids: string[], data: any[]) => {
    data.forEach((obj) => {
        ids.push(obj.id)
        if (obj.children && obj.children.length > 0) {
            getTreeIds(ids, obj.children)
        }
    })
}

// 提交排序
const sort = () => {
    let ids: string[] = []
    getTreeIds(ids, sortData.value)
    doSort(ids.toString(), siteId.value).then((res: any) => {
        modal.msgSuccess(res.msg)
        showSort.value = false
        list()
    })
}

onMounted(() => {
    siteList()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>