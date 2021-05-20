<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.user.list`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.user.list.sync'"
          size="small"
          icon="fa fa-cloud-download"
          @click="openDown"
        >
          {{ $t(`wx.user.list.sync`) }}
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
          <el-form-item
            :label="$t(`wx.user.list.form.openid`)"
          >
            <el-input
              v-model="pageData.openid"
              :placeholder="$t(`wx.user.list.form.openid`)"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
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
            @sort-change="doPageSort"
          >
            <el-table-column :label="$t(`wx.user.list.form.openid`)" header-align="left" prop="openid" width="250" />

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

            <el-table-column sortable :label="$t(`wx.user.list.form.subscribeAt`)" header-align="center" align="center" prop="subscribeAt">
              <template slot-scope="scope">
                {{ scope.row.subscribeAt*1000 | moment("datetime") }}
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
      :title="$t(`wx.user.list.sync`)"
      :visible.sync="downDialogVisible"
      width="40%"
    >
      <el-row>
        <el-alert
          :title="$t(`wx.user.list.sync.tip`)"
          type="success"
        />
      </el-row>
      <el-row style="padding-top:5px;text-align: center">
        <el-progress type="circle" :percentage="progressNum" :status="progressStatus" />
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="downDialogVisible = false">{{ $t(`system.commons.button.cancel`) }}</el-button>
        <el-button type="primary" :loading="btnLoading" @click="doDown">{{ $t(`wx.user.list.sync.button`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_WX_USER_LIST,
  API_WX_USER_DOWN
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
      downDialogVisible: false,
      listData: [],
      pageData: {
        wxid: '',
        openid: '',
        nickname: '',
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
      progressNum: 0,
      progressStatus: null
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
    openDown() {
      this.progressNum = 0
      this.progressStatus = null
      this.downDialogVisible = true
    },
    doDown() {
      var t = setInterval(() => {
        this.progressNum += 10
        if (this.progressNum >= 100) {
          window.clearInterval(t)
        }
      }, 400)
      this.btnLoading = true
      this.$axios.$get(API_WX_USER_DOWN + this.account.wxid).then((d) => {
        this.btnLoading = false
        this.progressNum = 100
        if (d.code === 0) {
          this.progressStatus = 'success'
          this.downDialogVisible = false
          this.$message({
            message: d.msg,
            type: 'success'
          })
          this.listPage()
        } else {
          this.progressStatus = 'exception'
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
      this.$axios.$post(API_WX_USER_LIST, this.pageData).then((res) => {
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
