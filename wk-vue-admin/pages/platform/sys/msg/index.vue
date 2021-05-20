<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>消息管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.manage.msg.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          发送消息
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          ref="searchForm"
          :inline="true"
          :model="pageData"
          size="small"
          class="platform-content-search-form"
        >
          <el-form-item>
            <el-radio-group
              v-model="pageData.type"
              @change="typeChange"
            >
              <el-radio-button label="">全部消息</el-radio-button>
              <el-radio-button
                v-for="type in types"
                :key="type.value"
                :label="type.value"
              >{{ type.text }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="消息标题">
            <el-input
              v-model="pageData.title"
              placeholder="消息标题"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
          </el-form-item>
          <div class="platform-content-search-op">
            <el-button
              size="small"
              @click="doReSearch"
            >
              重 置
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="doSearch"
            >
              查 询
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
            <el-table-column type="expand">
              <template slot-scope="scope">
                <el-form
                  label-position="left"
                  inline
                  class="demo-table-expand"
                  size="mini"
                >
                  <el-form-item
                    label="跳转路径"
                  >
                    <span>{{ scope.row.url }}</span>
                  </el-form-item>
                  <el-form-item
                    label="消息内容"
                    :show-overflow-tooltip="true"
                  >
                    <span v-html="scope.row.note" />
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column
              prop="type"
              label="消息类型"
              sortable
            >
              <template slot-scope="scope">
                {{ $t(scope.row.type.text) }}
              </template>
            </el-table-column>
            <el-table-column
              prop="title"
              label="消息标题"
              header-align="center"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              prop="all_num"
              label="全部用户数"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="text"
                  @click="viewUserList('all', scope.row.id)"
                >{{ scope.row.all_num }}</el-button>
              </template>
            </el-table-column>
            <el-table-column
              prop="unread_num"
              label="未读用户数"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="text"
                  @click="
                    viewUserList('unread', scope.row.id)
                  "
                >{{ scope.row.unread_num }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column
              sortable
              prop="sendAt"
              label="发送时间"
              header-align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.sendAt | moment("datetime") }}
              </template>
            </el-table-column>
            <el-table-column
              sortable
              prop="delFlag"
              label="消息状态"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <el-tag
                  v-if="scope.row.delFlag"
                  type="danger"
                >已撤销</el-tag>
                <el-tag
                  v-if="!scope.row.delFlag"
                  type="success"
                >已发送</el-tag>
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
                  v-permission="'sys.manage.msg.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  :disabled="scope.row.delFlag"
                  @click.native.prevent="deleteRow(scope.row)"
                >
                  撤销
                </el-button>
              </template>
            </el-table-column>
          </el-table>
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

    <!-- 发送消息 -->
    <el-dialog
      title="发送消息"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="65%"
    >
      <el-form
        ref="addForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="80px"
      >
        <el-form-item prop="title" label="消息标题">
          <el-input
            v-model="formData.title"
            maxlength="255"
            placeholder="消息标题"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="url" label="跳转路径">
          <el-input
            v-model="formData.url"
            placeholder="跳转路径"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="type" label="消息类型">
          <el-radio-group
            v-model="formData.type"
          >
            <el-radio
              v-for="type in types"
              :key="type.value"
              :label="type.value"
            >{{ type.text }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="scope" label="发送范围">
          <el-radio-group
            v-model="formData.scope"
          >
            <el-radio-button
              v-for="type in scopes"
              :key="type.value"
              :label="type.value"
            >{{ type.text }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="'SCOPE' == formData.scope" label="指定用户">
          <el-row>
            <el-button
              icon="el-icon-search"
              @click="openSelect"
            >选择用户</el-button>
            <el-button
              icon="el-icon-delete"
              type="danger"
              @click="clearSelect"
            >清除选择</el-button>
          </el-row>
          <el-table
            :data="userTableData"
            style="width: 100%;margin-top:5px;"
            size="small"
            height="200"
          >
            <el-table-column
              fixed
              prop="loginname"
              label="用户名"
            >
              <template slot-scope="scope">
                {{ scope.row.loginname }} ({{ scope.row.username }})
              </template>
            </el-table-column>
            <el-table-column
              prop="email"
              label="EMail"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              prop="mobile"
              label="手机号"
            />
            <el-table-column prop="unit" label="所属单位">
              <template slot-scope="scope">
                <span v-if="scope.row.unit != null">
                  {{ scope.row.unit.name }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item prop="note" label="消息内容">
          <div>
            <QuillEditor
              ref="myQuillEditor"
              style="height:300px;margin-bottom:40px;"
              :text="formData.note"
              :uploadurl="uploadurl"
              :filedomain="filedomain"
            />
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doAdd"
        >发 送</el-button>
      </span>
    </el-dialog>

    <!-- 选择发送用户 -->
    <el-dialog
      title="选择用户"
      :visible.sync="selectDialogVisible"
      :close-on-click-modal="false"
      width="65%"
    >
      <div class="btn-group tool-button mt5">
        <el-input v-model="selectPageForm.searchKeyword" placeholder="查询条件" @keyup.enter.native="doSelectSearch">
          <el-select slot="prepend" v-model="selectPageForm.searchName" size="mini" placeholder="查询字段" style="width: 120px;">
            <el-option label="用户名" value="loginname" />
            <el-option label="姓名" value="username" />
            <el-option label="手机号" value="mobile" />
            <el-option label="EMail" value="email" />
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="doSelectSearch" />

        </el-input>
      </div>
      <el-table
        :data="selectTableData"
        size="mini"
        style="width: 100%;margin-top: 10px;margin-bottom: 10px;"
        header-align="center"
        :default-sort="{prop: 'createdAt', order: 'descending'}"
        @sort-change="selectPageOrder"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="50"
        />
        <el-table-column
          fixed
          prop="loginname"
          label="用户名"
        >
          <template slot-scope="scope">
            {{ scope.row.loginname }} ({{ scope.row.username }})
          </template>
        </el-table-column>
        <el-table-column
          prop="mobile"
          label="手机号"
        />
        <el-table-column
          prop="email"
          label="EMail"
          :show-overflow-tooltip="true"
        />
        <el-table-column prop="unit" label="所属单位">
          <template slot-scope="scope">
            <span v-if="scope.row.unit != null">
              {{ scope.row.unit.name }}
            </span>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :total="selectPageForm.totalCount"
        layout="total, sizes, prev, pager, next, jumper"
        :page-size="selectPageForm.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :current-page="selectPageForm.pageNo"
        @size-change="selectPageSizeChange"
        @current-change="selectPageNumberChange"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="selectDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doSelect"
        >选 择</el-button>
      </span>
    </el-dialog>

    <el-drawer
      :title="viewUserTitle"
      :visible.sync="viewDialogVisible"
      :size="'50%'"
    >
      <el-table
        v-loading="viewListLoading"
        :data="viewTableData"
        header-align="center"
        style="width: 100%;margin-top: 10px;margin-bottom: 10px;"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
        size="small"
        @sort-change="viewPageOrder"
      >
        <el-table-column
          fixed
          prop="loginname"
          label="用户名"
          width="150"
        />
        <el-table-column
          prop="username"
          label="姓名"
          width="120"
        />
        <el-table-column
          prop="mobile"
          label="手机号"
        />
        <el-table-column
          prop="email"
          label="EMail"
        />
        <el-table-column
          prop="unitname"
          label="所属单位"
        />
      </el-table>
      <el-pagination
        :current-page="viewPageForm.pageNo"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="viewPageForm.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="viewPageForm.totalCount"
        @size-change="viewPageSizeChange"
        @current-change="viewPageNumberChange"
      />
    </el-drawer>
  </div>
</template>

<script>
import QuillEditor from '@/components/QuillEditor'
import { API_UPLOAD_IMAGE } from '@/constant/api/platform/pub/upload'
import { mapState } from 'vuex'
import {
  API_SYS_MSG_LIST,
  API_SYS_MSG_DELETE,
  API_SYS_MSG_DATA,
  API_SYS_MSG_GET_USER_VIEW_LIST,
  API_SYS_MSG_SELECT_USER_LIST,
  API_SYS_MSG_CREATE
} from '@/constant/api/platform/sys/msg'
export default {
  middleware: ['authenticated', 'check_permissions'],
  components: {
    QuillEditor
  },
  data() {
    return {
      loading: true,
      btnLoading: false,
      addDialogVisible: false,
      listLoading: false,
      viewListLoading: false,
      selectDialogVisible: false,
      listData: [],
      apps: [],
      types: [],
      scopes: [],
      pageData: {
        title: '',
        type: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      viewDialogVisible: false,
      viewTableData: [],
      viewUserTitle: '',
      viewPageForm: {
        id: '',
        type: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'loginname',
        pageOrderBy: 'descending'
      },
      selectPageForm: {
        searchKeyword: '',
        searchName: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'loginname',
        pageOrderBy: 'descending'
      },
      formData: { type: 'USER', scope: 'SCOPE' },
      userTableData: [],
      selectUsers: [],
      selectTableData: [],
      filedomain: '',
      uploadurl: ''
    }
  },
  computed: {
    ...mapState({
      conf: (state) => state.conf
    }),
    formRules() {
      const formRules = {
        title: [
          {
            required: true,
            message: '消息标题',
            trigger: ['blur', 'change']
          }
        ],
        type: [
          {
            required: true,
            message: '消息类型',
            trigger: ['blur', 'change']
          }
        ],
        scope: [
          {
            required: true,
            message: '发送范围',
            trigger: ['blur', 'change']
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.initData()
      this.listPage()
      this.filedomain = this.conf.AppFileDomain
      this.uploadurl = process.env.API + API_UPLOAD_IMAGE
    }
  },
  methods: {
    initData() {
      this.$axios.$get(API_SYS_MSG_DATA).then((res) => {
        if (res.code === 0) {
          this.apps = res.data.apps
          this.types = res.data.types
          this.scopes = res.data.scopes
        }
      })
    },
    // 查询条件消息类型变化事件
    typeChange() {
      this.pageData.pageNo = 1
      this.listPage()
    },
    // 打开选择用户
    openSelect() {
      this.selectDialogVisible = true
      this.selectPageLoad()
    },
    // 清空已选用户
    clearSelect() {
      this.userTableData = []
    },
    // 执行选择用户
    doSelect() {
      this.selectDialogVisible = false
      this.selectUsers.forEach((o) => {
        this.userTableData.push(o)
      })
    },
    // 选择用户时事件
    handleSelectionChange(val) {
      this.selectUsers = val
    },
    // 页排序事件
    selectPageOrder(column) {
      this.selectPageForm.pageOrderName = column.prop
      this.selectPageForm.pageOrderBy = column.order
      this.selectPageLoad()
    },
    // 页码变动事件
    selectPageNumberChange(val) {
      this.selectPageForm.pageNo = val
      this.selectPageLoad()
    },
    // 页大小变动事件
    selectPageSizeChange(val) {
      this.selectPageForm.pageSize = val
      this.selectPageLoad()
    },
    // 搜索符合条件的用户
    doSelectSearch() {
      this.selectPageForm.pageNo = 1
      this.selectPageLoad()
    },
    // 查询用户
    selectPageLoad() {
      this.$axios.$post(API_SYS_MSG_SELECT_USER_LIST, this.selectPageForm).then((res) => {
        if (res.code === 0) {
          this.selectTableData = res.data.list
          this.selectPageForm.totalCount = res.data.totalCount
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
      this.listLoading = true
      this.$axios.$post(API_SYS_MSG_LIST, this.pageData).then((res) => {
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
    doReSearch() {
      this.pageData = {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      }
      this.$refs['searchForm'].resetFields()
    },
    // 页排序事件
    viewPageOrder(column) {
      this.viewPageForm.pageOrderName = column.prop
      this.viewPageForm.pageOrderBy = column.order
      this.viewPageLoad()
    },
    // 页码变动事件
    viewPageNumberChange(val) {
      this.viewPageForm.pageNo = val
      this.viewPageLoad()
    },
    // 页大小变动事件
    viewPageSizeChange(val) {
      this.viewPageForm.pageSize = val
      this.viewPageLoad()
    },
    // 用户列表
    viewPageLoad() {
      this.viewListLoading = true
      this.$axios
        .$post(API_SYS_MSG_GET_USER_VIEW_LIST, this.viewPageForm)
        .then((res) => {
          this.viewListLoading = false
          if (res.code === 0) {
            this.viewTableData = res.data.list
            this.viewPageForm.totalCount = res.data.totalCount
          }
        })
    },
    // 打开用户侧边栏
    viewUserList(type, id) {
      if (type === 'all') {
        this.viewUserTitle = '全部用户'
      }
      if (type === 'unread') {
        this.viewUserTitle = '未读用户'
      }
      this.viewDialogVisible = true
      this.viewPageForm.type = type
      this.viewPageForm.id = id
      this.viewPageLoad()
    },
    // 发送消息
    openAdd() {
      if (this.$refs['addForm']) { // 表单初始化,消除上次操作残留
        this.$refs['addForm'].resetFields()
      }
      this.clearSelect()
      if (this.$refs.myQuillEditor) {
        this.$refs.myQuillEditor.content = ''
      }
      this.addDialogVisible = true
    },
    // 执行发送消息
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          var users = []
          if (this.selectUsers) {
            this.selectUsers.forEach((o) => {
              users.push(o.id)
            })
          }
          if (this.formData.scope === 'SCOPE' && users.length === 0) {
            this.$message({
              message: '请选择用户',
              type: 'error'
            })
            return false
          }
          this.$set(this.formData, 'note', this.$refs.myQuillEditor.content)
          this.$set(this.formData, 'users', users.toString())
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_MSG_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.listPage()
                this.addDialogVisible = false
              }
            })
        }
      })
    },
    // 执行删除字符串
    deleteRow(row) {
      this.$confirm(
        '确定撤销 ' + row.title + ' ？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_SYS_MSG_DELETE + row.id).then((res) => {
          if (res.code === 0) {
            this.$message({
              message: res.msg,
              type: 'success'
            })
            this.listPage()
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style>
/* 表格扩展 */
.demo-table-expand {
    font-size: 0px;
    padding-left: 115px;
}
.demo-table-expand label {
    width: 120px;
    color: #99a9bf;
}
.demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
}
.demo-table-expand .el-form-item .el-form-item__label {
    font-size: 12px;
}
.demo-table-expand .el-form-item .el-form-item__content {
    font-size: 12px;
}
.el-drawer__body {
    overflow-y: auto;
}
.el-drawer__wrapper div,
span,
button,
i {
    outline: none;
}
</style>
