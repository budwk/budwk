<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.tpl.list`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.tpl.list.get'"
          size="small"
          icon="fa fa-cloud-download"
          @click="openGet"
        >
          {{ $t(`wx.tpl.list.get`) }}
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

    <el-dialog
      :title="$t(`wx.tpl.list.get`)"
      :visible.sync="getDialogVisible"
      width="40%"
    >
      <el-row style="padding-top:5px;text-align: center">
        <el-progress type="circle" :percentage="progressNum" :status="progressStatus" />
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="getDialogVisible = false">{{ $t(`system.commons.button.cancel`) }}</el-button>
        <el-button type="primary" :loading="btnLoading" @click="doGet">{{ $t(`wx.tpl.list.button.get`) }}</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_TPL_LIST_LIST,
  API_WX_TPL_LIST_GET
} from '@/constant/api/platform/wx/tpl_list'
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
    // 打开字新增窗口
    openGet() {
      this.progressNum = 0
      this.progressStatus = null
      this.getDialogVisible = true
    },
    // 提交表单
    doGet() {
      var t = setInterval(() => {
        this.progressNum += 10
        if (this.progressNum >= 100) {
          window.clearInterval(t)
        }
      }, 400)
      this.btnLoading = true
      this.$axios.$get(API_WX_TPL_LIST_GET + this.account.wxid).then((d) => {
        this.btnLoading = false
        this.progressNum = 100
        if (d.code === 0) {
          this.progressStatus = 'success'
          this.getDialogVisible = false
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
      this.$axios.$post(API_WX_TPL_LIST_LIST, this.pageData).then((res) => {
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

