<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>文章管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'cms.content.article.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新增文章
        </el-button>
        <el-button
          v-permission="'cms.content.article.delete'"
          size="small"
          type="danger"
          :disabled="tableSelectData.length === 0"
          @click="deleteMore"
        >
          删除文章
        </el-button>
      </div>
    </h4>

    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          :inline="true"
          :model="pageData"
          class="platform-content-search-form"
        >
          <el-form-item label="所属站点">
            <el-select
              v-model="site.id"
              placeholder="所属站点"
              size="mini"
              @change="siteChange"
            >
              <el-option
                v-for="item in sites"
                :key="item.id"
                :label="item.site_name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item
            label="文章标题"
          >
            <el-input
              v-model="pageData.title"
              placeholder="文章标题"
              class="platform-content-search-input"
              @keyup.enter.native="doSearch"
            />
          </el-form-item>
          <div class="platform-content-search-op">
            <el-button
              size="small"
              type="primary"
              @click="doSearch"
            >
              查询
            </el-button>
          </div>
        </el-form>
      </div>
      <el-row :gutter="10">
        <el-col :span="4">
          <el-tree
            node-key="id"
            :data="treeData"
            :props="treeProps"
            :current-node-key="'root'"
            @current-change="treeChange"
          />
        </el-col>
        <el-col :span="20">
          <div class="platform-content-list">

            <div class="platform-content-list-table">
              <el-table
                ref="dataTable"
                v-loading="listLoading"
                :data="listData"
                :default-sort="{
                  prop: 'createdAt',
                  order: 'descending'
                }"
                stripe
                @sort-change="doPageSort"
                @selection-change="tableSelectChange"
                @row-click="userRowClick"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column
                  prop="title"
                  label="栏目标题"
                  sortable
                />
                <el-table-column
                  sortable
                  label="发布时间"
                  header-align="center"
                  prop="publishAt"
                  width="160"
                >
                  <template slot-scope="scope">
                    {{ scope.row.publishAt | moment("datetime") }} - {{ scope.row.endAt | moment("datetime") }}
                  </template>
                </el-table-column>
                <el-table-column
                  label="启用状态"
                  header-align="center"
                  prop="disabled"
                  align="center"
                  width="80px"
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
                  width="200"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      size="small"
                      @click.native.prevent="
                        openSee(scope.row)
                      "
                    >
                      查看
                    </el-button>
                    <el-button
                      v-permission="
                        'cms.content.article.update'
                      "
                      type="text"
                      size="small"
                      @click.native.prevent="
                        openUpdate(scope.row)
                      "
                    >
                      修改
                    </el-button>
                    <el-button
                      v-permission="
                        'cms.content.article.delete'
                      "
                      type="text"
                      size="small"
                      class="button-delete-color"
                      @click.native.prevent="
                        deleteRow(scope.row)
                      "
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
        </el-col>
      </el-row>
    </div>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-form ref="dialogForm" :model="formData" :rules="formRules" size="small" label-width="80px">
        <el-form-item v-if="action==='add'" prop="channelName" label="所属栏目">
          {{ pageData.channelName }}
        </el-form-item>
        <el-form-item prop="title" label="栏目标题">
          <el-input
            v-model="formData.title"
            maxlength="255"
            placeholder="栏目标题"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="author" label="作者">
          <el-input
            v-model="formData.author"
            maxlength="50"
            placeholder="作者"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="info" label="文章简介">
          <el-input v-model="formData.info" type="textarea" />
        </el-form-item>
        <el-form-item prop="url" label="URL">
          <el-input
            v-model="formData.url"
            maxlength="50"
            placeholder="URL"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="publishAt" label="发布时间">
          <el-date-picker
            v-model="formData.time"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="timestamp"
            type="datetimerange"
            range-separator="至"
            start-placeholder="起始时间"
            end-placeholder="截至时间"
          />
        </el-form-item>

        <el-form-item prop="disabled" label="启用状态">
          <el-switch
            v-model="formData.disabled"
            active-color="#ff4949"
            inactive-color="#13ce66"
          />
        </el-form-item>
        <el-form-item prop="picUrl" label="标题图">
          <el-upload
            class="avatar-uploader"
            tabindex="5"
            :action="uploadUrl"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :on-success="function(resp,file,fileList){return handlePicSuccess(resp,file,fileList,'picUrl')}"
          >

            <img v-if="formData.picUrl" :src="conf.AppFileDomain+formData.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="content" label="文章内容">
          <div>
            <QuillEditor
              ref="myQuillEditor"
              style="height:320px;"
              :text="formData.content"
              :uploadurl="uploadurl"
              :filedomain="filedomain"
            />
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doSave"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-drawer
      title="查看文章"
      :visible.sync="drawerSeeVisible"
      :direction="'rtl'"
      :wrapper-closable="true"
      :size="'50%'"
    >
      <el-row style="text-align: center">
        <h4>{{ formData.title }}</h4>
      </el-row>
      <el-row style="font-size:12px;">
        <span>作者：{{ formData.author }}</span>
        <span>发布时间: {{ formData.publishAt | moment("datetime") }} - {{ formData.endAt | moment("datetime") }}</span>
      </el-row>
      <el-row style="margin-top: 5px !important;margin-bottom: 5px !important;border-bottom: 1px solid #DDD;" />
      <el-row>
        <div style="font-size:12px;" v-html="formData.content" />
      </el-row>
      <span slot="footer" class="dialog-footer">
        <el-button @click="viewDialogVisible = false">关 闭</el-button>
      </span>
    </el-drawer>
  </div>
