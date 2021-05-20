<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.tpl.log`) }}</span>
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
            ref="listTable"
            v-loading="listLoading"
            :data="listData"
            stripe
            @sort-change="doPageSort"
          >

            <el-table-column
              :label="$t(`wx.tpl.list.form.template_id`)"
              header-align="left"
              prop="template_id"
            />

            <el-table-column
              :label="$t(`wx.tpl.list.form.title`)"
              header-align="left"
              prop="title"
              width="120"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              :label="$t(`wx.tpl.list.form.primary_industry`)"
              header-align="left"
              prop="primary_industry"
              width="120"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                {{ scope.row.primary_industry }} - {{ scope.row.deputy_industry }}
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.tpl.list.form.example`)"
              header-align="left"
              prop="example"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              :label="$t(`wx.tpl.list.form.content`)"
              header-align="left"
              prop="content"
            />

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

  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_TPL_LOG_LIST
} from '@/constant/api/platform/wx/tpl_log'
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
      getDialogVisible: false,
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
          {
            required: true,
            message: this.$t(`wx.tpl.list.form.id`),
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
      this.$axios.$post(API_WX_TPL_LOG_LIST, this.pageData).then((res) => {
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

