<template>
  <div>
    <el-popover placement="bottom-end" :hide-after="0" :width="260" trigger="click" popper-class="user-info-box">
      <template #reference>
        <div class="size-icon--style">
          <el-badge :value="size" class="notice">
            <svg-icon class-name="size-icon" icon-class="bell" />
          </el-badge>
        </div>
      </template>
      <div>

      </div>
    </el-popover>
  </div>
</template>
<script setup lang="ts">
import { useWebSocket } from '@vueuse/core'
import { computed, onMounted, reactive, ref } from 'vue'
import { useUserInfo } from '/@/stores/userInfo'

const userInfo = useUserInfo()
const size = ref(0)

const state = reactive({
    server: import.meta.env.VITE_AXIOS_WS_BASE_URL,
    sendValue: '',
    recordList: [] as any[],
})


const onMessage = (ws: WebSocket, event: MessageEvent) => {
    console.log(event.data)
}

const onConnected = (ws: WebSocket) => {
    send(JSON.stringify({ userId: userInfo.user.id, action: 'join', token: userInfo.getToken() }))
}

const { status, data, send, close, open, ws } = useWebSocket(state.server, {
    autoReconnect: {
        retries: 3,
        delay: 2000,
        onFailed() {
            alert('Failed to connect WebSocket after 3 retries')
        }
    },
    heartbeat: {
        message: '{}',//ping
        interval: 2000
    },
    onMessage: onMessage,
    onConnected: onConnected
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
      vertical-align:top !important;
      margin-top: 12px;
   }
   > svg {
      vertical-align: 10px !important;
   }
}
</style>