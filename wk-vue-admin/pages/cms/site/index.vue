<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>站点管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'cms.sites.site.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新建站点
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-list">
        <div class="platform-content-list-table">
          <el-table
            v-loading="listLoading"
            :data="listData"
            stripe
            @sort-change="doPageSort"
          >
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
              sortable
            />
            <el-table-column
              prop="site_icp"
              label="ICP备案号"
              sortable
            />
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'cms.sites.site.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'cms.sites.site.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="deleteRow(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="platform-content-list-pagination">
          <el-pagination
            :current-page="pageData.pageNo"
            :page-size="pageData.pageSize"
            :total="pageData.totalCount"
            class="platform-pagenation"
            background
            :page-sizes="[10, 20, 30, 50]"
            layout="sizes, prev, pager, next"
            @current-change="doChangePage"
            @size-change="doSizeChange"
          />
        </div>
      </div>
    </div>

    <el-dialog
      title="创建站点"
      :visible.sync="addDialogVisible"
      width="50%"
    >
      <el-form ref="addForm" :model="formData" :rules="formRules" size="small" label-width="120px">
        <el-tabs v-model="activeName" type="card">
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

            <el-form-item label="LOGO">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'site_logo')}"
              >

                <img v-if="formData.site_logo" :src="conf.AppFileDomain+formData.site_logo" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
              </el-upload>
            </el-form-item>

            <el-form-item label="移动端LOGO">
              <el-upload
                class="avatar-uploader"
                tabindex="6"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'site_wap_logo')}"
              >

                <img v-if="formData.site_wap_logo" :src="conf.AppFileDomain+formData.site_wap_logo" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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

            <el-form-item label="微博二维码">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'weibo_qrcode')}"
              >

                <img v-if="formData.weibo_qrcode" :src="conf.AppFileDomain+formData.weibo_qrcode" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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

            <el-form-item label="微信公众号二维码">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'wechat_qrcode')}"
              >

                <img v-if="formData.wechat_qrcode" :src="conf.AppFileDomain+formData.wechat_qrcode" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改站点"
      :visible.sync="updateDialogVisible"
      width="50%"
    >
      <el-form ref="updateForm" :model="formData" :rules="formRules" size="small" label-width="120px">
        <el-tabs v-model="activeName" type="card">
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

            <el-form-item label="LOGO">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'site_logo')}"
              >

                <img v-if="formData.site_logo" :src="conf.AppFileDomain+formData.site_logo" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
              </el-upload>
            </el-form-item>

            <el-form-item label="移动端LOGO">
              <el-upload
                class="avatar-uploader"
                tabindex="6"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'site_wap_logo')}"
              >

                <img v-if="formData.site_wap_logo" :src="conf.AppFileDomain+formData.site_wap_logo" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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

            <el-form-item label="微博二维码">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'weibo_qrcode')}"
              >

                <img v-if="formData.weibo_qrcode" :src="conf.AppFileDomain+formData.weibo_qrcode" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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

            <el-form-item label="微信公众号二维码">
              <el-upload
                class="avatar-uploader"
                tabindex="5"
                :action="uploadUrl"
                :headers="headers"
                name="Filedata"
                :show-file-list="false"
                :on-success="function(resp,file,fileList){return handleLogoSuccess(resp,file,fileList,'wechat_qrcode')}"
              >

                <img v-if="formData.wechat_qrcode" :src="conf.AppFileDomain+formData.wechat_qrcode" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon" />
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
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { API_UPLOAD_IMAGE } from '@/constant/api/cms/upload'
import {
  API_CMS_SITE_LIST,
  API_CMS_SITE_CREATE,
  API_CMS_SITE_DELETE,
  API_CMS_SITE_UPDATE,
  API_CMS_SITE_GET
} from '@/constant/api/cms/site'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      listData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      activeName: 'base',
      headers: {
        'X-Token': this.$cookies.get('X-Token')
      },
      uploadUrl: process.env.API + API_UPLOAD_IMAGE // 图片上传路径
    }
  },
  computed: {
    ...mapState({
      conf: (state) => state.conf, // 后台配置参数
      siteInfo: (state) => state.siteInfo, // 平台信息
      userInfo: (state) => state.userInfo // 用户信息
    }),
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
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
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.listPage()
    }
  },
  methods: {
    // 打开字新增窗口
    openAdd() {
      this.formData = {}
      this.addDialogVisible = true
    },
    // 提交表单
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_SITE_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.addDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_CMS_SITE_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.updateDialogVisible = true
        }
      })
    },
    // 提交表单
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_SITE_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.updateDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 执行删除
    deleteRow(row) {
      this.$confirm(
        '确定删除？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.btnLoading = true
        this.$axios
          .$delete(API_CMS_SITE_DELETE + row.id)
          .then((res) => {
            this.btnLoading = false
            if (res.code === 0) {
              this.$message({
                message: res.msg,
                type: 'success'
              })
              this.listPage()
            }
          })
      }).catch(() => {})
    },
    // 页码变动事件
    doChangePage(val) {
      this.pageData.pageNo = val
      this.listPage()
    },
    // 页大小变动事件
    doSizeChange(val) {
      this.pageData.pageSize = val
      this.listPage()
    },
    // 页排序事件
    doPageSort(column) {
      this.pageData.pageOrderName = column.prop
      this.pageData.pageOrderBy = column.order
      this.listPage()
    },
    // 获取分页查询数据
    listPage() {
      this.listLoading = true
      this.$axios.$post(API_CMS_SITE_LIST, this.pageData).then((res) => {
        this.listLoading = false
        if (res.code === 0) {
          this.listData = res.data.list
          this.pageData.totalCount = res.data.totalCount
        }
      })
    },
    // 条件查询展示第一页内容
    doSearch() {
      this.pageData.pageNo = 1
      this.listPage()
    },
    beforeImageUpload(file) {

    },
    handleLogoSuccess(response, file, fileList, attrName) {
      if (response.code === 0) {
        // 对象属性直接赋值不会触发试图更新，采用 Vue.set 、this.$set或者Object.assign({}，this.obj)创建新对象
        // 或者也可以定义一个 refresh 属性 ，当数据发生改变时，该属性同时变化，也会触发视图更新
        // this.formData.site_logo =response.data;
        var file_url = response.data.url
        this.$set(this.formData, attrName, file_url)
      } else {
        this.$set(this.formData, attrName, '')
      }
    }
  }
}
</script>

<style>
    .el-upload input[type='file'] {
        display: none;
    }

    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }

    .avatar-uploader .el-upload:hover {
        border-color: #409EFF;
    }

    .avatar-uploader-icon {
        font-size: 28px;
        color: #8c939d;
        width: 128px;
        height: 128px;
        line-height: 128px;
        text-align: center;
    }

    .avatar {
        width: 128px;
        height: 128px;
        display: block;
    }
</style>
