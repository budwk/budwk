<template>
    <div class="app-container">
        <el-alert
        v-if="loading"
        title="加载服务器信息需要 1秒, 请稍等..."
        type="success"
        style="width:300px;"
      />
      <el-row>
        <el-col :span="24">
          <el-card header="Redis信息">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%">
                <tbody>
                  <tr>
                    <td><div class="cell">Redis版本</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.redis_version }}</div></td>
                    <td><div class="cell">运行模式</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.redis_mode == "standalone" ? "单机" : "集群" }}</div></td>
                    <td><div class="cell">端口</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.tcp_port }}</div></td>
                    <td><div class="cell">客户端数</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.connected_clients }}</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">运行时间(天)</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.uptime_in_days }}</div></td>
                    <td><div class="cell">使用内存</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.used_memory_human }}</div></td>
                    <td><div class="cell">使用CPU</div></td>
                    <td><div v-if="redisData" class="cell">{{ parseFloat(redisData.used_cpu_user_children).toFixed(2) }}</div></td>
                    <td><div class="cell">内存配置</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.maxmemory_human }}</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">AOF是否开启</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.aof_enabled == "0" ? "否" : "是" }}</div></td>
                    <td><div class="cell">RDB是否成功</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.rdb_last_bgsave_status }}</div></td>
                    <td><div class="cell">Key数量</div></td>
                    <td><div v-if="redisData.dbSize" class="cell">{{ redisData.dbSize }} </div></td>
                    <td><div class="cell">网络入口/出口</div></td>
                    <td><div v-if="redisData" class="cell">{{ redisData.instantaneous_input_kbps }}kps/{{ redisData.instantaneous_output_kbps }}kps</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12" class="mt10 pr5">
          <el-card v-loading="loading" header="CPU">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%;">
                <thead>
                  <tr>
                    <th class="is-leaf"><div class="cell">属性</div></th>
                    <th class="is-leaf"><div class="cell">值</div></th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><div class="cell">核心数</div></td>
                    <td><div v-if="serverData.cpu" class="cell">{{ serverData.cpu.cpuNum }}</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">用户使用率</div></td>
                    <td><div v-if="serverData.cpu" class="cell">{{ serverData.cpu.used }}%</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">系统使用率</div></td>
                    <td><div v-if="serverData.cpu" class="cell">{{ serverData.cpu.sys }}%</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">当前空闲率</div></td>
                    <td><div v-if="serverData.cpu" class="cell">{{ serverData.cpu.free }}%</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="12" class="mt10 pl5">
          <el-card v-loading="loading" header="内存">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%;">
                <thead>
                  <tr>
                    <th class="is-leaf"><div class="cell">属性</div></th>
                    <th class="is-leaf"><div class="cell">内存</div></th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><div class="cell">总内存</div></td>
                    <td><div v-if="serverData.mem" class="cell">{{ serverData.mem.total }}G</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">已用内存</div></td>
                    <td><div v-if="serverData.mem" class="cell">{{ serverData.mem.used }}G</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">剩余内存</div></td>
                    <td><div v-if="serverData.mem" class="cell">{{ serverData.mem.free }}G</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">使用率</div></td>
                    <td><div v-if="serverData.mem" class="cell" :class="{'text-danger': serverData.mem.usage > 80}">{{ serverData.mem.usage }}%</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="24" class="mt10">
          <el-card v-loading="loading" header="服务器信息">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%;">
                <tbody>
                  <tr>
                    <td><div class="cell">服务器名称</div></td>
                    <td><div v-if="serverData.sys" class="cell">{{ serverData.sys.hostName }}</div></td>
                    <td><div class="cell">操作系统</div></td>
                    <td><div v-if="serverData.sys" class="cell">{{ serverData.sys.osName }}</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">服务器IP</div></td>
                    <td><div v-if="serverData.sys" class="cell">{{ serverData.sys.hostIp }}</div></td>
                    <td><div class="cell">系统架构</div></td>
                    <td><div v-if="serverData.sys" class="cell">{{ serverData.sys.osArch }}</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="24" class="mt10">
          <el-card v-loading="loading" header="Java虚拟机信息">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%;">
                <tbody>
                  <tr>
                    <td><div class="cell">Java名称</div></td>
                    <td><div v-if="serverData.jvm" class="cell">{{ serverData.jvm.name }}</div></td>
                    <td><div class="cell">Java版本</div></td>
                    <td><div v-if="serverData.jvm" class="cell">{{ serverData.jvm.version }}</div></td>
                  </tr>
                  <tr>
                    <td><div class="cell">启动时间</div></td>
                    <td><div v-if="serverData.jvm" class="cell">{{ serverData.jvm.startTime }}</div></td>
                    <td><div class="cell">运行时长</div></td>
                    <td><div v-if="serverData.jvm" class="cell">{{ serverData.jvm.runTime }}</div></td>
                  </tr>
                  <tr>
                    <td colspan="1"><div class="cell">安装路径</div></td>
                    <td colspan="3"><div v-if="serverData.jvm" class="cell">{{ serverData.jvm.home }}</div></td>
                  </tr>
                  <tr>
                    <td colspan="1"><div class="cell">项目路径</div></td>
                    <td colspan="3"><div v-if="serverData.sys" class="cell">{{ serverData.sys.userDir }}</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>

        <el-col :span="24" class="mt10">
          <el-card v-loading="loading" header="磁盘状态">
            <div class="el-table el-table--enable-row-hover el-table--medium">
              <table cellspacing="0" style="width: 100%;">
                <thead>
                  <tr>
                    <th class="is-leaf"><div class="cell">盘符路径</div></th>
                    <th class="is-leaf"><div class="cell">文件系统</div></th>
                    <th class="is-leaf"><div class="cell">盘符类型</div></th>
                    <th class="is-leaf"><div class="cell">总大小</div></th>
                    <th class="is-leaf"><div class="cell">可用大小</div></th>
                    <th class="is-leaf"><div class="cell">已用大小</div></th>
                    <th class="is-leaf"><div class="cell">已用百分比</div></th>
                  </tr>
                </thead>
                <tbody v-if="serverData.files">
                  <tr v-for="(sysFile,index) in serverData.files" :key="index">
                    <td><div class="cell">{{ sysFile.dirName }}</div></td>
                    <td><div class="cell">{{ sysFile.sysTypeName }}</div></td>
                    <td><div class="cell">{{ sysFile.typeName }}</div></td>
                    <td><div class="cell">{{ sysFile.total }}</div></td>
                    <td><div class="cell">{{ sysFile.free }}</div></td>
                    <td><div class="cell">{{ sysFile.used }}</div></td>
                    <td><div class="cell" :class="{'text-danger': sysFile.usage > 80}">{{ sysFile.usage }}%</div></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </el-card>
        </el-col>
      </el-row>

    </div>
</template>
<script setup lang="ts" name="platform-sys-server">
import { onMounted, ref } from 'vue'
import { getRedisInfo, getServerInfo } from '/@/api/platform/sys/monitor'

const loading = ref(true)
const serverData = ref([])
const redisData = ref({
    redis_version: '',
    redis_mode: '',
    tcp_port: 0,
    connected_clients: 0,
    uptime_in_days: 0,
    used_memory_human: 0,
    used_cpu_user_children: 0,
    maxmemory_human: '',
    aof_enabled: '',
    rdb_last_bgsave_status: '',
    dbSize: 0,
    instantaneous_input_kbps: 0,
    instantaneous_output_kbps: 0
})

const initData = () => {
    getRedisInfo().then((res)=>{
        redisData.value = res.data
    })
    getServerInfo().then((res)=>{
        loading.value = false
        serverData.value = res.data
    })
}


onMounted(() => {
    initData()
})
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
.is-leaf {
    text-align: left;
}
</style>