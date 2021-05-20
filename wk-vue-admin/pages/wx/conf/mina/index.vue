<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.conf.mina`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.conf.mina.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          {{ $t(`wx.conf.mina.create`) }}
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
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
              type="selection"
              width="50"
            />

            <el-table-column
              :label="$t(`wx.conf.mina.form.appname`)"
              header-align="left"
              prop="appname"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              :label="$t(`wx.conf.mina.form.appid`)"
              header-align="left"
              prop="appid"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              :label="$t(`wx.conf.mina.form.wxid`)"
              header-align="left"
              prop="wxid"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                {{ getAccountName(scope.row.wxid) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="mchid"
              :label="$t(`wx.conf.pay.form.name`)"
            >
              <template slot-scope="scope">
                {{ getPayName(scope.row.mchid) }}
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              :label="$t(`system.commons.txt.ext`)"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'wx.conf.mina.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  {{
                    $t(`system.commons.button.update.mini`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.conf.mina.delete'"
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
      :title="$t(`wx.conf.mina.create`)"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="125px"
      >
        <el-form-item prop="appname" :label="$t(`wx.conf.mina.form.appname`)">
          <el-input
            v-model="formData.appname"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appname`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appid" :label="$t(`wx.conf.mina.form.appid`)">
          <el-input
            v-model="formData.appid"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appid`)"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appsecret" :label="$t(`wx.conf.mina.form.appsecret`)">
          <el-input
            v-model="formData.appsecret"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appsecret`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="wxid" :label="$t(`wx.conf.mina.form.wxid`)">
          <el-select v-model="formData.wxid" clearable :placeholder="$t(`el.select.placeholder`)">
            <el-option
              v-for="item in accounts"
              :key="item.id"
              :label="item.appname"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item prop="mchid" :label="$t(`wx.conf.pay.form.name`)">
          <el-select v-model="formData.mchid" clearable :placeholder="$t(`el.select.placeholder`)">
            <el-option
              v-for="item in payData"
              :key="item.mchid"
              :label="item.name"
              :value="item.mchid"
            />
          </el-select>
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
      :title="$t(`wx.conf.mina.update`)"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="125px"
      >
        <el-form-item prop="appname" :label="$t(`wx.conf.mina.form.appname`)">
          <el-input
            v-model="formData.appname"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appname`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appid" :label="$t(`wx.conf.mina.form.appid`)">
          <el-input
            v-model="formData.appid"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appid`)"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appsecret" :label="$t(`wx.conf.mina.form.appsecret`)">
          <el-input
            v-model="formData.appsecret"
            maxlength="120"
            :placeholder="$t(`wx.conf.mina.form.appsecret`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="wxid" :label="$t(`wx.conf.mina.form.wxid`)">
          <el-select v-model="formData.wxid" clearable :placeholder="$t(`el.select.placeholder`)">
            <el-option
              v-for="item in accounts"
              :key="item.id"
              :label="item.appname"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item prop="mchid" :label="$t(`wx.conf.pay.form.name`)">
          <el-select v-model="formData.mchid" clearable :placeholder="$t(`el.select.placeholder`)">
            <el-option
              v-for="item in payData"
              :key="item.mchid"
              :label="item.name"
              :value="item.mchid"
            />
          </el-select>
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
  API_WX_CONF_MINA_LIST,
  API_WX_CONF_MINA_GET,
  API_WX_CONF_MINA_CREATE,
  API_WX_CONF_MINA_UPDATE,
  API_WX_CONF_MINA_DELETE,
  API_WX_CONFIG_PAY_QUERY
} from '@/constant/api/platform/wx/mina'
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
      visiableData: [],
      payData: []
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
        appname: [
          {
            required: true,
            message: this.$t(`wx.conf.mina.form.appname`),
            trigger: 'blur'
          }
        ],
        appid: [
          {
            required: true,
            message: this.$t(`wx.conf.mina.form.appid`),
            trigger: 'blur'
          }
        ],
        appsecret: [
          {
            required: true,
            message: this.$t(`wx.conf.mina.form.appsecret`),
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
      this.queryPay()
    }
  },
  methods: {
    queryPay() {
      this.$axios.$get(API_WX_CONFIG_PAY_QUERY).then((d) => {
        if (d.code === 0) {
          this.payData = d.data
        }
      })
    },
    getAccountName(id) {
      const index = this.accounts.findIndex((item) => {
        return item.id === id
      })
      return index >= 0 ? this.accounts[index].appname : ''
    },
    getPayName(mchid) {
      const index = this.payData.findIndex((item) => {
        return item.mchid === mchid
      })
      return index >= 0 ? this.payData[index].name : ''
    },
    // 是否显示密钥
    isVisable(appid) {
      if (this.visiableData.indexOf(appid) >= 0) {
        return true
      }
      return false
    },
    // 隐藏密钥
    setNotVisable: function(appid) {
      var index = this.visiableData.indexOf(appid)
      if (index >= 0) {
        this.visiableData.splice(index, 1)
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
        id: ''
      }
      this.createDialogVisible = true
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_CONF_MINA_CREATE, this.formData)
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
      this.$axios.$get(API_WX_CONF_MINA_GET + row.id).then((d) => {
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
            .$post(API_WX_CONF_MINA_UPDATE, this.formData)
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
        this.$axios.$delete(API_WX_CONF_MINA_DELETE + row.id).then((d) => {
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
      this.$axios.$post(API_WX_CONF_MINA_LIST, this.pageData).then((res) => {
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