</template>

<script>
import moment from 'moment'
import QuillEditor from '@/components/QuillEditor'
import { mapState } from 'vuex'
import { API_UPLOAD_IMAGE } from '@/constant/api/cms/upload'
import {
  API_CMS_CHANNEL_LIST_SITE
} from '@/constant/api/cms/channel'
import {
  API_CMS_ARTICLE_GET_CHANNEL_TREE,
  API_CMS_ARTICLE_LIST,
  API_CMS_ARTICLE_DISABLED,
  API_CMS_ARTICLE_CREATE,
  API_CMS_ARTICLE_DELETE,
  API_CMS_ARTICLE_UPDATE,
  API_CMS_ARTICLE_GET,
  API_CMS_ARTICLE_DELETE_MORE
} from '@/constant/api/cms/article'
export default {
  middleware: ['authenticated', 'check_permissions'],
  components: {
    QuillEditor
  },
  data() {
    // const _self = this
    return {
      btnLoading: false,
      listLoading: false,
      dialogVisible: false,
      viewDialogVisible: false,
      drawerSeeVisible: false,
      titleSee: '',
      dialogTitle: '',
      action: '',
      userId: '',
      listData: [],
      tableSelectData: [],
      pageData: {
        title: '',
        siteid: '',
        channelId: '',
        channelName: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {},
      treeProps: {
        children: 'children',
        label: 'label'
      },
      sites: [],
      site: {
        id: '',
        site_name: ''
      },
      treeData: [],
      filedomain: '',
      headers: {
        'X-Token': this.$cookies.get('X-Token')
      },
      uploadUrl: process.env.API + API_UPLOAD_IMAGE // 图片上传路径
    }
  },
  computed: {
    ...mapState({
      conf: (state) => state.conf, // 后台配置参数
      siteInfo: (state) => state.siteInfo, // 平台信息
      userInfo: (state) => state.userInfo // 用户信息
    }),
    formRules() {
      const formRules = {
        title: [
          {
            required: true,
            message: '文章标题',
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.filedomain = this.conf.AppFileDomain
      this.uploadurl = process.env.API + API_UPLOAD_IMAGE
      this.loadSite()
    }
  },
  methods: {
    // 加载站点信息
    loadSite() {
      this.$axios.$get(API_CMS_CHANNEL_LIST_SITE).then((d) => {
        if (d.code === 0) {
          this.sites = d.data
          if (this.sites && this.sites.length > 0) {
            this.site.id = this.sites[0].id
            this.site.site_name = this.sites[0].site_name
            this.getChannelTree()
            this.listPage()
          }
        }
      })
    },
    // 切换站点事件
    siteChange(val) {
      this.site.site_name = ''
      if (this.sites && this.sites.length > 0) {
        var index = this.sites.findIndex(obj => obj.id === val)
        this.site.site_name = this.sites[index].site_name
      }
      this.$set(this.pageData, 'channelId', '')
      this.getChannelTree()
      this.listPage()
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
      this.pageData.siteid = this.site.id
      this.$axios.$post(API_CMS_ARTICLE_LIST, this.pageData).then((d) => {
        this.listLoading = false
        if (d.code === 0) {
          this.listData = d.data.list
          this.pageData.totalCount = d.data.totalCount
        }
      })
    },
    // 条件查询展示第一页内容
    doSearch() {
      this.pageData.pageNo = 1
      this.listPage()
    },
    // 全选事件
    tableSelectChange(val) {
      this.tableSelectData = val
    },
    // 行点击选中
    userRowClick(row) {
      // this.$refs['userTable'].toggleRowSelection(row)
    },
    // 获取栏目树数据
    getChannelTree() {
      this.$axios.$get(API_CMS_ARTICLE_GET_CHANNEL_TREE + this.site.id).then((d) => {
        if (d.code === 0) {
          this.treeData = d.data
        }
      })
    },
    // 栏目点击事件
    treeChange(row, node) {
      this.pageData.channelId = row.id
      this.pageData.channelName = row.name
      this.doSearch()
    },
    // 启用禁用
    disabledChange(row) {
      this.$axios.$post(API_CMS_ARTICLE_DISABLED, { id: row.id, disabled: row.disabled }).then((d) => {
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
    handlePicSuccess(response, file, fileList, attrName) {
      if (response.code === 0) {
        var file_url = response.data.url
        this.$set(this.formData, attrName, file_url)
      } else {
        this.$set(this.formData, attrName, '')
      }
    },
    // 打开新增窗口
    openAdd() {
      if (typeof (this.pageData.channelId) === 'undefined' || this.pageData.channelId === '' || this.pageData.channelId === 'root') {
        this.$message({
          message: '未选择栏目',
          type: 'error'
        })
        return false
      }
      if (this.$refs['dialogForm']) { // 表单初始化,消除上次操作残留
        this.$refs['dialogForm'].resetFields()
      }
      this.dialogVisible = true
      this.dialogTitle = '新增文章'
      this.action = 'add'
      this.formData = {
        id: '',
        content: '',
        author: this.userInfo.user.username,
        time: [
          moment().format('x'),
          moment('2099-12-31 23:59:59').format('x')
        ]
      }
    },
    // 根据动作判断如何执行
    doSave() {
      if (this.action === 'add') {
        this.doAdd()
      }
      if (this.action === 'update') {
        this.doUpdate()
      }
    },
    // 执行添加文章操作
    doAdd() {
      this.$refs['dialogForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'content', this.$refs.myQuillEditor.content)
          this.$set(this.formData, 'siteid', this.site.id)
          this.$set(this.formData, 'channelId', this.pageData.channelId)
          this.$set(this.formData, 'time_param', JSON.stringify(this.formData.time))
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_ARTICLE_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.dialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 预览文章
    openSee(row) {
      this.$axios.$get(API_CMS_ARTICLE_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.drawerSeeVisible = true
        }
      })
    },
    // 执行删除
    deleteRow(row) {
      this.$confirm(
        '确定删除 ' + row.title + '？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$delete(API_CMS_ARTICLE_DELETE + row.id).then((d) => {
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
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_CMS_ARTICLE_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.action = 'update'
          this.dialogTitle = '修改文章'
          this.$set(this.formData, 'time', [
            this.formData.publishAt,
            this.formData.endAt
          ])
          if (this.$refs.myQuillEditor) {
            this.$refs.myQuillEditor.content = this.formData.content
          }
          this.dialogVisible = true
        }
      })
    },
    // 提交修改表单
    doUpdate() {
      this.$refs['dialogForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'content', this.$refs.myQuillEditor.content)
          this.$set(this.formData, 'siteid', this.site.id)
          this.$set(this.formData, 'channelId', this.pageData.channelId)
          this.$set(this.formData, 'time_param', JSON.stringify(this.formData.time))
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_ARTICLE_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.dialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 批量删除文章
    deleteMore() {
      var ids = []
      var titles = []
      this.tableSelectData.forEach((obj) => {
        titles.push(obj.title)
        ids.push(obj.id)
      })
      this.$confirm(
        '确定删除 ' + titles.toString() + ' ？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_CMS_ARTICLE_DELETE_MORE, { ids: ids.toString(), names: titles.toString() }).then((d) => {
          if (d.code === 0) {
            this.$message({
              message: d.msg,
              type: 'success'
            })
            this.listPage()
          }
        })
      }).catch(() => {})
    }
  }
}
</script>
<style>
/* 单位树 */
.el-tree-node__label {
    font-size: 12px;
}
.el-tree-node__content:hover {
    background-color: #ebecf0;
}
.el-tree .is-current > div:first-child {
    background-color: #ebecf0;
}
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
}
.demo-table-expand .el-form-item .el-form-item__label {
    font-size: 12px;
}
.demo-table-expand .el-form-item .el-form-item__content {
    font-size: 12px;
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
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
}

.el-upload input[type='file'] {
    display: none;
}

.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 128px;
    height: 128px;
    line-height: 128px;
    text-align: center;
}

.avatar {
    width: 128px;
    height: 128px;
    display: block;
}

.el-dialog__footer {
    padding-top: 50px;
}
</style>
