<template>
    <div class="login-container">
        <div class="login-border">
            <el-row>
                <el-col :span="12" class="login-left">
                    <img src="~assets/images/banner.jpg" style="height: 485px;width: 100%;">
                </el-col>

                <el-col :span="12">
                    <div class="login-main">
                        <el-row style="height: 60px;text-align: center;">
                            <span class="title" style="margin-top: -5px;line-height: 65px;vertical-align: top;">
                                {{ platformInfo.AppName }}
                            </span>
                            <span style="font-size: 11px;">
                                {{ platformInfo.AppVersion }}
                            </span>
                        </el-row>
                        <el-tabs v-model="tabActiveName" style="max-height: 275px;">
                            <el-tab-pane label="用户登录" name="loginByPassword">
                                <el-form @keyup.enter="handleLogin(loginFormRef)" ref="loginFormRef" :rules="loginRules"
                                    :model="loginData" class="login-form" auto-complete="on" label-position="left">
                                    <el-form-item prop="loginname">
                                        <el-input ref="loginnameRef" v-model="loginData.loginname" type="text"
                                            placeholder="用户名">
                                            <template #prefix>
                                                <icon name="fa fa-user" class="form-item-icon" size="16"
                                                    color="var(--el-input-icon-color)" />
                                            </template>
                                        </el-input>

                                    </el-form-item>
                                    <el-form-item prop="password">
                                        <el-input ref="passwordRef" v-model="loginData.password" type="password"
                                            autocomplete="new-password" placeholder="登录密码" show-password>
                                            <template #prefix>
                                                <icon name="fa fa-unlock-alt" class="form-item-icon" size="16"
                                                    color="var(--el-input-icon-color)" />
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                    <el-form-item v-if="loginData.captchaHasEnabled" prop="captchaCode">
                                        <el-input ref="captchaRef" v-model="loginData.captchaCode" auto-complete="off"
                                            placeholder="验证码" style="width: 50%" clearable>
                                            <template #prefix>
                                                <icon name="fa fa-key" class="form-item-icon" size="16"
                                                    color="var(--el-input-icon-color)" />
                                            </template>
                                        </el-input>
                                        <div class="login-code">
                                            <img :src="captchaData.captchaCodeImg" @click="getCaptchaCode">
                                        </div>
                                    </el-form-item>
                                    <el-form-item style="width: 100%;margin-bottom: 5px;">
                                        <el-button :loading="loginData.btnLoading" type="primary" style="width: 100%"
                                            @click="handleLogin(loginFormRef)"
                                            >
                                            <span v-if="!loginData.btnLoading">登 录</span>
                                            <span v-else>登 录 中...</span>
                                        </el-button>
                                    </el-form-item>
                                </el-form>
                            </el-tab-pane>
                            <el-tab-pane label="短信登录" name="loginBySmscode">
                                <el-form @keyup.enter="handleLogin(smsLoginFormRef)" ref="smsLoginFormRef"
                                    class="login-form" status-icon :model="loginData" :rules="loginBySmsRules"
                                    label-width="0">
                                    <el-form-item prop="mobile">
                                        <el-input ref="mobileRef" v-model="loginData.mobile" auto-complete="off"
                                            placeholder="手机号码">
                                            <template #prefix>
                                                <icon name="fa fa-phone" class="form-item-icon" size="16"
                                                    color="var(--el-input-icon-color)" />
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                    <el-form-item prop="smscode">
                                        <el-input ref="smscodeRef" v-model="loginData.smscode" auto-complete="off"
                                            placeholder="短信验证码">
                                            <template #prefix>
                                                <icon name="fa fa-key" class="form-item-icon" size="16"
                                                    color="var(--el-input-icon-color)" />
                                            </template>
                                            <template #append>
                                                <span style="cursor: pointer;" class="msg-text"
                                                    :class="[{ display: smsData.msgKey }]" @click="handleSmsSend">
                                                    {{ smsData.msgText }}
                                                </span>
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                    <el-form-item style="margin-bottom: 5px;">
                                        <el-button :loading="loginData.btnLoading" type="primary"
                                            @click="handleLogin(smsLoginFormRef)" style="width: 100%">
                                            <span v-if="!loginData.btnLoading">登 录</span>
                                            <span v-else>登 录 中...</span>
                                        </el-button>
                                    </el-form-item>
                                </el-form>
                            </el-tab-pane>
                        </el-tabs>
                        <div class="get-pwd">
                            <el-button type="primary" link @click="showSetPwd">忘记密码</el-button>
                        </div>
                        <div class="third-login">
                            <el-divider>第三方登录</el-divider>
                            <div class="third-way">
                                <span class="third-icon" @click="handleSocial()">
                                    <img src="~assets/images/wechat.svg" style="width: 32px;height: 32px;">
                                </span>
                                <span class="third-icon" @click="handleSocial()">
                                    <img src="~assets/images/qq.svg" style="width: 32px;height: 32px;">
                                </span>
                                <span class="third-icon" @click="handleSocial()">
                                    <img src="~assets/images/alipay.svg" style="width: 32px;height: 32px;">
                                </span>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div id="el-login-footer" class="login-footer">
            <span>
                <a href="https://budwk.com" target="_blank">BudWk.com</a> ©
                <span>{{year}}</span>
            </span>
        </div>
        <el-dialog title="重置密码" v-model="pwdVisible" :close-on-click-modal="false" :show-close="true" width="35%">
            <el-row>
                <el-form ref="pwdFormRef" :model="pwdFormData" :rules="pwdRules" label-width="100px">
                    <el-form-item prop="loginname" label="用户名">
                        <el-input ref="pwdFormLoginnameRef" v-model="pwdFormData.loginname" placeholder="请输入用户名"
                            auto-complete="off" tabindex="1" style="width:220px;" />
                    </el-form-item>
                    <el-form-item v-if="pwdStep>=2" prop="type" label="验证方式"
                        :rules="{ required: true, message: '请选择验证方式', trigger: ['blur','change']}">
                        <el-radio-group v-model="pwdFormData.type" tabindex="2">
                            <el-radio v-for="obj in pwdType" :key="obj.key" :label="obj.key">{{ obj.val }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item v-if="pwdStep==3" prop="password" label="新密码"
                        :rules="[{ required: true, message: '请输入新密码', trigger: ['blur','change']},buildValidatorData({ name: 'password' })]">
                        <el-input v-model="pwdFormData.password" auto-complete="off" placeholder="请输入新密码" show-password>
                        </el-input>
                    </el-form-item>
                    <el-form-item v-if="pwdStep==3" prop="code" label="验证码"
                        :rules="{ required: true, message: '请输入验证码', trigger: ['blur','change']}">
                        <el-input v-model="pwdFormData.code" placeholder="验证码" auto-complete="off" />
                    </el-form-item>
                    <el-form-item v-if="pwdFormData.pwdMsg" style="color: red">{{ pwdFormData.pwdMsg }}</el-form-item>
                </el-form>
            </el-row>

            <template #footer>
                <el-button :loading="pwdFormData.pwdBtnLoading" type="primary" @click="doPwd(pwdFormRef)">
                    <span v-if="pwdStep==1">验证账号</span>
                    <span v-if="pwdStep==2">发送验证码</span>
                    <span v-if="pwdStep==3">保存新密码</span>
                </el-button>
            </template>
        </el-dialog>
    </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, nextTick, watch, onBeforeMount } from 'vue'
