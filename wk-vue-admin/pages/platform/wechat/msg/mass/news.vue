<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>图文素材
        <el-button type="text" icon="fa fa-arrow-circle-left" @click="goBack">返回</el-button>
      </span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.msg.mass.news.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新建图文
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-list">
        <div class="platform-content-list-table">
          <el-table
            v-loading="listLoading"
            :data="listData"
            :default-sort="{
              prop: 'createdAt',
              order: 'descending'
            }"
            stripe
            @sort-change="doPageSort"
          >
            <el-table-column
              label=""
              header-align="left"
              prop="picurl"
              :show-overflow-tooltip="true"
              width="80"
            >
              <template slot-scope="scope">
                <img v-if="scope.row.picurl!=''" :src="conf.AppFileDomain+scope.row.picurl" width="30" height="30">
              </template>
            </el-table-column>

            <el-table-column label="图文标题" header-align="left" prop="title" :show-overflow-tooltip="true" />

            <el-table-column label="图文作者" header-align="left" prop="author" :show-overflow-tooltip="true" width="120" />

            <el-table-column sortable label="创建时间" header-align="center" align="center" prop="createdAt">
              <template slot-scope="scope">
                {{ scope.row.createdAt | moment("datetime") }}
              </template>
            </el-table-column>

            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'wx.msg.mass.news'"
                  type="text"
                  size="small"
                  @click.native.prevent="openView(scope.row)"
                >
                  预览
                </el-button>
                <el-button
                  v-permission="'wx.msg.mass.news.delete'"
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
      title="新建图文"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="160px"
      >
        <el-form-item prop="title" label="所属公众号">
          {{ formData.wxname }}
        </el-form-item>

        <el-form-item prop="title" label="图文标题">
          <el-input
            v-model="formData.title"
            maxlength="255"
            placeholder="图文标题"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="author" label="图文作者">
          <el-input
            v-model="formData.author"
            maxlength="50"
            placeholder="图文作者"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="picurl" label="缩略图(64kb以内)">
          <el-upload
            class="avatar-uploader"
            tabindex="3"
            :action="uploadUrl"
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

        <el-form-item prop="digest" label="摘要">
          <el-input v-model="formData.digest" type="textarea" />
        </el-form-item>

        <el-form-item prop="content_source_url" label="原文链接">
          <el-checkbox v-model="checkedSourceUrl">原文链接</el-checkbox>
          <el-input
            v-if="checkedSourceUrl"
            v-model="formData.url"
            maxlength="50"
            placeholder="原文链接"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="show_cover_pic" label="显示封面">
          <el-checkbox v-model="formData.show_cover_pic" true-label="1" false-label="0">显示封面</el-checkbox>
          <el-alert
            v-if="formData.show_cover_pic=='1'"
            title="会自动使用文章中第一张图片作为封面图"
            type="info"
            close-text="知道了"
          />
        </el-form-item>

        <el-form-item prop="need_open_comment" label="打开评论">
          <el-checkbox v-model="formData.need_open_comment" true-label="1" false-label="0">打开评论</el-checkbox>
        </el-form-item>

        <el-form-item prop="only_fans_can_comment" label="粉丝评论">
          <el-checkbox v-model="formData.only_fans_can_comment" true-label="1" false-label="0">只给粉丝评论</el-checkbox>
        </el-form-item>

        <el-form-item prop="content" label="图文内容">
          <div>
            <QuillEditor
              ref="myQuillEditor"
              style="height:320px;"
              :text="formData.content"
              :uploadurl="uploadUrlImage"
              :filedomain="filedomain"
              @changeContent="changeContent"
            />
          </div>
        </el-form-item>
        <el-form-item style="padding-top:75px;">
          <el-alert
            style="margin-top: 5px;"
            title="图文中上传的图片只可在微信中查看"
            type="info"
            close-text="知道了"
          />
          <el-alert
            style="margin-top: 5px;"
            title="具备微信支付权限的公众号，才可以使用a标签，其他公众号不能使用"
            type="info"
            close-text="知道了"
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

    <el-drawer
      title="预览图文"
      :visible.sync="viewDialogVisible"
      :direction="'rtl'"
      :wrapper-closable="true"
      :size="'50%'"
    >
      <el-row style="text-align: center">
        <h4><img v-if="formData.picurl" :src="conf.AppFileDomain+formData.picurl" width="30" height="30"> {{ formData.title }}</h4>
      </el-row>
      <el-row>
        <span style="font-size:12px;">图文作者： {{ formData.author }}</span>
      </el-row>
      <el-row style="margin-top: 5px !important;margin-bottom: 5px !important;border-bottom: 1px solid #DDD;" />
      <el-row>
        <span style="font-size:12px;" v-html="formData.content" />
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </span>
    </el-drawer>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import QuillEditor from '@/components/QuillEditor'
