<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>服务列表</span>
    </h4>
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
              <el-form-item label="服务名称">
                <el-input
                  v-model="pageData.serviceNameParam"
                  placeholder="请输入服务名称"
                  maxlength="255"
                  auto-complete="off"
                  type="text"
                />
              </el-form-item>
            </el-col>
            <el-col :span="9">
              <el-form-item label="分组名称">
                <el-input
                  v-model="pageData.groupNameParam"
                  placeholder="请输入分组名称"
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
            stripe
          >
            <el-table-column
              prop="name"
              label="服务名"
              header-align="center"
            />
            <el-table-column
              prop="groupName"
              label="分组名称"
              width="150"
            />
            <el-table-column
              prop="clusterCount"
              label="集群数目"
              width="80"
              header-align="center"
              align="center"
            />
            <el-table-column
              prop="ipCount"
              label="实例数"
              width="80"
              header-align="center"
              align="center"
            />
            <el-table-column
              prop="healthyInstanceCount"
              label="健康实例数"
              width="100"
              header-align="center"
              align="center"
            />
            <el-table-column
              prop="triggerFlag"
              label="触发保护阈值"
              width="110"
              header-align="center"
              align="center"
            />
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
              width="100"
            >
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click.native.prevent="
                    openSee(scope.row)
                  "
                >
                  详情
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
      title="服务详情"
      :visible.sync="seeDialogVisible"
      :close-on-click-modal="false"
      width="66%"
    >
      <span v-if="formData.clusterName">集群: {{ formData.clusterName }}</span>
      <div class="platform-content-list-table" style="margin-top:5px;">
        <el-table
          v-loading="listLoading"
          :data="detailListData"
          stripe
        >
          <el-table-column
            prop="ip"
            label="IP"
            header-align="center"
            width="110"
          />
          <el-table-column
            prop="port"
            label="端口"
            width="80"
          />
          <el-table-column
            prop="ephemeral"
            label="临时实例"
            width="80"
            header-align="center"
            align="center"
          >
            <template slot-scope="scope">
              {{ scope.row.ephemeral }}
            </template>
          </el-table-column>
          <el-table-column
            prop="weight"
            label="权重"
            width="80"
            header-align="center"
            align="center"
          />
          <el-table-column
            prop="healthy"
            label="健康状态"
            width="80"
            header-align="center"
            align="center"
          >
            <template slot-scope="scope">
              {{ scope.row.healthy }}
            </template>
          </el-table-column>
          <el-table-column
            prop="metadata"
            label="元数据"
            header-align="left"
            align="left"
          >
            <template slot-scope="scope">
              <span v-html="getInfo(scope.row.metadata)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="platform-content-list-pagination">
        <el-pagination
          :current-page="detailPageData.pageNo"
          :page-size="detailPageData.pageSize"
          :total="detailPageData.totalCount"
          class="platform-pagenation"
          background
          :page-sizes="[10, 20, 30, 50]"
          layout="sizes, prev, pager, next"
          @current-change="doDetailChangePage"
          @size-change="doDetailSizeChange"
        />
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="seeDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_SYS_MONITOR_NACOS_SERVICES,
  API_SYS_MONITOR_NACOS_SERVICE,
  API_SYS_MONITOR_NACOS_DETAIL
} from '@/constant/api/platform/sys/monitor'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      addDialogVisible: false,
      seeDialogVisible: false,
      listData: [],
      pageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0
      },
      detailPageData: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0
      },
      detailListData: [],
      formData: {}
    }
  },
  computed: {
    ...mapState({
      conf: state => state.conf // 后台配置参数
    }),
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        appId: [
          {
            required: true,
            message: '所属应用',
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
    // 打开窗口
    openSee(row) {
      this.seeDialogVisible = true
      this.$axios.$post(API_SYS_MONITOR_NACOS_SERVICE, { serviceName: row.name, groupName: row.groupName }).then((res) => {
        if (res.code === 0) {
          this.formData = res.data
          this.$set(this.formData, 'clusterName', this.formData.clusters[0].name)
          this.$set(this.detailPageData, 'clusterName', this.formData.clusterName)
          this.$set(this.detailPageData, 'serviceName', row.name)
          this.$set(this.detailPageData, 'groupName', row.groupName)
          this.listDetailPage()
        }
      })
    },
    getInfo(data) {
      var note = ''
      for (var key in data) {
        note += key + '=' + data[key] + '<br>'
      }
      return note
    },
    // 页码变动事件
    doDetailChangePage(val) {
      this.detailPageData.pageNo = val
      this.listPage()
    },
    // 页大小变动事件
    doDetailSizeChange(val) {
      this.detailPageData.pageSize = val
      this.listDetailPage()
    },
    // 获取分页查询数据
    listDetailPage() {
      this.listLoading = true
      this.$axios.$post(API_SYS_MONITOR_NACOS_DETAIL, this.detailPageData).then((res) => {
        this.listLoading = false
        if (res.code === 0) {
          this.detailListData = res.data.list
          this.detailPageData.totalCount = res.data.count
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
    // 获取分页查询数据
    listPage() {
      this.listLoading = true
      this.$axios.$post(API_SYS_MONITOR_NACOS_SERVICES, this.pageData).then((res) => {
        this.listLoading = false
        if (res.code === 0) {
          this.listData = res.data.serviceList
          this.pageData.totalCount = res.data.count
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
        totalCount: 0
      }
      this.$refs['searchForm'].resetFields()
    }
  }
}
</script>
