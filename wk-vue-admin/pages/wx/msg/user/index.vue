<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.msg.user`) }}</span>
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
          <el-form-item
            :label="$t(`wx.user.list.form.nickname`)"
          >
            <el-input
              v-model="pageData.nickname"
              :placeholder="$t(`wx.user.list.form.nickname`)"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
          </el-form-item>
          <el-form-item
            :label="$t(`wx.msg.user.form.content`)"
          >
            <el-input
              v-model="pageData.content"
              :placeholder="$t(`wx.msg.user.form.content`)"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
          </el-form-item>
          <div class="platform-content-search-op">
            <el-button
              size="small"
              type="primary"
              @click="doSearch"
            >
              {{ $t(`system.commons.button.search`) }}
            </el-button>
          </div>
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
            <el-table-column type="expand">
              <template slot-scope="scope">
                <el-form label-position="left" inline class="el-table-expand" size="mini">
                  <el-form-item :label="$t(`wx.msg.user.form.replyContent`)">
                    <span v-if="scope.row.replyId && scope.row.replyId!=''">{{ scope.row.reply.content }}</span>
                  </el-form-item>
                  <el-form-item :label="$t(`wx.msg.user.form.replyAt`)">
                    <span v-if="scope.row.replyId && scope.row.replyId!=''">
                      {{ scope.row.reply.createdAt | moment("datetime") }}</span>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.user.list.form.nickname`)"
              header-align="left"
              prop="nickname"
              :show-overflow-tooltip="true"
            />

            <el-table-column :label="$t(`wx.msg.user.form.type`)" header-align="left" prop="type" width="80">
              <template slot-scope="scope">
                <span v-if="scope.row.type==='txt'">
                  {{ $t(`wx.msg.user.form.type.txt`) }}
                </span>
                <span v-if="scope.row.type==='image'">
                  {{ $t(`wx.msg.user.form.type.image`) }}
                </span>
                <span v-if="scope.row.type==='video'">
                  {{ $t(`wx.msg.user.form.type.video`) }}
                </span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.msg.user.form.content`)"
              header-align="left"
              prop="content"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.type==='txt'">
                  {{ scope.row.content }}
                </span>
                <span v-if="scope.row.type==='image'">
                  <a :href="conf.AppFileDomain+scope.row.content" target="_blank">
                    <img :src="conf.AppFileDomain+scope.row.content" width="30" height="30">
                  </a>
                </span>
                <span v-if="scope.row.type==='video'">
                  <a :href="conf.AppFileDomain+scope.row.content" target="_blank">
                    <i class="fa fa-film" style="font-size: 2em;" />
                  </a>
                </span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.msg.user.form.status`)"
              header-align="left"
              prop="replyId"
              :show-overflow-tooltip="true"
              width="160"
            >
              <template slot-scope="scope">
                <span v-if="!scope.row.replyId || scope.row.replyId==''">{{ $t(`wx.msg.user.form.status.no`) }}
                  <el-button size="mini" round @click="openReply(scope.row)">{{ $t(`wx.msg.user.reply`) }}</el-button>
                </span>
                <span v-if="scope.row.replyId && scope.row.replyId!=''">{{ $t(`wx.msg.user.form.status.yes`) }}</span>
              </template>
            </el-table-column>

            <el-table-column sortable :label="$t(`wx.msg.user.form.createdAt`)" header-align="center" align="center" prop="createdAt">
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
      :title="$t(`wx.msg.user.reply.title`)"
      :visible.sync="replyDialogVisible"
      width="40%"
    >
      <el-form ref="replyForm" :model="formData" :rules="formRules" size="small" label-width="80px">
        <el-form-item :label="$t(`wx.msg.user.nickname`)">
          {{ formData.nickname }}
        </el-form-item>
        <el-form-item :label="$t(`wx.msg.user.form.content`)">
          <span v-if="formData.type==='txt'">
            {{ formData.msgContent }}
          </span>
          <span v-if="formData.type==='image'">
            <a :href="conf.AppFileDomain+formData.msgContent" target="_blank">
              <img :src="conf.AppFileDomain+formData.msgContent" width="30" height="30">
            </a>
          </span>
          <span v-if="formData.type==='video'">
            <a :href="conf.AppFileDomain+formData.msgContent" target="_blank">
              <i class="fa fa-film" style="font-size: 2em;" />
            </a>
          </span>
        </el-form-item>

        <el-form-item :label="$t(`wx.msg.user.form.replyContent`)" prop="replyContent">
          <el-input v-model="formData.replyContent" type="textarea" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button :loading="btnLoading" @click="replyDialogVisible = false">{{ $t(`system.commons.button.cancel`) }}</el-button>
        <el-button type="primary" @click="doReply">{{ $t(`wx.msg.user.reply.button`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_MSG_USER_LIST,
  API_WX_MSG_USER_REPLY
} from '@/constant/api/platform/wx/msg_user'
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
      listData: [],
      pageData: {
        content: '',
        nickname: '',
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
      }
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
        replyContent: [
          {
            required: true,
            message: this.$t(`wx.msg.user.form.replyContent`),
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
      this.listPage()
    },
    openReply(row) {
      this.replyDialogVisible = true
      this.formData.msgid = row.id
      this.formData.type = row.type
      this.formData.msgContent = row.content
      this.formData.openid = row.openid
      this.formData.nickname = row.nickname
    },
    doReply() {
      this.$refs['replyForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_MSG_USER_REPLY + this.account.wxid, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.replyDialogVisible = false
                this.listPage()
              }
            })
        }
      })
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
      this.$axios.$post(API_WX_MSG_USER_LIST, this.pageData).then((res) => {
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
