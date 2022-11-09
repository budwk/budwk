<template>
  <div class="login-container">
    <div class="login-border">
      <el-row type="flex">
        <el-col :span="12" class="login-left">
          <img
            src="/assets/images/banner.jpg"
            style="height: 485px"
          >
        </el-col>

        <el-col>
          <div class="login-main">
            <el-row style="height: 70px;text-align: center;">
              <span class="title" style="margin-top: -20px;line-height: 65px;vertical-align: top;">
                {{ siteInfo.appTitle }}
              </span>
              <span style="font-size: 11px;">
                {{ siteInfo.appVersion }}
              </span>
            </el-row>
            <el-tabs v-model="activeName" style="max-height: 275px;">
              <el-tab-pane label="密码登录" name="loginByPassword">
                <span slot="label">
                  <i slot="prefix" class>
                    <i class="el-icon-user" />
                  </i>
                  用户登录
                </span>
                <el-form
                  ref="loginForm"
                  :model="loginForm"
                  :rules="loginRules"
                  class="login-form"
                  auto-complete="on"
                  label-position="left"
                >
                  <el-form-item prop="loginname">
                    <el-input
                      v-model="loginForm.loginname"
                      type="text"
                      auto-complete="off"
                      placeholder="用户名"
                      prefix-icon="el-icon-user"
                    />
                  </el-form-item>
                  <el-form-item prop="password">
                    <el-input
                      v-model="loginForm.password"
                      type="password"
                      autocomplete="new-password"
                      placeholder="登录密码"
                      prefix-icon="el-icon-lock"
                      @keyup.enter.native="handleLogin"
                    />
                  </el-form-item>
                  <el-form-item prop="captchaCode">
                    <el-input
                      v-model="loginForm.captchaCode"
                      auto-complete="off"
                      placeholder="验证码"
                      style="width: 50%"
                      prefix-icon="el-icon-key"
                      @keyup.enter.native="handleLogin"
                    />
                    <div class="login-code">
                      <img :src="captchaCodeImg" @click="getCaptchaCode">
                    </div>
                  </el-form-item>
                  <el-form-item style="width: 100%;margin-bottom: 5px;">
                    <el-button
                      :loading="btnLoading"
                      size="medium"
                      type="primary"
                      style="width: 100%"
                      @click.native.prevent="handleLogin"
                    >
                      <span v-if="!btnLoading">登 录</span>
                      <span v-else>登 录 中...</span>
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
              <el-tab-pane label="短信登录" name="loginBySmscode">
                <span slot="label">
                  <i slot="prefix" class>
                    <i class="el-icon-mobile-phone" />
                  </i>
                  短信登录
                </span>

                <el-form
                  ref="smsLoginForm"
                  class="login-form"
                  status-icon
                  :model="loginForm"
                  :rules="loginBySmsRules"
                  label-width="0"
                >
                  <el-form-item prop="mobile">
                    <el-input
                      v-model="loginForm.mobile"
                      auto-complete="off"
                      placeholder="手机号码"
                      prefix-icon="el-icon-mobile-phone"
                      @keyup.enter.native="handleLogin"
                    />
                  </el-form-item>
                  <el-form-item prop="smscode">
                    <el-input
                      v-model="loginForm.smscode"
                      auto-complete="off"
                      placeholder="短信验证码"
                      prefix-icon="el-icon-key"
                      @keyup.enter.native="handleLogin"
                    >
                      <template slot="append">
                        <span
                          style="cursor: pointer;"
                          class="msg-text"
                          :class="[{ display: msgKey }]"
                          @click="handleSend"
                        >
                          {{ msgText }}
                        </span>
                      </template>
                    </el-input>
                  </el-form-item>
                  <el-form-item style="margin-bottom: 5px;">
                    <el-button
                      :loading="btnLoading"
                      size="medium"
                      type="primary"
                      style="width: 100%"
                      @click.native.prevent="handleLogin"
                    >
                      <span v-if="!btnLoading">登 录</span>
                      <span v-else>登 录 中...</span>
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
            </el-tabs>
            <div class="get-pwd" align="right">
              <el-button type="text" @click="showSetPwd">忘记密码</el-button>
            </div>
            <div class="third-login">
              <el-divider>第三方登录</el-divider>
              <div class="third-way">
                <span class="third-icon" @click="handleSocial()">
                  <img src="/assets/images/wechat.svg" style="width: 32px;height: 32px;">
                </span>
                <span class="third-icon" @click="handleSocial()">
                  <img src="/assets/images/qq.svg" style="width: 32px;height: 32px;">
                </span>
                <span class="third-icon" @click="handleSocial()">
                  <img src="/assets/images/alipay.svg" style="width: 32px;height: 32px;">
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div id="el-login-footer" class="login-footer">
      <span>
        <a href="https://budwk.com" target="_blank">budwk.com</a> ©
        <span>{{ year }}</span>
      </span>
      <span>⋅</span>
      <span>{{ siteInfo.appCopyright }}</span>
    </div>

    <el-dialog
      title="重置密码"
      :visible.sync="pwdVisible"
      :close-on-click-modal="false"
      :show-close="true"
      width="30%"
    >
      <el-row>
        <el-form ref="pwdForm" :model="pwdFormData" :rules="pwdRules" size="small" label-width="100px">
          <el-form-item prop="loginname" label="用户名">
            <el-input
              ref="pwdFormLoginname"
              v-model="pwdFormData.loginname"
              size="small"
              placeholder="请输入用户名"
              auto-complete="off"
              tabindex="1"
              style="width:220px;"
            />
          </el-form-item>
          <el-form-item
            v-if="pwdStep>=2"
            prop="type"
            label="验证方式"
            :rules="{ required: true, message: '请选择验证方式', trigger: ['blur','change']}"
          >
            <el-radio-group
              v-model="pwdFormData.type"
              tabindex="2"
            >
              <el-radio v-for="obj in pwdType" :key="obj.key" :label="obj.key">{{ obj.val }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            v-if="pwdStep==3"
            prop="password"
            label="新密码"
            :rules="[{ required: true, message: '请输入新密码', trigger: ['blur','change']},{pattern: /^(?!([a-zA-Z\d]*|[\d!@#\$%_\.]*|[a-zA-Z!@#\$%_\.]*)$)[a-zA-Z\d!@#\$%_\.]{8,}$/,message:'密码需8位以上并且包含数字、字母、特殊字符', trigger: ['blur','change']}]"
          >
            <el-input
              v-model="pwdFormData.password"
              :type="passwordType"
              auto-complete="off"
              placeholder="请输入新密码"
            >
              <i
                slot="suffix"
                class="ti-eye"
                @click="showPasswordType"
              />
            </el-input>
          </el-form-item>
          <el-form-item
            v-if="pwdStep==3"
            prop="code"
            label="验证码"
            :rules="{ required: true, message: '请输入验证码', trigger: ['blur','change']}"
          >
            <el-input
              v-model="pwdFormData.code"
              size="small"
              placeholder="验证码"
              auto-complete="off"
              tabindex="1"
            />
          </el-form-item>
          <el-form-item v-if="pwdMsg" style="color: red">{{ pwdMsg }}</el-form-item>
        </el-form>
        <el-button :loading="pwdBtnLoading" style="margin-top: 10px;float: right;" type="primary" size="small" @click="doPwd">
          <span v-if="pwdStep==1">验证账号</span>
          <span v-if="pwdStep==2">发送验证码</span>
          <span v-if="pwdStep==3">保存新密码</span>
        </el-button>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { API_AUTH_LOGIN, API_AUTH_CAPTCHA, API_AUTH_SMSCODE, API_AUTH_CHECK_LOGINNAME, API_AUTH_PWD_SENDCODE, API_AUTH_PWD_SAVE } from '@/constant/api/ucenter/auth'
import { mapState } from 'vuex'
const MSGINIT = '发送验证码'
const MSGSCUCCESS = '{time}秒后重发'
const MSGTIME = 60
const MOBILE_REGEX = /^1[3456789]\d{9}$/
export default {
  layout: 'login',
  middleware: 'notAuthenticated',
  data() {
    return {
      activeName: 'loginByPassword',
      msgText: MSGINIT,
      msgTime: MSGTIME,
      msgKey: false,
      year: 2021,
      captchaCodeImg: '',
      loginRules: {
        loginname: [
          {
            required: true,
            message: '用户名不可为空',
            trigger: 'blur'
          }
        ],
        password: [
          {
            required: true,
            message: '密码不可为空',
            trigger: 'blur'
          }
        ],
        captchaCode: [
          {
            required: true,
            message: '验证码不可为空',
            trigger: 'blur'
          },
          {
            pattern: /[\w](?:[\w-]*[\w])?/, message: '请輸入4位验证码', trigger: 'blur'
          }
        ]
      },
      loginBySmsRules: {
        mobile: [
          {
            required: true,
            message: '手机号码不可为空',
            trigger: 'blur'
          },
          {
            pattern: MOBILE_REGEX, message: '请輸入正确的手机号码', trigger: 'blur'
          }
        ],
        smscode: [
          {
            required: true,
            message: '短信验证码不可为空',
            trigger: 'blur'
          },
          {
            pattern: /^\d{4}$/, message: '请輸入4位短信验证码', trigger: 'blur'
          }
        ]
      },
      loginForm: {
        loginname: 'superadmin',
        password: '1',
        mobile: '',
        smscode: '',
        captchaKey: '',
        captchaCode: '',
        type: 'password',
        appId: ''
      },
      btnLoading: false,
      pwdFormData: {
        loginname: '',
        type: '',
        password: '',
        code: ''
      },
      pwdType: [],
      pwdRules: {
        loginname: [
          { required: true, message: '请输入用户名', trigger: ['blur', 'change'] }
        ]
      },
      passwordType: 'password',
      pwdVisible: false,
      pwdStep: 1,
      pwdMsg: '',
      pwdBtnLoading: false
    }
  },
  computed: {
    ...mapState(['siteInfo'])
  },
  watch: {
    activeName(newVal, oldVal) {
      if (this.$refs['loginForm']) { this.$refs['loginForm'].resetFields() }
      if (this.$refs['smsLoginForm']) { this.$refs['smsLoginForm'].resetFields() }
      if (newVal === 'loginByPassword') {
        this.getCaptchaCode()
      }
    }
  },
  created() {
    this.year = new Date().getFullYear()
    this.getCaptchaCode()
    this.loginForm.appId = this.siteInfo.appId
  },
  methods: {
    handleSocial() {
      this.$message.warning('敬请期待')
    },
    async handleLogin() {
      try {
        var formName = 'loginForm'
        if (this.activeName === 'loginBySmscode') {
          formName = 'smsLoginForm'
        }
        await this.$refs[formName].validate()
        this.btnLoading = true
        const { data, code } = await this.$axios.$post(
          API_AUTH_LOGIN,
          this.loginForm
        )
        this.btnLoading = false
        if (code !== 0) {
          this.getCaptchaCode()
          return
        }
        // 登录成功后设置cookie值,但不设置state.auth 这样才可以再进入系统后获取菜单及用户信息
        this.$cookies.set('wk-user-token', data)
        this.$cookies.set('appId', this.siteInfo.appId)
        this.$cookies.set('appPath', this.siteInfo.appPath)
        window.location.href = this.siteInfo.appPath
      } catch (e) {
        this.btnLoading = false
      }
    },
    async handleSend() {
      if (!this.checkMobile(this.loginForm.mobile)) {
        this.$message({
          message: '请输入手机号码',
          type: 'error'
        })
        return false
      }
      const { code } = await this.$axios.$post(
        API_AUTH_SMSCODE,
        { mobile: this.loginForm.mobile }
      )
      if (code === 0) {
        this.$message({
          message: '短信发送成功，请注意查收',
          type: 'success',
          duration: 5000
        })
        this.msgText = MSGSCUCCESS.replace('{time}', this.msgTime)
        this.msgKey = true
        var time = setInterval(() => {
          this.msgTime--
          this.msgText = MSGSCUCCESS.replace('{time}', this.msgTime)
          if (this.msgTime === 0) {
            this.msgTime = MSGTIME
            this.msgText = MSGINIT
            this.msgKey = false
            clearInterval(time)
          }
        }, 1000)
      }
    },
    checkMobile(str) {
      return MOBILE_REGEX.test(str)
    },
    async getCaptchaCode() {
      const { data, code } = await this.$axios.$get(API_AUTH_CAPTCHA)
      if (code === 0) {
        this.loginForm.captchaKey = data.key
        this.captchaCodeImg = data.code
      }
    },
    showSetPwd() {
      this.pwdVisible = true
      this.pwdStep = 1
      this.$nextTick(() => {
        // 重置表单
        this.$refs['pwdForm'].resetFields()
        // 设置焦点
        this.$refs['pwdFormLoginname'].$el.querySelector('input').focus()
      })
    },
    doPwd() {
      if (this.pwdStep === 1) {
        this.checkUserInfo()
      }
      if (this.pwdStep === 2) {
        this.sendCaptchaCode()
      }
      if (this.pwdStep === 3) {
        this.setNewPassword()
      }
    },
    async checkUserInfo() {
      await this.$refs['pwdForm'].validate()
      this.pwdBtnLoading = true
      const { data, code } = await this.$axios.$post(
        API_AUTH_CHECK_LOGINNAME,
        { loginname: this.pwdFormData.loginname }
      )
      this.pwdBtnLoading = false
      if (code !== 0) {
        return
      }
      this.pwdType = data
      this.pwdStep = 2
    },
    async sendCaptchaCode() {
      await this.$refs['pwdForm'].validate()
      this.pwdBtnLoading = true
      const { msg, code } = await this.$axios.$post(
        API_AUTH_PWD_SENDCODE,
        this.pwdFormData
      )
      this.pwdBtnLoading = false
      if (code !== 0) {
        return
      }
      this.pwdMsg = msg
      this.pwdStep = 3
    },
    async setNewPassword() {
      await this.$refs['pwdForm'].validate()
      this.pwdBtnLoading = true
      const { code } = await this.$axios.$post(
        API_AUTH_PWD_SAVE,
        this.pwdFormData
      )
      this.pwdBtnLoading = false
      if (code !== 0) {
        return
      }
      this.pwdVisible = false
      this.pwdMsg = ''
      this.pwdStep = 1
      this.$message({
        message: '密码重置成功，请刷新页面重新登录！',
        type: 'success',
        duration: 5000
      })
    },
    showPasswordType() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
    }
  }
}
</script>
<style scoped>
    #app {
        height: 100%;
    }

    html body {
        position: relative;
        height: 100vh;
        padding: 0;
        margin: 0;
        font-family: Avenir, Helvetica, Arial, sans-serif;
        font-size: 14px;
        color: #2c3e50;
        background: #f6f8f9;
        -webkit-font-smoothing: antialiased;
    }

    .login-container {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 100vh;
        background-image: url(/assets/images/login_bg.svg);
        background-color: #f0f2f5;
    }

    @media screen and (max-width: 768px) {
        .login-left {
            display: none;
        }
    }

    a {
        color: #1890ff;
        text-decoration: none;
        cursor: pointer;
    }

    .login-main {
        width: 380px;
        height: 485px;
        padding: 15px 25px 15px 25px;
        background: #ffffff;
    }

    .title {
        margin: 10px auto 20px auto;
        font-size: 20px;
        font-weight: 700;
        color: rgba(0, 0, 0, 0.85);
        text-align: center;
    }

    .login-form .el-input {
        height: 38px;
    }

    .login-form input {
        height: 38px;
    }

    .login-form .input-icon {
        width: 14px;
        height: 39px;
        margin-left: 2px;
    }

    .login-tip {
        font-size: 13px;
        color: #bfbfbf;
        text-align: center;
    }

    .login-code {
        display: inline-block;
        float: right;
        width: 45%;
        height: 38px;
    }

    .login-code img {
        vertical-align: middle;
        cursor: pointer;
    }

    .login-footer {
        position: fixed;
        bottom: 1rem;
        width: 100%;
        height: 1rem;
        font-size: 0.9rem;
        font-weight: 600;
        line-height: 1rem;
        color: #2c3e50;
        text-align: center;
    }

    .tips {
        margin-bottom: 10px;
        font-size: 14px;
        color: #fff;
    }

    .get-pwd {
        float: right;
        display: inline-block;
        text-align: right;
        flex-direction: row;
        flex-wrap: wrap;
        vertical-align: middle;
    }

    .get-pwd button{
        padding-left: 10px;
        font-size: 12px;
    }

    .third-login {
        margin-top: 50px;
        flex-direction: row;
        flex-wrap: wrap;
        vertical-align: middle;
    }

    .third-login .third-icon {
        margin: 0 20px;
        cursor: pointer;
        fill: rgba(0, 0, 0, 0.2);
    }

    .third-way {
        display: flex;
        justify-content: center;
    }

    .third-icon:hover {
        fill: rebeccapurple;
    }
</style>
