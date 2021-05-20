<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>系统参数</span>
      <div class="platform-list-op">
        <el-button
          v-permission="'sys.config.param.create'"
          size="small"
          type="primary"
          @click="openAdd"
        >
          新增参数
        </el-button>
      </div>
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
              <el-form-item label="所属应用">
                <el-select
                  v-model="pageData.appId"
                  placeholder="所属应用"
                  size="medium"
                  style="width:100%;"
                  :clearable="true"
                  @change="appChange"
                >
                  <template v-for="item in apps">
                    <el-option
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </template>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="9">
              <el-form-item label="参数名称">
                <el-input
                  v-model="pageData.configKey"
                  placeholder="参数名称"
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
            @sort-change="doPageSort"
          >
            <el-table-column
              v-if="!pageData.appId"
              prop="appId"
              label="所属应用"
              sortable
            />
            <el-table-column
              prop="configKey"
              label="参数Key"
              sortable
            />
            <el-table-column
              prop="configValue"
              label="参数值"
              sortable
            />
            <el-table-column
              prop="opened"
              label="是否开放"
              header-align="center"
              align="center"
            >
              <template slot-scope="scope">
                <span v-if="scope.row.opened">是</span>
                <span v-else>否</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="note"
              label="参数说明"
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
                  v-permission="'sys.config.param.update'"
                  type="text"
                  size="small"
                  @click.native.prevent="
                    openUpdate(scope.row)
                  "
                >
                  修改
                </el-button>
                <el-button
                  v-permission="'sys.config.param.delete'"
                  type="text"
                  size="small"
                  class="button-delete-color"
                  :disabled="
                    scope.row.configKey.indexOf('App') === 0
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
      title="新增参数"
      :visible.sync="addDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="addForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="appId" label="所属应用">
          <el-select
            v-model="formData.appId"
            placeholder="所属应用"
            size="mini"
          >
            <template v-for="item in apps">
              <el-option
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item prop="type" label="参数类型">
          <el-radio-group v-model="formData.type" @change="formTypeChange">
            <el-radio v-for="item in types" :key="item.value" :label="item.value">{{ item.text }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          prop="configKey"
          label="参数Key"
        >
          <el-input
            v-model="formData.configKey"
            maxlength="100"
            placeholder="参数Key"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='TEXT'"
          prop="configValue"
          label="参数值"
        >
          <el-input
            v-model="formData.configValue"
            maxlength="100"
            placeholder="参数值"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='BOOL'"
          prop="configValue"
          label="参数值"
        >
          <el-radio-group v-model="formData.configValue">
            <el-radio label="true">是</el-radio>
            <el-radio label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="formData.type==='IMAGE'"
          prop="configValue"
          label="参数值"
        >
          <el-upload
            :action="uploadUrl"
            name="Filedata"
            list-type="picture-card"
            limit:1
            :before-upload="beforeFileUpload"
            :show-file-list="false"
            :on-success="
              (resp, file) => {
                return handleUploadSuccess(resp, file)
              }
            "
          >
            <img
              v-if="formData.configValue"
              :src="conf['AppFileDomain'] + formData.configValue"
              class="avatar"
            >
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="opened" label="是否开放">
          <el-switch
            v-model="formData.opened"
            :active-value="true"
            :inactive-value="false"
            active-color="#2476e0"
            inactive-color="#999"
          />
        </el-form-item>
        <el-form-item
          prop="note"
          label="参数说明"
        >
          <el-input v-model="formData.note" type="textarea" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button
          size="small"
          type="primary"
          :loading="btnLoading"
          @click="doAdd"
        >确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="修改参数"
      :visible.sync="updateDialogVisible"
      :close-on-click-modal="false"
      width="50%"
    >
      <el-form
        ref="editForm"
        :model="formData"
        :rules="formRules"
        size="small"
        label-width="120px"
      >
        <el-form-item prop="appId" label="所属应用">
          <el-select
            v-model="formData.appId"
            placeholder="所属应用"
            size="mini"
          >
            <template v-for="item in apps">
              <el-option
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </template>
          </el-select>
        </el-form-item>
        <el-form-item prop="type" label="参数类型">
          <el-radio-group v-model="formData.type" @change="formTypeChange">
            <el-radio v-for="item in types" :key="item.value" :label="item.value">{{ item.text }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          prop="configKey"
          label="参数Key"
        >
          <el-input
            v-model="formData.configKey"
            maxlength="100"
            placeholder="参数Key"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='TEXT'"
          prop="configValue"
          label="参数值"
        >
          <el-input
            v-model="formData.configValue"
            maxlength="100"
            placeholder="参数值"
            auto-complete="off"
            tabindex="2"
            type="text"
          />
        </el-form-item>
        <el-form-item
          v-if="formData.type==='BOOL'"
          prop="configValue"
          label="参数值"
        >
          <el-radio-group v-model="formData.configValue">
            <el-radio label="true">是</el-radio>
            <el-radio label="false">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="formData.type==='IMAGE'"
          prop="configValue"
          label="参数值"
        >
          <el-upload
            :action="uploadUrl"
            name="Filedata"
            list-type="picture-card"
            limit:1
            :before-upload="beforeFileUpload"
            :show-file-list="false"
            :on-success="
              (resp, file) => {
                return handleUploadSuccess(resp, file)
              }
            "
          >
            <img
              v-if="formData.configValue"
              :src="conf['AppFileDomain'] + formData.configValue"
              class="avatar"
            >
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item prop="opened" label="是否开放">
          <el-switch
            v-model="formData.opened"
            :active-value="true"
            :inactive-value="false"
            active-color="#2476e0"
            inactive-color="#999"
          />
        </el-form-item>
        <el-form-item
          prop="note"
          label="参数说明"
        >
          <el-input v-model="formData.note" type="textarea" />
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
import {
  API_SYS_PARAM_LIST,
  API_SYS_PARAM_GET,
  API_SYS_PARAM_CREATE,
  API_SYS_PARAM_DELETE,
  API_SYS_PARAM_UPDATE,
  API_SYS_PARAM_DATA
} from '@/constant/api/platform/sys/param'
import { API_UPLOAD_IMAGE } from '@/constant/api/platform/pub/upload'
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
        appId: 'COMMON',
        configKey: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      },
      formData: {
      },
      uploadUrl: process.env.API + API_UPLOAD_IMAGE // 图片上传路径
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
        ],
        type: [
          {
            required: true,
            message: '参数类型',
            trigger: 'blur'
          }
        ],
        configKey: [
          {
            required: true,
            message: '参数Key',
            trigger: 'blur'
          }
        ],
        configValue: [
          {
            required: true,
            message: '参数值',
            trigger: 'blur'
          }
        ]
      }
      return formRules
    }
  },
  created() {
    if (process.browser) {
      this.initData()
      this.listPage()
    }
  },
  methods: {
    // 打开字新增窗口
    openAdd() {
      this.formData = {
        appId: this.pageData.appId,
        type: 'TEXT'
      }
      this.addDialogVisible = true
    },
    // 提交表单
    doAdd() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_PARAM_CREATE, this.formData)
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
    // 打开修改窗口
    openUpdate(row) {
      this.$axios.$get(API_SYS_PARAM_GET + row.id).then((d) => {
        if (d.code === 0) {
          this.formData = d.data
          this.$set(this.formData, 'type', d.data.type.value)
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
            .$post(API_SYS_PARAM_UPDATE, this.formData)
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
          .$delete(API_SYS_PARAM_DELETE + row.id)
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
      this.$axios.$post(API_SYS_PARAM_LIST, this.pageData).then((res) => {
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
        appId: 'COMMON',
        configKey: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
      }
      this.$refs['searchForm'].resetFields()
    },
    appChange() {
      this.doSearch()
    },
    formTypeChange(val) {
      if (val === 'BOOL') {
        this.$set(this.formData, 'configValue', 'true')
      } else {
        this.$set(this.formData, 'configValue', '')
      }
    },
    // 文件上传成功后进行保存
    handleUploadSuccess(resp, file) {
      if (resp.code !== 0) {
        this.$message.error(resp.msg)
        return
      }
      this.$set(this.formData, 'configValue', resp.data.url)
    },
    // 上传之前判断文件格式及大小
    beforeFileUpload(file) {
      var isJPG = file.type === 'image/jpeg'
      var isPNG = file.type === 'image/png'
      var isICO = file.type === 'image/x-icon'
      if (!isJPG && !isPNG && !isICO) {
        this.$message.error('文件类型不正确')
        return false
      }
      var isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('文件需小于2M')
        return false
      }
      return true
    },
    initData() {
      this.$axios.$get(API_SYS_PARAM_DATA).then((res) => {
        if (res.code === 0) {
          this.apps = res.data.apps
          this.types = res.data.types
        }
      })
    }
  }
}
</script>
<style scoped>
.avatar {
    width: 145px;
    height: 145px;
    display: block;
    padding: 2px 2px;
    border-radius: 6px;
}
</style>
