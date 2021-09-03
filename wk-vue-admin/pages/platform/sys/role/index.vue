<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>角色管理</span>
      <div class="platform-list-op">
        <el-button
          v-if="tabIndex==='USERLIST'"
          v-permission="'sys.manage.role.update'"
          size="small"
          type="primary"
          :disabled="roleCode==='public'"
          @click="openUser"
        >
          关联用户到角色
        </el-button>
        <el-button
          v-if="tabIndex!=='USERLIST'"
          v-permission="'sys.manage.role.update'"
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doMenu"
        >
          保存权限到角色
        </el-button>
      </div>
    </h4>
    <el-row :gutter="10">
      <el-col :span="5" class="role-left">
        <el-row v-permission="'sys.manage.role.create'" style="text-align:right;padding-right:2px;">
          <span style="font-size:14px;float:left;">所属公司</span>
          <el-button
            type="text"
            size="small"
            style="margin-top:-7px;"
            @click="openAdd"
          >
            新建角色/角色组
          </el-button>
        </el-row>
        <el-row>
          <el-select v-model="unitId" size="small" style="width:100%;" @change="unitChange">
            <el-option
              v-for="unit in units"
              :key="unit.id"
              :value="unit.id"
              :label="unit.name"
            >
              <span v-if="'COMPANY'===unit.type.value">&nbsp;&nbsp;</span>
              {{ unit.name }}
            </el-option>
          </el-select>
        </el-row>
        <el-row v-if="groups.length>0">
          <ul v-for="group in groups" :key="group.id" class="role-group" :index="'group_'+group.id">
            <li class="role-group-item" @mouseover="enter(group.id)">
              <div class="group-name">{{ group.name }}</div>
              <div v-if="btnIndex===group.id" class="operate">
                <el-button type="text" @click="openUpdateGroup(group)"><i class="fa fa-pencil-square-o" /></el-button>
                <el-button type="text" style="margin-left:2px;" @click="openDeleteGroup(group)"><i class="fa fa-trash-o" /></el-button>
              </div>
            </li>
            <li v-for="role in group.roles" :key="role.id" class="role-group-item" :index="role.id" @click="clickRole(role)" @mouseover="enter(role.id)">
              <div :class="roleId===role.id?'active role':'role'">{{ role.name }}</div>
              <div v-if="btnIndex===role.id&&role.code!=='public'" class="operate">
                <el-button type="text" @click="openUpdateRole(role)"><i class="fa fa-pencil-square-o" /></el-button>
                <el-button type="text" style="margin-left:2px;" @click="openDeleteRole(role)"><i class="fa fa-trash-o" /></el-button>
              </div>
            </li>
          </ul>
        </el-row>
        <el-row v-else>
          <div style="margin-top:20px;">暂无数据</div>
        </el-row>
      </el-col>
      <el-col :span="19" style="padding-left:10px;">
        <span style="font-size:14px;">权限分配</span>
        <el-tabs v-model="tabIndex" type="card" style="padding-top:7px;" @tab-click="platTabClick">
          <el-tab-pane name="USERLIST" label="用户列表">
            <div>
              <el-form
                ref="searchForm"
                :inline="true"
                :model="pageData"
                size="mini"
              >
                <el-form-item label="用户">
                  <el-input
                    v-model="pageData.username"
                    placeholder="用户名或姓名"
                    maxlength="255"
                    auto-complete="off"
                    type="text"
                    clearable
                  />
                </el-form-item>
                <el-button
                  size="mini"
                  type="primary"
                  @click="doSearch"
                >
                  查 询
                </el-button>
              </el-form>
              <div class="platform-content-list-table">
                <el-table
                  v-loading="listLoading"
                  :data="listData"
                  stripe
                  @sort-change="doPageSort"
                >
                  <el-table-column
                    prop="id"
                    label="用户"
                  >
                    <template slot-scope="scope">
                      {{ scope.row.loginname }}({{
                        scope.row.username
                      }})
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="unitname"
                    label="单位"
                  />
                  <el-table-column
                    prop="postId"
                    label="职务"
                  >
                    <template slot-scope="scope">
                      {{ getPost(scope.row.postId) }}
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
                        v-permission="'sys.manage.role.delete'"
                        type="text"
                        size="small"
                        class="button-delete-color"
                        :disabled="roleCode==='sysadmin'&&'superadmin'===scope.row.loginname"
                        @click.native.prevent="removeUser(scope.row)"
                      >
                        移除
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
          </el-tab-pane>
          <el-tab-pane v-for="app in apps" :key="app.id" :name="app.id" :label="app.name">
            <span slot="label"><img v-if="app.icon" :src="app.icon" style="width:12px;height:12px"> {{ app.name }}</span>
            <div>
              <el-row style="margin-bottom: 3px;">
                <el-button size="mini" @click="menuRoleSelAll('tree_'+app.id)">全选</el-button>
                <el-button size="mini" @click="menuRoleSelClear('tree_'+app.id)">清除</el-button>
                <span style="font-size:12px;padding-left:10px;">勾选联动</span>
                <el-switch v-model="treeCheckStrictly" size="mini" />
              </el-row>
              <el-tree
                :ref="'tree_'+app.id"
                :data="doMenuData"
                :default-checked-keys="doMenuCheckedData"
                default-expand-all
                :render-content="renderContent"
                :highlight-current="true"
                :check-strictly="!treeCheckStrictly"
                show-checkbox
                node-key="id"
                :props="treeProps"
                style="padding-top:10px;"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
    <el-dialog
      v-permission="'sys.manage.role.create'"
      title="新建角色/角色组"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <el-form
        ref="addForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="100px"
      >
        <el-form-item
          prop="type"
          label="分类"
        >
          <el-radio-group v-model="formData.type" size="small" @change="typeChange">
            <el-radio label="role">角色</el-radio>
            <el-radio label="group">角色组</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="formData.type==='role'"
          prop="groupId"
          label="所属角色组"
        >
          <el-select v-model="formData.groupId" size="small" style="width:100%;">
            <el-option
              v-for="group in groups"
              :key="group.id"
              :value="group.id"
              :label="group.name"
            >
              {{ group.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="name"
          :label="formData.type==='role'?'角色名称':'组名'"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            :placeholder="formData.type==='role'?'角色名称':'组名'"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='role'"
          prop="code"
          label="角色代码"
        >
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="角色代码"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='role'"
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
          :loading="btnLoading"
          size="small"
          type="primary"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      v-permission="'sys.manage.role.update'"
      :title="title"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <el-form
        ref="updateForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="100px"
      >
        <el-form-item
          v-if="formData.type==='role'"
          prop="groupId"
          label="所属角色组"
        >
          <el-select v-model="formData.groupId" size="small" style="width:100%;">
            <el-option
              v-for="group in groups"
              :key="group.id"
              :value="group.id"
              :label="group.name"
            >
              {{ group.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="name"
          :label="formData.type==='role'?'角色名称':'组名'"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            :placeholder="formData.type==='role'?'角色名称':'组名'"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='role'"
          prop="code"
          label="角色代码"
        >
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="角色代码"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='role'"
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
          :loading="btnLoading"
          size="small"
          type="primary"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      v-permission="'sys.manage.role.update'"
      title="查询用户"
      :visible.sync="userDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <div>
        <div style="position: absolute;left: 350px;color:red;padding-top:5px;font-size:12px;">* 仅列出所属公司/直属部门/子部门用户</div>
        <el-form
          ref="searchForm"
          :inline="true"
          :model="selUserPageData"
          size="mini"
        >
          <el-form-item label="用户">
            <el-input
              v-model="selUserPageData.username"
              placeholder="用户名或姓名"
              maxlength="255"
              auto-complete="off"
              type="text"
              clearable
            />
          </el-form-item>
          <el-button
            size="mini"
            type="primary"
            @click="doSelUserSearch"
          >
            查 询
          </el-button>
        </el-form>
        <div class="platform-content-list-table">
          <el-table
            v-loading="listLoading"
            :data="selUserListData"
            stripe
            @sort-change="selUserDoPageSort"
            @selection-change="selUserChange"
          >
            <el-table-column
              type="selection"
              width="55"
            />
            <el-table-column
              prop="id"
              label="用户"
            >
              <template slot-scope="scope">
                {{ scope.row.loginname }}({{
                  scope.row.username
                }})
              </template>
            </el-table-column>
            <el-table-column
              prop="unitname"
              label="单位"
            />
            <el-table-column
              prop="postId"
              label="职务"
            >
              <template slot-scope="scope">
                {{ getPost(scope.row.postId) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="platform-content-list-pagination">
          <el-pagination
            :current-page="selUserPageData.pageNo"
            :page-size="selUserPageData.pageSize"
            :total="selUserPageData.totalCount"
            class="platform-pagenation"
            background
            :page-sizes="[10, 20, 30, 50]"
            layout="sizes, prev, pager, next"
            @current-change="selUserDoChangePage"
            @size-change="selUserDoSizeChange"
          />
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="userDialogVisible = false">取 消</el-button>
        <el-button
          :loading="btnLoading"
          size="small"
          type="primary"
          @click="doLink"
        >关 联</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_SYS_ROLE_UNIT,
  API_SYS_ROLE_GROUP,
  API_SYS_ROLE_APP,
  API_SYS_ROLE_CREATE,
  API_SYS_ROLE_UPDATE,
  API_SYS_ROLE_DELETE,
  API_SYS_ROLE_USERLIST,
  API_SYS_ROLE_SELECT_USER,
  API_SYS_ROLE_POST,
  API_SYS_ROLE_LINK_USER,
  API_SYS_ROLE_UNLINK_USER,
  API_SYS_ROLE_GET_DO_MENU,
  API_SYS_ROLE_DO_MENU
} from '@/constant/api/platform/sys/role'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      listLoading: false,
      btnLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      userDialogVisible: false,
      treeCheckStrictly: false,
      title: '',
      units: [],
      groups: [],
      posts: [],
      unitId: '',
      roleId: '',
      roleCode: '',
      roleName: '',
      appId: 'COMMON',
      apps: [],
      tabIndex: 'USERLIST',
      formData: {},
      btnIndex: '',
      listData: [],
      pageData: {
        roleId: '',
        username: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      selUserListData: [],
      selUserPageData: {
        roleId: '',
        username: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      selUsers: [],
      doMenuData: [],
      doMenuCheckedData: [],
      treeProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  computed: {
    formRules() {
      const formRules = {
        type: [
          {
            required: true,
            message: '分类',
            trigger: 'blur'
          }
        ],
        name: [
          {
            required: true,
            message: this.formData.type === 'role' ? '角色名称' : '组名',
            trigger: 'blur'
          }
        ],
        code: [
          {
            required: this.formData.type === 'role',
            message: '角色代码',
            trigger: 'blur'
          }
        ],
        groupId: [
          {
            required: this.formData.type === 'role',
            message: '角色组',
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.listPost()
      this.listUnit()
      this.listApp()
    }
  },
  methods: {
    enter(index) {
      this.btnIndex = index
    },
    typeChange() {

    },
    removeUser(user) {
      var name = user.loginname + '(' + user.username + ')'
      this.$confirm(
        '确定从角色 ' + this.roleName + ' 中移除 ' + name,
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios
          .$post(API_SYS_ROLE_UNLINK_USER, { roleId: this.roleId, id: user.id, name: name })
          .then((d) => {
            if (d.code === 0) {
              this.$message.success(d.msg)
              this.pageData.pageNo = 1
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
      this.$axios.$post(API_SYS_ROLE_USERLIST, this.pageData).then((res) => {
        this.listLoading = false
        if (res.code === 0) {
          this.listData = res.data.list
          this.pageData.totalCount = res.data.totalCount
        }
      })
    },
    doSearch() {
      this.pageData.pageNo = 1
      this.listPage()
    },
    openUser() {
      if (!this.roleId || this.roleId === '') {
        this.$message.warning('未选择角色')
        return
      }
      this.userDialogVisible = true
      this.$set(this.selUserPageData, 'unitId', this.unitId)
      this.$set(this.selUserPageData, 'username', '')
      this.selUserListPage()
    },
    selUserChange(val) {
      this.selUsers = val
    },
    doLink() {
      if (this.selUsers.length === 0) {
        return this.$message.warning('没有勾选用户')
      }
      var ids = []
      var names = []
      this.selUsers.forEach(ele => {
        ids.push(ele.id)
        names.push(ele.loginname + '(' + ele.username + ')')
      })
      this.btnLoading = true
      this.$axios
        .$post(API_SYS_ROLE_LINK_USER, { roleId: this.roleId, roleCode: this.roleCode, ids: ids.toString(), names: names.toString() })
        .then((d) => {
          this.btnLoading = false
          if (d.code === 0) {
            this.$message.success(d.msg)
            this.pageData.pageNo = 1
            this.listPage()
            this.userDialogVisible = false
          }
        })
    },
    // 页码变动事件
    selUserDoChangePage(val) {
      this.pageData.pageNo = val
      this.selUserListPage()
    },
    // 页大小变动事件
    selUserDoSizeChange(val) {
      this.pageData.pageSize = val
      this.selUserListPage()
    },
    // 页排序事件
    selUserDoPageSort(column) {
      this.pageData.pageOrderName = column.prop
      this.pageData.pageOrderBy = column.order
      this.selUserListPage()
    },
    // 获取分页查询数据
    selUserListPage() {
      this.listLoading = true
      this.$set(this.selUserPageData, 'roleId', this.roleId)
      this.$axios.$post(API_SYS_ROLE_SELECT_USER, this.selUserPageData).then((res) => {
        this.listLoading = false
        if (res.code === 0) {
          this.selUserListData = res.data.list
          this.selUserPageData.totalCount = res.data.totalCount
        }
      })
    },
    doSelUserSearch() {
      this.selUserPageData.pageNo = 1
      this.selUserListPage()
    },
    // 打开新增窗口
    openAdd() {
      this.formData = {
        type: this.groups.length > 0 ? 'role' : 'group'
      }
      this.addDialogVisible = true
    },
    // 提交新增数据
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'unitId', this.unitId)
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_ROLE_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.listGroup(this.unitId)
                this.addDialogVisible = false
              }
            })
        }
      })
    },
    clickRole(role) {
      this.roleId = role.id
      this.roleCode = role.code
      this.roleName = role.name
      this.$set(this.pageData, 'roleId', this.roleId)
      this.listPage()
      this.platTabClick()
    },
    openUpdateGroup(group) {
      this.title = '修改角色组'
      this.formData = JSON.parse(JSON.stringify(group))
      this.$set(this.formData, 'type', 'group')
      this.updateDialogVisible = true
    },
    openDeleteGroup(group) {
      this.$confirm(
        '确定删除角色组 ' + group.name,
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios
          .$post(API_SYS_ROLE_DELETE, { type: 'group', id: group.id, name: group.name })
          .then((d) => {
            if (d.code === 0) {
              this.$message({
                message: d.msg,
                type: 'success'
              })
              this.listGroup(this.unitId)
            }
          })
      }).catch(() => {})
    },
    openUpdateRole(role) {
      this.title = '修改角色'
      this.formData = JSON.parse(JSON.stringify(role))
      this.$set(this.formData, 'type', 'role')
      this.updateDialogVisible = true
    },
    openDeleteRole(role) {
      this.$confirm(
        '确定删除角色 ' + role.name,
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios
          .$post(API_SYS_ROLE_DELETE, { type: 'role', id: role.id, name: role.name })
          .then((d) => {
            if (d.code === 0) {
              this.$message({
                message: d.msg,
                type: 'success'
              })
              this.listGroup(this.unitId)
            }
          })
      }).catch(() => {})
    },
    // 提交修改数据
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_ROLE_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.listGroup(this.unitId)
                this.updateDialogVisible = false
              }
            })
        }
      })
    },
    // 获取选择的节点id
    getTreeAllIds(ids, obj) {
      const _self = this
      if (obj && obj.length > 0) {
        obj.forEach(function(o) {
          ids.push(o.id)
          if (o.children) {
            _self.getTreeAllIds(ids, o.children)
          }
        })
      }
    },
    // 分配权限窗 全选权限
    menuRoleSelAll(val) {
      var ids = []
      this.getTreeAllIds(ids, this.doMenuData)
      this.$refs[val][0].setCheckedKeys(ids)
    },
    // 分配权限窗 取消全选
    menuRoleSelClear(val) {
      this.$refs[val][0].setCheckedKeys([])
    },
    platTabClick() {
      if (this.tabIndex === 'USERLIST') {
        return
      }
      this.appId = this.tabIndex
      this.loadDoMenuData()
    },
    // 树横向排列
    renderContent(h, { node, data }) {
      let className = ''
      let icon = ''
      if (data.type === 'data') {
        className = 'especially'
      }
      if (data.type === 'menu') {
        if (node.expanded) {
          icon = 'fa fa-folder-open-o'
        } else {
          icon = 'fa fa-folder-o'
        }
      }
      return (
      // 在需要做横向排列的模块做标记
        <div class={className}><i class={icon}/> {data.label}</div>
      )
    },
    // 改变tree节点样式
    changeTreeClass() {
      var levelName = document.getElementsByClassName('especially') // levelname是上面的最底层节点的名字
      for (var i = 0; i < levelName.length; i++) {
        // cssFloat 兼容 ie6-8  styleFloat 兼容ie9及标准浏览器
        levelName[i].parentNode.parentNode.style.cssText = 'padding-left: 30px;'
        levelName[i].parentNode.style.cssFloat = 'left' // 最底层的节点，包括多选框和名字都让他左浮动
        levelName[i].parentNode.style.styleFloat = 'left'
        levelName[i].parentNode.style.cssText = levelName[i].parentNode.style.cssText + 'padding-left: 0px;'
        levelName[i].parentNode.onmouseover = function() {
          this.style.backgroundColor = '#fff'
        }
      }
    },
    // 加载可分配的权限菜单
    loadDoMenuData() {
      this.$axios
        .$get(API_SYS_ROLE_GET_DO_MENU, { params: { roleId: this.roleId, appId: this.appId }})
        .then((d) => {
          if (d.code === 0) {
            this.doMenuData = d.data.menuTree
            this.doMenuCheckedData = d.data.menuIds
            this.$nextTick(() => {
              this.changeTreeClass()
            })
          }
        })
    },
    doMenu() {
      if (!this.roleId || this.roleId === '') {
        this.$message.warning('未选择角色')
        return
      }
      this.$confirm(
        '确定保存权限？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        var ids = this.$refs['tree_' + this.appId][0].getCheckedKeys()
        this.btnLoading = true
        this.$axios
          .$post(API_SYS_ROLE_DO_MENU, { roleId: this.roleId, roleCode: this.roleCode, appId: this.appId, menuIds: ids.toString() })
          .then((d) => {
            this.btnLoading = false
            if (d.code === 0) {
              this.$message.success(d.msg)
            }
          })
      }).catch(() => {})
    },
    listUnit() {
      this.$axios.$get(API_SYS_ROLE_UNIT).then((d) => {
        if (d.code === 0) {
          this.units = d.data
          if (this.units.length > 0) {
            this.unitId = this.units[0].id
            this.listGroup(this.unitId)
          }
        }
      })
    },
    listGroup(unitId) {
      this.$axios.$get(API_SYS_ROLE_GROUP, { params: { unitId: unitId }}).then((d) => {
        if (d.code === 0) {
          this.groups = d.data
          if (this.groups.length > 0 && this.groups[0].roles.length > 0) {
            this.roleId = this.groups[0].roles[0].id
            this.roleCode = this.groups[0].roles[0].code
            this.roleName = this.groups[0].roles[0].name
            this.$set(this.pageData, 'roleId', this.roleId)
            this.listPage()
            this.platTabClick()
          } else {
            this.roleId = ''
            this.roleCode = ''
            this.roleName = ''
            this.$set(this.pageData, 'roleId', '')
            this.listPage()
            this.platTabClick()
          }
        }
      })
    },
    listApp() {
      this.$axios.$get(API_SYS_ROLE_APP).then((d) => {
        if (d.code === 0) {
          this.apps = d.data
          if (this.apps.length > 0) {
            this.appId = this.apps[0].id
          }
        }
      })
    },
    listPost() {
      this.$axios.$get(API_SYS_ROLE_POST).then((d) => {
        if (d.code === 0) {
          this.posts = d.data
        }
      })
    },
    getPost(val) {
      var index = this.posts.findIndex(obj => obj.id === val)
      return index >= 0 ? this.posts[index].name : ''
    },
    unitChange() {
      this.listGroup(this.unitId)
    }
  }
}
</script>

<style scoped>
.role-left {
    border-right: solid 1px #e6e6e6;
    min-height: 450px;
    font-size: 12px;
}
.role-group {
    list-style: none;
    margin: 10px 0 0;
    padding: 0;
}
.role-group li{
    line-height: 35px;
}
.role-group .role {
    padding-left: 20px;
    cursor: pointer;
    font-weight: 500;
}
.active {
    background-color: rgba(36,118,224,.1);
    color: #2476e0;
    font-weight: 600;
}
.role-group-item {
    position: relative;
    min-height: 42px;
    line-height: 42px;
}
.group-name {
    color: #999;
    margin-top: 10px;
    padding-left: 5px;
}
.operate {
    position: absolute;
    right: 10px;
    top: 0;
}
.especially {
    width: 100px;
}
</style>
