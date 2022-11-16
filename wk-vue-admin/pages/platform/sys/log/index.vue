<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>日志管理</span>
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
            <el-col :span="5">
              <el-form-item label="日志类型">
                <el-select
                  v-model="pageData.type"
                  placeholder="日志类型"
                  size="medium"
                  style="width:100%;"
                >
                  <el-option
                    v-for="item in types"
                    :key="item.value"
                    :label="item.text"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="所属应用">
                <el-select
                  v-model="pageData.appId"
                  placeholder="所属应用"
                  size="medium"
                  style="width:100%;"
                >
                  <template v-for="item in apps">
                    <el-option
                      v-if="item.id!=='COMMON'"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="日志标签">
                <el-input
                  v-model="pageData.tag"
                  placeholder="日志标签"
                  maxlength="255"
                  auto-complete="off"
                  type="text"
                />
              </el-form-item>
            </el-col>
            <el-col :span="7">
              <el-form-item label="日志内容">
                <el-input
                  v-model="pageData.msg"
                  placeholder="日志内容"
                  maxlength="255"
                  auto-complete="off"
                  type="text"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="5">
              <el-form-item label="用户名">
                <el-input
                  v-model="pageData.loginname"
                  placeholder="用户名"
                  maxlength="255"
                  auto-complete="off"
                  type="text"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label="姓名昵称">
                <el-input
                  v-model="pageData.username"
                  placeholder="姓名或昵称"
                  maxlength="255"
                  auto-complete="off"
                  type="text"
                />
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="searchDate"
                  type="datetimerange"
                  :picker-options="pickerOptions"
                  range-separator="从"
                  start-placeholder="开始时间"
                  end-placeholder="截至时间"
                  align="right"
                  size="medium"
                  :default-time="['00:00:00', '23:59:59']"
                  value-format="yyyy-MM-dd HH:mm:ss"
                />
              </el-form-item>
            </el-col>
            <el-col :span="4" style="float:right;">
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
                    label="请求路径"
                  >
                    <span>
                      {{
                        scope.row.url ||
                          ''
                      }}</span>
                  </el-form-item>
                  <el-form-item
                    label="请求参数"
                  >
                    <span>
                      {{
                        scope.row.params ||
                          '未记录'
                      }}</span>
                  </el-form-item>
                  <el-form-item
                    label="UserAgent"
                  >
                    <span>
                      {{
                        scope.row.browser ||
                          ''
                      }}</span>
                  </el-form-item>

                  <el-form-item
                    label="操作系统"
                  >
                    <span>
                      {{
                        scope.row.os ||
                          ''
                      }}</span>
                  </el-form-item>

                  <el-form-item
                    label="执行类"
                  >
                    <span>
                      {{
                        scope.row.method ||
                          ''
                      }}</span>
                  </el-form-item>
                  <el-form-item
                    label="响应结果"
                  >
                    <span>
                      {{
                        scope.row.result ||
                          '未记录'
                      }}</span>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column
              prop="loginname"
              label="操作人"
              sortable
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                {{ scope.row.loginname }}({{
                  scope.row.username
                }})
              </template>
            </el-table-column>
            <el-table-column
              prop="createdAt"
              label="操作时间"
              sortable
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.createdat">{{
                  scope.row.createdat | moment("datetime")
                }}</span>
                <span v-if="scope.row.createdAt">{{
                  scope.row.createdAt | moment("datetime")
                }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="type"
              label="日志类型"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <span v-for="type in types" :key="type.value">
                  <span v-if="scope.row.type===type.value">{{ type.text }}</span>
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="tag"
              label="日志标签"
              header-align="center"
              align="center"
            />
            <el-table-column
              prop="msg"
              label="日志内容"
              header-align="left"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              prop="ip"
              label="IP"
              header-align="left"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              prop="executeTime"
              label="耗时"
              header-align="left"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span style="background-color:rgb(135, 208, 104);color:#fff;display: inline-block;width:38px;border-radius: 2px;padding-left: 5px;">{{ scope.row.executeTime }}ms</span>
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
  </div>
</template>

<script>
import moment from 'moment'
import { API_SYS_LOG_DATA, API_SYS_LOG_LIST } from '@/constant/api/platform/sys/log'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      apps: [],
      types: [],
      listData: [],
      searchDate: [],
      pageData: {
        type: '',
        appId: '',
        searchDate: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      pickerOptions: {
        disabledDate(time) {
          var threeMonths = moment()
            .add(-3, 'months')
            .valueOf()
          return (
            time.getTime() > Date.now() ||
                        time.getTime() < threeMonths
          )
        },
        shortcuts: [
          {
            text: '最近一周',
            onClick(picker) {
              var end = new Date()
              var start = new Date()
              var temp = moment()
                .add(-1, 'weeks')
                .valueOf()
              start.setTime(temp)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近一个月',
            onClick(picker) {
              var end = new Date()
              var start = new Date()
              var temp = moment()
                .add(-1, 'months')
                .valueOf()
              start.setTime(temp)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '最近三个月',
            onClick(picker) {
              var end = new Date()
              var start = new Date()
              var temp = moment()
                .add(-3, 'months')
                .valueOf()
              start.setTime(temp)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      }
    }
  },
  created() {
    if (process.browser) {
      this.initData()
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
      if (this.searchDate) {
        this.pageData.searchDate = this.searchDate.toString()
      } else {
        this.pageData.searchDate = ''
      }
      this.$axios.$post(API_SYS_LOG_LIST, this.pageData).then((res) => {
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
        type: '',
        appId: '',
        searchDate: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      }
      this.searchDate = []
      this.$refs['searchForm'].resetFields()
    },
    initData() {
      this.$axios.$get(API_SYS_LOG_DATA).then((res) => {
        if (res.code === 0) {
          this.apps = res.data.apps
          this.types = res.data.types
        }
      })
    }
  }
}
</script>

<style>
/* 表格 */
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
    font-size: 12px;
}
.demo-table-expand .el-form-item .el-form-item__label {
    font-size: 12px;
}
.demo-table-expand .el-form-item .el-form-item__content {
    font-size: 12px;
}
</style>
