<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.conf.pay`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.conf.pay.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          {{ $t(`wx.conf.pay.create`) }}
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-list">
        <div class="platform-content-list-table">
          <el-table
            v-loading="listLoading"
            :data="listData"
            stripe
            @sort-change="doPageSort"
          >
            <el-table-column
              prop="name"
              :label="$t(`wx.conf.pay.form.name`)"
            />
            <el-table-column
              prop="mchid"
              :label="$t(`wx.conf.pay.form.mchid`)"
            />
            <el-table-column
              prop="v3key"
              :label="$t(`wx.conf.pay.form.v3key`)"
            />
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              :label="$t(`system.commons.txt.ext`)"
              width="230"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'wx.conf.pay'"
                  type="text"
                  size="small"
                  @click.native.prevent="openCert(scope.row)"
                >
                  {{
                    $t(`wx.conf.pay.cert`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.conf.pay.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  {{
                    $t(`system.commons.button.update.mini`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.conf.pay.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="openDelete(scope.row)"
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
      :title="$t(`wx.conf.pay.create`)"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="145px"
      >
        <el-form-item prop="name" :label="$t(`wx.conf.pay.form.name`)">
          <el-input
            v-model="formData.name"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.name`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="mchid" :label="$t(`wx.conf.pay.form.mchid`)">
          <el-input
            v-model="formData.mchid"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.mchid`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3key" :label="$t(`wx.conf.pay.form.v3key`)">
          <el-input
            v-model="formData.v3key"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.v3key`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3keyPath" :label="$t(`wx.conf.pay.form.v3keyPath`)">
          <el-input
            v-model="formData.v3keyPath"
            maxlength="100"
            placeholder="apiclient_key.pem"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3certPath" :label="$t(`wx.conf.pay.form.v3certPath`)">
          <el-input
            v-model="formData.v3certPath"
            maxlength="100"
            placeholder="apiclient_cert.pem"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3certP12Path" :label="$t(`wx.conf.pay.form.v3certP12Path`)">
          <el-input
            v-model="formData.v3certP12Path"
            maxlength="100"
            placeholder="apiclient_cert.p12"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
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
      :title="$t(`wx.conf.pay.update`)"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="145px"
      >
        <el-form-item prop="name" :label="$t(`wx.conf.pay.form.name`)">
          <el-input
            v-model="formData.name"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.name`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="mchid" :label="$t(`wx.conf.pay.form.mchid`)">
          <el-input
            v-model="formData.mchid"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.mchid`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3key" :label="$t(`wx.conf.pay.form.v3key`)">
          <el-input
            v-model="formData.v3key"
            maxlength="100"
            :placeholder="$t(`wx.conf.pay.form.v3key`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3keyPath" :label="$t(`wx.conf.pay.form.v3keyPath`)">
          <el-input
            v-model="formData.v3keyPath"
            maxlength="100"
            placeholder="apiclient_key.pem"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3certPath" :label="$t(`wx.conf.pay.form.v3certPath`)">
          <el-input
            v-model="formData.v3certPath"
            maxlength="100"
            placeholder="apiclient_cert.pem"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="v3certP12Path" :label="$t(`wx.conf.pay.form.v3certP12Path`)">
          <el-input
            v-model="formData.v3certP12Path"
            maxlength="100"
            placeholder="apiclient_cert.p12"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
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

    <el-dialog
      :title="$t(`wx.conf.pay.cert`)"
      :visible.sync="certDialogVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <div class="platform-content-list-table">
        <el-table
          :data="certListData"
          stripe
          :default-sort="{prop: 'expire_at', order: 'descending'}"
          @sort-change="certDoPageSort"
        >
          <el-table-column
            prop="mchid"
            :label="$t(`wx.conf.pay.form.mchid`)"
          />
          <el-table-column
            prop="serial_no"
            :label="$t(`wx.conf.pay.cert.form.serial_no`)"
          />
          <el-table-column
            prop="effective_at"
            :label="$t(`wx.conf.pay.cert.form.effective_at`)"
          >
            <template slot-scope="scope">
              {{ scope.row.effective_at | moment('datetime') }}
            </template>
          </el-table-column>
          <el-table-column
            prop="expire_at"
            sortable
            :label="$t(`wx.conf.pay.cert.form.expire_at`)"
          >
            <template slot-scope="scope">
              {{ scope.row.expire_at | moment('datetime') }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="platform-content-list-pagination">
        <el-pagination
          :current-page="certPageData.pageNo"
          :total="certPageData.totalCount"
          class="platform-pagenation"
          background
          layout="prev, pager, next"
          @current-change="certDoChangePage"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          @click="certDialogVisible=false"
        >{{ $t(`system.commons.button.close`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_CONFIG_PAY_LIST,
  API_WX_CONFIG_PAY_CREATE,
  API_WX_CONFIG_PAY_DELETE,
  API_WX_CONFIG_PAY_UPDATE,
  API_WX_CONFIG_PAY_GET,
  API_WX_CONFIG_PAYCERT_LIST
} from '@/constant/api/platform/wx/pay'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      createDialogVisible: false,
      updateDialogVisible: false,
      certDialogVisible: false,
      listData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      certListData: [],
      certPageData: {
        mchid: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'expire_at',
        pageOrderBy: 'descending'
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
        name: [
          {
            required: true,
            message: this.$t(`wx.conf.pay.form.name`),
            trigger: 'blur'
          }
        ],
        mchid: [
          {
            required: true,
            message: this.$t(`wx.conf.pay.form.mchid`),
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.listPage()
    }
  },
  methods: {
    // 打开字新增窗口
    openCreate() {
      this.formData = {
        id: '',
        payEnabled: false,
        payInfo: {}
      }
      this.createDialogVisible = true
    },
    openCert(row) {
      this.certDialogVisible = true
      this.certPageData.mchid = row.mchid
      this.certListPage()
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_CONFIG_PAY_CREATE, this.formData)
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
      this.$axios.$get(API_WX_CONFIG_PAY_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          if (this.formData.payInfo && this.formData.payInfo.length() > 2) {
            this.$set(this.formData, 'payInfo', JSON.parse(this.formData.payInfo))
          }
          this.updateDialogVisible = true
        }
      })
    },
    // 提交表单
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_CONFIG_PAY_UPDATE, this.formData)
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
    openDelete(row) {
      this.$confirm(
        this.$t(`wx.conf.pay.delete.tip`, { name: row.name }),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.btnLoading = true
        this.$axios
          .$delete(API_WX_CONFIG_PAY_DELETE + row.id)
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
      this.$axios.$post(API_WX_CONFIG_PAY_LIST, this.pageData).then((res) => {
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
    certDoChangePage(val) {
      this.certPageData.pageNo = val
      this.certListPage()
    },
    // 页排序事件
    certDoPageSort(column) {
      this.certPageData.pageOrderName = column.prop
      this.certPageData.pageOrderBy = column.order
      this.certListPage()
    },
    // 获取分页查询数据
    certListPage() {
      this.$axios.$post(API_WX_CONFIG_PAYCERT_LIST, this.certPageData).then((res) => {
        if (res.code === 0) {
          this.certListData = res.data.list
          this.certPageData.totalCount = res.data.totalCount
        }
      })
    }
  }
}
</script>
