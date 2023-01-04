<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" icon="Plus" @click="handleCreate" v-permission="['cms.sites.site.create']">新增
                </el-button>
            </el-col>
        </el-row>
        <el-table v-loading="tableLoading" :data="tableData" row-key="id">
            <el-table-column
              prop="site_name"
              label="站点名称"
              sortable
            />
            <el-table-column
              prop="id"
              label="站点标识"
              sortable
            />
            <el-table-column
              prop="site_domain"
              label="域名"
            />
            <el-table-column
              prop="site_icp"
              label="ICP备案号"
            />
            <el-table-column fixed="right" header-align="center" align="center" label="操作" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-tooltip content="修改" placement="top">
                        <el-button
link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                            v-permission="['cms.sites.site.update']"></el-button>
                        </el-tooltip>
                    <el-tooltip content="删除" placement="top" >
                        <el-button
link type="danger" icon="Delete"
                        @click="handleDelete(scope.row)" v-permission="['cms.sites.site.delete']"></el-button>
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

        <el-dialog title="新增站点" v-model="showCreate" width="50%">
            <el-form ref="createRef" :model="formData" :rules="formRules" label-width="150px">
                <el-tabs v-model="activeName" type="card" style="margin-right:30px;">
          <el-tab-pane label="基本信息" name="base">
            <el-form-item prop="id" label="站点标识">
              <el-input
                v-model="formData.id"
                maxlength="100"
                placeholder="站点标识为大写字母"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_name" label="站点名称">
              <el-input
                v-model="formData.site_name"
                maxlength="100"
                placeholder="站点名称"
                auto-complete="off"
                tabindex="2"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_domain" label="域名">
              <el-input
                v-model="formData.site_domain"
                placeholder="http://"
                auto-complete="off"
                tabindex="3"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_icp" label="ICP备案号">
              <el-input
                v-model="formData.site_icp"
                maxlength="100"
                placeholder="ICP备案号"
                auto-complete="off"
                tabindex="4"
                type="text"
              />
            </el-form-item>

            <el-form-item label="LOGO" class="label_font">
              <el-upload
action="#" :auto-upload="false" :on-change="uploadSiteLogo" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.site_logo" :src="platformInfo.AppFileDomain + formData.site_logo" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>

            <el-form-item label="移动端LOGO" class="label_font">
                <el-upload
action="#" :auto-upload="false" :on-change="uploadSiteWapLogo" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.site_wap_logo" :src="platformInfo.AppFileDomain + formData.site_wap_logo" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>

            <el-form-item prop="footer_content" label="底部信息">
              <el-input
                v-model="formData.footer_content"
                placeholder="底部信息"
                auto-complete="off"
                tabindex="7"
                type="textarea"
              />
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="联系方式" name="contact">
            <el-form-item prop="site_qq" label="QQ">
              <el-input
                v-model="formData.site_qq"
                maxlength="100"
                placeholder="QQ"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_email" label="EMail">
              <el-input
                v-model="formData.site_email"
                maxlength="100"
                placeholder="EMail"
                auto-complete="off"
                tabindex="2"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_tel" label="联系电话">
              <el-input
                v-model="formData.site_tel"
                maxlength="100"
                placeholder="联系电话"
                auto-complete="off"
                tabindex="3"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="weibo_name" label="微博名称">
              <el-input
                v-model="formData.weibo_name"
                maxlength="100"
                placeholder="微博名称"
                auto-complete="off"
                tabindex="4"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="weibo_url" label="微博网址">
              <el-input
                v-model="formData.weibo_url"
                maxlength="100"
                placeholder="微博网址"
                auto-complete="off"
                tabindex="5"
                type="text"
              />
            </el-form-item>

            <el-form-item label="微博二维码" class="label_font">
              <el-upload
action="#" :auto-upload="false" :on-change="uploadWeiboQrcode" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.weibo_qrcode" :src="platformInfo.AppFileDomain + formData.weibo_qrcode" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>
            <el-form-item prop="wechat_name" label="微信公众号名称">
              <el-input
                v-model="formData.wechat_name"
                maxlength="100"
                placeholder="微信公众号名称"
                auto-complete="off"
                tabindex="6"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="wechat_id" label="微信公众号账号">
              <el-input
                v-model="formData.wechat_id"
                maxlength="100"
                placeholder="微信公众号账号"
                auto-complete="off"
                tabindex="7"
                type="text"
              />
            </el-form-item>

            <el-form-item label="微信公众号二维码" class="label_font">
                <el-upload