import {
  API_WX_MSG_MASS_NEWS_LIST,
  API_WX_MSG_MASS_NEWS_DETAIL,
  API_WX_MSG_MASS_NEWS_DELETE,
  API_WX_MSG_MASS_NEWS_CREATE
} from '@/constant/api/wechat/msg_mass'
import {
  API_WX_FILE_UPLOAD_THUMB,
  API_WX_FILE_UPLOAD_IMAGE_METERIAL
} from '@/constant/api/wechat/file'
export default {
  middleware: ['authenticated', 'check_permissions'],
  components: {
    QuillEditor
  },
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      createDialogVisible: false,
      updateDialogVisible: false,
      checkedSourceUrl: false,
      viewDialogVisible: false,
      wxid: '',
      wxname: '',
      listData: [],
      pageData: {
        wxid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
        id: '',
        fileSize: 0,
        filePath: '',
        content: ''
      },
      fileUploadFlag: false,
      fileUploadPercent: 0,
      uploadUrl: process.env.API + API_WX_FILE_UPLOAD_THUMB + this.wxid, // 缩略图上传路径
      filedomain: '',
      headers: {
        'wk-user-token': this.$cookies.get('wk-user-token')
      },
      uploadUrlImage: process.env.API + API_WX_FILE_UPLOAD_IMAGE_METERIAL + this.wxid // 图片上传路径
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
          { required: true, message: '图文标题', trigger: 'blur' }
        ],
        picurl: [
          { required: true, message: '缩略图', trigger: 'blur' }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.wxid = this.$route.query.wxid
      this.wxname = this.$route.query.wxname
      this.pageData.wxid = this.wxid
      this.uploadUrl = process.env.API + API_WX_FILE_UPLOAD_THUMB + this.wxid
      this.filedomain = this.conf.AppFileDomain
      this.uploadUrlImage = process.env.API + API_WX_FILE_UPLOAD_IMAGE_METERIAL + this.wxid
      this.listPage()
    }
  },
  methods: {
    changeContent(val) {
      this.$set(this.formData, 'content', val)
    },
    // 判断缩略图大小
    beforeImageUpload(file) {
      var isLt2M = file.size / 1024 < 64
      if (file.type !== 'image/jpeg' && file.type !== 'image/png') {
        this.$message.error('缩略图只能是 JPG 或 PNG 格式')
        return false
      }
      if (!isLt2M) {
        this.$message.error(this.$t(`图片文件大小不能超过 {size}`, { size: '64KB' }))
        return false
      }
      return true
    },
    // 缩略图上传成功后
    handlePicSuccess(response, file, fileList) {
      if (response.code === 0) {
        this.$set(this.formData, 'picurl', response.data.picurl)
        this.$set(this.formData, 'thumb_media_id', response.data.id)
      } else {
        this.$message({
          message: response.msg,
          type: 'error'
        })
        this.$set(this.formData, 'picurl', '')
        this.$set(this.formData, 'thumb_media_id', '')
      }
    },
    // 打开预览
    openView(row) {
      this.$axios.$get(API_WX_MSG_MASS_NEWS_DETAIL + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.viewDialogVisible = true
        }
      })
    },
    // 打开新增页面
    openCreate() {
      this.createDialogVisible = true
      this.formData = {
        wxid: this.wxid,
        wxname: this.wxname,
        show_cover_pic: '0',
        digest: '',
        content: ''
      }
      if (this.$refs['createForm']) { this.$refs['createForm'].resetFields() }
      if (this.$refs.myQuillEditor) {
        this.$refs.myQuillEditor.content = ''
      }
    },
    // 提交新增表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'content', this.$refs.myQuillEditor.content)
          this.btnLoading = true
          this.$axios
            .$post(API_WX_MSG_MASS_NEWS_CREATE, this.formData)
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
          .$delete(API_WX_MSG_MASS_NEWS_DELETE + row.id)
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
      this.$axios.$post(API_WX_MSG_MASS_NEWS_LIST, this.pageData).then((res) => {
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
    goBack() {
      this.$router.push('/wechat/admin/msg/mass')
    }
  }
}
</script>

<style>

.note {
    width: 100%;
    height: 320px;
}

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

.el-drawer__body {
    overflow-y: auto;
}
.el-drawer__wrapper div,
span,
button,
i {
    outline: none;
}
</style>
