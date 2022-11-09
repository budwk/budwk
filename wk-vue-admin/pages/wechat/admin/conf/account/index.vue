<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>账号配置</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.conf.account.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新建公众号
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
              prop="appname"
              label="公众号名称"
            />
            <el-table-column
              prop="id"
              label="ID"
            />
            <el-table-column
              prop="appid"
              label="AppID"
            />
            <el-table-column
              prop="mchid"
              label="支付商户号"
            >
              <template slot-scope="scope">
                {{ getPayName(scope.row.mchid) }}
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'wx.conf.account.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'wx.conf.account.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="openDelete(scope.row)"
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
      title="新建公众号"
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
        <el-form-item prop="id" label="唯一标识">
          <el-input
            v-model="formData.id"
            maxlength="100"
            placeholder="唯一标识"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>

        <el-form-item label="配置URL">
          {{ conf.AppDomain }}/wechat/open/weixin/msg/{{ formData.id }}
          <el-alert
            title="微信后台配置的URL"
            type="success"
            style="height:32px;"
          />
        </el-form-item>

        <el-form-item prop="appname" label="公众号名称">
          <el-input
            v-model="formData.appname"
            maxlength="100"
            placeholder="公众号名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appid" label="AppId">
          <el-input
            v-model="formData.appid"
            maxlength="100"
            placeholder="AppId"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appsecret" label="AppSecret">
          <el-input
            v-model="formData.appsecret"
            maxlength="100"
            placeholder="AppSecret"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="token" label="Token">
          <el-input
            v-model="formData.token"
            maxlength="100"
            placeholder="Token"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="encodingAESKey" label="EncodingAESKey">
          <el-input
            v-model="formData.encodingAESKey"
            maxlength="100"
            placeholder="EncodingAESKey"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="mchid" label="支付商户">
          <el-select v-model="formData.mchid" clearable placeholder="微信支付商户绑定">
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
        <el-button @click="createDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doCreate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改公众号"
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
        <el-form-item prop="id" label="唯一标识">
          <el-input
            v-model="formData.id"
            maxlength="100"
            placeholder="唯一标识"
            auto-complete="off"
            tabindex="1"
            type="text"
            :readonly="true"
            :disabled="true"
          />
        </el-form-item>

        <el-form-item label="配置URL">
          {{ conf.AppDomain }}/wechat/open/weixin/msg/{{ formData.id }}
          <el-alert
            title="微信后台配置的URL"
            type="success"
            style="height:32px;"
          />
        </el-form-item>

        <el-form-item prop="appname" label="公众号名称">
          <el-input
            v-model="formData.appname"
            maxlength="100"
            placeholder="公众号名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appid" label="AppId">
          <el-input
            v-model="formData.appid"
            maxlength="100"
            placeholder="AppId"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="appsecret" label="AppSecret">
          <el-input
            v-model="formData.appsecret"
            maxlength="100"
            placeholder="AppSecret"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="token" label="Token">
          <el-input
            v-model="formData.token"
            maxlength="100"
            placeholder="Token"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="encodingAESKey" label="EncodingAESKey">
          <el-input
            v-model="formData.encodingAESKey"
            maxlength="100"
            placeholder="EncodingAESKey"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="mchid" label="支付商户">
          <el-select v-model="formData.mchid" clearable placeholder="微信支付商户绑定">
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
        <el-button @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_CONFIG_LIST,
  API_WX_CONFIG_CREATE,
  API_WX_CONFIG_DELETE,
  API_WX_CONFIG_UPDATE,
  API_WX_CONFIG_GET,
  API_WX_CONFIG_PAY_QUERY
} from '@/constant/api/wechat/config'
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
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
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
        id: [
          {
            required: true,
            message: '微信ID',
            trigger: 'blur'
          }
        ],
        appname: [
          {
            required: true,
            message: '公众号名称',
            trigger: 'blur'
          }
        ],
        appid: [
          {
            required: true,
            message: 'AppId',
            trigger: 'blur'
          }
        ],
        appsecret: [
          {
            required: true,
            message: 'AppSecret',
            trigger: 'blur'
          }
        ],
        token: [
          {
            required: true,
            message: 'Token',
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
    getPayName(mchid) {
      const index = this.payData.findIndex((item) => {
        return item.mchid === mchid
      })
      return index >= 0 ? this.payData[index].name : ''
    },
    // 打开字新增窗口
    openCreate() {
      this.formData = {
        id: '',
        payEnabled: false
      }
      this.createDialogVisible = true
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_CONFIG_CREATE, this.formData)
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
      this.$axios.$get(API_WX_CONFIG_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
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
            .$post(API_WX_CONFIG_UPDATE, this.formData)
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
        '确定删除' + row.name + '？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.btnLoading = true
        this.$axios
          .$delete(API_WX_CONFIG_DELETE + row.id)
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
      this.$axios.$post(API_WX_CONFIG_LIST, this.pageData).then((res) => {
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