import { ElForm, ElInput, ElMessage, ElNotification } from 'element-plus'
import router from '/@/router'
import { useRoute } from 'vue-router'
import JSencrypt from 'jsencrypt'
import { buildValidatorData, checkMobile } from '/@/utils/validate'
import { usePlatformInfo } from '/@/stores/platformInfo'
import { useUserInfo } from '/@/stores/userInfo'
import { getCaptcha, getRsa, getSmsCode, doLogin, checkLoginname, sendResetPwdCode, saveNewPwd } from '/@/api/ucenter/auth'
import { cloneDeep } from 'lodash'

const year = new Date().getFullYear()
const platformInfo = usePlatformInfo()
const tabActiveName = ref("loginByPassword")
const { query } = useRoute()
const { redirect } = query

const MSGINIT = '发送验证码'
const MSGSCUCCESS = '{time}秒后重发'
let MSGTIME = 60
let rsaPublicKey = ''

let pwdVisible = ref(false)
let pwdStep = ref(1)
let pwdType = ref({})

const loginFormRef = ref<InstanceType<typeof ElForm>>()
const loginnameRef = ref<InstanceType<typeof ElInput>>()
const passwordRef = ref<InstanceType<typeof ElInput>>()
const captchaRef = ref<InstanceType<typeof ElInput>>()
const mobileRef = ref<InstanceType<typeof ElInput>>()
const smscodeRef = ref<InstanceType<typeof ElInput>>()
const smsLoginFormRef = ref<InstanceType<typeof ElForm>>()
const pwdFormRef = ref<InstanceType<typeof ElForm>>()
const pwdFormLoginnameRef = ref<InstanceType<typeof ElInput>>()
const loginData = reactive({
    loginname: '',
    password: '',
    captchaCode: '',
    keep: false,
    captchaKey: '',
    captchaHasEnabled: true,
    smscode: '',
    mobile: '',
    rsaKey: '',
    appId: platformInfo.AppDefault,
    type: 'password',
    btnLoading: false
})
const captchaData = reactive({
    captchaCodeImg: ''
})
const smsData = reactive({
    msgText: MSGINIT,
    msgKey: false,
    msgTime: MSGTIME
})
const pwdFormData = reactive({
    loginname: '',
    type: '',
    password: '',
    code: '',
    pwdMsg: '',
    pwdBtnLoading: false
})

