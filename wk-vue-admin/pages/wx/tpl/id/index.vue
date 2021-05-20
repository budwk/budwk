<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.tpl.id`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.tpl.id.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          {{ $t(`wx.tpl.id.create`) }}
        </el-button>
        <el-button
          v-permission="'wx.tpl.id.delete'"
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
              :label="$t(`wx.tpl.id.form.id`)"
              header-align="left"
              prop="id"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              :label="$t(`wx.tpl.id.form.template_id`)"
              header-align="left"
              prop="template_id"
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
                  v-permission="'wx.tpl.id.delete'"
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
      :title="$t(`wx.tpl.id.create`)"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="id" :label="$t(`wx.tpl.id.form.id`)">
          <el-input
            v-model="formData.id"
            maxlength="200"
            :placeholder="$t(`wx.tpl.id.form.id`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
          <div style="padding-top:10px;">
            <el-alert
              :title="$t(`wx.tpl.id.form.id.tip`)"
              type="warning"
              effect="dark"
            />
          </div>
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

  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_TPL_ID_LIST,
  API_WX_TPL_ID_CREATE,
  API_WX_TPL_ID_DELETE,
  API_WX_TPL_ID_DELETE_MORE
} from '@/constant/api/platform/wx/tpl_id'
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
        id: [
          {
            required: true,
            message: this.$t(`wx.tpl.id.form.id`),
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
            .$post(API_WX_TPL_ID_CREATE, this.formData)
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
        this.$t(`system.error.delete.confirm`),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_WX_TPL_ID_DELETE, { wxid: this.account.wxid, id: row.id }).then((d) => {
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
      var template_ids = []
      this.tableSelectData.forEach((obj) => {
        template_ids.push(obj.template_id)
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
        this.$axios.$post(API_WX_TPL_ID_DELETE_MORE, { wxid: this.account.wxid, ids: ids.toString(), template_ids: template_ids.toString() }).then((d) => {
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
      this.$axios.$post(API_WX_TPL_ID_LIST, this.pageData).then((res) => {
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

