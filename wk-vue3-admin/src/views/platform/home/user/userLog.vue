<template>
    <div class="app-container">
        <el-timeline>
            <el-timeline-item v-for="(item, idx) in logData" :key="idx" 
            :timestamp="formatTime(item.createdAt)">
                <span v-if="item.exception" class="log-exception-color"><s>{{ item.msg }}</s></span>
                <span v-else>{{ item.msg }}</span>
                <el-tag class="log-tag">{{ item.tag }}</el-tag>
            </el-timeline-item>
        </el-timeline>
        <pagination :total="queryParams.totalCount" v-model:page="queryParams.pageNo"
                        v-model:limit="queryParams.pageSize" @pagination="list" />
    </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, toRefs } from "vue"
import modal from "/@/utils/modal"
import { getUserLog } from '/@/api/platform/home/user'

const logData = ref([])

const data = reactive({
    queryParams: {
        pageNo: 1,
        pageSize: 10,
        totalCount: 0,
        pageOrderName: 'createdAt',
        pageOrderBy: 'descending'
    },
})

const { queryParams } = toRefs(data)

const list = () => {
    getUserLog(queryParams.value).then((res)=>{
        logData.value = res.data.list 
        queryParams.value.totalCount = res.data.totalCount
    })
}

onMounted(()=>{
    list()
})
</script>
<style scoped>
.log-exception-color {
    color: #f56c6c;
}
.log-tag {
    margin-left:5px;
}
</style>