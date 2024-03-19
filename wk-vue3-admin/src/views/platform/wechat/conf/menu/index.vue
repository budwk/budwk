<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['wx.conf.menu.create']">新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="info" icon="Promotion" @click="handlePush"
                    v-permission="['wx.conf.menu.update']">发布至微信
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button plain type="success" icon="Sort" @click="handleCreate"
                    v-permission="['wx.conf.menu.update']">菜单排序
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-select v-model="wxid" class="m-2" placeholder="切换公众号" @change="accountChange">
                    <el-option v-for="item in accounts" :key="item.id" :label="item.appname" :value="item.id" />
                </el-select>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id" :default-expand-all="true"
            :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
            <template v-for="(item, idx) in columns" :key="idx">
                <el-table-column :prop="item.prop" :label="item.label" :fixed="item.fixed" v-if="item.show"
                    :show-overflow-tooltip="item.overflow" :align="item.align" :width="item.width"
                    :sortable="item.sortable">
                    <template v-if="item.prop == 'createdAt'" #default="scope">
                        <span>{{ formatTime(scope.row.createdAt) }}</span>
                    </template>
                    <template v-if="item.prop == 'menuType'" #default="scope">
                        <span v-if="scope.row.menuType==''">菜单</span>
                        <span v-if="scope.row.menuType=='view'">链接</span>
                        <span v-if="scope.row.menuType=='click'">事件</span>
                        <span v-if="scope.row.menuType=='miniprogram'">小程序</span>
                    </template>
                    <template v-if="item.prop == 'id'" #default="scope">
                        <span v-if="scope.row.menuType==''">-</span>
                        <span v-if="scope.row.menuType=='view'">{{ scope.row.url }}</span>
                        <span v-if="scope.row.menuType=='click'">关键词: {{ scope.row.menuKey }}</span>
                        <span v-if="scope.row.menuType=='miniprogram'">小程序: {{ scope.row.appid }}</span>
                    </template>
                </el-table-column>
            </template>
            <el-table-column fixed="right" header-align="center" align="center" label="操作"
                class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="新建子菜单" placement="top">
                        <el-button link type="primary" icon="CirclePlus" @click="handleCreate(scope.row)"
                            v-permission="['wx.conf.account.create']"></el-button>
                    </el-tooltip>
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
                            <el-radio-group v-model="formData.menuType">
                                <el-radio value="">菜单</el-radio>
                                <el-radio value="view">链接</el-radio>
                                <el-radio value="click">事件</el-radio>
                                <el-radio value="miniprogram">小程序</el-radio>
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

        <el-dialog title="修改公众号" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="125px">
                <el-row :gutter="10" style="padding-right:20px;">
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
                            <el-radio-group v-model="formData.menuType">
                                <el-radio value="">菜单</el-radio>
                                <el-radio value="view">链接</el-radio>
                                <el-radio value="click">事件</el-radio>
                                <el-radio value="miniprogram">小程序</el-radio>
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
                    <el-button type="primary" @click="update">确 定</el-button>
                    <el-button @click="showUpdate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

    </div>
</template>
<script setup lang="ts" name="platform-wechat-conf-menu">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete, doPush, doSort, getKeywordList } from '/@/api/platform/wechat/menu'
import { getAccountList } from '/@/api/platform/wechat/account'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { handleTree } from '/@/utils/common'


const platformInfo = usePlatformInfo()

const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()


const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const sortData = ref([])
const accounts = ref([])
const wxid = ref('')
const menuOptions = ref([])
const checked1 = ref(false)
const checked2 = ref(false)
const account = ref({ wxname: '', appid: '', wxid: '' })
const keyList = ref([])

// 验证URL
const validateUrl = (rule: any, value: any, callback: any) => {
    // const exp = new RegExp(/http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- .\/?%&=]*)?/)
    if ((formData.value.menuType === 'view' || formData.value.menuType === 'miniprogram') && formData.value.url === '') {
        callback(new Error('请输入正确的URL路径'))
    } else {
        callback()
    }
}

// 验证关键词
const validateK = (rule: any, value: any, callback: any) => {
    if (formData.value.menuType === 'click' && (typeof (formData.value.menuKey) === 'undefined' || formData.value.menuKey === '')) {
        callback(new Error('请选择关键词'))
    } else {
        callback()
    }
}
// 验证小程序APPID
const validateA = (rule: any, value: any, callback: any) => {
    if (formData.value.menuType === 'miniprogram' && (typeof (formData.value.appid) === 'undefined' || formData.value.appid === '')) {
        callback(new Error('请输入appid'))
    } else {
        callback()
    }
}
// 验证小程序pagepath
const validateP = (rule: any, value: any, callback: any) => {
    if (formData.value.menuType === 'miniprogram' && (typeof (formData.value.pagepath) === 'undefined' || formData.value.pagepath === '')) {
        callback(new Error('请输入pagepath'))
    } else {
        callback()
    }
}

