<template>
  <div>
    <el-popover placement="bottom-end" :hide-after="0" :width="260" trigger="hover" popper-class="user-info-box">
      <template #reference>
        <div class="size-icon--style">
          <el-badge :value="size" class="notice">
            <svg-icon class-name="size-icon" icon-class="bell" />
          </el-badge>
        </div>
      </template>
      <div class="panel panel-default no-m">
        <el-row class="panel-heading small">
          <el-col>
            <b>站内通知</b>
          </el-col>
        </el-row>
        <div class="list-group">
          <li class="list-group-item" v-for="(item,idx) in notice.list" :key="'msg_'+idx">
            <el-button
link type="primary"
             @click="goTo(item.url?item.url:'/platform/home/msg?id='+item.msgId)"
             style="text-align:left;"
             >
  
              <el-row>
                <el-col :span="24" style="padding-bottom: 5px;white-space: normal;">{{ item.title }}</el-col>
                <el-col :span="24" style="color: #00c1de;">{{ item.time }}</el-col>
              </el-row>
            </el-button>
          </li>
          <li v-if="size==0" class="list-group-item">
            <div class="m-body">
                <span class="time small">暂无新消息</span>
              </div>
          </li>
        </div>
        <div class="panel-footer">
          <el-button link type="primary" @click="goTo('/platform/home/msg')" size="small">查看更多</el-button>
        </div>
      </div>

    </el-popover>
  </div>
</template>
<script setup lang="ts">
import { useWebSocket } from '@vueuse/core'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useUserInfo } from '/@/stores/userInfo'
import { getWsMsg } from '/@/api/platform/home/msg'
import modal from '/@/utils/modal'
import router from "/@/router"

const userInfo = useUserInfo()
const size = ref(0)
const notice = ref({})

const state = reactive({
    server: '' + import.meta.env.VITE_AXIOS_WS_BASE_URL,
    sendValue: '',
    recordList: [] as any[],
})

const goTo = (path: string) => {
    router.push(path)
}

// 加入room
const onConnected = () => {
    send(JSON.stringify({ userId: userInfo.user.id, action: 'join', token: userInfo.getToken() }))
    getWsMsg()
}

const { status, data, send, ws } = useWebSocket(state.server, {
    autoReconnect: {
        retries: 3,
        delay: 2000,
        onFailed() {
            console.log('Failed to connect WebSocket after 3 retries')
        }
    },
    heartbeat: {
        message: '{}',//ping心跳内容
        interval: 2000
    },
    onConnected: onConnected
})

// 获取消息
watch(data, (message) => {
    const res = JSON.parse(message)
    if (res.action === 'offline') { // 账号下线通知
        modal.alertCallback('您的帐号在其他地方登录，您已被迫下线，如果不是您本人操作，请及时修改密码。', '下线通知', false)
            .then(()=>{
                userInfo.logoutNotLogin()
            })
    } else if (res.action === 'notice') { // 消息通知
        notice.value = res
        size.value = res.size
        if (res.notify && res.size > 0) {
            modal.notifySuccess('您有' + res.size + '条新消息，请查收！')
        }
    }
})


</script>
<style lang='scss' scoped>
.size-icon--style {
  font-size: 18px;
  line-height: 50px;
  padding-right: 7px;

  &:hover {
    .svg-icon {
      animation: twinkle 0.3s ease-in-out;
    }
  }
}

.notice {
  &.el-badge {
    vertical-align: top !important;
    margin-top: 12px;
  }

  >svg {
    vertical-align: 10px !important;
  }
}
.m-body {
  display: block;
  overflow: hidden;
}
.time {
  display: block;
}
.panel-heading {
  padding-bottom: 10px;
}
.panel-footer {
  border-top: 1px solid #eeeff8;
  padding-top: 10px;
}
</style>