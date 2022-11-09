<template>
  <div class="platform-content">
    <h4 class="platform-content-title">
      <span>账户安全配置</span>
    </h4>
    <div class="platform-content-info">
      <el-form ref="saveForm" :model="formData" label-width="320px">
        <el-form-item label="是否启用账户安全配置">
          <el-switch v-model="formData.hasEnabled" />
        </el-form-item>
        <el-row v-if="formData.hasEnabled">
          <el-form-item label="是否启用用户单一登录">
            <el-switch v-model="formData.userSessionOnlyOne" />
            <span style="color:#BBBBBB;padding-left:20px;">注：启用后用户登录会将其他会话踢下线</span>
          </el-form-item>
          <el-row><h4 class="platform-content-title">密码规则配置</h4></el-row>
          <el-form-item label="密码最小长度">
            <el-input-number v-model="formData.pwdLengthMin" size="small" :min="1" :max="50" />
          </el-form-item>
          <el-form-item label="密码最大长度">
            <el-input-number v-model="formData.pwdLengthMax" size="small" :min="1" :max="50" />
          </el-form-item>
          <el-form-item label="密码字符要求">
            <el-select v-model="formData.pwdCharMust" size="small" placeholder="密码字符要求" style="min-width:320px;">
              <el-option label="无要求" :value="0" />
              <el-option label="必须同时包含字母和数字" :value="1" />
              <el-option label="必须同时包含大写字母、小写字母、数字" :value="2" />
              <el-option label="必须同时包含大写、小写字母、特殊字符、数字" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="密码中不能包含的用户信息">
            <el-checkbox-group v-model="pwdCharNot">
              <el-checkbox label="loginname">用户名</el-checkbox>
              <el-checkbox label="email">电子邮箱</el-checkbox>
              <el-checkbox label="mobile">手机号吗</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="是否开启密码重复性检查">
            <el-switch v-model="formData.pwdRepeatCheck" />
          </el-form-item>
          <el-form-item label="重复性检查记录数">
            <el-input-number v-model="formData.pwdRepeatNum" :disabled="!formData.pwdRepeatCheck" size="small" :min="0" :max="99" />
            <span style="color:#BBBBBB;padding-left:20px;">注：重复性检查记录数设置为空或0时则不检查</span>
          </el-form-item>
          <el-row><h4 class="platform-content-title">密码校检配置</h4></el-row>
          <el-form-item label="一天内密码错误次数超过最大重试次数锁定账号">
            <el-switch v-model="formData.pwdRetryLock" />
          </el-form-item>
          <el-form-item label="密码错误最大重试次数">
            <el-input-number v-model="formData.pwdRetryNum" :disabled="!formData.pwdRetryLock" size="small" :min="0" :max="99" />
            <span style="color:#BBBBBB;padding-left:20px;">注：登录密码最大校检次数不设置或设置为0则不进行校验</span>
          </el-form-item>
          <el-form-item label="超过密码错误最大重试次数处理方式">
            <el-select v-model="formData.pwdRetryAction" :disabled="!formData.pwdRetryLock" size="small" placeholder="处理方式" style="min-width:200px;">
              <el-option label="不处理" :value="0" />
              <el-option label="锁定账户" :value="1" />
              <el-option label="指定时间内禁止登录" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="禁止登录时长(单位:秒)">
            <el-input-number v-model="formData.pwdRetryTime" :disabled="!formData.pwdRetryLock||2!=formData.pwdRetryAction" size="small" :min="0" :max="25920000" />
            <span style="color:#BBBBBB;padding-left:20px;">注：账户锁定时间设置为0或为空时则不锁定，最长锁定时间为100天</span>
          </el-form-item>
          <el-row><h4 class="platform-content-title">密码变更通知</h4></el-row>
          <el-form-item label="指定密码过期时间(单位:天)">
            <el-input-number v-model="formData.pwdTimeoutDay" size="small" :min="0" :max="10000000" />
            <span style="color:#BBBBBB;padding-left:20px;">注：指定密码时间设置为0或空时将不过期</span>
          </el-form-item>
          <el-form-item label="后台重置密码后下次登录是否强制修改密码">
            <el-switch v-model="formData.pwdResetChange" />
          </el-form-item>
          <el-row><h4 class="platform-content-title">用户名/手机号输错锁定IP</h4></el-row>
          <el-form-item label="超过最大输错次数锁定IP">
            <el-switch v-model="formData.nameRetryLock" />
          </el-form-item>
          <el-form-item label="用户名/手机号最大重试次数">
            <el-input-number v-model="formData.nameRetryNum" :disabled="!formData.nameRetryLock" size="small" :min="0" :max="99" />
          </el-form-item>
          <el-form-item label="IP禁止登录时长(单位:秒)">
            <el-input-number v-model="formData.nameTimeout" :disabled="!formData.nameRetryLock" size="small" :min="0" :max="25920000" />
          </el-form-item>
        </el-row>
        <el-row><h4 class="platform-content-title">登录验证码配置</h4></el-row>
        <el-form-item label="是否启用登录验证码">
          <el-switch v-model="formData.captchaHasEnabled" />
        </el-form-item>
        <el-row v-if="formData.captchaHasEnabled">
          <el-form-item label="验证码类型">
            <el-select v-model="formData.captchaType" size="small" placeholder="验证码类型" style="min-width:320px;">
              <el-option label="小学数学题" :value="0" />
              <el-option label="四位纯数字" :value="1" />
              <el-option label="四位纯字母" :value="2" />
              <el-option label="四位数字字母" :value="3" />
              <el-option label="四位汉字" :value="4" />
              <el-option label="两位汉字" :value="5" />
              <el-option label="四位纯数字(动态图片)" :value="11" />
              <el-option label="四位纯字母(动态图片)" :value="22" />
              <el-option label="四位数字字母(动态图片)" :value="33" />
              <el-option label="四位汉字(动态图片)" :value="44" />
              <el-option label="两位汉字(动态图片)" :value="55" />
            </el-select>
          </el-form-item>
        </el-row>
        <el-form-item>
          <el-button v-permission="'sys.config.security.save'" type="primary" size="small" @click="doSave">保 存</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import {
  API_SYS_SECURITY_GET,
  API_SYS_SECURITY_SAVE
} from '@/constant/api/platform/sys/security'
export default {
  middleware: ['authenticated', 'check_permissions'],
  data() {
    return {
      loading: true,
      btnLoading: false,
      listLoading: false,
      formData: {
        hasEnabled: false,
        pwdLengthMin: 6,
        pwdLengthMax: 20,
        pwdCharMust: 0,
        pwdCharNot: '',
        pwdRepeatCheck: false,
        pwdRepeatNum: 0,
        pwdRetryLock: false,
        pwdRetryNum: 0,
        pwdRetryAction: 0,
        pwdRetryTime: 0,
        pwdTimeoutDay: 0,
        pwdResetChange: false,
        nameRetryLock: false,
        nameRetryNum: 3,
        nameTimeout: 60,
        userSessionOnlyOne: false,
        captchaHasEnabled: false,
        captchaType: 0
      },
      pwdCharNot: []
    }
  }, computed: {
    ...mapState({
      conf: state => state.conf // 后台配置参数
    })
  },
  created() {
    if (process.browser) {
      this.initData()
    }
  },
  methods: {
    doSave() {
      this.$refs['saveForm'].validate((valid) => {
        if (valid) {
          this.$set(this.formData, 'pwdCharNot', this.pwdCharNot.toString())
          this.btnLoading = true
          this.$axios
            .$post(API_SYS_SECURITY_SAVE, this.formData)
            .then((d) => {
              this.btnLoading = false
              if (d.code === 0) {
                this.$message.success(d.msg)
              }
            })
        }
      })
    },
    initData() {
      this.$axios.$get(API_SYS_SECURITY_GET).then((res) => {
        if (res.code === 0 && res.data) {
          this.formData = res.data
          if (this.formData.pwdCharNot) {
            this.pwdCharNot = this.formData.pwdCharNot.split(',')
          }
        }
      })
    }
  }
}
</script>
