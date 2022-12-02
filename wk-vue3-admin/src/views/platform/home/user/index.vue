<template>
    <div class="app-container">
        <el-row :gutter="20">
         <el-col :span="6" :xs="24">
            <el-card class="box-card">
               <template v-slot:header>
                 <div class="clearfix">
                   <span>个人信息</span>
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
<script setup lang="ts">
import resetPwd from './resetPwd.vue'
import userAvatar from './userAvatar.vue'
import userLog from './userLog.vue'

import { onMounted, ref } from 'vue'
import { getUserInfo } from "/@/api/platform/home/user"
import { formatTime } from '/@/utils/common'
const activeTab = ref('userLog')
const formData = ref({
    avatar: '',
    username: '',
    loginname: '',
    email: '',
    mobile: '',
    unit: {},
    post: '',
    roles: []
})
const roles = ref('')

const getUser = () => {
    getUserInfo().then((res)=>{
        formData.value = res.data
        if(formData.value.roles){
            roles.value = formData.value.roles.map(item => item.name) as string
        }
    })
}

onMounted(()=>{
    getUser()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-home-user'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>