const data = reactive({
    formData: {
        id: '',
        wxid: '',
        menuName: '',
        menuType: '',
        url: '',
        menuKey: '',
        appid: '',
        pagepath: '',
        parentId: ''
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
        menuName: [{ required: true, message: "菜单名称不能为空", trigger: ["blur", "change"] }],
        url: [{ validator: validateUrl, trigger: ['blur', 'change'] }],
        menuKey: [{ validator: validateK, trigger: ['blur', 'change'] }],
        appid: [{ validator: validateA, trigger: ['blur', 'change'] }],
        pagepath: [{ validator: validateP, trigger: ['blur', 'change'] }]
    },
})

const { queryParams, formData, formRules } = toRefs(data)

const columns = ref([
    { prop: 'menuName', label: `菜单名称`, show: true },
    { prop: 'menuType', label: `菜单类型`, show: true },
    { prop: 'id', label: `配置内容`, show: true },
    { prop: 'createdAt', label: `创建时间`, show: true },
])

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        wxid: wxid.value,
        menuName: '',
        menuType: '',
        url: '',
        menuKey: '',
        appid: '',
        pagepath: '',
        parentId: ''
    }
    formEl?.resetFields()
}


const checkedChange1 = (val: string) => {
    if (!formData.value.url) {
        return
    }
    if (checked1.value) {
        var str = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + account.value.appid + '&redirect_uri=$s&response_type=code&scope=snsapi_base&state=11624317#wechat_redirect'
        formData.value.url = str.replace('$s', encodeURIComponent(formData.value.url))
    } else {
        var url = formData.value.url
        var str2 = url.substring(url.indexOf('redirect_uri=') + 13, url.indexOf('&response_type='))
        formData.value.url = decodeURIComponent(str2)
    }
}

const checkedChange2 = (val: string) => {
    if (!formData.value.url) {
        return
    }
    if (checked2.value) {
        var str = platformInfo.AppDomain + '/wechat/open/auth/' + account.value.wxid + '/oauth?goto_url=$s'
        formData.value.url = str.replace('$s', formData.value.url)
    } else {
        var url = formData.value.url
        formData.value.url = url.substring(url.indexOf('goto_url=') + 9)
    }
}

// 查询表格
const list = () => {
    tableLoading.value = true
    getList(wxid.value).then((res) => {
        tableLoading.value = false
        tableData.value = handleTree(res.data) as never
        menuOptions.value = handleTree(res.data) as never
        sortData.value = handleTree(JSON.parse(JSON.stringify(res.data))) as never
    })
}

const accountChange = (val: string) => {
    queryParams.value.wxid = val
    account.value.wxname = ''
    if (accounts.value && accounts.value.length > 0) {
        var index = accounts.value.findIndex(obj => obj.id === val)
        account.value.wxname = accounts.value[index].appname
        account.value.appid = accounts.value[index].appid
        account.value.wxid = accounts.value[index].id
    }
    list()
}

const listAccount = () => {
    getAccountList().then((res) => {
        accounts.value = res.data as never
        if (accounts.value.length > 0) {
            wxid.value = accounts.value[0].id
            queryParams.value.wxid = accounts.value[0].id
            formData.value.wxid = accounts.value[0].id
            account.value.wxname = accounts.value[0].appname
            account.value.appid = accounts.value[0].appid
            account.value.wxid = accounts.value[0].id
            list()
            listKeyword()
        }
    })
}

const listKeyword = () => {
    getKeywordList(wxid.value).then((res) => {
        const r: { value: any; id: any }[] = []
        res.data.forEach((o: any) => {
            r.push({ value: o.keyword, id: o.keyword })
        })
        keyList.value = r as never
    })
}

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
    modal.confirm('确定删除 ' + row.appname + '？').then(() => {
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

const handlePush = () => {
    modal.confirm('确定将推送至微信平台？').then(() => {
        return doPush(wxid.value)
    }).then(() => {
        modal.msgSuccess('推送成功')
    }).catch(() => { })
}

onMounted(() => {
    listAccount()
})
</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>