action="#" :auto-upload="false" :on-change="uploadWechatQrcode" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.wechat_qrcode" :src="platformInfo.AppFileDomain + formData.wechat_qrcode" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="SEO信息" name="seo">
            <el-form-item prop="seo_keywords" label="Keywords">
              <el-input
                v-model="formData.seo_keywords"
                maxlength="100"
                placeholder="关键词"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="seo_description" label="Description">
              <el-input
                v-model="formData.seo_description"
                maxlength="120"
                placeholder="站点描述"
                auto-complete="off"
                tabindex="2"
                type="textarea"
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="create">确 定</el-button>
                    <el-button @click="showCreate = false">取 消</el-button>
                </div>
            </template>
        </el-dialog>

        <el-dialog title="修改站点" v-model="showUpdate" width="50%">
            <el-form ref="updateRef" :model="formData" :rules="formRules" label-width="150px">
                <el-tabs v-model="activeName" type="card" style="margin-right:30px;">
          <el-tab-pane label="基本信息" name="base">
            <el-form-item prop="id" label="站点标识">
              <el-input
                v-model="formData.id"
                maxlength="100"
                placeholder="站点标识为大写字母"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_name" label="站点名称">
              <el-input
                v-model="formData.site_name"
                maxlength="100"
                placeholder="站点名称"
                auto-complete="off"
                tabindex="2"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_domain" label="域名">
              <el-input
                v-model="formData.site_domain"
                placeholder="http://"
                auto-complete="off"
                tabindex="3"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_icp" label="ICP备案号">
              <el-input
                v-model="formData.site_icp"
                maxlength="100"
                placeholder="ICP备案号"
                auto-complete="off"
                tabindex="4"
                type="text"
              />
            </el-form-item>

            <el-form-item label="LOGO" class="label_font">
              <el-upload
action="#" :auto-upload="false" :on-change="uploadSiteLogo" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.site_logo" :src="platformInfo.AppFileDomain + formData.site_logo" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>

            <el-form-item label="移动端LOGO" class="label_font">
                <el-upload
action="#" :auto-upload="false" :on-change="uploadSiteWapLogo" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.site_wap_logo" :src="platformInfo.AppFileDomain + formData.site_wap_logo" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>

            <el-form-item prop="footer_content" label="底部信息">
              <el-input
                v-model="formData.footer_content"
                placeholder="底部信息"
                auto-complete="off"
                tabindex="7"
                type="textarea"
              />
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="联系方式" name="contact">
            <el-form-item prop="site_qq" label="QQ">
              <el-input
                v-model="formData.site_qq"
                maxlength="100"
                placeholder="QQ"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_email" label="EMail">
              <el-input
                v-model="formData.site_email"
                maxlength="100"
                placeholder="EMail"
                auto-complete="off"
                tabindex="2"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="site_tel" label="联系电话">
              <el-input
                v-model="formData.site_tel"
                maxlength="100"
                placeholder="联系电话"
                auto-complete="off"
                tabindex="3"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="weibo_name" label="微博名称">
              <el-input
                v-model="formData.weibo_name"
                maxlength="100"
                placeholder="微博名称"
                auto-complete="off"
                tabindex="4"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="weibo_url" label="微博网址">
              <el-input
                v-model="formData.weibo_url"
                maxlength="100"
                placeholder="微博网址"
                auto-complete="off"
                tabindex="5"
                type="text"
              />
            </el-form-item>

            <el-form-item label="微博二维码" class="label_font">
              <el-upload
action="#" :auto-upload="false" :on-change="uploadWeiboQrcode" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.weibo_qrcode" :src="platformInfo.AppFileDomain + formData.weibo_qrcode" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>
            <el-form-item prop="wechat_name" label="微信公众号名称">
              <el-input
                v-model="formData.wechat_name"
                maxlength="100"
                placeholder="微信公众号名称"
                auto-complete="off"
                tabindex="6"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="wechat_id" label="微信公众号账号">
              <el-input
                v-model="formData.wechat_id"
                maxlength="100"
                placeholder="微信公众号账号"
                auto-complete="off"
                tabindex="7"
                type="text"
              />
            </el-form-item>

            <el-form-item label="微信公众号二维码" class="label_font">
                <el-upload
