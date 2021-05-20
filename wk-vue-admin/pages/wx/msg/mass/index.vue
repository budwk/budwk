<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.msg.mass`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.msg.mass'"
          size="small"
          icon="fa fa-book"
          @click="gotoNews"
        >
          {{ $t(`wx.msg.mass.news`) }}
        </el-button>
        <el-button
          v-permission="'wx.msg.mass'"
          size="small"
          icon="fa fa-paper-plane"
          @click="openSend"
        >
          {{ $t(`wx.msg.mass.push`) }}
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
            v-loading="listLoading"
            :data="listData"
            stripe
            :default-sort="{prop: 'createdAt', order: 'descending'}"
            @sort-change="doPageSort"
          >
            <el-table-column :label="$t(`wx.msg.mass.form.name`)" header-align="left" prop="name" />

            <el-table-column :label="$t(`wx.msg.mass.form.type`)" header-align="left" prop="type" width="120">
              <template slot-scope="scope">
                <span v-if="scope.row.type=='image'">{{ $t(`wx.msg.mass.form.type.image`) }}</span>
                <span v-if="scope.row.type=='news'">{{ $t(`wx.msg.mass.form.type.news`) }}</span>
                <span v-if="scope.row.type=='text'">{{ $t(`wx.msg.mass.form.type.text`) }}</span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.msg.mass.form.scope`)"
              header-align="left"
              prop="id"
              :show-overflow-tooltip="true"
              width="120"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.scope=='some'">{{ $t(`wx.msg.mass.form.some`) }}</span>
                <span v-if="scope.row.scope=='all'">{{ $t(`wx.msg.mass.form.all`) }}</span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.msg.mass.form.massSend.status`)"
              header-align="left"
              prop="id"
              :show-overflow-tooltip="true"
              width="120"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.massSend">
                  <span v-if="scope.row.massSend.status==1">{{ $t(`wx.msg.mass.form.massSend.status.success`) }}</span>
                  <span v-if="scope.row.massSend.status!=1">{{ $t(`wx.msg.mass.form.massSend.status.fail`) }}</span>
                </span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.msg.mass.form.massSend.errMsg`)"
              header-align="left"
              prop="id"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.massSend">{{ scope.row.massSend.errMsg }}</span>
              </template>
            </el-table-column>

            <el-table-column sortable :label="$t(`wx.msg.mass.form.massSend.createdAt`)" header-align="center" align="center" prop="createdAt">
              <template slot-scope="scope">
                {{ scope.row.createdAt | moment("datetime") }}
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
      :title="$t(`wx.msg.mass.push`)"
      :visible.sync="sendDialogVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-form ref="sendForm" :model="formData" :rules="formRules" size="small" label-width="80px">
        <el-form-item prop="name" :label="$t(`wx.msg.mass.form.name`)">
          <el-input
            v-model="formData.name"
            maxlength="255"
            :placeholder="$t(`wx.msg.mass.form.name`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item class="is-required" prop="type" :label="$t(`wx.msg.mass.form.type`)">
          <el-radio-group v-model="formData.type" size="medium">
            <el-radio label="text">{{ $t(`wx.msg.mass.form.type.text`) }}</el-radio>
            <el-radio label="image">{{ $t(`wx.msg.mass.form.type.image`) }}</el-radio>
            <el-radio label="news">{{ $t(`wx.msg.mass.form.type.news`) }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="formData.type=='news'" class="is-required" prop="newsids" :label="$t(`wx.msg.mass.form.type.news`)">
          <el-button size="mini" @click="openNews">{{ $t(`wx.msg.mass.openNews`) }}</el-button>
          <el-button size="mini" @click="clearNews">{{ $t(`wx.msg.mass.clearNews`) }}</el-button>

          <el-tree
            style="padding-top: 8px;"
            :data="newsTreeData"
            :allow-drop="sortAllowDrop"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            draggable
          >
            <span slot-scope="node" class="custom-tree-node">
              <span>{{ node.data.label }}</span>
              <span style="padding-left:10px;">
                <el-button
                  type="text"
                  size="mini"
                  @click="remove(node)"
                >
                  Delete
                </el-button>
              </span>
            </span>
          </el-tree>
          <el-alert
            v-if="newsTreeData.length>0"
            style="padding:10px;height: 32px;"
            :title="$t(`wx.msg.mass.news.sort`)"
            type="success"
          />
        </el-form-item>

        <el-form-item v-if="formData.type=='image'" prop="picurl" :label="$t(`wx.msg.mass.form.type.image.size`)">
          <el-upload
            class="avatar-uploader"
            tabindex="5"
            :action="uploadImageUrl"
            :headers="headers"
            name="Filedata"
            :before-upload="beforeImageUpload"
            :show-file-list="false"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picurl')}"
          >
            <img v-if="formData.picurl" :src="conf.AppFileDomain + formData.picurl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>

        <el-form-item v-if="formData.type=='text'" class="is-required" :label="$t(`wx.msg.mass.form.type.text`)" prop="content">
          <el-input v-model="formData.content" type="textarea" />
        </el-form-item>

        <el-form-item class="is-required" prop="scope" :label="$t(`wx.msg.mass.form.scope`)">
          <el-radio-group v-model="formData.scope" size="medium">
            <el-radio label="all">{{ $t(`wx.msg.mass.form.scope.all`) }}</el-radio>
            <el-radio label="some">{{ $t(`wx.msg.mass.form.scope.some`) }}</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="formData.scope=='some'" class="is-required" :label="$t(`wx.msg.mass.openUser`)" prop="receivers">
          <el-button size="mini" @click="openUser">{{ $t(`wx.msg.mass.openUser`) }}</el-button>
          <el-input v-model="formData.receivers" style="margin-top: 5px;" type="textarea" />
          <el-alert
            style="margin-top: 5px;"
            :title="$t(`wx.msg.mass.openUser.tip`)"
            type="success"
          />
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="sendDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button type="primary" :loading="btnLoading" @click="doSend">{{ $t(`wx.msg.mass.send`) }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t(`wx.msg.mass.openUser`)"
      :visible.sync="userDialogVisible"
      width="70%"
    >
      <div class="btn-group tool-button">
        <el-form
          :inline="true"
          class="platform-content-search-form"
        >
          <el-form-item
            :label="$t(`wx.user.list.form.nickname`)"
          >
            <el-input
              v-model="pageData2.nickname"
              :placeholder="$t(`wx.user.list.form.nickname`)"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch2"
            />
          </el-form-item>
          <el-button icon="el-icon-search" size="mini" @click="doSearch2" />
        </el-form>
      </div>
      <el-table
        :data="tableData2"
        size="small"
        header-align="center"
        style="width: 100%;padding-top: 5px;"
        :default-sort="{prop: 'createdAt', order: 'descending'}"
        @sort-change="doPageSort2"
        @selection-change="handleSelectionChangeUser"
      >
        <el-table-column
          type="selection"
          width="50"
        />

        <el-table-column
          :label="$t(`wx.user.list.form.nickname`)"
          header-align="left"
          prop="nickname"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <img v-if="scope.row.headimgurl!=''" :src="scope.row.headimgurl" width="30" height="30">
            <span>{{ scope.row.nickname }}</span>
          </template>
        </el-table-column>

        <el-table-column
          :label="$t(`wx.user.list.form.sex`)"
          header-align="left"
          prop="sex"
          :show-overflow-tooltip="true"
          width="80"
        >
          <template slot-scope="scope">
            <span v-if="scope.row.sex==1">{{ $t(`wx.user.list.form.sex.1`) }}</span>
            <span v-if="scope.row.sex==2">{{ $t(`wx.user.list.form.sex.2`) }}</span>
            <span v-if="scope.row.sex==0">{{ $t(`wx.user.list.form.sex.0`) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          :label="$t(`wx.user.list.form.area`)"
          header-align="left"
          prop="id"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            {{ scope.row.country }} <span v-if="scope.row.province">-</span> {{ scope.row.province }} <span v-if="scope.row.city">-</span> {{ scope.row.city }}
          </template>
        </el-table-column>

        <el-table-column
          :label="$t(`wx.user.list.form.subscribe`)"
          header-align="left"
          prop="subscribe"
          :show-overflow-tooltip="true"
          width="80"
        >
          <template slot-scope="scope">
            <span v-if="!scope.row.subscribe">{{ $t(`wx.user.list.form.subscribe.no`) }}</span>
            <span v-if="scope.row.subscribe" style="color:green;">{{ $t(`wx.user.list.form.subscribe.yes`) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="pageData2.pageNo"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageData2.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageData2.totalCount"
        @size-change="doSizeChange2"
        @current-change="doChangePage2"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="userDialogVisible = false">{{ $t(`system.commons.button.cancel`) }}</el-button>
        <el-button type="primary" @click="doUser">{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t(`wx.msg.mass.openNews`)"
      :visible.sync="newsDialogVisible"
      width="50%"
    >
      <el-table
        :data="tableData3"
        size="small"
        header-align="center"
        style="width: 100%;padding-top: 5px;"
        :default-sort="{prop: 'createdAt', order: 'descending'}"
        @sort-change="doPageSort3"
        @selection-change="handleSelectionChangeNews"
      >

        <el-table-column
          type="selection"
          width="50"
        />

        <el-table-column :label="$t(`wx.msg.mass.news.form.title`)" header-align="left" prop="title" :show-overflow-tooltip="true" />

        <el-table-column sortable :label="$t(`system.commons.txt.createdAt`)" header-align="center" align="center" prop="createdAt">
          <template slot-scope="scope">
            {{ scope.row.createdAt | moment("datetime") }}
          </template>
        </el-table-column>

      </el-table>
      <el-pagination
        :current-page="pageData3.pageNo"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="pageData3.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageData3.totalCount"
        @size-change="doSizeChange3"
        @current-change="doChangePage3"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="newsDialogVisible = false">{{ $t(`system.commons.button.cancel`) }}</el-button>
        <el-button type="primary" @click="doNews">{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_MSG_MASS_LIST,
  API_WX_MSG_MASS_NEWS_LIST,
  API_WX_MSG_MASS_PUSH
} from '@/constant/api/platform/wx/msg_mass'
import {
  API_WX_FILE_UPLOAD_IMAGE
} from '@/constant/api/platform/wx/file'
import {
  API_WX_USER_LIST
} from '@/constant/api/platform/wx/user'
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
      replyDialogVisible: false,
      sendDialogVisible: false,
      newsDialogVisible: false,
      userDialogVisible: false,
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending',
        wxid: ''
      },
      pageData2: {
        nickname: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending',
        wxid: ''
      },
      pageData3: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending',
        wxid: ''
      },
      tableData: [],
      tableData2: [],
      tableData3: [],
      listData: [],
      formData: {
      },
      selectUsers: [],
      selectNews: [],
      newsTreeData: [],
      accounts: [],
      account: {
        wxid: '',
        wxname: ''
      },
      headers: {
        'X-Token': this.$cookies.get('X-Token')
      },
      uploadImageUrl: ''
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
        name: [
          { required: true, message: this.$t(`wx.msg.mass.form.name`), trigger: 'blur' }
        ],
        content: [
          { required: false, message: this.$t(`wx.msg.mass.form.type.text`), trigger: 'blur' },
          { validator: this.validateText, trigger: ['blur', 'change'] }
        ],
        receivers: [
          { required: false, message: this.$t(`wx.msg.mass.form.receivers`), trigger: 'blur' },
          { validator: this.validateSome, trigger: ['blur', 'change'] }
        ],
        newsids: [
          { required: false, message: this.$t(`wx.msg.mass.form.type.news`), trigger: 'blur' },
          { validator: this.validateNews, trigger: ['blur', 'change'] }
        ],
        picurl: [
          { required: false, message: this.$t(`wx.msg.mass.form.type.image`), trigger: 'blur' },
          { validator: this.validatePic, trigger: ['blur', 'change'] }
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
    validateText(rule, value, callback) {
      if (this.formData.type === 'text' && this.formData.content === '') {
        callback(new Error(this.$t(`wx.msg.mass.error.text`)))
      } else {
        callback()
      }
    },
    validateSome(rule, value, callback) {
      if (this.formData.scope === 'some' && this.formData.receivers.length === 0) {
        callback(new Error(this.$t(`wx.msg.mass.error.openid`)))
      } else if (this.formData.scope === 'some' && this.formData.receivers.length < 2) {
        callback(new Error(this.$t(`wx.msg.mass.error.openid.2`)))
      } else {
        callback()
      }
    },
    validateNews(rule, value, callback) {
      if (this.formData.type === 'news' && this.newsTreeData.length === 0) {
        callback(new Error(this.$t(`wx.msg.mass.error.news`)))
      } else {
        callback()
      }
    },
    validatePic(rule, value, callback) {
      if (this.formData.type === 'image' && this.formData.picurl === '') {
        callback(new Error(this.$t(`wx.msg.mass.error.image`)))
      } else {
        callback()
      }
    },
    // 打开会员选择
    openUser() {
      this.userDialogVisible = true
      this.listPage2()
    },
    // 选则会员
    handleSelectionChangeUser(val) {
      this.selectUsers = val
    },
    // 选中会员
    doUser() {
      this.userDialogVisible = false
      if (this.selectUsers && this.selectUsers.length > 0) {
        var openids = []
        this.selectUsers.forEach((o) => {
          openids.push(o.openid)
        })
        if (this.formData && this.formData.receivers.length > 5) {
          this.$set(this.formData, 'receivers', this.formData.receivers + ',' + openids.toString())
        } else {
          this.$set(this.formData, 'receivers', openids.toString())
        }
      }
    },
    // 打开选择图文
    openNews() {
      this.newsDialogVisible = true
      this.listPage3()
    },
    // 选则图文
    handleSelectionChangeNews(val) {
      this.selectNews = val
    },
    // 选中图文
    doNews() {
      this.newsDialogVisible = false
      if (this.selectNews && this.selectNews.length > 0) {
        this.selectNews.forEach((o) => {
          this.formData.newsids.push(o.id)
          this.newsTreeData.push({ id: o.id, label: o.title })
        })
      }
    },
    // 排序树控制不可跨级拖拽
    sortAllowDrop(moveNode, inNode, type) {
      if (moveNode.data.parentId === inNode.data.parentId) {
        return type === 'prev'
      }
    },
    // 清除选择的图文
    clearNews() {
      this.newsTreeData = []
    },
    // 删除选择的图文
    remove(node) {
      var data = node.data
      // 注意:对数组对象进行深度复制,否则原始对象不能进行删除操作
      var temp = JSON.parse(JSON.stringify(this.newsTreeData))
      var index = temp.findIndex(function(o) {
        return o.id === data.id
      })
      temp.splice(index, 1)
      this.newsTreeData = temp
    },
    openSend() {
      this.formData = {
        wxid: this.account.wxid,
        type: 'text',
        content: '',
        scope: 'all',
        receivers: '',
        picurl: '',
        newsids: []
      }
      this.newsTreeData = []
      if (this.$refs['sendForm']) { this.$refs['sendForm'].resetFields() }
      this.sendDialogVisible = true
    },
    doSend() {
      this.$refs['sendForm'].validate((valid) => {
        if (valid) {
          var newsids = []
          this.newsTreeData.forEach((o) => {
            newsids.push(o.id)
          })
          this.$set(this.formData, 'newsids', newsids.toString())
          this.btnLoading = true
          this.$axios
            .$post(API_WX_MSG_MASS_PUSH, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.sendDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 跳转到图文管理页
    gotoNews() {
      this.$router.push({
        path: '/platform/wx/msg/mass/news',
        query: {
          wxid: this.account.wxid,
          wxname: this.account.wxname
        }
      })
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
            this.uploadImageUrl = process.env.API + API_WX_FILE_UPLOAD_IMAGE + this.account.wxid
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
      this.uploadImageUrl = process.env.API + API_WX_FILE_UPLOAD_IMAGE + val
      this.listPage()
    },
    // 图片上传成功后
    handlePicSuccess(response, file, fileList, attrName) {
      if (response.code === 0) {
        this.$set(this.formData, 'picurl', response.data.picurl)
        this.$set(this.formData, 'media_id', response.data.id)
      } else {
        this.$message({
          message: response.msg,
          type: 'error'
        })
        this.$set(this.formData, 'picurl', '')
        this.$set(this.formData, 'media_id', '')
      }
    },
    // 图片上传前判断大小
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
      this.$axios.$post(API_WX_MSG_MASS_LIST, this.pageData).then((res) => {
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
    // 页码变动事件
    doChangePage2(val) {
      this.pageData2.pageNo = val
      this.listPage2()
    },
    // 页大小变动事件
    doSizeChange2(val) {
      this.pageData2.pageSize = val
      this.listPage2()
    },
    // 页排序事件
    doPageSort2(column) {
      this.pageData2.pageOrderName = column.prop
      this.pageData2.pageOrderBy = column.order
      this.listPage2()
    },
    // 获取微信会员分页数据
    listPage2() {
      this.$set(this.pageData2, 'wxid', this.account.wxid)
      this.$axios.$post(API_WX_USER_LIST, this.pageData2).then((res) => {
        if (res.code === 0) {
          this.tableData2 = res.data.list
          this.pageData2.totalCount = res.data.totalCount
        }
      })
    },
    // 条件查询展示第一页内容
    doSearch2() {
      this.pageData2.pageNo = 1
      this.listPage2()
    },
    // 页码变动事件
    doChangePage3(val) {
      this.pageData3.pageNo = val
      this.listPage3()
    },
    // 页大小变动事件
    doSizeChange3(val) {
      this.pageData3.pageSize = val
      this.listPage3()
    },
    // 页排序事件
    doPageSort3(column) {
      this.pageData3.pageOrderName = column.prop
      this.pageData3.pageOrderBy = column.order
      this.listPage3()
    },
    // 获取图文分页数据
    listPage3() {
      this.$set(this.pageData3, 'wxid', this.account.wxid)
      this.$axios.$post(API_WX_MSG_MASS_NEWS_LIST, this.pageData3).then((res) => {
        if (res.code === 0) {
          this.tableData3 = res.data.list
          this.pageData3.totalCount = res.data.totalCount
        }
      })
    },
    // 条件查询展示第一页内容
    doSearch3() {
      this.pageData3.pageNo = 1
      this.listPage3()
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
  .el-table-expand {
    font-size: 0;
  }
  .el-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .el-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
