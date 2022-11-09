<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>会员列表</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.user.list.sync'"
          size="small"
          icon="fa fa-cloud-download"
          @click="openDown"
        >
          同步会员资料
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
          <el-form-item
            label="openid"
          >
            <el-input
              v-model="pageData.openid"
              placeholder="openid"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
          </el-form-item>
          <el-form-item
            label="微信昵称"
          >
            <el-input
              v-model="pageData.nickname"
              placeholder="微信昵称"
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
              搜索
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
            <el-table-column label="openid" header-align="left" prop="openid" width="250" />

            <el-table-column
              label="微信昵称"
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
              label="性别"
              header-align="left"
              prop="sex"
              :show-overflow-tooltip="true"
              width="80"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.sex==1">男</span>
                <span v-if="scope.row.sex==2">女</span>
                <span v-if="scope.row.sex==0">未知</span>
              </template>
            </el-table-column>

            <el-table-column
              label="所在区域"
              header-align="left"
              prop="id"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                {{ scope.row.country }} <span v-if="scope.row.province">-</span> {{ scope.row.province }} <span v-if="scope.row.city">-</span> {{ scope.row.city }}
              </template>
            </el-table-column>

            <el-table-column
              label="关注公众号"
              header-align="center"
              align="center"
              prop="subscribe"
              :show-overflow-tooltip="true"
              width="100"
            >
              <template slot-scope="scope">
                <span v-if="!scope.row.subscribe">未关注</span>
                <span v-if="scope.row.subscribe" style="color:green;">已关注</span>
              </template>
            </el-table-column>

            <el-table-column sortable label="关注时间" header-align="center" align="center" prop="subscribeAt">
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
      title="同步会员资料"
      :visible.sync="downDialogVisible"
      width="40%"
    >
      <el-row>
        <el-alert
          title="微信公众号后台配置好接入URL后，同步操作只用手动执行一次"
          type="success"
        />
      </el-row>
      <el-row style="padding-top:5px;text-align: center">
        <el-progress type="circle" :percentage="progressNum" :status="progressStatus" />
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="downDialogVisible = false">关 闭</el-button>
        <el-button type="primary" :loading="btnLoading" @click="doDown">开始同步</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_WX_USER_LIST,
  API_WX_USER_DOWN
} from '@/constant/api/wechat/user'
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
