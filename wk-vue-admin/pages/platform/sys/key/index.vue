<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>密钥管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.config.key.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新建密钥
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
              label="密钥名称"
              sortable
              width="180"
            />
            <el-table-column
              prop="appid"
              label="APP ID"
              sortable
              width="180"
            />
            <el-table-column
              prop="appkey"
              label="APP KEY"
              align="center"
            >
              <template slot-scope="scope">
                <span v-if="isVisable(scope.row.appid)" class="bg-success">{{ scope.row.appkey }}</span><a v-if="!isVisable(scope.row.appid)" style="padding-left:5px;color: #06C;cursor: pointer;" @click="visiableData.push(scope.row.appid)">显示</a><a v-if="isVisable(scope.row.appid)" style="padding-left:5px;color: #06C;cursor: pointer;" @click="setNotVisable(scope.row.appid)">隐藏</a>
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
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'sys.config.key.delete'"
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
      title="新建密钥"
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
        <el-form-item
          prop="name"
          label="密钥名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="密钥名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="btnLoading"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  API_SYS_KEY_LIST,
  API_SYS_KEY_CREATE,
  API_SYS_KEY_DELETE,
  API_SYS_KEY_DISABLED
} from '@/constant/api/platform/sys/key'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
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
        lang_value: {}
      },
      visiableData: []
    }
  },
  computed: {
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        name: [
          {
            required: true,
            message: '密钥名称',
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
    // 是否显示密钥
    isVisable(appid) {
      if (this.visiableData.indexOf(appid) >= 0) {
        return true
      }
      return false
    },
    // 隐藏密钥
    setNotVisable: function(appid) {
      var index = this.visiableData.indexOf(appid)
      if (index >= 0) {
        this.visiableData.splice(index, 1)
      }
    },
    // 打开字新增窗口
    openAdd() {
      this.formData = {}
      this.addDialogVisible = true
    },
    // 提交表单
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_KEY_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.addDialogVisible = false
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
        this.btnLoading = true
        this.$axios
          .$delete(API_SYS_KEY_DELETE + row.appid)
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
    // 启用禁用
    disabledChange(row) {
      this.$axios.$post(API_SYS_KEY_DISABLED, row).then((d) => {
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
      this.$axios.$post(API_SYS_KEY_LIST, this.pageData).then((res) => {
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
