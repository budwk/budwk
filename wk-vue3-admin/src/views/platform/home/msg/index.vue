<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="4">
                <el-menu class="el_menu_left" default-active="all" :default-openeds="['msg']" :unique-opened="true"
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
            <el-col :span="20" v-if="id" >
            <el-row style="width:100%">
                <el-col :span="24">
                    <h4 style="display: flex;align-items: center;height: 36px;font-size:16px;">
                    <el-button link type="primary" @click="goBack"><back style="width:1rem;height:1rem;"/> 返回</el-button>
                    </h4>
                </el-col>
                <el-col :span="24" class="msgData_title">
                    {{ formData.title }}
                </el-col>
                <el-col :span="24" class="msgData_date">
                    {{  formatTime(formData.sendAt) }}
                </el-col>
                <el-divider />
                <el-col :span="24" class="msgData_note">
                    <div v-html="formData.note"></div>
                </el-col>
            </el-row>
        </el-col>
            <el-col :span="20" v-else>
                <el-row :gutter="10" class="mb8">
                    <el-col :span="1.5">
                        <el-radio-group v-model="queryParams.type" placeholder="消息类型" @change="typeChange">
                            <el-radio-button :label="''">全部类型</el-radio-button>
                            <el-radio-button :label="item.value" v-for="(item) in types" :key="item.value">{{ item.text
                            }}
                            </el-radio-button>
                        </el-radio-group>
                    </el-col>
                </el-row>
                <el-table ref="tableRef" v-loading="tableLoading" :data="tableData" row-key="id" stripe
                    :default-sort="defaultSort" @selection-change="handleSelectChange">
                    <el-table-column type="selection" width="50" />
                    <el-table-column prop="title" label="消息标题">
                        <template #default="scope">
                            <el-button link type="primary" @click="goTo(scope.row.msgid)"
                                :style="scope.row.status == 0 ? 'font-weight:900;' : ''">
                                {{ scope.row.title }}
                            </el-button>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sendat" label="发送时间" width="180">
                        <template #default="scope">
                            {{ formatTime(scope.row.sendat) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="type" label="消息类型" width="150">
                        <template #default="scope">
                            {{ findOneValue(types, scope.row.type, 'text', 'value') }}
                        </template>
                    </el-table-column>
                </el-table>
                <el-row>
                    <div style="padding-top:22px;">
                        <el-checkbox v-model="isSelectAll" @change="changeSelectAll" style="margin:0 20px 0 12px;" />
                        <el-button :disabled="selectData.length == 0" @click="setMoreRead"
                            v-permission="['home.msg.read']">设置已读
                        </el-button>
                        <el-button @click="setAllRead" v-permission="['home.msg.read']">全部已读
                        </el-button>
                    </div>
                    <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                        v-model:limit="queryParams.pageSize" @pagination="list" />
                </el-row>
            </el-col>
        </el-row>
    </div>
</template>
<script setup lang="ts" name="platform-home-msg">
import { computed, onMounted, reactive, ref, toRefs, unref, watch } from 'vue'
import { getList, getInfo, getData, doReadAll, doReadMore, doReadOne } from '/@/api/platform/home/msg'
import { findOneValue, formatTime } from '/@/utils/common'
import { ElTable } from 'element-plus'
import { useRouter } from 'vue-router'
import { usePlatformInfo } from '/@/stores/platformInfo'
import modal from '/@/utils/modal'

const router = useRouter()
const { currentRoute } = useRouter()
const { params, query } = unref(currentRoute)
const id = ref('')
const platformInfo = usePlatformInfo()

const tableRef = ref<InstanceType<typeof ElTable>>()

const tableLoading = ref(false)
const tableData = ref([])
const types = ref([])
const scopes = ref([])
const isSelectAll = ref(false)
const selectData = ref([])

const defaultSort = ref({ prop: "sendat", order: "descending" })

const goTo = (val: string) => {
    router.push('/platform/home/msg?id=' + val)
}

const goBack = () => {
    router.push('/platform/home/msg')
}

watch(currentRoute,(newVal)=>{
    if('/platform/home/msg' == newVal.path) {
        if(newVal.query.id){
            id.value = newVal.query.id
            getMsg()
        } else {
            id.value = ''
            list()
        }
    }
})

const data = reactive({
    formData: {
        id: '',
        title: '',
        url: '',
        sendAt: 0,
        note: '',
    },
    queryParams: {
        status: 'all',
        type: '',
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'sendAt',
        pageOrderBy: 'descending'
    },
})

const { formData, queryParams } = toRefs(data)

const typeChange = () => {
    queryParams.value.pageNo = 1
    list()
}

const changeSelectAll = () => {
    tableRef.value?.toggleAllSelection()
}

const handleSelectChange = (row: any[]) => {
    if (row.length == tableData.value.length) {
        isSelectAll.value = true
    } else {
        isSelectAll.value = false
    }
    selectData.value = JSON.parse(JSON.stringify(row))
}

const handleSelect = (val: string) => {
    queryParams.value.status = val
    queryParams.value.pageNo = 1
    list()
}

const setMoreRead = () => {
    const ids = selectData.value.map(item => item.id)
    modal.confirm('您确定要标记 ' + ids.length + ' 条消息为已读吗？').then(() => {
        return doReadMore(ids.toString())
    }).then(() => {
        list()
        modal.msgSuccess('操作成功')
    }).catch(() => { })
}


const setAllRead = () => {
    modal.confirm('您确定要标记全部消息为已读吗？').then(() => {
        return doReadAll()
    }).then(() => {
        list()
        modal.msgSuccess('操作成功')
    }).catch(() => { })
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

const getMsg = () => {
    if(id.value) {
        getInfo(id.value).then((res)=>{
            formData.value = res.data
            // 设置消息为已读
            if(!platformInfo.AppDemoEnv) {
                doReadOne(id.value).then()
            }
        })
    }
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
    if(query.id){
        id.value = query.id
    }
    getMsg()
})
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

.el_menu_left .el-menu-item-group__title {
    padding: 0px;
    height: 0px;
}

.el_menu_left .el-menu-item.is-active {
    background-color: #f0f0f0;
}

.el_menu_left .el-menu-item {
    background-color: #fff;
}

.el_menu_left .el-menu-item:hover {
    background-color: #F4F6F8;
}
.read a{
    text-decoration: none;
    color: #666;
  }
  .unread a{
    text-decoration: none;
    color: #409eff;
    font-weight: bold;
  }
  .msgData_title {
    font-size: 18px;text-align: center;margin-top: 5px;margin-bottom: 5px;
  }
  .msgData_date {
    font-size: 14px;text-align: center;margin-top: 5px;margin-bottom: 5px;
  }
  .msgData_note {
    padding: 20px 20px;
  }
</style>