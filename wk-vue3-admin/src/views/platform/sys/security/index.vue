<template>
    <div class="app-container">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button plain type="primary" @click="save" v-permission="['sys.config.security.save']" :loading="btnLoading">
                    <i class="fa fa-save" style="padding-right:5px;"/>保 存
                </el-button>
            </el-col>
        </el-row>
        <el-form ref="saveFormRef" :model="formData" label-width="320px">
            <el-card class="box-card">
                <template #header>
                    <div class="card-header">
                        <span>账户安全配置</span>
                    </div>
                </template>
                <div class="text item">
                    <el-form-item label="是否启用账户安全配置">
                        <el-switch v-model="formData.hasEnabled" />
                    </el-form-item>
                </div>
            </el-card>

            <el-card class="box-card" v-if="formData.hasEnabled">
                <template #header>
                    <div class="card-header">
                        <span>用户登录会话</span>
                    </div>
                </template>
                <div class="text item">
                    <el-form-item label="是否启用用户单一登录">
                        <el-switch v-model="formData.userSessionOnlyOne" />
                        <span style="color: #bbbbbb; padding-left: 20px">注：启用后用户登录会将其他会话踢下线,建议开启(WebSokcet Redis Key会减少一些)</span>
                    </el-form-item>
                </div>
            </el-card>

            <el-card class="box-card" v-if="formData.hasEnabled">
                <template #header>
                    <div class="card-header">
                        <span>密码规则配置</span>
                    </div>
                </template>
                <div class="text item">
                    <el-col :span="24">
                        <el-form-item label="密码最小长度">
                            <el-input-number v-model="formData.pwdLengthMin" :min="1" :max="50" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="密码最大长度">
                            <el-input-number v-model="formData.pwdLengthMax" :min="1" :max="50" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="密码字符要求">
                            <el-select v-model="formData.pwdCharMust" placeholder="密码字符要求" style="min-width: 320px">
                                <el-option label="无要求" :value="0" />
                                <el-option label="必须同时包含字母和数字" :value="1" />
                                <el-option label="必须同时包含大写字母、小写字母、数字" :value="2" />
                                <el-option label="必须同时包含大写、小写字母、特殊字符、数字" :value="3" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="密码中不能包含的用户信息">
                            <el-checkbox-group v-model="pwdCharNot">
                                <el-checkbox label="loginname">用户名</el-checkbox>
                                <el-checkbox label="email">电子邮箱</el-checkbox>
                                <el-checkbox label="mobile">手机号吗</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="是否开启密码重复性检查">
                            <el-switch v-model="formData.pwdRepeatCheck" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="重复性检查记录数">
                            <el-input-number v-model="formData.pwdRepeatNum" :disabled="!formData.pwdRepeatCheck"
                                :min="0" :max="99" />
                            <span style="color: #bbbbbb; padding-left: 20px">注：重复性检查记录数设置为空或0时则不检查</span>
                        </el-form-item>
                    </el-col>
                </div>
            </el-card>

            <el-card class="box-card" v-if="formData.hasEnabled">
                <template #header>
                    <div class="card-header">
                        <span>密码校检配置</span>
                    </div>
                </template>
                <div class="text item">

                    <el-col :span="24">
                        <el-form-item label="一天内密码错误次数超过最大重试次数锁定账号">
                            <el-switch v-model="formData.pwdRetryLock" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="密码错误最大重试次数">
                            <el-input-number v-model="formData.pwdRetryNum" :disabled="!formData.pwdRetryLock" :min="0"
                                :max="99" />
                            <span style="color: #bbbbbb; padding-left: 20px">注：登录密码最大校检次数不设置或设置为0则不进行校验</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="超过密码错误最大重试次数处理方式">
                            <el-select v-model="formData.pwdRetryAction" :disabled="!formData.pwdRetryLock"
                                placeholder="处理方式" style="min-width: 200px">
                                <el-option label="不处理" :value="0" />
                                <el-option label="锁定账户" :value="1" />
                                <el-option label="指定时间内禁止登录" :value="2" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="禁止登录时长(单位:秒)">
                            <el-input-number v-model="formData.pwdRetryTime"
                                :disabled="!formData.pwdRetryLock || 2 != formData.pwdRetryAction" :min="0"
                                :max="25920000" />
                            <span style="color: #bbbbbb; padding-left: 20px">注：账户锁定时间设置为0或为空时则不锁定，最长锁定时间为100天</span>
                        </el-form-item>
                    </el-col>
                </div>
            </el-card>

            <el-card class="box-card" v-if="formData.hasEnabled">
                <template #header>
                    <div class="card-header">
                        <span>密码变更通知</span>
                    </div>
                </template>
                <div class="text item">
                    <el-row>
                        <el-col :span="24">
                            <el-form-item label="指定密码过期时间(单位:天)">
                                <el-input-number v-model="formData.pwdTimeoutDay" :min="0" :max="10000000" />
                                <span style="color: #bbbbbb; padding-left: 20px">注：指定密码时间设置为0或空时将不过期</span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="后台重置密码后下次登录是否强制修改密码">
                                <el-switch v-model="formData.pwdResetChange" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
            </el-card>

            <el-card class="box-card" v-if="formData.hasEnabled">
                <template #header>
                    <div class="card-header">
                        <span>防攻击配置</span>
                    </div>
                </template>
                <div class="text item">
                    <el-row>
                        <el-col :span="24">
                            <el-form-item label="用户名/手机号超过最大输错次数锁定IP">
                                <el-switch v-model="formData.nameRetryLock" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="用户名/手机号最大重试次数">
                                <el-input-number v-model="formData.nameRetryNum" :disabled="!formData.nameRetryLock"
                                    :min="0" :max="99" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="IP禁止登录时长(单位:秒)">
                                <el-input-number v-model="formData.nameTimeout" :disabled="!formData.nameRetryLock"
                                    :min="0" :max="25920000" />
                            </el-form-item>
                        </el-col>

                    </el-row>
                </div>
            </el-card>

            <el-card class="box-card">
                <template #header>
                    <div class="card-header">
                        <span>登录验证码配置</span>
                    </div>
                </template>
                <div class="text item">
                    <el-col :span="24">
                        <el-form-item label="是否启用登录验证码">
                            <el-switch v-model="formData.captchaHasEnabled" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="formData.captchaHasEnabled">
                        <el-form-item label="验证码类型">
                            <el-select v-model="formData.captchaType" placeholder="验证码类型" style="min-width: 320px">
                                <el-option label="数学题" :value="0" />
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
                    </el-col>
                </div>
            </el-card>
        </el-form>
    </div>
