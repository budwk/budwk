<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>栏目管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'cms.content.channel.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新建栏目
        </el-button>

        <el-button
          v-permission="'cms.content.channel.update'"
          size="small"
          @click="openSort"
        >
          栏目排序
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          :inline="true"
          class="platform-content-search-form"
        >
          <el-form-item label="所属站点">
            <el-select
              v-model="site.id"
              placeholder="所属站点"
              size="medium"
              @change="channelChange"
            >
              <el-option
                v-for="item in sites"
                :key="item.id"
                :label="item.site_name"
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
              label="栏目名称"
              header-align="center"
              prop="name"
              width="230px"
              :show-overflow-tooltip="true"
              align="left"
            />

            <el-table-column
              label="栏目标识"
              header-align="left"
              prop="code"
              :show-overflow-tooltip="true"
              align="left"
            />

            <el-table-column
              label="栏目路径"
              header-align="left"
              prop="url"
              :show-overflow-tooltip="true"
              align="left"
            />

            <el-table-column
              label="栏目类型"
              header-align="left"
              prop="type"
              :show-overflow-tooltip="true"
              align="left"
            >
              <template slot-scope="scope">
                {{ formatType(scope.row.type) }}
              </template>
            </el-table-column>

            <el-table-column
              label="启用状态"
              header-align="left"
              prop="disabled"
              align="left"
              width="120px"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <el-switch
                  v-model="scope.row.disabled"
                  size="small"
                  :active-value="false"
                  :inactive-value="true"
                  active-color="green"
                  inactive-color="red"
                  @change="disabledChange(scope.row)"
                />
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
                  type="text"
                  size="small"
                  @click.native.prevent="addSubRow(scope.row)"
                >
                  新建子栏目
                </el-button>
                <el-button
                  type="text"
                  size="small"
                  @click.native.prevent="updateRow(scope.row)"
                >
                  修改
                </el-button>
                <el-button
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
      title="新建栏目"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="addForm" :model="formData" :rules="formRules" size="small" label-width="80px">
        <el-form-item prop="siteId" label="所属站点">
          <span>{{ site.site_name }}</span>
        </el-form-item>
        <el-form-item prop="parentId" label="父级栏目" label-width="80px">
          <el-cascader
            v-if="!isAddFromSub"
            v-model="formData.parentId"
            style="width: 100%"
            :options="menuOptions"
            :props="props"
            tabindex="1"
            placeholder="不选则为根目录"
          />
          <el-input v-if="isAddFromSub" v-model="formData.parentName" type="text" :disabled="true" />
        </el-form-item>
        <el-form-item prop="name" label="栏目名称">
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="栏目名称"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="code" label="栏目标识">
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="栏目标识为大写字母"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="type" label="栏目类型">
          <el-radio-group v-model="formData.type">
            <el-radio-button
              v-for="type in channelType"
              :key="type.value"
              :label="type.value"
            >{{ type.text }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="url" label="URL">
          <el-input
            v-model="formData.url"
            maxlength="100"
            placeholder="URL"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="target" label="打开方式">
          <el-radio-group v-model="formData.target">
            <el-radio label="_blank">新窗口</el-radio>
            <el-radio label="_self">本窗口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="disabled" label="启用状态">
          <el-switch
            v-model="formData.disabled"
            active-color="#ff4949"
            inactive-color="#13ce66"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          :loading="btnLoading"
          type="primary"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改栏目"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form ref="updateForm" :model="formData" :rules="formRules" size="small" label-width="80px">
        <el-form-item prop="siteId" label="所属站点">
          <span>{{ site.site_name }}</span>
        </el-form-item>
        <el-form-item prop="parentId" label="父级栏目" label-width="80px">
          <el-input v-model="formData.parentName" type="text" :disabled="true" />
        </el-form-item>
        <el-form-item prop="name" label="栏目名称">
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="栏目名称"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="code" label="栏目标识">
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="栏目标识为大写字母"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="type" label="栏目类型">
          <el-radio-group v-model="formData.type">
            <el-radio-button
              v-for="type in channelType"
              :key="type.value"
              :label="type.value"
            >{{ type.text }}</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="url" label="URL">
          <el-input
            v-model="formData.url"
            maxlength="100"
            placeholder="URL"
            auto-complete="off"
            tabindex="6"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="target" label="打开方式">
          <el-radio-group v-model="formData.target">
            <el-radio label="_blank">新窗口</el-radio>
            <el-radio label="_self">本窗口</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item prop="disabled" label="启用状态">
          <el-switch
            v-model="formData.disabled"
            active-color="#ff4949"
            inactive-color="#13ce66"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          :loading="btnLoading"
          type="primary"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="栏目排序"
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
          size="small"
          :loading="btnLoading"
          type="primary"
          @click="doSort"
        >确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_CMS_CHANNEL_CHILD,
  API_CMS_CHANNEL_TREE,
  API_CMS_CHANNEL_CREATE,
  API_CMS_CHANNEL_DELETE,
  API_CMS_CHANNEL_GET,
  API_CMS_CHANNEL_UPDATE,
  API_CMS_CHANNEL_SORT_TREE,
  API_CMS_CHANNEL_SORT,
  API_CMS_CHANNEL_DISABLED,
  API_CMS_CHANNEL_LIST_SITE,
  API_CMS_CHANNEL_GET_TYPE
} from '@/constant/api/cms/channel'
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
      tableData: [],
      tableKey: '',
      isAddFromSub: false,
      options: [],
      menuOptions: [],
      sortMenuData: [],
      channelType: [],
      sites: [],
      site: {},
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
            .$get(API_CMS_CHANNEL_TREE + _self.site.id, {
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
        name: [
          {
            required: true,
            message: '栏目名称',
            trigger: 'blur'
          }
        ],
        code: [
          {
            required: true,
            message: '栏目标识',
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.loadSite()
      this.getType()
    }
  },
  methods: {
    // 获取栏目类型
    async getType() {
      const { data } = await this.$axios.$get(API_CMS_CHANNEL_GET_TYPE, {})
      if (data) {
        this.channelType = data
      }
    },
    // 加载站点信息
    loadSite() {
      this.$axios.$get(API_CMS_CHANNEL_LIST_SITE).then((d) => {
        if (d.code === 0) {
          this.sites = d.data
          if (this.sites && this.sites.length > 0) {
            this.site.id = this.sites[0].id
            this.site.site_name = this.sites[0].site_name
            this.loadData()
          }
        }
      })
    },
    channelChange(val) {
      this.site.site_name = ''
      if (this.sites && this.sites.length > 0) {
        var index = this.sites.findIndex(obj => obj.id === val)
        this.site.site_name = this.sites[index].site_name
      }
      this.loadData()
    },
    // 初始化加载第一级数据
    loadData() {
      this.listLoading = true
      this.$axios.$get(API_CMS_CHANNEL_CHILD + this.site.id).then((d) => {
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
        .$get(API_CMS_CHANNEL_CHILD + this.site.id, { params: { pid: tree.id }})
        .then((d) => {
          if (d.code === 0) {
            resolve(d.data)
          }
        })
    },
    // 枚举类展示文本内容
    formatType(val) {
      if (this.channelType && this.channelType.length > 0) {
        var index = this.channelType.findIndex(obj => obj.value === val)
        return this.$t(this.channelType[index].text)
      }
      return ''
    },
    // 打开新增窗口
    openAdd() {
      this.formData = {
        id: '',
        parentId: '',
        parentName: '',
        type: 'ARTICLE',
        target: '_blank',
        siteId: this.site.id
      }
      this.options = [] // 清空级联选择器的数据,使其数据动态更新
      this.addDialogVisible = true
      this.isAddFromSub = false
    },
    // 提交新增数据
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_CHANNEL_CREATE, this.formData)
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
    // 新增子菜单
    addSubRow(row) {
      this.formData = {
        id: '',
        parentId: row.id,
        parentName: row.name,
        disabled: false,
        type: 'ARTICLE',
        target: '_blank',
        siteId: this.site.id
      }
      this.isAddFromSub = true
      if (this.$refs['addForm']) this.$refs['addForm'].resetFields()
      this.addDialogVisible = true
    },
    // 打开修改窗口
    updateRow(row) {
      this.$axios.$get(API_CMS_CHANNEL_GET + row.id).then((d) => {
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
            .$post(API_CMS_CHANNEL_UPDATE, this.formData)
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
        this.$axios.$delete(API_CMS_CHANNEL_DELETE + row.id).then((d) => {
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
      this.$axios.$get(API_CMS_CHANNEL_SORT_TREE + this.site.id).then((d) => {
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
    // 启用禁用
    disabledChange(row) {
      this.$axios.$post(API_CMS_CHANNEL_DISABLED, row).then((d) => {
        if (d.code === 0) {
          this.$message({
            message: d.msg,
            type: 'success'
          })
        } else {
          setTimeout(function() {
            row.disabled = !row.disabled
          }, 300)
        }
      })
    },
    // 提交排序数据
    doSort() {
      this.btnLoading = true
      var ids = []
      this.getTreeIds(ids, this.sortMenuData)
      this.$axios
        .$post(API_CMS_CHANNEL_SORT + this.site.id, { ids: ids.toString() })
        .then((d) => {
          this.btnLoading = false
          if (d.code === 0) {
            this.sortDialogVisible = false
            this.loadData()
          }
        })
    }
  }
}
</script>
