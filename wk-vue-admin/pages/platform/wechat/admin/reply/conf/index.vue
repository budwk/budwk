<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>事件配置</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'wx.reply.conf.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新增事件
        </el-button>
        <el-button
          v-permission="'wx.reply.conf.delete'"
          size="small"
          type="danger"
          :disabled="tableSelectData.length === 0"
          @click="deleteMore"
        >
          批量删除
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
          <el-form-item label="事件类型">
            <el-radio-group v-model="pageData.type" size="small" @change="listPage">
              <el-radio-button v-for="type in types" :key="type.val" :label="type.val">{{ $t(type.txt) }}</el-radio-button>
            </el-radio-group>
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
              label="事件类型"
              header-align="left"
              prop="type"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="'follow'===scope.row.type">关注事件</span>
                <span v-if="'keyword'===scope.row.type">关键词事件</span>
              </template>
            </el-table-column>

            <el-table-column
              v-if="pageData.type==='keyword'"
              label="关键词"
              header-align="left"
              prop="keyword"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              label="消息类型"
              header-align="left"
              prop="msgType"
              :show-overflow-tooltip="true"
            />

            <el-table-column
              label="回复内容"
              header-align="left"
              prop="content"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.msgType=='txt'">
                  {{ scope.row.replyTxt.title }}
                </span>
                <span v-if="scope.row.msgType=='image'">
                  <img :src="conf.AppFileDomain+scope.row.replyImg.picurl" width="35" height="35">
                </span>
                <span v-if="scope.row.msgType=='news'">
                  {{ scope.row.replyNews.title }}
                </span>
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
                  v-permission="'wx.reply.conf.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'wx.reply.conf.delete'"
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
      title="新增事件"
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
        <el-form-item prop="type" label="事件类型">
          <span v-if="'follow'===formData.type">关注事件</span>
          <span v-if="'keyword'===formData.type">关键词事件</span>
        </el-form-item>

        <el-form-item v-if="formData.type=='keyword'" class="is-required" prop="keyword" label="关键词">
          <el-input
            v-model="formData.keyword"
            maxlength="100"
            placeholder="关键词"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="scope" label="消息类型">
          <el-radio-group v-model="formData.msgType" size="medium" @change="msgTypeChange">
            <el-radio label="txt">文本</el-radio>
            <el-radio label="news">图文</el-radio>
            <el-radio label="image">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='txt'" class="is-required" label="绑定文本" prop="content">
          <el-select v-model="formData.content" placeholder="请选择文本">
            <el-option
              v-for="item in txtList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='news'" class="is-required" label="绑定图文" prop="content">
          <el-select v-model="formData.content" placeholder="请选择图文">
            <el-option
              v-for="item in newsList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='image'" class="is-required" label="绑定图片" prop="content">
          <el-col v-for="(o, index) in imageList" :key="o.id" :span="6" :offset="index > 0 ? 3 : 0">
            <el-card style="width: 120px;height: 120px;">
              <img :src="conf.AppFileDomain+o.picurl" style="height: 60px;">
              <div style="padding: 3px;">
                <span v-if="formData.content==o.id" style="font-size: 12px;color: red;">已选中</span>
                <div class="bottom clearfix">
                  <el-button type="text" class="button" @click="imgClassSel(o.id)">选中</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
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
      title="修改事件"
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
        <el-form-item prop="type" label="事件类型">
          <span v-if="'follow'===formData.type">关注事件</span>
          <span v-if="'keyword'===formData.type">关键词事件</span>
        </el-form-item>

        <el-form-item v-if="formData.type=='keyword'" class="is-required" prop="keyword" label="关键词">
          <el-input
            v-model="formData.keyword"
            maxlength="100"
            placeholder="关键词"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item class="is-required" prop="scope" label="消息类型">
          <el-radio-group v-model="formData.msgType" size="medium" @change="msgTypeChange">
            <el-radio label="txt">文本</el-radio>
            <el-radio label="news">图文</el-radio>
            <el-radio label="image">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='txt'" class="is-required" label="绑定文本" prop="content">
          <el-select v-model="formData.content" placeholder="请选择文本">
            <el-option
              v-for="item in txtList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='news'" class="is-required" label="绑定图文" prop="content">
          <el-select v-model="formData.content" placeholder="请选择图文">
            <el-option
              v-for="item in newsList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="formData.msgType=='image'" class="is-required" label="绑定图片" prop="content">
          <el-col v-for="(o, index) in imageList" :key="o.id" :span="6" :offset="index > 0 ? 3 : 0">
            <el-card style="width: 120px;height: 120px;">
              <img :src="conf.AppFileDomain+o.picurl" style="height: 60px;">
              <div style="padding: 3px;">
                <span v-if="formData.content==o.id" style="font-size: 12px;color: red;">已选中</span>
                <div class="bottom clearfix">
                  <el-button type="text" class="button" @click="imgClassSel(o.id)">选中</el-button>
                </div>
              </div>
            </el-card>
          </el-col>
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
  API_WX_REPLY_CONF_LIST,
  API_WX_REPLY_CONF_GET,
  API_WX_REPLY_CONF_CREATE,
  API_WX_REPLY_CONF_UPDATE,
  API_WX_REPLY_CONF_DELETE,
  API_WX_REPLY_CONF_DELETE_MORE,
  API_WX_REPLY_CONF_LIST_CONTENT
} from '@/constant/api/wechat/reply_conf'
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
      createDialogVisible: false,
      updateDialogVisible: false,
      listData: [],
      tableSelectData: [],
      pageData: {
        wxid: '',
        type: 'follow',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      types: [
        { val: 'follow', txt: '关注事件' },
        { val: 'keyword', txt: '关键词事件' }
      ],
      accounts: [],
      account: {
        wxid: '',
        wxname: ''
      },
      action: '',
      txtList: [],
      imageList: [],
      newsList: []
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
        keyword: [
          {
            required: false,
            message: '关键词',
            trigger: 'blur'
          },
          { validator: this.validateKeyword, trigger: ['blur', 'change'] }
        ],
        content: [
          {
            required: true,
            message: '回复内容',
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
    validateKeyword(rule, value, callback) {
      if (this.formData.type === 'keyword' && (typeof (this.formData.keyword) === 'undefined' || this.formData.keyword === '')) {
        callback(new Error('请输入关键词'))
      } else if (this.formData.type === 'keyword' && this.formData.keyword.length > 20) {
        callback(new Error('关键词最大长度为20个字符'))
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
            this.listPage()
            this.initSelectContent()
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
        wxid: this.account.wxid,
        type: this.pageData.type,
        msgType: 'txt',
        keyword: '',
        content: ''
      }
      if (this.$refs['createForm']) { this.$refs['createForm'].resetFields() }
      this.createDialogVisible = true
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_WX_REPLY_CONF_CREATE, this.formData)
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
      this.$axios.$get(API_WX_REPLY_CONF_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.action = 'update'
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
            .$post(API_WX_REPLY_CONF_UPDATE, this.formData)
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
        '确定删除？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_WX_REPLY_CONF_DELETE + row.id).then((d) => {
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
      var types = []
      this.tableSelectData.forEach((obj) => {
        types.push(obj.type)
        ids.push(obj.id)
      })
      this.$confirm(
        '确定批量删除？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_WX_REPLY_CONF_DELETE_MORE, { ids: ids.toString(), types: types.toString() }).then((d) => {
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
    msgTypeChange(val) {
      this.$set(this.formData, 'content', '')
    },
    initSelectContent() {
      this.$axios.$post(API_WX_REPLY_CONF_LIST_CONTENT, { wxid: this.account.wxid, msgType: 'txt' }).then((d) => {
        if (d.code === 0) {
          this.txtList = d.data
        }
      })
      this.$axios.$post(API_WX_REPLY_CONF_LIST_CONTENT, { wxid: this.account.wxid, msgType: 'image' }).then((d) => {
        if (d.code === 0) {
          this.imageList = d.data
        }
      })
      this.$axios.$post(API_WX_REPLY_CONF_LIST_CONTENT, { wxid: this.account.wxid, msgType: 'news' }).then((d) => {
        if (d.code === 0) {
          this.newsList = d.data
        }
      })
    },
    imgClassSel(val) {
      this.formData.content = val
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
      this.$axios.$post(API_WX_REPLY_CONF_LIST, this.pageData).then((res) => {
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