action="#" :auto-upload="false" :on-change="uploadWechatQrcode" :show-file-list="false"
                    :before-upload="beforeUpload">
                    <img v-if="formData.wechat_qrcode" :src="platformInfo.AppFileDomain + formData.wechat_qrcode" class="logo_img"/>
                    <el-button v-else>
                        选择
                        <el-icon class="el-icon--right">
                            <Upload />
                        </el-icon>
                    </el-button>
                </el-upload>
            </el-form-item>
          </el-tab-pane>
          <el-tab-pane label="SEO信息" name="seo">
            <el-form-item prop="seo_keywords" label="Keywords">
              <el-input
                v-model="formData.seo_keywords"
                maxlength="100"
                placeholder="关键词"
                auto-complete="off"
                tabindex="1"
                type="text"
              />
            </el-form-item>
            <el-form-item prop="seo_description" label="Description">
              <el-input
                v-model="formData.seo_description"
                maxlength="120"
                placeholder="站点描述"
                auto-complete="off"
                tabindex="2"
                type="textarea"
              />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
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
<script setup lang="ts" name="platform-cms-site">
import { nextTick, onMounted, reactive, ref } from 'vue'
import modal from '/@/utils/modal'
import { doCreate, doUpdate, getInfo, getList, doDelete } from '/@/api/platform/cms/site'
import { toRefs } from '@vueuse/core'
import { ElForm } from 'element-plus'
import { fileUpload } from '/@/api/common'
import { usePlatformInfo } from "/@/stores/platformInfo"

const platformInfo = usePlatformInfo()


const createRef = ref<InstanceType<typeof ElForm>>()
const updateRef = ref<InstanceType<typeof ElForm>>()

const showCreate = ref(false)
const showUpdate = ref(false)
const tableLoading = ref(false)
const tableData = ref([])
const activeName = ref('base')

const data = reactive({
    formData: {
        id: '',
        name: '',
        code: '',
        site_name: '',
        site_domain: '',
        site_icp: '',
        site_logo: '',
        site_wap_logo: '',
        weibo_qrcode: '',
        wechat_qrcode: '',
        footer_content: '',
        site_qq: '',
        site_email: '',
        site_tel: '',
        weibo_name: '',
        weibo_url: '',
        wechat_name: '',
        wechat_id: '',
        seo_keywords: '',
        seo_description: ''
    },
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
        id: [
            { required: true, message: '站点标识', trigger: 'blur' }
        ],
        site_name: [
            { required: true, message: '站点名称', trigger: 'blur' }
        ],
        site_email: [
            { type: 'email', message: 'EMail 不正确', trigger: 'blur' }
        ],
        seo_description: [
            { max: 120, message: '站点描述120字以内', trigger: 'blur' }
        ]
    },
})

const { queryParams, formData, formRules } = toRefs(data)

// 重置表单
const resetForm = (formEl: InstanceType<typeof ElForm> | undefined) => {
    formData.value = {
        id: '',
        name: '',
        code: '',
        site_name: '',
        site_domain: '',
        site_icp: '',
        site_logo: '',
        site_wap_logo: '',
        weibo_qrcode: '',
        wechat_qrcode: '',
        footer_content: '',
        site_qq: '',
        site_email: '',
        site_tel: '',
        weibo_name: '',
        weibo_url: '',
        wechat_name: '',
        wechat_id: '',
        seo_keywords: '',
        seo_description: ''
    }
    formEl?.resetFields()
}

const beforeUpload = (file: any) => {
    if (file.type.indexOf("image/") == -1) {
        modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。")
    }
}

const uploadSiteLogo = (file: any) => {
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUpload(f,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.site_logo = res.data.url
        }
    })
}

const uploadSiteWapLogo = (file: any) => {
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUpload(f,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.site_wap_logo = res.data.url
        }
    })
}

const uploadWeiboQrcode = (file: any) => {
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUpload(f,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.weibo_qrcode = res.data.url
        }
    })
}

const uploadWechatQrcode = (file: any) => {
    let f = new FormData()
    f.append('Filedata', file.raw)
    fileUpload(f,{},'image').then((res) => {
        if (res.code == 0) {
            formData.value.wechat_qrcode = res.data.url
        }
    })
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
    modal.confirm('确定删除 '+ row.site_name + '？').then(() => {
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
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.logo_img {
    width: 50px;
    height: 50px;   
}
.label_font {
    font-weight: 700;
}
</style>