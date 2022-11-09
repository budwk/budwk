<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>菜单管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.conf.menu.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新建菜单
        </el-button>

        <el-button
          v-permission="'wx.conf.menu.push'"
          size="small"
          icon="fa fa-cloud-upload"
          @click="pushMenu"
        >
          推至微信
        </el-button>

        <el-button
          v-permission="'wx.conf.menu.update'"
          size="small"
          @click="openSort"
        >
          菜单排序
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
              label="菜单名称"
              header-align="center"
              prop="menuName"
              width="200"
              :show-overflow-tooltip="true"
              align="left"
            />

            <el-table-column
              label="菜单类型"
              header-align="center"
              align="center"
              prop="menuType"
              :show-overflow-tooltip="true"
              width="120"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.menuType==''">菜单</span>
                <span v-if="scope.row.menuType=='view'">链接</span>
                <span v-if="scope.row.menuType=='click'">事件</span>
                <span v-if="scope.row.menuType=='miniprogram'">小程序</span>
              </template>
            </el-table-column>

            <el-table-column
              label="配置内容"
              header-align="left"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.menuType==''">-</span>
                <span v-if="scope.row.menuType=='view'">{{ scope.row.url }}</span>
                <span v-if="scope.row.menuType=='click'">关键词: {{ scope.row.menuKey }}</span>
                <span v-if="scope.row.menuType=='miniprogram'">小程序: {{ scope.row.appid }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
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
                  添加子级
                </el-button>
                <el-button
                  v-permission="'wx.conf.menu.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="updateRow(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'wx.conf.menu.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="deleteRow(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <el-dialog
      title="新建菜单"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="createForm" :model="formData" :rules="formRules" size="small" label-width="100px">
        <el-form-item prop="wxid" label="所属公众号">
          <span>{{ formData.wxname }}</span>
        </el-form-item>
        <el-form-item prop="parentId" label="父级菜单" label-width="100px">
          <el-cascader
            v-if="!isAddFromSub"
            v-model="formData.parentId"
            style="width: 100%"
            :options="menuOptions"
            :props="props"
            tabindex="1"
            placeholder="父级菜单"
          />
          <el-input v-if="isAddFromSub" v-model="formData.parentName" type="text" :disabled="true" />
        </el-form-item>
        <el-form-item prop="menuName" label="菜单名称">
          <el-input
            v-model="formData.menuName"
            maxlength="100"
            placeholder="菜单名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            title="一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替"
            type="warning"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            title="只可设置3个一级菜单，只可设置5个二级菜单"
            type="warning"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="menuType" label="菜单类型">
          <el-radio-group v-model="formData.menuType" size="medium">
            <el-radio label="">菜单</el-radio>
            <el-radio label="view">链接</el-radio>
            <el-radio label="click">事件</el-radio>
            <el-radio label="miniprogram">小程序</el-radio>
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
          <el-checkbox v-model="checked1" @change="checkedChange1">网页Oauth2.0</el-checkbox>
          <el-checkbox v-model="checked2" @change="checkedChange2">应用Oauth2.0</el-checkbox>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="url" label="url">
          <el-input
            v-model="formData.url"
            placeholder="小程序URL"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="appid" label="appid">
          <el-input
            v-model="formData.appid"
            placeholder="appid"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="pagepath" label="pagepath">
          <el-input
            v-model="formData.pagepath"
            placeholder="小程序入口页"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='click'" class="is-required" label="绑定事件" prop="menuKey">
          <el-select v-model="formData.menuKey" placeholder="关键词">
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
        <el-button @click="createDialogVisible = false">取 消</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doCreate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改菜单"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="updateForm" :model="formData" :rules="formRules" size="small" label-width="100px">
        <el-form-item prop="wxid" label="所属公众号">
          <span>{{ account.wxname }}</span>
        </el-form-item>
        <el-form-item prop="menuName" label="菜单名称">
          <el-input
            v-model="formData.menuName"
            maxlength="100"
            placeholder="菜单名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            title="一级菜单最多4个汉字，二级菜单最多7个汉字，多出来的部分将会以“...”代替"
            type="warning"
          />
          <el-alert
            style="height: 30px;margin-top: 3px;"
            title="只可设置3个一级菜单，只可设置5个二级菜单"
            type="warning"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="menuType" label="菜单类型">
          <el-radio-group v-model="formData.menuType" size="medium">
            <el-radio label="">菜单</el-radio>
            <el-radio label="view">链接</el-radio>
            <el-radio label="click">事件</el-radio>
            <el-radio label="miniprogram">小程序</el-radio>
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
          <el-checkbox v-model="checked1" @change="checkedChange1">网页Oauth2.0</el-checkbox>
          <el-checkbox v-model="checked2" @change="checkedChange2">应用Oauth2.0</el-checkbox>
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="url" label="url">
          <el-input
            v-model="formData.url"
            placeholder="小程序URL"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="appid" label="appid">
          <el-input
            v-model="formData.appid"
            placeholder="appid"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='miniprogram'" class="is-required" prop="pagepath" label="pagepath">
          <el-input
            v-model="formData.pagepath"
            placeholder="小程序入口页"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item v-if="formData.menuType=='click'" class="is-required" label="绑定事件" prop="menuKey">
          <el-select v-model="formData.menuKey" placeholder="关键词">
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
        <el-button @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="菜单排序"
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
        <el-button @click="sortDialogVisible = false">取 消</el-button>
        <el-button
          :loading="btnLoading"
          type="primary"
          @click="doSort"
        >确 定</el-button>
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
} from '@/constant/api/wechat/menu'
import {
  API_WX_CONFIG_LIST_ACCOUNT
} from '@/constant/api/wechat/config'
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
            message: '菜单名称',
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
          { required: false, message: '关键词', trigger: 'blur' },
          { validator: this.validateK, trigger: ['blur', 'change'] }
        ],
        appid: [
          { required: false, message: 'appid', trigger: 'blur' },
          { validator: this.validateA, trigger: ['blur', 'change'] }
        ],
        pagepath: [
          { required: false, message: 'pagepath', trigger: 'blur' },
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
        callback(new Error('请输入正确的URL路径'))
      } else {
        callback()
      }
    },
    // 验证关键词
    validateK(rule, value, callback) {
      if (this.formData.menuType === 'click' && (typeof (this.formData.menuKey) === 'undefined' || this.formData.menuKey === '')) {
        callback(new Error('请选择关键词'))
      } else {
        callback()
      }
    },
    // 验证小程序APPID
    validateA(rule, value, callback) {
      if (this.formData.menuType === 'miniprogram' && (typeof (this.formData.appid) === 'undefined' || this.formData.appid === '')) {
        callback(new Error('请输入appid'))
      } else {
        callback()
      }
    },
    // 验证小程序pagepath
    validateP(rule, value, callback) {
      if (this.formData.menuType === 'miniprogram' && (typeof (this.formData.pagepath) === 'undefined' || this.formData.pagepath === '')) {
        callback(new Error('请输入pagepath'))
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
        var str = this.conf.AppDomain + '/wechat/open/auth/' + this.account.wxid + '/oauth?goto_url=$s'
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
        '确定删除？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
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
        '确定推送至微信平台？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
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
