<template>
  <div>
    <el-card shadow="never">
      <template slot="header">
        <el-breadcrumb>
          <el-breadcrumb-item>个人资料</el-breadcrumb-item>
        </el-breadcrumb>
      </template>
      <el-tabs v-model="activeName">
        <el-tab-pane label="基础信息" name="base">
          <el-row>
            <el-col :xs="24" :sm="21" :md="18" :lg="15" :xl="12">
              <el-form
                ref="infoForm"
                :model="sysUserInfoData"
                :rules="infoRules"
                label-width="120px"
              >
                <el-form-item
                  prop="mobile"
                  label="手机号码"
                >
                  <el-input
                    v-model="sysUserInfoData.mobile"
                    autocomplete="off"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item
                  prop="username"
                  label="姓名"
                >
                  <el-input
                    v-model="sysUserInfoData.username"
                    autocomplete="off"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item
                  prop="email"
                  label="EMail"
                >
                  <el-input
                    v-model="sysUserInfoData.email"
                    autocomplete="off"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="submit"
                    :loading="btnLoadding"
                    @click="doChangeInfo"
                  >保存</el-button>
                </el-form-item></el-form>
            </el-col></el-row>
        </el-tab-pane>
        <el-tab-pane
          label="头像"
          name="avatar"
        >
          <el-row>
            <el-col :xs="24" :sm="21" :md="18" :lg="15" :xl="12">
              <el-form size="medium" label-width="100px">
                <el-form-item>
                  <el-avatar
                    shape="square"
                    :size="120"
                    :src="avatar"
                  />
                </el-form-item>
                <el-form-item>
                  <el-upload
                    :action="uploadUrl"
                    :headers="headers"
                    name="Filedata"
                    limit:1
                    :before-upload="beforeFileUpload"
                    :show-file-list="false"
                    :on-success="handleUploadSuccess"
                  >
                    <el-button
                      icon="el-icon-upload"
                      size="small"
                    >上传头像</el-button>
                  </el-upload>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="pwd">
          <el-row>
            <el-col :xs="24" :sm="21" :md="18" :lg="15" :xl="12">
              <el-form
                ref="pwdForm"
                :model="sysUserPwdData"
                :rules="pwdRules"
                label-width="125px"
              >
                <el-form-item
                  prop="oldPassword"
                  label="旧密码"
                >
                  <el-input
                    v-model="sysUserPwdData.oldPassword"
                    tabindex="1"
                    type="password"
                    autocomplete="off"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item
                  prop="newPassword"
                  label="新密码"
                  label-width="125px"
                >
                  <el-input
                    v-model="sysUserPwdData.newPassword"
                    tabindex="2"
                    type="password"
                    autocomplete="off"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item
                  prop="passwordAgain"
                  label="再输一遍新密码"
                  label-width="125px"
                >
                  <el-input
                    v-model="sysUserPwdData.passwordAgain"
                    autocomplete="off"
                    tabindex="3"
                    type="password"
                    style="width:250px;"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="submit"
                    :loading="btnLoadding"
                    @click="doChangePwd"
                  >保存</el-button>
                </el-form-item></el-form>
            </el-col>
          </el-row>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { checkMobile } from '@/utils/validate'
import { API_UPLOAD_IMAGE } from '@/constant/api/platform/pub/upload'
import {
  API_HOME_USER_CHANGE_PWD,
  API_HOME_USER_CHANGE_INFO,
  API_HOME_USER_SET_AVATAR
} from '@/constant/api/home/home'
export default {
  data() {
    return {
      btnLoadding: false,
      activeName: 'base',
      btnLoading: false,
      sysUserInfoData: {},
      sysUserPwdData: {},
      headers: {
        'wk-user-token': this.$cookies.get('wk-user-token')
      },
      uploadUrl: process.env.API + API_UPLOAD_IMAGE, // 图片上传路径
      avatar: ''
    }
  },
  computed: {
    ...mapState({
      conf: (state) => state.conf, // 后台配置参数
      siteInfo: (state) => state.siteInfo, // 平台信息
      userInfo: (state) => state.userInfo // 用户信息
    }),
    infoRules() {
      const infoRules = {
        username: [
          {
            required: true,
            message: '姓名不可为空',
            trigger: 'blur'
          }
        ],
        email: [
          {
            required: false,
            type: 'email',
            message: 'EMail不可为空',
            trigger: 'blur'
          }
        ],
        mobile: [{
          required: true,
          message: '手机号码不可为空',
          trigger: 'blur'
        }, { validator: this.validateMobile, trigger: ['blur', 'change'] }]
      }
      return infoRules
    },
    pwdRules() {
      const pwdRules = {
        oldPassword: [
          {
            required: true,
            message: '请输入旧密码',
            trigger: 'blur'
          }
        ],
        newPassword: [
          {
            required: true,
            message: '请输入新密码',
            trigger: 'blur'
          }
        ],
        passwordAgain: [
          {
            required: true,
            message: '请再输一遍新密码',
            trigger: 'blur'
          },
          { validator: this.validatePwdAgain, trigger: ['blur', 'change'] }
        ]
      }
      return pwdRules
    }
  },
  created() {
    this.initUserInfo()
  },
  methods: {
    // 手机号码验证
    validateMobile(rule, value, callback) {
      if (value && !checkMobile(value)) {
        return callback(
          new Error('手机号码不正确')
        )
      } else {
        callback()
      }
    },
    // 密码验证
    validatePwdAgain(rule, value, callback) {
      if (value !== this.sysUserPwdData.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    // 初始化用户信息
    initUserInfo() {
      this.sysUserInfoData = JSON.parse(
        JSON.stringify(this.userInfo.user)
      )
      if (this.userInfo.user.avatar) {
        this.avatar =
                    this.conf.AppFileDomain + this.userInfo.user.avatar
      }
      if (this.$route.query.tab) {
        this.activeName = this.$route.query.tab
      }
    },
    // 提交修改用户信息
    doChangeInfo() {
      this.$refs['infoForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_HOME_USER_CHANGE_INFO, this.sysUserInfoData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
                this.dialogUserInfoVisible = false
              }
            })
        }
      })
    },
    // 提交修改密码
    doChangePwd() {
      this.$refs['pwdForm'].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          this.$axios
            .$post(API_HOME_USER_CHANGE_PWD, this.sysUserPwdData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
                this.dialogUserPwdVisible = false
              }
            })
        }
      })
    },
    // 文件上传成功后进行保存
    handleUploadSuccess(resp, file) {
      var file_url = resp.data.url
      this.$axios
        .$post(API_HOME_USER_SET_AVATAR, { avatar: file_url })
        .then((d) => {
          if (d.code === 0) {
            this.$message.success(d.msg)
            this.avatar = this.conf.AppFileDomain + file_url
          }
        })
    },
    // 上传之前判断文件格式及大小
    beforeFileUpload(file) {
      var isJPG = file.type === 'image/jpeg'
      var isPNG = file.type === 'image/png'
      if (!isJPG && !isPNG) {
        this.$message.error('头像图片格式不正确')
        return false
      }
      var isLt500k = file.size / 1024 <= 500
      if (!isLt500k) {
        this.$message.error('头像图片需小于500KB')
        return false
      }
      return true
    }
  }
}
</script>
