<template>
    <el-form ref="pwdRef" :model="user" :rules="rules" label-width="80px">
       <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
       </el-form-item>
       <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password />
       </el-form-item>
       <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="user.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
       </el-form-item>
       <el-form-item>
       <el-button type="primary" @click="submit">保存</el-button>
       <el-button type="danger" @click="close">关闭</el-button>
       </el-form-item>
    </el-form>
 </template>
 
<script setup lang="ts">
import { doChangePwd } from "/@/api/platform/home/user"
import { reactive, ref } from "vue"
import { buildValidatorData } from '/@/utils/validate'
import { ElForm } from "element-plus"
import tag from "/@/utils/tag"
import modal from "/@/utils/modal"
 
const pwdRef = ref<InstanceType<typeof ElForm>>() 
const user = reactive({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})
 
const equalToPassword = (rule: any, value: string, callback: any) => {
    if (user.newPassword !== value) {
        callback(new Error("两次输入的密码不一致"))
    } else {
        callback()
    }
}

const rules = ref({
    oldPassword: [{ required: true, message: "旧密码不能为空", trigger: "blur" }],
    newPassword: [{ required: true, message: "新密码不能为空", trigger: "blur" }, buildValidatorData({ name: 'password', title: '新密码' })],
    confirmPassword: [{ required: true, message: "确认密码不能为空", trigger: "blur" }, { required: true, validator: equalToPassword, trigger: "blur" }]
})
 
const submit = () => {
    pwdRef.value?.validate(valid => {
        if (valid) {
            doChangePwd(user.oldPassword, user.newPassword).then((res)=>{
                modal.msgSuccess('修改成功')
                pwdRef.value?.resetFields()
            })
        }
    })
}

const close = () => {
    tag.closePage()
}
</script>
 