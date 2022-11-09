<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>图文内容</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.reply.news.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新增图文
        </el-button>
        <el-button
          v-permission="'wx.reply.news.delete'"
          size="small"
          type="danger"
          :disabled="tableSelectData.length === 0"
          @click="deleteMore"
        >
          批量删除
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          :inline="true"
          class="platform-content-search-form"
        >
          <el-form-item label="所属公众号">
            <el-select
              v-model="account.wxid"
              placeholder="所属公众号"
              size="medium"
              @change="channelChange"
            >
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="item.appname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="platform-content-list">
        <div class="platform-content-list-table">
          <el-table
            ref="listTable"
            v-loading="listLoading"
            :data="listData"
            stripe
            @sort-change="doPageSort"
            @selection-change="tableSelectChange"
            @row-click="tableRowClick"
          >
            <el-table-column
              type="selection"
              width="50"
            />

            <el-table-column
              label="图文标题"
              header-align="left"
              prop="title"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              label="描述"
              header-align="left"
              prop="description"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              label="标题图(2M以内)"
              header-align="left"
              prop="title"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <a :href="conf.AppFileDomain+scope.row.picUrl2" target="_blank">
                  <img :src="conf.AppFileDomain+scope.row.picUrl2" width="30" height="30">
                </a>
              </template>
            </el-table-column>

            <el-table-column
              label="跳转链接"
              header-align="left"
              prop="url"
              :show-overflow-tooltip="true"
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
                  v-permission="'wx.reply.news.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'wx.reply.news.delete'"
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
      title="新增图文"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="title" label="图文标题">
          <el-input
            v-model="formData.title"
            maxlength="100"
            placeholder="图文标题"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="description" label="描述">
          <el-input v-model="formData.description" type="textarea" tabindex="2" />
        </el-form-item>
        <el-form-item prop="picUrl2" label="标题图(2M以内)">
          <el-upload
            class="avatar-uploader"
            tabindex="3"
            :action="uploadUrlImageMeterial"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picUrl2')}"
          >
            <img v-if="formData.picUrl2" :src="conf.AppFileDomain+formData.picUrl2" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="url" label="跳转链接">
          <el-input
            v-model="formData.url"
            placeholder="https://"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doCreate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改图文"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="title" label="图文标题">
          <el-input
            v-model="formData.title"
            maxlength="100"
            placeholder="图文标题"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="description" label="描述">
          <el-input v-model="formData.description" type="textarea" tabindex="2" />
        </el-form-item>
        <el-form-item prop="picUrl2" label="标题图(2M以内)">
          <el-upload
            class="avatar-uploader"
            tabindex="3"
            :action="uploadUrlImageMeterial"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picUrl2')}"
          >
            <img v-if="formData.picUrl2" :src="conf.AppFileDomain+formData.picUrl2" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="url" label="跳转链接">
          <el-input
            v-model="formData.url"
            placeholder="https://"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">取 消</el-button>
        <el-button
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
import {
  API_WX_REPLY_NEWS_LIST,
  API_WX_REPLY_NEWS_GET,
  API_WX_REPLY_NEWS_CREATE,
  API_WX_REPLY_NEWS_UPDATE,
  API_WX_REPLY_NEWS_DELETE,
  API_WX_REPLY_NEWS_DELETE_MORE
} from '@/constant/api/wechat/reply_news'
import {
  API_WX_FILE_UPLOAD_IMAGE_METERIAL
} from '@/constant/api/wechat/file'
import {
  API_WX_CONFIG_LIST_ACCOUNT
} from '@/constant/api/wechat/config'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      createDialogVisible: false,
      updateDialogVisible: false,
      listData: [],
      tableSelectData: [],
      pageData: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      accounts: [],
      account: {
        wxid: '',
        wxname: ''
      },
      headers: {
        'wk-user-token': this.$cookies.get('wk-user-token')
      },
      uploadUrlImageMeterial: '' // 图片上传路径

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
        title: [
          {
            required: true,
            message: '图文标题',
            trigger: 'blur'
          }
        ],
        description: [
          {
            required: true,
            message: '图文内容',
            trigger: 'blur'
          }
        ],
        picUrl2: [
          {
            required: true,
            message: '标题图',
            trigger: 'blur'
          }
        ],
        url: [
          {
            required: true,
            message: '跳转链接',
            trigger: 'blur'
          },
          { validator: this.validateUrl, trigger: ['blur', 'change'] }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.loadAccount()
    }
  },
  methods: {
    // 验证链接地址
    validateUrl(rule, value, callback) {
      if (typeof value !== 'undefined' && value !== '' && (value.indexOf('http://') === 0 || value.indexOf('https://') === 0)) {
        callback()
      } else {
        callback(new Error('链接地址不正确'))
      }
    },
    // 加载微信账号信息
    loadAccount() {
      this.$axios.$get(API_WX_CONFIG_LIST_ACCOUNT).then((d) => {
        if (d.code === 0) {
          this.accounts = d.data
          if (this.accounts && this.accounts.length > 0) {
            this.account.wxid = this.accounts[0].id
            this.account.wxname = this.accounts[0].appname
            this.account.appid = this.accounts[0].appid
            this.uploadUrlImageMeterial = process.env.API + API_WX_FILE_UPLOAD_IMAGE_METERIAL + this.account.wxid
            this.listPage()
          }
        }
      })
    },
    // 切换微信公众号
    channelChange(val) {
      this.account.wxname = ''
      if (this.accounts && this.accounts.length > 0) {
        var index = this.accounts.findIndex(obj => obj.id === val)
        this.account.wxname = this.accounts[index].wxname
        this.account.appid = this.accounts[index].appid
      }
      this.uploadUrlImageMeterial = process.env.API + API_WX_FILE_UPLOAD_IMAGE_METERIAL + this.account.wxid
      this.listPage()
    },
    // 判断图大小
    beforeImageUpload(file) {
      var isLt2M = file.size / 1024 / 1024 < 2
      if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
        this.$message.error('缩略图只能是 JPG 或 PNG 格式')
        return false
      }
      if (!isLt2M) {
        this.$message.error(this.$t(`图片文件大小不能超过 {size}`, { size: '2MB' }))
        return false
      }
      return true
    },
    // 图上传成功后
    handlePicSuccess(response, file, fileList) {
      if (response.code === 0) {
        this.$set(this.formData, 'picUrl', response.data.wx_picurl)
        this.$set(this.formData, 'picUrl2', response.data.picurl)
        this.$set(this.formData, 'mediaId', response.data.id)
      } else {
        this.$message({
          message: response.msg,
          type: 'error'
        })
        this.$set(this.formData, 'picUrl1', '')
        this.$set(this.formData, 'picUrl2', '')
        this.$set(this.formData, 'mediaId', '')
      }
    },
    // 打开字新增窗口
    openCreate() {
      this.formData = {
        id: '',
        wxid: this.account.wxid
      }
      this.createDialogVisible = true
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_REPLY_NEWS_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.createDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_WX_REPLY_NEWS_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.updateDialogVisible = true
        }
      })
    },
    // 提交修改表单
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_REPLY_NEWS_UPDATE, this.formData)
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
        this.$axios.$delete(API_WX_REPLY_NEWS_DELETE + row.id).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
            this.listPage()
          }
        })
      }).catch(() => {})
    },
    // 批量删除文章
    deleteMore() {
      var ids = []
      var titles = []
      this.tableSelectData.forEach((obj) => {
        titles.push(obj.title)
        ids.push(obj.id)
      })
      this.$confirm(
        '确定批量删除？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_WX_REPLY_NEWS_DELETE_MORE, { ids: ids.toString(), titles: titles.toString() }).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
            this.listPage()
          }
        })
      }).catch(() => {})
    },
    // 全选事件
    tableSelectChange(val) {
      this.tableSelectData = val
    },
    // 行点击选中
    tableRowClick(row) {
      this.$refs['listTable'].toggleRowSelection(row)
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
      this.$set(this.pageData, 'wxid', this.account.wxid)
      this.listLoading = true
      this.$axios.$post(API_WX_REPLY_NEWS_LIST, this.pageData).then((res) => {
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
      width: 80px;
      height: 80px;
      line-height: 80px;
      text-align: center;
  }

  .avatar {
      width: 80px;
      height: 80px;
      display: block;
  }
</style>
