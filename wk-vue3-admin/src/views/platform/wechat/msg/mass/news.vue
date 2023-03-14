<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate"
                    v-permission="['wx.msg.mass.news.create']">新建图文
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe  @sort-change="sortChange" :default-sort="{ prop: 'createdAt', order: 'descending' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'picurl'" #default="scope">
                        <img v-if="scope.row.picurl!=''" :src="platformInfo.AppFileDomain+scope.row.picurl" width="30" height="30">
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="查看" placement="top">
                        <el-button link type="primary" icon="View" @click="handleView(scope.row)"
                            ></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                        <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)"
                            v-permission="['wx.msg.mass.news.delete']"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <el-row>
            <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                v-model:limit="queryParams.pageSize" @pagination="list" />
        </el-row>
        <el-dialog title="新增公众号" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="130px">
                <el-row :gutter="10" style="padding-right:20px;">
                    <el-col :span="24">
                        <el-form-item prop="parentId" label="父级菜单">
                            <el-cascader v-model="formData.parentId" style="width: 100%" value-key="id"
                                :options="menuOptions"
                                :props="{ checkStrictly: true, value: 'id', label: 'menuName', children: 'children' }"
                                tabindex="1" placeholder="父级菜单" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item prop="menuName" label="菜单名称">
                            <el-input v-model="formData.menuName" maxlength="100" placeholder="菜单名称" auto-complete="off"
                                tabindex="1" type="text" />
                            <el-alert style="height: 30px;margin-top: 3px;" title="一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替"
                                type="warning" />
                            <el-alert style="height: 30px;margin-top: 3px;" title="只可设置3个一级菜单，只可设置5个二级菜单" type="warning" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item class="is-required" prop="menuType" label="菜单类型">
                            <el-radio-group v-model="formData.menuType" size="medium">
                                <el-radio label="">菜单</el-radio>
                                <el-radio label="view">链接</el-radio>
                                <el-radio label="click">事件</el-radio>
                                <el-radio label="miniprogram">小程序</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="formData.menuType == 'view'" class="is-required" prop="url" label="URL">
                            <el-input v-model="formData.url" placeholder="https://" auto-complete="off" tabindex="3"
                                type="text" />
                            <el-checkbox v-model="checked1" @click="checkedChange1">网页Oauth2.0</el-checkbox>
                            <el-checkbox v-model="checked2" @click="checkedChange2">应用Oauth2.0</el-checkbox>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-if="formData.menuType == 'miniprogram'" class="is-required" prop="url" label="url">
                            <el-input v-model="formData.url" placeholder="小程序URL" auto-complete="off" tabindex="3"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-if="formData.menuType == 'miniprogram'" class="is-required" prop="appid"
                            label="appid">
                            <el-input v-model="formData.appid" placeholder="appid" auto-complete="off" tabindex="4"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-if="formData.menuType == 'miniprogram'" class="is-required" prop="pagepath"
                            label="pagepath">
                            <el-input v-model="formData.pagepath" placeholder="小程序入口页" auto-complete="off" tabindex="5"
                                type="text" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item v-if="formData.menuType == 'click'" class="is-required" label="绑定事件" prop="menuKey">
                            <el-select v-model="formData.menuKey" placeholder="关键词">
                                <el-option v-for="item in keyList" :key="item.id" :label="item.value" :value="item.id" />
                            </el-select>
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
    </div>
</template>
<script setup lang="ts" name="platform-wechat-msg-news">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { getList, getNewsList, doCreate, doDelete, doPush } from '/@/api/platform/wechat/mass'
import { getAccountList } from '/@/api/platform/wechat/account'
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

const data = reactive({
    formData: {
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
        title: [{ required: true, message: '图文标题', trigger: 'blur' }],
        picurl: [{ required: true, message: '缩略图', trigger: 'blur' }]
    }
})

const { queryParams, formData, formRules} = toRefs(data)

const columns = ref([
    { prop: 'picurl', label: ``, show: true, width: 80 },
    { prop: 'title', label: `标题`, show: true },
    { prop: 'author', label: `作者`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true, sortable: true, width: 180 }
])


// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
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

const sortChange = (column: any) => {
    queryParams.value.pageOrderName = column.prop
    queryParams.value.pageOrderBy = column.order
    list()
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getNewsList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
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