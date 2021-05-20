<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>定时任务</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.manage.task.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新增任务
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
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
          >
            <el-table-column
              prop="name"
              label="任务名称"
              header-align="center"
              align="center"
            />
            <el-table-column
              prop="iocName"
              :show-overflow-tooltip="true"
              label="IOC对象名"
              header-align="center"
              align="left"
            />
            <el-table-column
              prop="jobName"
              :show-overflow-tooltip="true"
              label="执行方法名"
              header-align="center"
              align="left"
            />
            <el-table-column
              prop="cron"
              label="时间表达式"
              header-align="center"
              align="center"
            />
            <el-table-column
              label="是否启用"
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
              align="center"
              width="300px"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'sys.manage.task'"
                  type="text"
                  size="small"
                  @click.native.prevent="seeRow(scope.row)"
                >
                  执行记录
                </el-button>
                <el-button
                  v-permission="'sys.manage.task.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="doNow(scope.row)"
                >
                  立即执行
                </el-button>
                <el-button
                  v-permission="'sys.manage.task.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="updateRow(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'sys.manage.task.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  :disabled="
                    scope.row.jobName &&
                      listData.length === 1 &&
                      scope.row.jobName === 'demo'
                  "
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
      title="创建任务"
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
          label="任务名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="50"
            placeholder="任务名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="iocName"
          label="Ioc对象名"
        >
          <el-input
            v-model="formData.iocName"
            maxlength="255"
            placeholder="Ioc对象名"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="jobName"
          label="执行方法名"
        >
          <el-input
            v-model="formData.jobName"
            maxlength="255"
            placeholder="执行方法名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="cron"
          label="时间表达式"
        >
          <cron-input v-model="formData.cron" />
        </el-form-item>
        <el-form-item
          prop="params"
          label="传递参数"
        >
          <el-input
            v-model="formData.params"
            maxlength="255"
            placeholder="传递参数"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="disabled"
          label="启用禁用"
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
          :loading="btnLoading"
          type="primary"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改任务"
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
        <el-form-item
          prop="name"
          label="任务名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="50"
            placeholder="任务名称"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="iocName"
          label="Ioc对象名"
        >
          <el-input
            v-model="formData.iocName"
            maxlength="255"
            placeholder="Ioc对象名"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="jobName"
          label="执行方法名"
        >
          <el-input
            v-model="formData.jobName"
            maxlength="255"
            placeholder="执行方法名"
            auto-complete="off"
            tabindex="3"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="cron"
          label="时间表达式"
        >
          <cron-input v-model="formData.cron" />
        </el-form-item>
        <el-form-item
          prop="params"
          label="传递参数"
        >
          <el-input
            v-model="formData.params"
            maxlength="255"
            placeholder="传递参数"
            auto-complete="off"
            tabindex="4"
            type="text"
          />
        </el-form-item>
        <el-form-item
          prop="disabled"
          label="启用禁用"
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
          :loading="btnLoading"
          type="primary"
          @click="doUpdate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="执行记录"
      :visible.sync="seeDialogVisible"
      :close-on-click-modal="false"
      width="75%"
    >
      <div class="platform-content-list-table">
        <el-table
          :data="listDataHistory"
          :default-sort="{
            prop: 'createdAt',
            order: 'descending'
          }"
          stripe
          @sort-change="doPageSortHistory"
        >
          <el-table-column
            prop="instanceId"
            label="实例ID"
            header-align="center"
            align="center"
            width="210px"
          />
          <el-table-column
            prop="jobId"
            :show-overflow-tooltip="true"
            label="作业ID"
            header-align="center"
            align="left"
            width="210px"
          />
          <el-table-column
            prop="success"
            label="是否成功"
            header-align="center"
            align="center"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.success">
                成功
              </span>
              <span v-if="!scope.row.success" style="color:red;">
                失败
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="message"
            label="错误信息"
            header-align="center"
            align="center"
          />
          <el-table-column
            prop="tookTime"
            label="耗时(单位:ms)"
            header-align="center"
            align="center"
          />
          <el-table-column
            prop="endTime"
            label="结束时间"
            header-align="center"
            align="center"
          >
            <template slot-scope="scope">
              <span v-if="scope.row.endTime">{{
                scope.row.endTime | moment("datetime")
              }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="platform-content-list-pagination">
        <el-pagination
          :current-page="pageDataHistory.pageNo"
          :page-size="pageDataHistory.pageSize"
          :total="pageDataHistory.totalCount"
          class="platform-pagenation"
          background
          :page-sizes="[10, 20, 30, 50]"
          layout="sizes, prev, pager, next"
          @current-change="doChangePageHistory"
          @size-change="doSizeChangeHistory"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import CronInput from './CronInput'
import {
  API_SYS_TASK_LIST,
  API_SYS_TASK_UPDATE,
  API_SYS_TASK_DELETE,
  API_SYS_TASK_CREATE,
  API_SYS_TASK_GET,
  API_SYS_TASK_DISABLED,
  API_SYS_TASK_HISTORY,
  API_SYS_TASK_DONOW
} from '@/constant/api/platform/sys/task'
export default {
  components: {
    CronInput
  },
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
      updateDialogVisible: false,
      seeDialogVisible: false,
      listData: [],
      formData: {
        cron: ''
      },
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      listDataHistory: [],
      pageDataHistory: {
        taskId: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      }
    }
  },
  computed: {
    getLocale() {
      return this.$i18n.locale
    },
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        name: [
          {
            required: true,
            message: '任务名称不可为空',
            trigger: 'blur'
          }
        ],
        iocName: [
          {
            required: true,
            message: 'Ioc对象名不可为空',
            trigger: 'blur'
          }
        ],
        jobName: [
          {
            required: true,
            message: '方法名不可为空',
            trigger: 'blur'
          }
        ],
        cron: [
          {
            required: true,
            message: '时间表达式不可为空',
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
      this.$axios.$post(API_SYS_TASK_LIST, this.pageData).then((res) => {
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
    // 打开新增窗口
    openAdd() {
      this.formData = {}
      this.addDialogVisible = true
    },
    // 提交新增数据
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_TASK_CREATE, this.formData)
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
    // 打开修改窗口
    updateRow(row) {
      this.$axios.$get(API_SYS_TASK_GET + row.id).then((d) => {
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
            .$post(API_SYS_TASK_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.listPage()
                this.updateDialogVisible = false
              }
            })
        }
      })
    },
    // 执行删除操作
    deleteRow(row) {
      this.$confirm(
        '删除任务？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_SYS_TASK_DELETE + row.id).then((d) => {
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
    // 启用禁用
    disabledChange(row) {
      this.$axios.$post(API_SYS_TASK_DISABLED, row).then((d) => {
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
    seeRow(row) {
      this.seeDialogVisible = true
      this.pageDataHistory.taskId = row.id
      this.doSearchHistory()
    },
    // 页码变动事件
    doChangePageHistory(val) {
      this.pageDataHistory.pageNo = val
      this.listPageHistory()
    },
    // 页大小变动事件
    doSizeChangeHistory(val) {
      this.pageDataHistory.pageSize = val
      this.listPageHistory()
    },
    // 页排序事件
    doPageSortHistory(column) {
      this.pageDataHistory.pageOrderName = column.prop
      this.pageDataHistory.pageOrderBy = column.order
      this.listPageHistory()
    },
    doSearchHistory() {
      this.pageData.pageNo = 1
      this.listPageHistory()
    },
    // 获取分页查询数据
    listPageHistory() {
      this.$axios.$post(API_SYS_TASK_HISTORY, this.pageDataHistory).then((res) => {
        if (res.code === 0) {
          this.listDataHistory = res.data.list
          this.pageDataHistory.totalCount = res.data.totalCount
        }
      })
    },
    doNow(row) {
      this.$confirm(
        '确定发送立即执行（执行一次）命令？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$get(API_SYS_TASK_DONOW + row.id).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
          }
        })
      }).catch(() => {})
    }
  },
  middleware: ['authenticated', 'check_permissions']
}
</script>
