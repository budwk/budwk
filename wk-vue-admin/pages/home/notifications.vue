<template>
  <div>
    <el-container>
      <el-aside style="width:160px;">
        <el-menu
          class="el_menu_left"
          default-active="unread"
          :default-openeds="['msg']"
          :unique-opened="true"
          @select="handleSelect"
        >
          <el-submenu index="msg">
            <template slot="title">消息中心</template>
            <el-menu-item-group>
              <el-menu-item index="all">全部消息</el-menu-item>
              <el-menu-item index="unread">未读消息</el-menu-item>
              <el-menu-item index="read">已读消息</el-menu-item>
            </el-menu-item-group>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-container v-if="id">
        <el-main style="padding: 2px 0 0 10px;overflow: hidden;">
          <div class="platform-content">
            <h4 style="display: flex;align-items: center;height: 36px;font-size:16px;">
              <span v-if="pageData.status==='unread'">未读消息</span>
              <span v-else>已读消息</span>
              <el-button type="text" icon="fa fa-arrow-circle-left" @click="goBack"> 返回</el-button>
            </h4>
            <div class="platform-content-info">
              <el-row class="msgData_title">
                {{ msgData.title }}
              </el-row>
              <el-row class="msgData_date">
                {{ msgData.sendAt | moment("datetime") }}
              </el-row>
              <el-row style="border-bottom: 1px solid #DDD;" />
              <el-row class="msgData_note">
                <div v-html="msgData.note" />
              </el-row>
            </div>
          </div>
        </el-main>
      </el-container>
      <el-container v-else>
        <el-main style="padding: 2px 0 0 10px;overflow: hidden;">
          <div class="platform-content">
            <h4 class="platform-content-title">
              <span>站内消息</span>
              <div class="platform-list-op">
                <el-button
                  size="small"
                  :disabled="selectDataList.length === 0"
                  @click="read"
                >
                  设为已读
                </el-button>
                <el-button
                  size="small"
                  @click="readAll"
                >
                  全部已读
                </el-button>
              </div>
            </h4>
            <div class="platform-content-info">
              <div class="platform-content-search">
                <el-form
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
                </el-form>
              </div>
              <div class="platform-content-list">
                <div class="platform-content-list-table">
                  <el-table
                    ref="msgTable"
                    v-loading="listLoading"
                    :data="listData"
                    stripe
                    @select-all="handleTableSelectAllChange"
                    @sort-change="doPageSort"
                    @selection-change="handleMsgTableChange"
                  >
                    <el-table-column
                      type="selection"
                      width="55"
                    />
                    <el-table-column
                      prop="title"
                      label="消息标题"
                      header-align="center"
                      :show-overflow-tooltip="true"
                    >
                      <template slot-scope="scope">
                        <span v-if="scope.row.status===0" class="unread">
                          <nuxt-link :to="{path:'/home/notifications',query:{id:scope.row.msgid}}">{{ scope.row.title }}</nuxt-link>
                        </span>
                        <span v-else class="read">
                          <nuxt-link :to="{path:'/home/notifications',query:{id:scope.row.msgid}}">{{ scope.row.title }}</nuxt-link>
                        </span>
                      </template>
                    </el-table-column>
                    <el-table-column
                      sortable
                      prop="sendAt"
                      label="发送时间"
                      header-align="center"
                      align="center"
                      width="220px"
                    >
                      <template slot-scope="scope">
                        {{ scope.row.sendat | moment("datetime") }}
                      </template>
                    </el-table-column>
                    <el-table-column
                      prop="type"
                      label="消息类型"
                      sortable
                      width="180px"
                    >
                      <template slot-scope="scope">
                        {{ formatType(scope.row.type) }}
                      </template>
                    </el-table-column>
                  </el-table>
                  <div class="platform-content-list-pagination">
                    <div style="float:left;">
                      <span style="padding:0 20px 0 16px;"><el-checkbox v-model="selectAll" @change="changeSelectAll" /></span>
                      <el-button size="small" :disabled="selectDataList.length === 0" @click="read">设为已读</el-button>
                      <el-button size="small" @click="readAll">全部已读</el-button>
                    </div>
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
          </div>
        </el-main>
      </el-container>
    </el-container>

  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_HOME_MSG_MY_LIST,
  API_HOME_MSG_DATA,
  API_HOME_MSG_GET,
  API_HOME_MSG_READ_ONE,
  API_HOME_MSG_READ_ALL,
  API_HOME_MSG_READ_MORE
} from '@/constant/api/home/home'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      addDialogVisible: false,
      listLoading: false,
      viewListLoading: false,
      selectDialogVisible: false,
      listData: [],
      types: [],
      pageData: {
        status: 'unread',
        type: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      filedomain: '',
      uploadurl: '',
      selectDataList: [],
      selectAll: false,
      id: this.$route.query.id,
      msgData: {}
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
        ]
      }
      return formRules
    }
  },
  watch: {
    '$route': function() {
      this.init()
    }
  },
  created() {
    if (process.browser) {
      this.initData()
      this.init()
    }
  },
  methods: {
    // 获取消息类型
    async initData() {
      const { data } = await this.$axios.$get(API_HOME_MSG_DATA, {})
      if (data) {
        this.types = data.types
      }
    },
    init() {
      this.id = this.$route.query.id
      if (this.id) {
        this.getMsg()
      } else {
        this.listPage()
      }
    },
    // 获取一条消息内容
    getMsg() {
      this.$axios.$post(API_HOME_MSG_GET + this.id).then((res) => {
        if (res.code === 0) {
          this.msgData = res.data
          this.readMsg(this.msgData.id)
        }
      })
    },
    // 设置一条消息为已读
    readMsg(val) {
      this.$axios.$post(API_HOME_MSG_READ_ONE + this.id).then((res) => {
      })
    },
    // 查询条件消息类型变化事件
    typeChange(val) {
      this.pageData.pageNo = 1
      this.pageData.type = val
      this.listPage()
    },
    // 根据状态获取消息
    handleSelect(key) {
      this.$router.push('/home/notifications')
      this.pageData.pageNo = 1
      this.pageData.status = key
      this.listPage()
    },
    // 返回上级
    goBack() {
      this.$router.push('/home/notifications')
    },
    // 表格勾选事件
    handleMsgTableChange(val) {
      this.selectDataList = val
      if (this.selectDataList.length === this.listData.length) {
        this.selectAll = true
      } else {
        this.selectAll = false
      }
    },
    // 底部全选复选框切换
    changeSelectAll() {
      this.$refs.msgTable.toggleAllSelection()
    },
    // 顶部全选复选框切换
    handleTableSelectAllChange(val) {
      if (val.length === this.listData.length) {
        this.selectAll = true
      } else {
        this.selectAll = false
      }
    },
    // 枚举类展示文本内容
    formatType(val) {
      if (this.types && this.types.length > 0) {
        var index = this.types.findIndex(obj => obj.value === val)
        return this.$t(this.types[index].text)
      }
      return ''
    },
    // 标记已读
    read() {
      var ids = []
      this.selectDataList.forEach((obj) => {
        ids.push(obj.id)
      })
      this.$confirm(
        '标记已读',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_HOME_MSG_READ_MORE, { ids: ids.toString() }).then((d) => {
          this.$message.success(d.msg)
          this.listPage()
        })
      }).catch(() => {})
    },
    // 全部已读
    readAll() {
      this.$confirm(
        '全部已读',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_HOME_MSG_READ_ALL, {}).then((d) => {
          this.$message.success(d.msg)
          this.listPage()
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
      this.$axios.$post(API_HOME_MSG_MY_LIST, this.pageData).then((res) => {
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

<style>
  .el-aside {
    color: #333;
    background-color: #EAEDF2;
    width:160px;
  }
  .el_menu_left {
    width:160px;
    overflow: hidden;
    height: 550px;
    background-color: #EAEDF2;
    border: 1px solid #eee;
  }
  .el_menu_left .el-submenu.is-active .el-submenu__title {
    background-color: #D9DEE4;
    font-weight: bold;
    text-indent: 20px;
  }
  .el_menu_left .el-menu-item-group__title{
    padding: 0px;
    height: 0px;
  }
  .el_menu_left .el-menu-item.is-active{
    background-color: #ffffff;
    font-size: 12px;
  }
  .el_menu_left .el-menu-item {
    background-color: #EAEDF2;
    font-size: 12px;
  }
  .el_menu_left .el-menu-item:hover{
    background-color: #F4F6F8;
  }
  .read a{
    text-decoration: none;
    color: #666;
  }
  .unread a{
    text-decoration: none;
    color: #409eff;
    font-weight: bold;
  }
  .msgData_title {
    font-size: 18px;text-align: center;margin-top: 10px;margin-bottom: 10px;
  }
  .msgData_date {
    font-size: 14px;text-align: center;margin-top: 10px;margin-bottom: 10px;
  }
  .msgData_note {
    font-size: 14px;
  }
</style>