watch(tabActiveName, (n, o) => {
    if ('loginByPassword' === n) {
        loginData.type = 'password'
        getCaptchaCode()
    } else {
        loginData.type = 'mobile'
    }
})

const loginRules = reactive({
    loginname: [buildValidatorData({ name: 'required', message: '请输入用户名' }), buildValidatorData({ name: 'account' })],
    password: [buildValidatorData({ name: 'required', message: '请输入登录密码' })],
    captchaCode: [
        buildValidatorData({ name: 'required', title: '请输入验证码' }),
        {
            min: 1,
            max: 6,
            message: '验证码长度 1-6位',
            trigger: 'blur',
        },
    ]
})

const loginBySmsRules = reactive({
    mobile: [buildValidatorData({ name: 'required', message: '请输入手机号码' }), buildValidatorData({ name: 'mobile' })],
    smscode: [
        buildValidatorData({ name: 'required', title: '请输入短信验证码' }),
        {
            pattern: /^\d{4}$/, message: '请輸入4位短信验证码', trigger: 'blur'
        },
    ]
})

const pwdRules = reactive({
    loginname: [buildValidatorData({ name: 'required', message: '请输入用户名' }), buildValidatorData({ name: 'account' })]
})

onMounted(() => {
    getCaptchaCode()
    initRsaInfo()
    initDemoInfo()
    focusInput()
})

//初始化演示环境账号&密码
const initDemoInfo = () => {
    if (platformInfo.AppDemoEnv) {
        loginData.loginname = 'superadmin'
        loginData.password = '1'
    }
}

//获取RSA加密需要的密钥信息
const initRsaInfo = () => {
    getRsa().then((res) => {
        loginData.rsaKey = res.data.rsaKey
        rsaPublicKey = res.data.rsaPublicKey
    })
}

//获取图形验证码
const getCaptchaCode = () => {
    getCaptcha().then((res) => {
        loginData.captchaKey = res.data.key
        captchaData.captchaCodeImg = res.data.code
        loginData.captchaHasEnabled = res.data.captchaHasEnabled
    })
}

//表单焦点切换
const focusInput = () => {
    if (loginData.loginname === '') {
        loginnameRef.value!.focus()
    } else if (loginData.password === '') {
        passwordRef.value!.focus()
    } else if (loginData.captchaCode === '') {
        captchaRef.value!.focus()
    }
}

