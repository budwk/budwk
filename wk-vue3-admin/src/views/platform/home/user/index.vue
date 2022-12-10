<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="6" :xs="24">
                <el-card class="box-card">
                    <template v-slot:header>
                        <div class="clearfix">
                            <span>个人资料</span>
                        </div>
                    </template>
                    <div>
                        <div class="text-center">
                            <userAvatar v-model="formData.avatar" />
                        </div>
                        <ul class="list-group list-group-striped">
                            <li class="list-group-item">
                                <svg-icon icon-class="user" /> 用户名称
                                <div class="pull-right">{{ formData.username }}</div>
                            </li>
                            <li class="list-group-item">
                                <svg-icon icon-class="phone" /> 手机号码
                                <div class="pull-right">{{ formData.mobile }}</div>
                            </li>
                            <li class="list-group-item">
                                <svg-icon icon-class="email" /> 用户邮箱
                                <div class="pull-right">{{ formData.email }}</div>
                            </li>
                            <li class="list-group-item">
                                <svg-icon icon-class="tree" /> 所属部门
                                <div class="pull-right">
                                    <span v-if="formData.post">{{ formData.post.name }} / </span>
                                    <span v-if="formData.unit">{{ formData.unit.name }}</span>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <svg-icon icon-class="peoples" /> 所属角色
                                <div class="pull-right">{{ roles.toString() }}</div>
                            </li>
                            <li class="list-group-item">
                                <svg-icon icon-class="date" /> 创建时间
                                <div class="pull-right">{{ formatTime(formData.createdAt) }}</div>
                            </li>
                        </ul>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="18" :xs="24">
                <el-card>
                    <template v-slot:header>
                        <div class="clearfix">
                            <span>用户信息</span>
                        </div>
                    </template>
                    <el-tabs v-model="activeTab">
                        <el-tab-pane label="操作日志" name="userLog">
                            <el-tag type="info">仅显示最近两个月</el-tag>
                            <userLog />
                        </el-tab-pane>
                        <el-tab-pane label="修改资料" name="userinfo">
                            <el-form ref="userRef" :model="formData" :rules="formRules" label-width="80px">
                                <el-form-item label="用户姓名" prop="username">
                                    <el-input v-model="formData.username" placeholder="请输入用户姓名" />
                                </el-form-item>
                                <el-form-item label="Email" prop="email">
                                    <el-input v-model="formData.email" placeholder="请输入Email" />
                                </el-form-item>
                                <el-form-item label="手机号码" prop="mobile">
                                    <el-input v-model="formData.mobile" placeholder="请输入手机号码" />
                                </el-form-item>
                                <el-form-item label="性别" prop="sex">
                                    <el-radio-group v-model="formData.sex">
                                        <el-radio :label="1">男</el-radio>
                                        <el-radio :label="2">女</el-radio>
                                        <el-radio :label="0">未知</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="submit">保存</el-button>
                                    <el-button type="danger" @click="close">关闭</el-button>
                                </el-form-item>
                            </el-form>
                        </el-tab-pane>
                        <el-tab-pane label="修改密码" name="resetPwd">
                            <resetPwd />
                        </el-tab-pane>
                    </el-tabs>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>    
<script setup lang="ts" name="platform-home-user">
import resetPwd from './resetPwd.vue'
import userAvatar from './userAvatar.vue'
import userLog from './userLog.vue'
import { onMounted, ref } from 'vue'
import { getUserInfo, doChangeInfo } from "/@/api/platform/home/user"
import { formatTime } from '/@/utils/common'
import { buildValidatorData } from '/@/utils/validate'
import { ElForm } from 'element-plus'
import { useUserInfo } from '/@/stores/userInfo'

import tag from "/@/utils/tag"
import modal from "/@/utils/modal"

const activeTab = ref('userLog')
const userInfo = useUserInfo()

const userRef = ref<InstanceType<typeof ElForm>>()
const roles = ref('')

const formData = ref({
    avatar: '',
    username: '',
    loginname: '',
    email: '',
    mobile: '',
    unit: {},
    post: '',
    sex: 0,
    roles: []
})
const formRules = ref({
    username: [{ required: true, message: "用户姓名不能为空", trigger: "blur" }],
    email: [buildValidatorData({ name: 'email', title: '电子邮箱' })],
    mobile: [buildValidatorData({ name: 'mobile' })]
})
const getUser = () => {
    getUserInfo().then((res) => {
        formData.value = res.data
        if (formData.value.roles) {
            roles.value = formData.value.roles.map(item => item.name) as string
        }
    })
}


const submit = () => {
    userRef.value?.validate(valid => {
        if (valid) {
            doChangeInfo(formData.value).then((res) => {
                modal.msgSuccess('修改成功')
                userInfo.user.username = formData.value.username
            })
        }
    })
}

const close = () => {
    tag.closePage()
}

onMounted(() => {
    getUser()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>