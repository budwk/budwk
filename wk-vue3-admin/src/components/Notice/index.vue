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
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
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
    isManualClose: false, // 标记是否是手动关闭
    reconnectCount: 0, // 重连次数计数
})

const goTo = (path: string) => {
    router.push(path)
}

// 加入room
const onConnected = (ws: WebSocket) => {
    // 重置重连计数
    state.reconnectCount = 0
    
    // 发送加入房间消息
    const joinMsg = {
        userId: userInfo.user.id,
        action: 'join',
        token: userInfo.getToken()
    }
    send(JSON.stringify(joinMsg))
    
    // 获取未读消息
    getWsMsg()
}

// 连接关闭处理
const onClose = (ws: WebSocket, event: CloseEvent) => {
    // 如果是手动关闭，不进行重连
    if (state.isManualClose) {
        return
    }
    
    // 如果是异常关闭，记录重连次数
    if (event.code !== 1000 && event.code !== 1001) {
        state.reconnectCount++
    }
}

// 错误处理
const onError = (ws: WebSocket, event: Event) => {
    
}

// 断开连接处理
const onDisconnected = (ws: WebSocket, event: CloseEvent) => {
    
}

// 消息接收处理
const onMessage = (ws: WebSocket, event: MessageEvent) => {
    
}

// 初始化 WebSocket
const { status, data, send, close, ws } = useWebSocket(state.server, {
    // 自动重连配置
    autoReconnect: {
        retries: 5, // 增加重试次数
        delay: 3000, // 延迟 3 秒重连
        onFailed() {
             modal.msgError('WebSocket 连接失败，请检查网络或刷新页面重试')
        }
    },
    // 禁用内置心跳，使用手动控制
    // heartbeat: false, 
    
    // 事件处理
    onConnected: (ws) => {
        onConnected(ws)
        startHeartbeat()
    },
    onMessage: onMessage,
    onClose: (ws, event) => {
        stopHeartbeat()
        onClose(ws, event)
    },
    onError: onError,
    onDisconnected: (ws, event) => {
        stopHeartbeat()
        onDisconnected(ws, event)
    },
    // 立即连接
    immediate: true
})

// 手动心跳定时器
let heartbeatTimer: any = null

// 启动心跳
const startHeartbeat = () => {
    stopHeartbeat() // 防止重复启动
    heartbeatTimer = setInterval(() => {
        if (ws.value && ws.value.readyState === WebSocket.OPEN) {
            // 发送空 JSON，后端会静默处理，不再导致断开
            send('{}') 
        } else {
            stopHeartbeat()
        }
    }, 30000)
}

// 停止心跳
const stopHeartbeat = () => {
    if (heartbeatTimer) {
        clearInterval(heartbeatTimer)
        heartbeatTimer = null
    }
}

// 监听连接状态变化
watch(status, (newStatus) => {
    if (newStatus !== 'OPEN') {
        stopHeartbeat()
    }
})

// 获取消息处理
watch(data, (message) => {
    if (!message) return
    
    try {
        const res = JSON.parse(message)
        
        if (res.action === 'offline') {
            // 账号下线通知
            // 标记为手动关闭，避免自动重连
            state.isManualClose = true
            
            modal.alertCallback(
                '您的帐号在其他地方登录，您已被迫下线，如果不是您本人操作，请及时修改密码。',
                '下线通知',
                false
            ).then(() => {
                // 关闭 WebSocket 连接
                close()
                // 退出登录
                userInfo.logoutNotLogin()
            })
        } else if (res.action === 'notice') {
            // 消息通知
            notice.value = res
            size.value = res.size
            
            if (res.notify && res.size > 0) {
                modal.notifySuccess('您有' + res.size + '条新消息，请查收！')
            }
        } 
    } catch (error) {
       
    }
})

// 组件卸载时清理
onUnmounted(() => {
    state.isManualClose = true
    
    // 发送离开房间消息
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        try {
            const leaveMsg = {
                userId: userInfo.user.id,
                action: 'left',
                token: userInfo.getToken()
            }
            send(JSON.stringify(leaveMsg))
        } catch (error) {
            
        }
    }
    
    close()
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