//提交登录
const handleLogin = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            const formParams = cloneDeep(loginData)
            const encrypt = new JSencrypt()
            encrypt.setPublicKey(rsaPublicKey)
            const newpwd = '' + encrypt.encrypt(loginData.password)
            formParams.password = newpwd
            loginData.btnLoading = true
            doLogin(formParams).then((res) => {
                loginData.btnLoading = false
                const userInfo = useUserInfo()
                userInfo.setToken(res.data)
                setTimeout(()=>{
                    ElMessage.success('登录成功！') 
                },300)
                router.push({ path: redirect?.toString() || "/platform/dashboard" });
            }).catch(() => {
                loginData.captchaCode = ''
                getCaptchaCode()
                focusInput()
                loginData.btnLoading = false
            })
        } else {
            return false
        }
    })
}

//发送短信验证码
const handleSmsSend = () => {
    if (!checkMobile(loginData.mobile)) {
        ElNotification.error('手机号码不正确')
        mobileRef.value!.focus()
        return
    }
    getSmsCode(loginData.mobile.toString()).then((res) => {
        ElNotification.success('短信发送成功，请注意查收')
        loginData.smscode = ''
        smscodeRef.value!.focus()
        smsData.msgText = MSGSCUCCESS.replace('{time}', MSGTIME.toString())
        smsData.msgKey = true
        smsData.msgTime = MSGTIME
        var time = setInterval(() => {
            smsData.msgTime--
            smsData.msgText = MSGSCUCCESS.replace('{time}', smsData.msgTime.toString())
            if (smsData.msgTime === 0) {
                smsData.msgTime = MSGTIME
                smsData.msgText = MSGINIT
                smsData.msgKey = false
                clearInterval(time)
            }
        }, 1000)
    })
}

//弹出重置密码框
const showSetPwd = () => {
    pwdVisible.value = true
    pwdStep.value = 1
    nextTick(() => {
        pwdFormData.loginname = ''
        pwdFormLoginnameRef.value!.focus()
    })
}

//找回密码分三步处理
const doPwd = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (pwdStep.value === 1) {
        checkUserInfo(formEl)
    }
    if (pwdStep.value === 2) {
        sendCaptchaCode(formEl)
    }
    if (pwdStep.value === 3) {
        setNewPassword(formEl)
    }
}

//核验账号
const checkUserInfo = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            pwdFormData.pwdBtnLoading = true
            checkLoginname(pwdFormData.loginname).then((res) => {
                pwdFormData.pwdBtnLoading = false
                pwdStep.value = 2
                pwdType.value = res.data
            }).catch(() => {
                pwdFormData.pwdBtnLoading = false
            })
        }
    })
}

//发送验证码(手机短信或Email)
const sendCaptchaCode = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            pwdFormData.pwdBtnLoading = true
            sendResetPwdCode(pwdFormData).then((res) => {
                pwdFormData.pwdBtnLoading = false
                pwdFormData.pwdMsg = res.msg
                pwdStep.value = 3
            }).catch(() => {
                pwdFormData.pwdBtnLoading = false
            })
        }
    })
}

//提交新密码
const setNewPassword = (formEl: InstanceType<typeof ElForm> | undefined) => {
    if (!formEl) return
    formEl.validate((valid) => {
        if (valid) {
            pwdFormData.pwdBtnLoading = true
            saveNewPwd(pwdFormData).then((res) => {
                pwdFormData.pwdBtnLoading = false
                pwdFormData.pwdMsg = ''
                pwdVisible.value = false
                ElNotification.success('密码重置成功，请刷新页面重新登录！')
            }).catch(() => {
                pwdFormData.pwdBtnLoading = false
            })
        }
    })
}

const handleSocial = () => {
    ElNotification.success('订购功能 QQ: wizzer 微信: wizzer')
}
</script>
<style scoped lang="scss">
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

.form-item-icon {
    height: auto;
    width: 16px;
}

.login-container {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    background-image: url(/@/assets/images/login_bg.svg);
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
    width: 45%;
    height: 38px;
    padding-left: 10px;
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

.get-pwd button {
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