<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>链接管理</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'cms.links.link.create'"
          size="small"
          type="primary"
          @click="openCreate"
        >
          新建链接
        </el-button>
        <el-button
          v-permission="'cms.links.link.delete'"
          size="small"
          type="danger"
          :disabled="tableSelectData.length === 0"
          @click="deleteMore"
        >
          删除链接
        </el-button>
      </div>
    </h4>
    <div class="platform-content-info">
      <div class="platform-content-search">
        <el-form
          :inline="true"
          class="platform-content-search-form"
        >
          <el-form-item label="所属分类">
            <el-select
              v-model="pageData.classId"
              placeholder="所属分类"
              size="medium"
              @change="classChange"
            >
              <el-option
                v-for="item in linkClasses"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="platform-content-list">
        <div class="platform-content-list-table">
          <el-table
            v-loading="listLoading"
            :data="listData"
            stripe
            @sort-change="doPageSort"
            @selection-change="tableSelectChange"
          >
            <el-table-column type="selection" width="50" />

            <el-table-column
              prop="name"
              label="链接名称"
              sortable
            />

            <el-table-column
              label="链接类型"
              header-align="left"
              prop="type"
              :show-overflow-tooltip="true"
            >
              <template slot-scope="scope">
                <span v-if="'txt'==scope.row.type">文本</span>
                <span v-if="'img'==scope.row.type">图片</span>
              </template>
            </el-table-column>
            <el-table-column
              label="URL"
              header-align="left"
              prop="url"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              label="打开方式"
              header-align="left"
              prop="target"
              :show-overflow-tooltip="true"
            />
            <el-table-column
              fixed="right"
              header-align="center"
              align="center"
              label="操作"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="'cms.links.link.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="openUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'cms.links.link.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  @click.native.prevent="openDelete(scope.row)"
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
      title="新建链接"
      :visible.sync="createDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="createForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item
          prop="classId"
          label="所属分类"
        >
          {{ formData.className }}
        </el-form-item>
        <el-form-item
          prop="name"
          label="链接名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="链接名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="type" label="链接类型">
          <el-radio-group v-model="formData.type" @change="typeChange">
            <el-radio label="txt">文本</el-radio>
            <el-radio label="img">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="'img'===formData.type" label="上传图片">
          <el-upload
            class="avatar-uploader"
            tabindex="5"
            :action="uploadUrl"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :on-success="function(resp,file,fileList){return handleImageSuccess(resp,file,fileList,'picurl')}"
          >
            <img v-if="formData.picurl" :src="conf.AppFileDomain+formData.picurl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="url" label="URL">
          <el-input
            v-model="formData.url"
            maxlength="100"
            placeholder="http://"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="target" label="打开方式">
          <el-radio-group v-model="formData.target">
            <el-radio label="_blank">新窗口</el-radio>
            <el-radio label="_self">本窗口</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="createDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doCreate"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改链接"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
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
          label="链接名称"
        >
          <el-input
            v-model="formData.name"
            maxlength="100"
            placeholder="链接名称"
            auto-complete="off"
            tabindex="1"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="type" label="链接类型">
          <el-radio-group v-model="formData.type" @change="typeChange">
            <el-radio label="txt">文本</el-radio>
            <el-radio label="img">图片</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="'img'===formData.type" label="上传图片">
          <el-upload
            class="avatar-uploader"
            tabindex="5"
            :action="uploadUrl"
            :headers="headers"
            name="Filedata"
            :show-file-list="false"
            :on-success="function(resp,file,fileList){return handleImageSuccess(resp,file,fileList,'picUrl')}"
          >
            <img v-if="formData.picUrl" :src="conf.AppFileDomain+formData.picUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="url" label="URL">
          <el-input
            v-model="formData.url"
            maxlength="100"
            placeholder="http://"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item prop="target" label="打开方式">
          <el-radio-group v-model="formData.target">
            <el-radio label="_blank">新窗口</el-radio>
            <el-radio label="_self">本窗口</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="updateDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
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
import { API_UPLOAD_IMAGE } from '@/constant/api/cms/upload'
import {
  API_CMS_LINK_LIST,
  API_CMS_LINK_CREATE,
  API_CMS_LINK_DELETE,
  API_CMS_LINK_UPDATE,
  API_CMS_LINK_GET,
  API_CMS_LINK_LIST_CLASS,
  API_CMS_LINK_DELETE_MORE
} from '@/constant/api/cms/link'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      createDialogVisible: false,
      updateDialogVisible: false,
      listData: [],
      linkClasses: [],
      tableSelectData: [],
      pageData: {
        classId: '',
        className: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
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
    // 表单验证,写在computed里切换多语言才会更新
    formRules() {
      const formRules = {
        name: [
          {
            required: true,
            message: '链接名称',
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.loadLinkClass()
    }
  },
  methods: {
    // 加载分类信息
    loadLinkClass() {
      this.$axios.$get(API_CMS_LINK_LIST_CLASS).then((d) => {
        if (d.code === 0) {
          this.linkClasses = d.data
          if (this.linkClasses && this.linkClasses.length > 0) {
            this.pageData.classId = this.linkClasses[0].id
            this.pageData.className = this.linkClasses[0].name
            this.listPage()
          }
        }
      })
    },
    // 分类变化事件
    classChange(val) {
      this.pageData.classId = val
      if (this.linkClasses && this.linkClasses.length > 0) {
        var index = this.linkClasses.findIndex(obj => obj.id === val)
        this.$set(this.pageData, 'className', this.linkClasses[index].name)
      }
      this.listPage()
    },
    // 类型变化了要清除数据
    typeChange(val) {
      if (val === 'txt') {
        this.formData.picurl = ''
      }
    },
    // 全选事件
    tableSelectChange(val) {
      this.tableSelectData = val
    },
    // 文件上传成功
    handleImageSuccess(response, file, fileList, attrName) {
      if (response.code === 0) {
        var file_url = response.data.url
        this.$set(this.formData, attrName, file_url)
      } else {
        this.$set(this.formData, attrName, '')
      }
    },
    // 打开字新增窗口
    openCreate() {
      this.formData = {
        classId: this.pageData.classId,
        className: this.pageData.className,
        type: 'txt',
        target: '_blank'
      }
      this.createDialogVisible = true
    },
    // 提交表单
    doCreate() {
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_LINK_CREATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.createDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_CMS_LINK_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.updateDialogVisible = true
        }
      })
    },
    // 提交表单
    doUpdate() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_CMS_LINK_UPDATE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message({
                  message: d.msg,
                  type: 'success'
                })
                this.updateDialogVisible = false
                this.listPage()
              }
            })
        }
      })
    },
    // 执行删除
    openDelete(row) {
      this.$confirm(
        '确定删除 ' + row.name + '？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.btnLoading = true
        this.$axios
          .$delete(API_CMS_LINK_DELETE + row.id)
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
    // 批量删除文章
    deleteMore() {
      var ids = []
      var names = []
      this.tableSelectData.forEach((obj) => {
        names.push(obj.name)
        ids.push(obj.id)
      })
      this.$confirm(
        '确定删除 ' + names.toString() + ' ？',
        '操作提示',
        {
          confirmButtonText: '确 定',
          cancelButtonText: '取 消',
          type: 'warning'
        }
      ).then(() => {
        this.$axios.$post(API_CMS_LINK_DELETE_MORE, { ids: ids.toString(), names: names.toString() }).then((d) => {
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
      this.$axios.$post(API_CMS_LINK_LIST, this.pageData).then((res) => {
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
</style>
