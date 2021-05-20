<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>单位管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.manage.unit.update'"
          size="small"
          @click="openSort"
        >
          单位排序
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
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
              label="单位名称"
              header-align="center"
              prop="name"
              width="250px"
              :show-overflow-tooltip="true"
              align="left"
            >
              <template slot-scope="scope">
                <i v-if="scope.row.type&&scope.row.type.value==='GROUP'" class="fa fa-building" />
                <i v-if="scope.row.type&&scope.row.type.value==='COMPANY'" class="fa fa-home" />
                {{ scope.row.name }}
              </template>
            </el-table-column>

            <el-table-column
              label="单位编码"
              header-align="center"
              prop="unitcode"
              :show-overflow-tooltip="true"
              align="center"
            />

            <el-table-column
              label="单位类型"
              header-align="center"
              prop="type"
              :show-overflow-tooltip="true"
              align="center"
            />

            <el-table-column
              label="单位人数"
              header-align="center"
              prop="allNumber"
              :show-overflow-tooltip="true"
              align="center"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.path==='0001'">直属{{ scope.row.userNumber }}人/共{{ scope.row.allNumber }}人</span>
                <span v-else>{{ scope.row.userNumber }}/{{ scope.row.allNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              header-align="center"
              :show-overflow-tooltip="true"
              align="center"
              width="190px"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'sys.manage.unit.create'"
                  type="text"
                  size="small"
                  @click.native.prevent="addSubRow(scope.row)"
                >
                  新增子单位
                </el-button>
                <el-button
                  v-permission="'sys.manage.unit.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="updateRow(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'sys.manage.unit.delete'"
                  :disabled="scope.row.level1"
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
      v-permission="'sys.manage.unit.create'"
      title="创建单位"
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
          prop="parentId"
          label="上级单位"
        >
          <el-cascader
            v-if="!isAddFromSub"
            v-model="formData.parentId"
            :props="props"
            :options="options"
            placeholder="上级单位"
            style="width: 100%"
          />
          <el-input
            v-if="isAddFromSub"
            v-model="formData.parentName"
            type="text"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item
          prop="type"
          label="单位类型"
        >
          <el-radio-group v-model="formData.type" size="small" @change="typeChange">
            <template v-for="type in types">
              <el-radio-button :key="type.value" :label="type.value">{{ type.text }}</el-radio-button>
            </template>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          prop="name"
          label="单位名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="单位名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="aliasName"
          label="单位别名"
        >
          <el-input
            v-model="formData.aliasName"
            maxlength="100"
            placeholder="单位别名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="unitcode"
          label="单位编码"
        >
          <el-input
            v-model="formData.unitcode"
            maxlength="100"
            placeholder="单位编码"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="address"
          label="单位地址"
        >
          <el-input
            v-model="formData.address"
            maxlength="100"
            placeholder="单位地址"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="telephone"
          label="联系电话"
        >
          <el-input
            v-model="formData.telephone"
            maxlength="100"
            placeholder="联系电话"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="email"
          label="电子信箱"
        >
          <el-input
            v-model="formData.email"
            maxlength="100"
            placeholder="电子信箱"
            auto-complete="off"
            tabindex="7"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="website"
          label="单位网站"
        >
          <el-input
            v-model="formData.website"
            maxlength="100"
            placeholder="单位网站"
            auto-complete="off"
            tabindex="8"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="note"
          label="备注"
        >
          <el-input v-model="formData.note" type="textarea" />
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
      v-permission="'sys.manage.unit.update'"
      title="修改单位"
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
          prop="type"
          label="单位类型"
        >
          <span v-if="formData.typeName">{{ formData.typeName }}</span>
        </el-form-item>
        <el-form-item
          prop="name"
          label="单位名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="单位名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="aliasName"
          label="单位别名"
        >
          <el-input
            v-model="formData.aliasName"
            maxlength="100"
            placeholder="单位别名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="unitcode"
          label="单位编码"
        >
          <el-input
            v-model="formData.unitcode"
            maxlength="100"
            placeholder="单位编码"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>

        <el-form-item
          prop="leader"
          label="单位领导"
        >
          <el-select
            v-model="formData.leaders"
            size="small"
            style="width: 65%"
            class="span_n"
            multiple
            filterable
            remote
            default-first-option
            reserve-keyword
            :remote-method="searchLeaderUser"
            :loading="searchUserLoading"
            placeholder="输入姓名进行查询"
            autocomplete="off"
          >
            <el-option
              v-for="item in leaders"
              :key="item.value"
              :label="item.label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          prop="higher"
          label="上级主管领导"
        >
          <el-select
            v-model="formData.highers"
            size="small"
            style="width: 65%"
            class="span_n"
            multiple
            filterable
            remote
            default-first-option
            reserve-keyword
            :remote-method="searchHigherUser"
            :loading="searchUserLoading"
            placeholder="输入姓名进行查询"
            autocomplete="off"
            :disabled="formData.path==='0001'"
          >
            <el-option
              v-for="item in highers"
              :key="item.value"
              :label="item.label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          prop="assigner"
          label="上级分管领导"
        >
          <el-select
            v-model="formData.assigners"
            size="small"
            style="width: 65%"
            class="span_n"
            multiple
            filterable
            remote
            default-first-option
            reserve-keyword
            :remote-method="searchAssignerUser"
            :loading="searchUserLoading"
            placeholder="输入姓名进行查询"
            autocomplete="off"
            :disabled="formData.path==='0001'"
          >
            <el-option
              v-for="item in assigners"
              :key="item.value"
              :label="item.label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="address"
          label="单位地址"
        >
          <el-input
            v-model="formData.address"
            maxlength="100"
            placeholder="单位地址"
            auto-complete="off"
            tabindex="5"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="telephone"
          label="联系电话"
        >
          <el-input
            v-model="formData.telephone"
            maxlength="100"
            placeholder="联系电话"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="email"
          label="电子信箱"
        >
          <el-input
            v-model="formData.email"
            maxlength="100"
            placeholder="电子信箱"
            auto-complete="off"
            tabindex="7"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type!=='UNIT'"
          prop="website"
          label="单位网站"
        >
          <el-input
            v-model="formData.website"
            maxlength="100"
            placeholder="单位网站"
            auto-complete="off"
            tabindex="8"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="note"
          label="备注"
        >
          <el-input v-model="formData.note" type="textarea" />
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
      v-permission="'sys.manage.unit.update'"
      title="单位排序"
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
        <el-button size="small" @click="sortDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          size="small"
          :loading="btnLoading"
          @click="doSort"
        >确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_SYS_UNIT_CHILD,
  API_SYS_UNIT_TREE,
  API_SYS_UNIT_CREATE,
  API_SYS_UNIT_DELETE,
  API_SYS_UNIT_GET,
  API_SYS_UNIT_UPDATE,
  API_SYS_UNIT_SORT_TREE,
  API_SYS_UNIT_SORT,
  API_SYS_UNIT_SEARCH_USER
} from '@/constant/api/platform/sys/unit'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    const _self = this
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      sortDialogVisible: false,
      isAddFromSub: false,
      searchUserLoading: false,
      types: [],
      leaders: [],
      highers: [],
      assigners: [],
      tableData: [],
      tableKey: '',
      options: [],
      sortMenuData: [],
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
            .$get(API_SYS_UNIT_TREE, {
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
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        parentId: [
          {
            required: true,
            message: '上级单位',
            trigger: 'blur'
          }
        ],
        type: [
          {
            required: true,
            message: '单位类型',
            trigger: 'blur'
          }
        ],
        name: [
          {
            required: true,
            message: '单位名称',
            trigger: 'blur'
          }
        ],
        email: [
          {
            required: false,
            message: '电子信箱',
            trigger: 'blur'
          },
          {
            type: 'email',
            message: '电子信箱不正确',
            trigger: ['blur', 'change']
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.loadData()
    }
  },
  methods: {
    // 初始化加载第一级数据
    loadData() {
      this.listLoading = true
      this.$axios.$get(API_SYS_UNIT_CHILD).then((d) => {
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
        .$get(API_SYS_UNIT_CHILD, { params: { pid: tree.id }})
        .then((d) => {
          if (d.code === 0) {
            resolve(d.data)
          }
        })
    },
    // 打开新增窗口
    openAdd() {
      this.isAddFromSub = false
      this.formData = {
        type: 'UNIT'
      }
      this.options = [] // 清空级联选择器的数据,使其数据动态更新
      this.addDialogVisible = true
    },
    // 提交新增数据
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_UNIT_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.loadData()
                this.addDialogVisible = false
              }
            })
        }
      })
    },
    // 打开修改窗口
    updateRow(row) {
      this.$axios.$get(API_SYS_UNIT_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data.unit
          this.$set(this.formData, 'typeName', this.formData.type.text)
          this.$set(this.formData, 'type', this.formData.type.value)
          this.updateDialogVisible = true
          this.searchLeaderUser()
          this.searchHigherUser()
          this.searchAssignerUser()
          var unitUserList = d.data.unitUserList
          if (unitUserList) {
            var leaders = []
            var highers = []
            var assigners = []
            unitUserList.forEach((u) => {
              if (u.leaderType.value === 'LEADER') {
                leaders.push({ value: u.userId, text: u.user.username })
              }
              if (u.leaderType.value === 'HIGHER') {
                highers.push({ value: u.userId, text: u.user.username })
              }
              if (u.leaderType.value === 'ASSIGNER') {
                assigners.push({ value: u.userId, text: u.user.username })
              }
            })
            this.$set(this.formData, 'leaders', leaders)
            this.$set(this.formData, 'highers', highers)
            this.$set(this.formData, 'assigners', assigners)
          }
        }
      })
    },
    getUserIds(val) {
      var ids = []
      if (val) {
        val.forEach((obj) => {
          ids.push(obj.value)
        })
      }
      return ids.toString()
    },
    // 提交修改数据
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'leader', this.getUserIds(this.formData.leaders))
          this.$set(this.formData, 'higher', this.getUserIds(this.formData.highers))
          this.$set(this.formData, 'assigner', this.getUserIds(this.formData.assigners))
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_UNIT_UPDATE, this.formData)
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
        '确定删除 ' + row.name + '？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_SYS_UNIT_DELETE + row.id).then((d) => {
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
      this.$axios.$get(API_SYS_UNIT_SORT_TREE).then((d) => {
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
        .$post(API_SYS_UNIT_SORT, { ids: ids.toString() })
        .then((d) => {
          this.btnLoading = false
          if (d.code === 0) {
            this.sortDialogVisible = false
            this.loadData()
          }
        })
    },
    addSubRow(row) {
      this.isAddFromSub = true
      this.types = [
        { value: 'UNIT', text: '部门' }
      ]
      if (row.type.value === 'GROUP') {
        this.types = [
          { value: 'COMPANY', text: '分公司' },
          { value: 'UNIT', text: '部门' }
        ]
      }
      this.formData = {
        type: 'UNIT',
        parentId: row.id,
        parentName: row.name
      }
      this.options = [] // 清空级联选择器的数据,使其数据动态更新
      if (this.$refs['addForm']) this.$refs['addForm'].resetFields()
      this.addDialogVisible = true
    },
    searchLeaderUser(query) {
      this.searchUserLoading = true
      this.$axios.$post(API_SYS_UNIT_SEARCH_USER, { query: query, unitId: this.formData.id }).then((d) => {
        this.searchUserLoading = false
        if (d.code === 0) {
          this.leaders = d.data.list
        }
      })
    },
    searchHigherUser(query) {
      this.searchUserLoading = true
      this.$axios.$post(API_SYS_UNIT_SEARCH_USER, { query: query, unitId: this.formData.parentId }).then((d) => {
        this.searchUserLoading = false
        if (d.code === 0) {
          this.highers = d.data.list
        }
      })
    },
    searchAssignerUser(query) {
      this.searchUserLoading = true
      this.$axios.$post(API_SYS_UNIT_SEARCH_USER, { query: query, unitId: this.formData.parentId }).then((d) => {
        this.searchUserLoading = false
        if (d.code === 0) {
          this.assigners = d.data.list
        }
      })
    },
    typeChange() {
      this.$set(this.formData, 'address', '')
      this.$set(this.formData, 'telephone', '')
      this.$set(this.formData, 'email', '')
      this.$set(this.formData, 'website', '')
    }
  }
}
</script>

