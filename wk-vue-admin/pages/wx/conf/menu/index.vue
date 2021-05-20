<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>{{ $t(`wx.conf.menu`) }}</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.conf.menu.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          {{ $t(`wx.conf.menu.create`) }}
        </el-button>

        <el-button
          v-permission="'wx.conf.menu.push'"
          size="small"
          icon="fa fa-cloud-upload"
          @click="pushMenu"
        >
          {{ $t(`wx.conf.menu.push`) }}
        </el-button>

        <el-button
          v-permission="'cms.content.channel.update'"
          size="small"
          @click="openSort"
        >
          {{ $t(`cms.content.channel.sort`) }}
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
            :key="tableKey"
            :data="tableData"
            style="width: 100%"
            size="small"
            :highlight-current-row="true"
            row-key="id"
            lazy
            :load="loadChild"
          >
            <el-table-column
              :label="$t(`wx.conf.menu.form.menuName`)"
              header-align="center"
              prop="menuName"
              width="200"
              :show-overflow-tooltip="true"
              align="left"
            />

            <el-table-column
              :label="$t(`wx.conf.menu.form.menuType`)"
              header-align="center"
              align="center"
              prop="menuType"
              :show-overflow-tooltip="true"
              width="120"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.menuType==''">{{ $t(`wx.conf.menu.form.menuType.menu`) }}</span>
                <span v-if="scope.row.menuType=='view'">{{ $t(`wx.conf.menu.form.menuType.view`) }}</span>
                <span v-if="scope.row.menuType=='click'">{{ $t(`wx.conf.menu.form.menuType.click`) }}</span>
                <span v-if="scope.row.menuType=='miniprogram'">{{ $t(`wx.conf.menu.form.menuType.miniprogram`) }}</span>
              </template>
            </el-table-column>

            <el-table-column
              :label="$t(`wx.conf.menu.form.content`)"
              header-align="left"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.menuType==''">-</span>
                <span v-if="scope.row.menuType=='view'">{{ scope.row.url }}</span>
                <span v-if="scope.row.menuType=='click'">{{ $t(`wx.conf.menu.form.menuKey`) }}: {{ scope.row.menuKey }}</span>
                <span v-if="scope.row.menuType=='miniprogram'">{{ $t(`wx.conf.menu.form.appid`) }}: {{ scope.row.appid }}</span>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t(`system.commons.txt.ext`)"
              header-align="center"
              :show-overflow-tooltip="true"
              align="right"
              width="180px"
            >
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.path.length<8"
                  v-permission="'wx.conf.menu.create'"
                  type="text"
                  size="small"
                  @click.native.prevent="addSubRow(scope.row)"
                >
                  {{
                    $t(`system.commons.button.addsub.mini`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.conf.menu.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="updateRow(scope.row)"
                >
                  {{
                    $t(`system.commons.button.update.mini`)
                  }}
                </el-button>
                <el-button
                  v-permission="'wx.conf.menu.delete'"
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
      </div>
    </div>

    <el-dialog
      :title="$t(`wx.conf.menu.create`)"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="createForm" :model="formData" :rules="formRules" size="small" label-width="100px">
        <el-form-item prop="wxid" :label="$t(`wx.conf.menu.form.wxid`)">
          <span>{{ formData.wxname }}</span>
        </el-form-item>
        <el-form-item prop="parentId" :label="$t(`wx.conf.menu.form.parentId`)" label-width="100px">
          <el-cascader
            v-if="!isAddFromSub"
            v-model="formData.parentId"
            style="width: 100%"
            :options="menuOptions"
            :props="props"
            tabindex="1"
            :placeholder="$t(`system.commons.txt.parentRoot`)"
          />
          <el-input v-if="isAddFromSub" v-model="formData.parentName" type="text" :disabled="true" />
        </el-form-item>
        <el-form-item prop="menuName" :label="$t(`wx.conf.menu.form.menuName`)">
          <el-input
            v-model="formData.menuName"
            maxlength="100"
            :placeholder="$t(`wx.conf.menu.form.menuName`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            :title="$t(`wx.conf.menu.form.menuName.tip1`)"
            type="warning"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            :title="$t(`wx.conf.menu.form.menuName.tip2`)"
            type="warning"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="menuType" :label="$t(`wx.conf.menu.form.menuType`)">
          <el-radio-group v-model="formData.menuType" size="medium">
            <el-radio label="">{{ $t(`wx.conf.menu.form.menuType.menu`) }}</el-radio>
            <el-radio label="view">{{ $t(`wx.conf.menu.form.menuType.view`) }}</el-radio>
            <el-radio label="click">{{ $t(`wx.conf.menu.form.menuType.click`) }}</el-radio>
            <el-radio label="miniprogram">{{ $t(`wx.conf.menu.form.menuType.miniprogram`) }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='view'" class="is-required" prop="url" label="URL">
          <el-input
            v-model="formData.url"
            placeholder="https://"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
          <el-checkbox v-model="checked1" @change="checkedChange1">{{ $t(`wx.conf.menu.form.oauth.web`) }}</el-checkbox>
          <el-checkbox v-model="checked2" @change="checkedChange2">{{ $t(`wx.conf.menu.form.oauth.app`) }}</el-checkbox>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="url" label="url">
          <el-input
            v-model="formData.url"
            :placeholder="$t(`wx.conf.menu.form.appurl`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="appid" label="appid">
          <el-input
            v-model="formData.appid"
            :placeholder="$t(`wx.conf.menu.form.appid`)"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="pagepath" label="pagepath">
          <el-input
            v-model="formData.pagepath"
            :placeholder="$t(`wx.conf.menu.form.pagepath`)"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='click'" class="is-required" :label="$t(`wx.conf.menu.form.menuKey.bind`)" prop="menuKey">
          <el-select v-model="formData.menuKey" :placeholder="$t(`wx.conf.menu.form.menuKey`)">
            <el-option
              v-for="item in keyList"
              :key="item.id"
              :label="item.value"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doCreate"
        >{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t(`cms.content.channel.update`)"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="updateForm" :model="formData" :rules="formRules" size="small" label-width="100px">
        <el-form-item prop="wxid" :label="$t(`wx.conf.menu.form.wxid`)">
          <span>{{ account.wxname }}</span>
        </el-form-item>
        <el-form-item prop="menuName" :label="$t(`wx.conf.menu.form.menuName`)">
          <el-input
            v-model="formData.menuName"
            maxlength="100"
            :placeholder="$t(`wx.conf.menu.form.menuName`)"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            :title="$t(`wx.conf.menu.form.menuName.tip1`)"
            type="warning"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            :title="$t(`wx.conf.menu.form.menuName.tip2`)"
            type="warning"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="menuType" :label="$t(`wx.conf.menu.form.menuType`)">
          <el-radio-group v-model="formData.menuType" size="medium">
            <el-radio label="">{{ $t(`wx.conf.menu.form.menuType.menu`) }}</el-radio>
            <el-radio label="view">{{ $t(`wx.conf.menu.form.menuType.view`) }}</el-radio>
            <el-radio label="click">{{ $t(`wx.conf.menu.form.menuType.click`) }}</el-radio>
            <el-radio label="miniprogram">{{ $t(`wx.conf.menu.form.menuType.miniprogram`) }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='view'" class="is-required" prop="url" label="URL">
          <el-input
            v-model="formData.url"
            placeholder="https://"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
          <el-checkbox v-model="checked1" @change="checkedChange1">{{ $t(`wx.conf.menu.form.oauth.web`) }}</el-checkbox>
          <el-checkbox v-model="checked2" @change="checkedChange2">{{ $t(`wx.conf.menu.form.oauth.app`) }}</el-checkbox>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="url" label="url">
          <el-input
            v-model="formData.url"
            :placeholder="$t(`wx.conf.menu.form.appurl`)"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="appid" label="appid">
          <el-input
            v-model="formData.appid"
            :placeholder="$t(`wx.conf.menu.form.appid`)"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="pagepath" label="pagepath">
          <el-input
            v-model="formData.pagepath"
            :placeholder="$t(`wx.conf.menu.form.pagepath`)"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='click'" class="is-required" :label="$t(`wx.conf.menu.form.menuKey.bind`)" prop="menuKey">
          <el-select v-model="formData.menuKey" :placeholder="$t(`wx.conf.menu.form.menuKey`)">
            <el-option
              v-for="item in keyList"
              :key="item.id"
              :label="item.value"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="updateDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doUpdate"
        >{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="$t(`cms.content.channel.sort`)"
      :visible.sync="sortDialogVisible"
      width="50%"
    >
      <el-tree
        ref="sortMenuTree"
        :data="sortMenuData"
        draggable
        :allow-drop="sortAllowDrop"
        node-key="id"
        :props="treeProps"
      >
        <span slot-scope="scope" class="custom-tree-node">
          <span>{{ scope.node.label }}</span>
        </span>
      </el-tree>
      <span slot="footer" class="dialog-footer">
        <el-button @click="sortDialogVisible = false">{{
          $t(`system.commons.button.cancel`)
        }}</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doSort"
        >{{ $t(`system.commons.button.ok`) }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_WX_MENU_CHILD,
  API_WX_MENU_TREE,
  API_WX_MENU_CREATE,
  API_WX_MENU_DELETE,
  API_WX_MENU_GET,
  API_WX_MENU_UPDATE,
  API_WX_MENU_SORT_TREE,
  API_WX_MENU_SORT,
  API_WX_MENU_LIST_KEYWORD,
  API_WX_MENU_PUSH
} from '@/constant/api/platform/wx/menu'
import {
  API_WX_CONFIG_LIST_ACCOUNT
} from '@/constant/api/platform/wx/config'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    const _self = this
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      createDialogVisible: false,
      updateDialogVisible: false,
      sortDialogVisible: false,
      tableData: [],
      tableKey: '',
      isAddFromSub: false,
      options: [],
      menuOptions: [],
      sortMenuData: [],
      channelType: [],
      accounts: [],
      account: {
        wxid: '',
        wxname: ''
      },
      checked1: false,
      checked2: false,
      keyList: [],
      formData: {
        id: '',
        parentId: ''
      },
      props: {
        lazy: true,
        checkStrictly: true,
        multiple: false,
        emitPath: false,
        // expandTrigger: 'hover'
        // 级联选择器bug::level of null,坐等升级 2.13.0 版本
        lazyLoad(node, resolve) {
          _self.$axios
            .$get(API_WX_MENU_TREE + _self.account.wxid, {
              params: { pid: node.value }
            })
            .then((d) => {
              if (d.code === 0) {
                resolve(d.data)
              }
            })
        }
      },
      treeProps: {
        children: 'children',
        label: 'label'
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
        menuName: [
          {
            required: true,
            message: this.$t(`wx.conf.menu.form.menuName`),
            trigger: 'blur'
          }
        ],
        url: [
          {
            required: false,
            message: this.$t(`wx.conf.menu.form.url`),
            trigger: 'blur'
          },
          { validator: this.validateUrl, trigger: ['blur', 'change'] }
        ],
        menuKey: [
          { required: false, message: this.$t(`wx.conf.menu.form.menuKey`), trigger: 'blur' },
          { validator: this.validateK, trigger: ['blur', 'change'] }
        ],
        appid: [
          { required: false, message: this.$t(`wx.conf.menu.form.appid`), trigger: 'blur' },
          { validator: this.validateA, trigger: ['blur', 'change'] }
        ],
        pagepath: [
          { required: false, message: this.$t(`wx.conf.menu.form.pagepath`), trigger: 'blur' },
          { validator: this.validateP, trigger: ['blur', 'change'] }
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
    // 验证URL
    validateUrl(rule, value, callback) {
      if ((this.formData.menuType === 'view' || this.formData.menuType === 'miniprogram') && this.formData.url === '') {
        callback(new Error(this.$t(`wx.conf.menu.error.url`)))
      } else {
        callback()
      }
    },
    // 验证关键词
    validateK(rule, value, callback) {
      if (this.formData.menuType === 'click' && (typeof (this.formData.menuKey) === 'undefined' || this.formData.menuKey === '')) {
        callback(new Error(this.$t(`wx.conf.menu.error.keyword`)))
      } else {
        callback()
      }
    },
    // 验证小程序APPID
    validateA(rule, value, callback) {
      if (this.formData.menuType === 'miniprogram' && (typeof (this.formData.appid) === 'undefined' || this.formData.appid === '')) {
        callback(new Error(this.$t(`wx.conf.menu.error.appid`)))
      } else {
        callback()
      }
    },
    // 验证小程序pagepath
    validateP(rule, value, callback) {
      if (this.formData.menuType === 'miniprogram' && (typeof (this.formData.pagepath) === 'undefined' || this.formData.pagepath === '')) {
        callback(new Error(this.$t(`wx.conf.menu.error.pagepath`)))
      } else {
        callback()
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
            this.loadData()
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
      this.loadData()
    },
    // 初始化加载第一级数据
    loadData() {
      this.listLoading = true
      this.$axios.$get(API_WX_MENU_CHILD + this.account.wxid).then((d) => {
        this.listLoading = false
        if (d.code === 0) {
          this.tableData = d.data
          this.tableKey = new Date().getTime()
        }
      })
    },
    // 动态加载子级
    loadChild(tree, treeNode, resolve) {
      this.$axios
        .$get(API_WX_MENU_CHILD + this.account.wxid, { params: { pid: tree.id }})
        .then((d) => {
          if (d.code === 0) {
            resolve(d.data)
          }
        })
    },
    queryKeyword() {
      this.$axios
        .$post(API_WX_MENU_LIST_KEYWORD, {
          wxid: this.account.wxid
        })
        .then((d) => {
          if (d.code === 0) {
            var res = []
            d.data.forEach((o) => {
              res.push({ value: o.keyword, id: o.keyword })
            })
            this.keyList = res
          } else {
            this.keyList = []
          }
        })
    },
    checkedChange1(val) {
      if (!this.formData.url) {
        return
      }
      if (this.checked1) {
        var str = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + this.account.appid + '&redirect_uri=$s&response_type=code&scope=snsapi_base&state=11624317#wechat_redirect'
        this.formData.url = str.replace('$s', encodeURIComponent(this.formData.url))
      } else {
        var url = this.formData.url
        var str2 = url.substring(url.indexOf('redirect_uri=') + 13, url.indexOf('&response_type='))
        this.formData.url = decodeURIComponent(str2)
      }
    },
    checkedChange2(val) {
      if (!this.formData.url) {
        return
      }
      if (this.checked2) {
        var str = this.conf.AppDomain + '/public/wx/wechat/' + this.account.wxid + '/oauth?goto_url=$s'
        this.formData.url = str.replace('$s', this.formData.url)
      } else {
        var url = this.formData.url
        this.formData.url = url.substring(url.indexOf('goto_url=') + 9)
      }
    },
    // 打开新增窗口
    openCreate() {
      this.formData = {
        wxid: this.account.wxid,
        wxname: this.account.wxname,
        menuType: '',
        url: ''
      }
      this.queryKeyword()
      this.options = [] // 清空级联选择器的数据,使其数据动态更新
      if (this.$refs['createForm']) this.$refs['createForm'].resetFields()
      this.createDialogVisible = true
      this.isAddFromSub = false
    },
    // 提交新增数据
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_MENU_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.loadData()
                this.createDialogVisible = false
              }
            })
        }
      })
    },
    // 新增子菜单
    addSubRow(row) {
      this.formData = {
        wxid: this.account.wxid,
        wxname: this.account.wxname,
        parentName: row.menuName,
        parentId: row.id,
        menuType: '',
        url: ''
      }
      this.queryKeyword()
      this.isAddFromSub = true
      if (this.$refs['createForm']) this.$refs['createForm'].resetFields()
      this.createDialogVisible = true
    },
    // 打开修改窗口
    updateRow(row) {
      this.$axios.$get(API_WX_MENU_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.updateDialogVisible = true
        }
      })
    },
    // 提交修改数据
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_MENU_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.loadData()
                this.updateDialogVisible = false
              }
            })
        }
      })
    },
    // 执行删除操作
    deleteRow(row) {
      this.$confirm(
        this.$t(`wx.conf.menu.delete.tip`),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_WX_MENU_DELETE + row.id).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
            this.loadData()
          }
        })
      }).catch(() => {})
    },
    // 打开排序窗口
    openSort() {
      this.$axios.$get(API_WX_MENU_SORT_TREE + this.account.wxid).then((d) => {
        if (d.code === 0) {
          this.sortMenuData = d.data
        }
      })
      this.sortDialogVisible = true
    },
    // 排序树控制不可跨级拖拽
    sortAllowDrop(moveNode, inNode, type) {
      if (moveNode.data.parentId === inNode.data.parentId) {
        return type === 'prev'
      }
    },
    // 组装树形数据
    getTreeIds(ids, data) {
      const _self = this
      data.forEach(function(obj) {
        ids.push(obj.id)
        if (obj.children && obj.children.length > 0) {
          _self.getTreeIds(ids, obj.children)
        }
      })
    },
    // 提交排序数据
    doSort() {
      this.btnLoading = true
      var ids = []
      this.getTreeIds(ids, this.sortMenuData)
      this.$axios
        .$post(API_WX_MENU_SORT + this.account.wxid, { ids: ids.toString() })
        .then((d) => {
          this.btnLoading = false
          if (d.code === 0) {
            this.sortDialogVisible = false
            this.loadData()
          }
        })
    },
    // 推送菜单到微信平台
    pushMenu() {
      this.$confirm(
        this.$t(`wx.conf.menu.push.tip`),
        this.$t(`system.commons.txt.notice`),
        {
          confirmButtonText: this.$t(`system.commons.button.ok`),
          cancelButtonText: this.$t(`system.commons.button.cancel`),
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_WX_MENU_PUSH + this.account.wxid).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
