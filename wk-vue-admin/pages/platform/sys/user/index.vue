<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>用户管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.manage.user.create'"
          size="small"
          type="primary"
          :disabled="unitData.length===0"
          @click="openAdd"
        >
          新建用户
        </el-button>
        <el-button
          v-permission="'sys.manage.user.delete'"
          size="small"
          type="danger"
          :loading="btnLoading"
          @click="deleteMore"
        >
          删除用户
        </el-button>
        <el-button
          v-permission="'sys.manage.user.export'"
          size="small"
          @click="exportUser"
        >
          导出数据
        </el-button>
      </div>
    </h4>
    <el-row :gutter="10">
      <el-col :span="5" class="unit-left">
        <el-row v-permission="'sys.manage.role.create'" style="text-align:right;padding-right:2px;">
          <span style="font-size:14px;float:left;">单位列表</span>
        </el-row>
        <el-tree
          ref="unitTree"
          node-key="id"
          :data="unitData"
          :props="treeProps"
          default-expand-all
          :render-content="renderContent"
          :expand-on-click-node="false"
          :check-on-click-node="true"
          style="padding-top:10px;"
          @current-change="unitChange"
        />
      </el-col>
      <el-col :span="19" style="padding-left:10px;">
        <div style="font-size:14px;">用户列表 <span style="font-size:12px;color:#2476e0;">{{ numberTitle }}</span></div>
        <div class="platform-content-info">
          <el-row>
            <el-form
              ref="searchForm"
              :inline="true"
              :model="pageData"
              class="platform-content-search-form-more"
            >
              <el-row>
                <el-col :span="9">
                  <el-form-item label="职务">
                    <el-select
                      v-model="pageData.postId"
                      placeholder="全部"
                      size="medium"
                      style="width:100%;"
                      :clearable="true"
                    >
                      <template v-for="item in posts">
                        <el-option
                          :key="item.id"
                          :label="item.name"
                          :value="item.id"
                        />
                      </template>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="9">
                  <el-form-item label="用户">
                    <el-input
                      v-model="pageData.query"
                      placeholder="用户名或姓名或手机号"
                      maxlength="255"
                      auto-complete="off"
                      type="text"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="6" style="float:right;">
                  <div class="platform-content-search-op-more">
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
                </el-col>
              </el-row>
            </el-form>
          </el-row>
          <div class="platform-content-list">
            <div class="platform-content-list-table">
              <el-table
                v-loading="listLoading"
                :data="listData"
                :default-sort="{
                  prop: 'createdAt',
                  order: 'descending'
                }"
                stripe
                @sort-change="doPageSort"
                @selection-change="userSelectChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column type="expand">
                  <template slot-scope="scope">
                    <el-form
                      label-position="left"
                      inline
                      class="demo-table-expand"
                      size="mini"
                    >
                      <el-form-item label="EMail">
                        <span>{{
                          scope.row.email
                        }}</span>
                      </el-form-item>
                      <el-form-item label="最后登录时间">
                        <span>
                          {{
                            scope.row.loginAt
                              | moment("datetime")
                          }}</span>
                      </el-form-item>
                      <el-form-item label="手机号码">
                        <span>{{
                          scope.row.mobile
                        }}</span>
                      </el-form-item>
                      <el-form-item label="最后登录IP">
                        <span>{{
                          scope.row.loginIp
                        }}</span>
                      </el-form-item>
                      <el-form-item label="创建人">
                        <span v-if="scope.row.createdByUser">{{
                          scope.row.createdByUser.loginname
                        }}({{
                          scope.row.createdByUser.username
                        }})</span>
                      </el-form-item>
                      <el-form-item label="修改人">
                        <span v-if="scope.row.updatedByUser">{{
                          scope.row.updatedByUser.loginname
                        }}({{
                          scope.row.updatedByUser.username
                        }})</span>
                      </el-form-item>
                      <el-form-item label="创建时间">
                        <span>
                          {{
                            scope.row.createdAt
                              | moment("datetime")
                          }}</span>
                      </el-form-item>
                      <el-form-item label="修改时间">
                        <span>
                          {{
                            scope.row.updatedAt
                              | moment("datetime")
                          }}</span>
                      </el-form-item>
                    </el-form>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="serialNo"
                  label="编号"
                />
                <el-table-column
                  prop="loginname"
                  label="用户"
                >
                  <template slot-scope="scope">
                    {{ scope.row.loginname }}({{
                      scope.row.username
                    }})
                  </template>
                </el-table-column>
                <el-table-column
                  prop="unit"
                  label="单位"
                >
                  <template slot-scope="scope">
                    <span v-if="scope.row.unit">{{ scope.row.unit.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="postId"
                  label="职务"
                >
                  <template slot-scope="scope">
                    {{ getPost(scope.row.postId) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="roles"
                  label="角色"
                >
                  <template slot-scope="scope">
                    <div v-if="scope.row.roles">
                      <span v-for="role in scope.row.roles" :key="role.id">{{ role.name }}|</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  label="状态"
                  header-align="center"
                  prop="disabled"
                  align="center"
                >
                  <template slot-scope="scope">
                    <i
                      v-if="!scope.row.disabled"
                      class="fa fa-circle"
                      style="color:green"
                    />
                    <i
                      v-if="scope.row.disabled"
                      class="fa fa-circle"
                      style="color:red"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  fixed="right"
                  header-align="center"
                  align="center"
                  label="操作"
                  width="120"
                >
                  <template slot-scope="scope">
                    <el-button
                      v-permission="'sys.manage.user.update'"
                      type="text"
                      size="small"
                      @click.native.prevent="
                        openUpdate(scope.row)
                      "
                    >
                      修改
                    </el-button>
                    <el-dropdown trigger="click">
                      <span class="el-dropdown-link">
                        更多
                        <i class="fa fa-angle-down" />
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-permission="'sys.manage.user.update'"
                          @click.native.prevent="resetPwd(scope.row)"
                        >
                          重置密码
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'sys.manage.user.update'"
                          @click.native.prevent="disabledChange(scope.row)"
                        >
                          {{ scope.row.disabled ? "启用" : "停用" }}
                        </el-dropdown-item>
                        <el-dropdown-item
                          v-permission="'sys.manage.user.delete'"
                          :disabled="scope.row.loginname === 'superadmin'"
                          @click.native.prevent="deleteRow(scope.row)"
                        >
                          删除
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
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
      </el-col>
    </el-row>
    <el-dialog
      title="新增用户"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <el-form
        ref="addForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="unitId" label="所属单位">
          {{ unit.name }}
        </el-form-item>
        <el-form-item v-if="!modifyIt" ref="serialNo" prop="serialNo" label="员工编号">
          {{ formData.serialNo }}
          <el-button icon="el-icon-edit" type="text" @click="modifyIt = true">修改</el-button>
        </el-form-item>
        <el-form-item v-if="modifyIt" ref="modifyIt" prop="serialNo" label="员工编号">
          <el-input-number
            v-model="formData.serialNo"
            :min="1"
            placeholder="员工编号"
            auto-complete="off"
          />
        </el-form-item>
        <el-form-item
          prop="mobile"
          label="手机号"
        >
          <el-input
            v-model="formData.mobile"
            maxlength="20"
            placeholder="手机号"
            auto-complete="off"
            tabindex="4"
            type="text"
            @input="mobileInput"
          />
        </el-form-item>
        <el-form-item
          prop="loginname"
          label="用户名"
        >
          <el-input
            v-model="formData.loginname"
            maxlength="100"
            placeholder="用户名"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="username"
          label="姓名"
        >
          <el-input
            v-model="formData.username"
            maxlength="100"
            placeholder="姓名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item ref="password" prop="password" label="初始密码">
          <el-input
            v-model="formData.password"
            maxlength="20"
            placeholder="不设置初始密码，则密码默认为手机号后六位"
            auto-complete="off"
            tabindex="5"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item
          prop="email"
          label="电子邮箱"
        >
          <el-input
            v-model="formData.email"
            maxlength="255"
            placeholder="电子邮箱"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item label="职务">
          <el-select
            v-model="formData.postId"
            placeholder="职务"
            size="small"
            style="width:100%"
            :clearable="true"
          >
            <template v-for="item in posts">
              <el-option
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="roleIds"
          label="分配角色"
        >
          <el-select
            v-model="formData.roleIds"
            size="small"
            style="width:100%"
            class="span_n"
            multiple
            filterable
            default-first-option
            placeholder="分配角色"
            autocomplete="off"
          >
            <el-option-group
              v-for="group in groups"
              :key="group.id"
              :label="group.name"
            >
              <el-option
                v-for="item in group.roles"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="avatar"
          label="头像"
        >
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            name="Filedata"
            list-type="picture-card"
            limit:1
            :before-upload="beforeFileUpload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img
              v-if="formData.avatar"
              :src="conf['AppFileDomain'] + formData.avatar"
              class="avatar"
            >
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item
          prop="disabled"
          label="启用状态"
        >
          <el-switch
            v-model="formData.disabled"
            size="small"
            :active-value="false"
            :inactive-value="true"
            active-color="green"
            inactive-color="red"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改用户"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="unitId" label="所属单位">
          {{ formData.unitName }}
        </el-form-item>
        <el-form-item v-if="!modifyIt" ref="serialNo" prop="serialNo" label="员工编号">
          {{ formData.serialNo }}
          <el-button icon="el-icon-edit" type="text" @click="modifyIt = true">修改</el-button>
        </el-form-item>
        <el-form-item v-if="modifyIt" ref="modifyIt" prop="serialNo" label="员工编号">
          <el-input-number
            v-model="formData.serialNo"
            :min="1"
            placeholder="员工编号"
            auto-complete="off"
          />
        </el-form-item>
        <el-form-item
          prop="mobile"
          label="手机号"
        >
          <el-input
            v-model="formData.mobile"
            maxlength="20"
            placeholder="手机号"
            auto-complete="off"
            tabindex="4"
            type="text"
            @input="mobileInput"
          />
        </el-form-item>
        <el-form-item
          prop="loginname"
          label="用户名"
        >
          <el-input
            v-model="formData.loginname"
            maxlength="100"
            placeholder="用户名"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="username"
          label="姓名"
        >
          <el-input
            v-model="formData.username"
            maxlength="100"
            placeholder="姓名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="email"
          label="电子邮箱"
        >
          <el-input
            v-model="formData.email"
            maxlength="255"
            placeholder="电子邮箱"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item label="职务">
          <el-select
            v-model="formData.postId"
            placeholder="职务"
            size="small"
            style="width:100%"
            :clearable="true"
          >
            <template v-for="item in posts">
              <el-option
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="roleIds"
          label="分配角色"
        >
          <el-select
            v-model="formData.roleIds"
            size="small"
            style="width:100%"
            class="span_n"
            multiple
            filterable
            default-first-option
            placeholder="分配角色"
            autocomplete="off"
          >
            <el-option-group
              v-for="group in groups"
              :key="group.id"
              :label="group.name"
            >
              <el-option
                v-for="item in group.roles"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="avatar"
          label="头像"
        >
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            name="Filedata"
            list-type="picture-card"
            limit:1
            :before-upload="beforeFileUpload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img
              v-if="formData.avatar"
              :src="conf['AppFileDomain'] + formData.avatar"
              class="avatar"
            >
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item
          prop="disabled"
          label="启用状态"
        >
          <el-switch
            v-model="formData.disabled"
            size="small"
            :active-value="false"
            :inactive-value="true"
            active-color="green"
            inactive-color="red"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
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
  API_SYS_USER_UNIT,
  API_SYS_USER_LIST,
  API_SYS_USER_POST,
  API_SYS_USER_COUNT,
  API_SYS_USER_NUMBER,
  API_SYS_USER_GROUP,
  API_SYS_USER_CREATE,
  API_SYS_USER_GET,
  API_SYS_USER_UPDATE,
  API_SYS_USER_RESETPWD,
  API_SYS_USER_DISABLED,
  API_SYS_USER_DELETE,
  API_SYS_USER_DELETE_MORE,
  API_SYS_USER_EXPORT
} from '@/constant/api/platform/sys/user'
import { API_UPLOAD_IMAGE } from '@/constant/api/platform/pub/upload'
const MOBILE_REGEX = /^1[3456789]\d{9}$/
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      listLoading: false,
      btnLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      isUnitType: false,
      modifyIt: false,
      treeProps: {
        children: 'children',
        label: 'label'
      },
      unitData: [],
      unit: { id: '', name: '' },
      listData: [],
      pageData: {
        unitId: '',
        unitPath: '',
        postId: '',
        query: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      posts: [],
      groups: [],
      userSelectData: [],
      numberTitle: '',
      uploadUrl: process.env.API + API_UPLOAD_IMAGE // 图片上传路径
    }
  },
  computed: {
    ...mapState({
      conf: state => state.conf // 后台配置参数
    }),
    formRules() {
      const formRules = {
        unitId: [
          {
            required: true,
            message: '所属单位',
            trigger: 'blur'
          }
        ],
        serialNo: [
          {
            required: true,
            message: '用户编号',
            trigger: 'blur'
          }
        ],
        loginname: [
          {
            required: true,
            message: '用户名',
            trigger: 'blur'
          }
        ],
        username: [
          {
            required: true,
            message: '姓名',
            trigger: 'blur'
          }
        ],
        mobile: [
          {
            required: true,
            message: '手机号',
            trigger: 'blur'
          },
          {
            pattern: MOBILE_REGEX, message: '请輸入正确的手机号码', trigger: ['blur', 'change']
          }
        ],
        email: [
          {
            required: false,
            message: '电子邮箱',
            trigger: 'blur'
          },
          {
            type: 'email', message: '请輸入正确的电子邮箱', trigger: ['blur', 'change']
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.loadPost()
      this.loadUnit()
    }
  },
  methods: {
    openAdd() {
      this.groups = []
      this.formData = {}
      if (this.$refs['addForm']) this.$refs['addForm'].resetFields()
      this.listGroup(this.unit.id)
      this.$axios.$get(API_SYS_USER_NUMBER).then((d) => {
        if (d.code === 0) {
          this.$set(this.formData, 'serialNo', d.data)
          this.$set(this.formData, 'unitId', this.unit.id)
          this.$set(this.formData, 'unitPath', this.unit.path)
          this.modifyIt = false
          this.addDialogVisible = true
        }
      })
    },
    mobileInput() {
      this.$set(this.formData, 'loginname', this.formData.mobile)
    },
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_USER_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
                this.addDialogVisible = false
                this.loadNumber()
                this.listPage()
              }
            })
        }
      })
    },
    openUpdate(row) {
      this.groups = []
      this.$axios.$get(API_SYS_USER_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data.user
          this.listGroup(this.formData.unitId)
          this.$set(this.formData, 'unitName', this.formData.unit.name)
          this.$set(this.formData, 'unit', null)
          this.$set(this.formData, 'roleIds', d.data.roleIds)
          this.updateDialogVisible = true
        }
      })
    },
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_USER_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
                this.updateDialogVisible = false
                this.loadNumber()
                this.listPage()
              }
            })
        }
      })
    },
    resetPwd(row) {
      this.$confirm(
        '确定重置 ' + row.loginname + '(' + row.username + ') 用户密码？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios
          .$get(API_SYS_USER_RESETPWD + row.id)
          .then((res) => {
            if (res.code === 0) {
              this.$alert('新密码为 ' + res.data + ' 请牢记！', '操作提示', {
                confirmButtonText: '确定',
                callback: action => {
                  //
                }
              })
            }
          })
      }).catch(() => {})
    },
    // 启用禁用
    disabledChange(row) {
      this.$axios.$post(API_SYS_USER_DISABLED, { id: row.id, loginname: row.loginname, disabled: !row.disabled }).then((d) => {
        if (d.code === 0) {
          this.$message.success(d.msg)
          setTimeout(function() {
            row.disabled = !row.disabled
          }, 200)
        }
      })
    },
    deleteRow(row) {
      this.$confirm(
        '确定删除 ' + row.loginname + '(' + row.username + ') ？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios
          .$delete(API_SYS_USER_DELETE + row.id, { params: { loginname: row.loginname }})
          .then((res) => {
            if (res.code === 0) {
              this.$message.success(res.msg)
              this.loadNumber()
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
      this.$set(this.pageData, 'unitId', this.unit.id)
      this.$set(this.pageData, 'unitPath', this.unit.path)
      this.$axios.$post(API_SYS_USER_LIST, this.pageData).then((res) => {
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
        unitId: '',
        unitPath: '',
        postId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      }
      this.$refs['searchForm'].resetFields()
    },
    userSelectChange(val) {
      this.userSelectData = val
    },
    deleteMore() {
      var ids = []
      var names = []
      if (this.userSelectData.length === 0) {
        this.$message.warning('请选择用户')
        return
      }
      this.userSelectData.forEach((obj) => {
        names.push(obj.loginname + '(' + obj.username + ')')
        ids.push(obj.id)
      })
      this.$confirm(
        '确定删除用户 ' + names.toString() + ' ？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_SYS_USER_DELETE_MORE, { ids: ids.toString(), names: names.toString() }).then((d) => {
          if (d.code === 0) {
            this.$message.success(d.msg)
            this.loadNumber()
            this.listPage()
          }
        })
      }).catch(() => {})
    },
    exportUser() {
      this.$axios.get(API_SYS_USER_EXPORT, {
        responseType: 'blob',
        params: this.pageData
      }).then(res => {
        const url = window.URL.createObjectURL(new Blob([res.data]))
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', 'user.xls')
        document.body.appendChild(link)
        link.click()
      })
    },
    loadUnit() {
      this.$axios.$get(API_SYS_USER_UNIT).then((d) => {
        if (d.code === 0) {
          this.unitData = d.data
          if (this.unitData.length > 0) {
            this.unit = this.unitData[0]
            this.$nextTick(() => {
              this.$refs['unitTree'].setCurrentKey(this.unit.id)
              this.setUnitType(this.unit.type.value)
              this.listPage()
              this.loadNumber()
            })
          }
        }
      })
    },
    setUnitType(val) {
      this.isUnitType = val === 'UNIT'
    },
    renderContent(h, { node, data }) {
      var icon = ''
      var className = ''
      if (node.isCurrent) {
        className = 'custom active'
      } else {
        className = 'custom'
      }
      if (data.type.value === 'GROUP') {
        icon = 'fa fa-building'
      }
      if (data.type.value === 'COMPANY') {
        icon = 'fa fa-home'
      }
      return (
        <div class={className}><i class={icon}/> {data.label}</div>
      )
    },
    // 文件上传成功后进行保存
    handleUploadSuccess(resp, file) {
      if (resp.code !== 0) {
        this.$message.error(resp.msg)
        return
      }
      this.$set(this.formData, 'avatar', resp.data.url)
    },
    // 上传之前判断文件格式及大小
    beforeFileUpload(file) {
      var isJPEG = file.type === 'image/jpeg'
      var isPNG = file.type === 'image/png'
      var isJPG = file.type === 'image/jpg'
      if (!isJPG && !isPNG && !isJPEG) {
        this.$message.error('文件类型不正确')
        return false
      }
      var isLt2M = file.size / 1024 < 300
      if (!isLt2M) {
        this.$message.error('文件需小于300KB')
        return false
      }
      return true
    },
    loadNumber() {
      this.$axios.$get(API_SYS_USER_COUNT + this.unit.path).then((d) => {
        if (d.code === 0) {
          var n = d.data
          this.numberTitle = '(总人数:' + n.allNumber + ' | 启用:' + n.enabledNumber + ' | 停用:' + n.disabledNumber + ')'
        }
      })
    },
    loadPost() {
      this.$axios.$get(API_SYS_USER_POST).then((d) => {
        if (d.code === 0) {
          this.posts = d.data
        }
      })
    },
    getPost(val) {
      var index = this.posts.findIndex(obj => obj.id === val)
      return index >= 0 ? this.posts[index].name : ''
    },
    listGroup(unitId) {
      this.$axios.$get(API_SYS_USER_GROUP, { params: { unitId: unitId }}).then((d) => {
        if (d.code === 0) {
          this.groups = d.data
        }
      })
    },
    unitChange(data, node) {
      this.unit = data
      this.setUnitType(this.unit.type.value)
      this.loadNumber()
      this.listPage()
    }
  }
}
</script>

<style lang="scss" scoped>
.unit-left {
    border-right: solid 1px #e6e6e6;
    min-height: 450px;
    font-size: 12px;
}
/deep/ .is-current > .el-tree-node__content {
     background-color: rgba(36,118,224,.1);
     color: #2476e0;
}
/deep/ .el-tree-node__content {
    height: 40px;
}
/* 表格 */
/deep/  .demo-table-expand {
    font-size: 12px;
    padding-left: 115px;
    display: inline-table;
    label {
        width: 120px;
        color: #99a9bf;
    }
    .el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        width: 50%;
    }
    .el-form-item__label {
        font-size: 12px;
    }
    .el-form-item__content {
        font-size: 12px;
    }
}
/deep/  .avatar-uploader {
    .el-upload--picture-card {
        width: 60px;
        height: 60px;
        line-height: 60px;
    }
}
.el-dropdown-link {
    color: #409EFF;
    font-size: 12px;
    cursor: pointer;
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
.el-upload {
    img {
        width: 60px;
        height: 60px;
    }
}

</style>
