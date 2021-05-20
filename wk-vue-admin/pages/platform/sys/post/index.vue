<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>职务管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.manage.post.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新增职务
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
              label="职务名称"
            />
            <el-table-column
              prop="code"
              label="职务编号"
            />
            <el-table-column
              prop="location"
              label="排序"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.location"
                  controls-position="right"
                  size="mini"
                  :min="1"
                  :max="100"
                  @change="locationChange(scope.row)"
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
                  v-permission="'sys.manage.post.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="
                    openUpdate(scope.row)
                  "
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'sys.manage.post.delete'"
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
      title="新增职务"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="35%"
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
          label="职务名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="职务名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="code"
          label="职务编号"
        >
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="职务编号"
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

    <el-dialog
      title="修改职务"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="35%"
    >
      <el-form
        ref="editForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item
          prop="name"
          label="职务名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="职务名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="code"
          label="职务编号"
        >
          <el-input
            v-model="formData.code"
            maxlength="100"
            placeholder="职务编号"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
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
  API_SYS_POST_LIST,
  API_SYS_POST_GET,
  API_SYS_POST_CREATE,
  API_SYS_POST_DELETE,
  API_SYS_POST_UPDATE,
  API_SYS_POST_LOCATION
} from '@/constant/api/platform/sys/post'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      apps: [],
      types: [],
      listData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      }
    }
  },
  computed: {
    ...mapState({
      conf: state => state.conf // 后台配置参数
    }),
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        name: [
          {
            required: true,
            message: '职务名称',
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
    // 打开字新增窗口
    openAdd() {
      this.formData = { }
      this.addDialogVisible = true
    },
    // 提交表单
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_POST_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
                this.addDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_SYS_POST_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.updateDialogVisible = true
        }
      })
    },
    // 提交表单
    doUpdate() {
      this.$refs['editForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_POST_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
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
        this.btnLoading = true
        this.$axios
          .$delete(API_SYS_POST_DELETE + row.id)
          .then((d) => {
            this.btnLoading = false
            if (d.code === 0) {
              this.$message.success(d.msg)
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
      this.$axios.$post(API_SYS_POST_LIST, this.pageData).then((res) => {
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
    locationChange(row) {
      this.$axios.$post(API_SYS_POST_LOCATION, { location: row.location, id: row.id }).then((res) => {
        if (res.code === 0) {
          this.pageData.pageNo = 1
          this.listPage()
        }
      })
    }
  }
}
</script>
