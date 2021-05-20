<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.reply.img`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.reply.img.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          {{ $t(`wx.reply.img.create`) }}
        </el-button>
        <el-button
          v-permission="'wx.reply.img.delete'"
          size="small"
          type="danger"
          :disabled="tableSelectData.length === 0"
          @click="deleteMore"
        >
          {{ $t(`system.error.delete.more`) }}
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          :inline="true"
          class="platform-content-search-form"
        >
          <el-form-item :label="$t(`wx.conf.menu.form.wxid`)">
            <el-select
              v-model="account.wxid"
              :placeholder="$t(`wx.conf.menu.form.wxid`)"
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
              :label="$t(`wx.reply.img.form.picurl`)"
              header-align="left"
              prop="title"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <a :href="conf.AppFileDomain+scope.row.picurl" target="_blank">
                  <img :src="conf.AppFileDomain+scope.row.picurl" width="30" height="30">
                </a>
              </template>
            </el-table-column>

            <el-table-column
              label="MediaId"
              header-align="left"
              prop="mediaId"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              :label="$t(`system.commons.txt.ext`)"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'wx.reply.img.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  {{
                    $t(`system.commons.button.update.mini`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.reply.img.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="deleteRow(scope.row)"
                >
                  {{
                    $t(`system.commons.button.delete.mini`)
                  }}
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
      :title="$t(`wx.reply.img.create`)"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="30%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="180px"
      >
        <el-form-item prop="picurl" :label="$t(`wx.reply.img.form.picurl.tip`)">
          <el-upload
            class="avatar-uploader"
            tabindex="3"
            :action="uploadUrlImageMeterial"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picurl')}"
          >
            <img v-if="formData.picurl" :src="conf.AppFileDomain+formData.picurl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doCreate"
        >{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t(`wx.reply.img.update`)"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="30%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="180px"
      >
        <el-form-item prop="picurl" :label="$t(`wx.reply.img.form.picurl`)">
          <el-upload
            class="avatar-uploader"
            tabindex="3"
            :action="uploadUrlImageMeterial"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picurl')}"
          >
            <img v-if="formData.picurl" :src="conf.AppFileDomain+formData.picurl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doUpdate"
        >{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_REPLY_IMG_LIST,
  API_WX_REPLY_IMG_GET,
  API_WX_REPLY_IMG_CREATE,
  API_WX_REPLY_IMG_UPDATE,
  API_WX_REPLY_IMG_DELETE,
  API_WX_REPLY_IMG_DELETE_MORE
} from '@/constant/api/platform/wx/reply_img'
import {
  API_WX_FILE_UPLOAD_IMAGE_METERIAL
} from '@/constant/api/platform/wx/file'
import {
  API_WX_CONFIG_LIST_ACCOUNT
} from '@/constant/api/platform/wx/config'
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
        'X-Token': this.$cookies.get('X-Token')
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
        picurl: [
          {
            required: true,
            message: this.$t(`wx.reply.img.form.picurl`),
            trigger: 'blur'
          }
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
        this.$message.error(this.$t(`wx.msg.mass.news.form.picurl.tip1`))
        return false
      }
      if (!isLt2M) {
        this.$message.error(this.$t(`wx.msg.mass.news.form.picurl.tip2`, { size: '2MB' }))
        return false
      }
      return true
    },
    // 图上传成功后
    handlePicSuccess(response, file, fileList) {
      if (response.code === 0) {
        this.$set(this.formData, 'picurl', response.data.picurl)
        this.$set(this.formData, 'mediaId', response.data.id)
      } else {
        this.$message({
          message: response.msg,
          type: 'error'
        })
        this.$set(this.formData, 'picurl', '')
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
            .$post(API_WX_REPLY_IMG_CREATE, this.formData)
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
      this.$axios.$get(API_WX_REPLY_IMG_GET + row.id).then((d) => {
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
            .$post(API_WX_REPLY_IMG_UPDATE, this.formData)
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
        this.$t(`system.error.delete.confirm`),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_WX_REPLY_IMG_DELETE + row.id).then((d) => {
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
      var picurls = []
      this.tableSelectData.forEach((obj) => {
        picurls.push(obj.picurl)
        ids.push(obj.id)
      })
      this.$confirm(
        this.$t(`system.error.delete.confirm`),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_WX_REPLY_IMG_DELETE_MORE, { ids: ids.toString(), picurls: picurls.toString() }).then((d) => {
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
      this.$axios.$post(API_WX_REPLY_IMG_LIST, this.pageData).then((res) => {
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
