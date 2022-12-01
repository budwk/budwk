<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="4">
                <el-menu class="el_menu_left" default-active="unread" 
                :default-openeds="['msg']" :unique-opened="true"
                    @select="handleSelect" active-text-color="#409eff">
                    <el-sub-menu index="msg">
                        <template #title>
                            <h4>消息中心</h4>
                        </template>
                        <el-menu-item-group>
                            <el-menu-item index="all">全部消息</el-menu-item>
                            <el-menu-item index="unread">未读消息</el-menu-item>
                            <el-menu-item index="read">已读消息</el-menu-item>
                        </el-menu-item-group>
                    </el-sub-menu>
                </el-menu>
            </el-col>
            <el-col :span="20">
                <el-row :gutter="10" class="mb8">
                    <el-col :span="1.5">
                        <el-radio-group v-model="queryParams.type" placeholder="消息类型" @change="typeChange">
                            <el-radio-button :label="''">全部类型</el-radio-button>
                            <el-radio-button :label="item.value" v-for="(item) in types" :key="item.value">{{ item.text }}
                            </el-radio-button>
                        </el-radio-group>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button plain type="primary" icon="Plus"
                            v-permission="['sys.manage.msg.create']">发送消息
                        </el-button>
                    </el-col>
                        </el-row>
                <el-table v-loading="tableLoading" :data="tableData" row-key="id" stripe
                    :default-sort="defaultSort">
                    <el-table-column prop="title" label="消息标题">
                        <template #default="scope">
                            <el-button link type="primary" @click="goTo('/home/msg?id='+scope.row.msgid)" 
                                :style="scope.row.status==0?'font-weight:900;':''"
                            >
                                {{scope.row.title}}
                            </el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sendat" label="发送时间" width="180">
                        <template #default="scope">
                            {{ formatTime(scope.row.sendat)}}
                        </template>
                    </el-table-column>    
                    <el-table-column prop="type" label="消息类型" width="150" >
                        <template #default="scope">
                            {{ findOneValue(types, scope.row.type, 'text', 'value')}}
                        </template>
                    </el-table-column>
                </el-table>
                <el-row>
                    <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                        v-model:limit="queryParams.pageSize" @pagination="list" />
                </el-row>
            </el-col>
        </el-row>
    </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref, toRefs, watch } from 'vue'
import { getList, getInfo, getData } from '/@/api/platform/home/msg'
import { findOneValue, formatTime } from '/@/utils/common';
import router from "/@/router"

const tableLoading = ref(false)
const tableData = ref([])
const types = ref([])
const scopes = ref([])

const defaultSort = ref({ prop: "sendat", order: "descending" })

const goTo = (path: string) => {
    router.push(path)
}

const data = reactive({
    formData: {
        id: '',
        title: '',
        url: '',
        type: 'USER',
        scope: 'SCOPE',
        note: '',
    },
    queryParams: {
        title: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'sendAt',
        pageOrderBy: 'descending'
    },
})

const { formData, queryParams } = toRefs(data)

const handleSelect = () => {

}

// 查询消息列表
const list = () => {
    tableLoading.value = true
    getList(queryParams.value).then((res) => {
        tableLoading.value = false
        tableData.value = res.data.list as never
        queryParams.value.totalCount = res.data.totalCount as never
    })
}

// 初始化字典数据
const getInitData = () => {
    getData().then((res) => {
        types.value = res.data.types
        scopes.value = res.data.scopes
    })
}

onMounted(() => {
    list()
    getInitData()
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-home-msg'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
  .el_menu_left {
    overflow: hidden;
    min-height: 550px;
  }
  .el_menu_left .el-submenu.is-active .el-submenu__title {
    background-color: #D9DEE4;
    font-weight: bold;
    text-indent: 20px;
  }
  .el_menu_left .el-menu-item-group__title{
    padding: 0px;
    height: 0px;
  }
  .el_menu_left .el-menu-item.is-active{
    background-color: #f0f0f0;
  }
  .el_menu_left .el-menu-item {
    background-color: #fff;
  }
  .el_menu_left .el-menu-item:hover{
    background-color: #F4F6F8;
  }
</style>