</template>
<script setup lang="ts" name="platform-sys-security">
import { onMounted, reactive, ref } from 'vue'
import { getInfo, doSave } from '/@/api/platform/sys/security'
import { toRefs } from '@vueuse/core'
import { ElForm, ElInput } from 'element-plus'
import modal from '/@/utils/modal'

const saveFormRef = ref<InstanceType<typeof ElForm>>()
const pwdCharNot = ref([])
const btnLoading = ref(false)

const data = reactive({
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
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: '',
        pageOrderBy: ''
    },
    formRules: {
    },
})

const { formData, formRules } = toRefs(data)


const save = () => {
    if (!saveFormRef.value) return
    saveFormRef.value.validate((valid) => {
        if (valid) {
            btnLoading.value = true
            formData.value.pwdCharNot = pwdCharNot.value.toString()
            doSave(formData.value).then((res: any) => {
                btnLoading.value = false
                modal.msgSuccess(res.msg)
            })
        }
    })
}

const getData = () => {
    getInfo().then((res)=>{
        formData.value = res.data
        if (formData.value.pwdCharNot) {
            pwdCharNot.value = formData.value.pwdCharNot.split(',') as never
        }
    })
}

onMounted(() => {
    getData()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.box-card {
    margin-bottom: 15px;
}
</style>