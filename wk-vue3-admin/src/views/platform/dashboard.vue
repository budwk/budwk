
<template>
    <div v-role="['sysadmin']" class="app-container home">

        <el-row :gutter="20">
            <el-col :sm="24" :lg="12" style="padding-left: 20px">
                <h2>BudWK 开源 Java Web开发框架</h2>
                <p>
                    BudWk 原名 NutzWk ，是基于国产框架 nutzboot 开发的开源 Java企业级Web开发框架，拥有近十年的开源历史，积累了一大批企业和个人用户，历经V1-V8数次迭代。
</p>
<p>
V8 在 V7具备的API网关、组件库、认证中心、控制中心等功能基础上，进一步对前后端功能进行升级改造、提升用户体验，同时大大减轻开发工作量，提升开发效率，为产品升级迭代提供极大便利。
</p>
<p>
框架同时提供 <b>微服务单应用</b> 及 <b>微服务分布式版本</b> 供选择，后台集权限体系、系统参数、数据字典、站内消息、定时任务、CMS、微信等最常用功能，使其具有上手容易、开发便捷、扩展灵活等特性，特别适合各类大中小型定制化项目需求。

</p>
                <p>
                    <b>当前版本:</b> <span>V8</span>
                </p>
                <p>
                    <el-tag type="danger">&yen;免费开源</el-tag>
                </p>
                <p>
                    <el-button type="primary" icon="Cloudy" plain
                        @click="goTarget('https://gitee.com/budwk/budwk')">访问Gitee</el-button>
                        <el-button type="primary" icon="Cloudy" plain
                        @click="goTarget('https://github.com/budwk/budwk')">访问Github</el-button>
                    <el-button icon="HomeFilled" plain @click="goTarget('https://budwk.com')">访问主页</el-button>
                </p>
            </el-col>

            <el-col :sm="24" :lg="12" style="padding-left: 50px">
                <el-row>
                    <el-col :span="12">
                        <h2>技术选型</h2>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="6">
                        <h4>后端技术</h4>
                        <ul>
                            <li>NutzBoot</li>
                            <li>Sa-Token</li>
                            <li>Druid</li>
                            <li>Hutool</li>
                            <li>Quartz</li>
                            <li>IdGenerator</li>
                            <li>Nacos [分布式版本]</li>
                            <li>Dubbo [分布式版本]</li>
                            <li>...</li>
                        </ul>
                    </el-col>
                    <el-col :span="6">
                        <h4>前端技术</h4>
                        <ul>
                            <li>Vue3</li>
                            <li>Element-Plus</li>
                            <li>Vite</li>
                            <li>Axios</li>
                            <li>Echarts</li>
                            <li>Jsencrypt</li>
                            <li>...</li>
                        </ul>
                    </el-col>
                </el-row>
            </el-col>
        </el-row>
        <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <template v-slot:header>
            <div class="clearfix">
              <span>联系信息</span>
            </div>
          </template>
          <div class="body">
            <p>
              <promotion style="width:1rem;height:1rem;" />官网：<el-link
                href="https://budwk.com"
                target="_blank"
                >https://budwk.com</el-link
              >
            </p>
            <p>
                <user-filled style="width:1rem;height:1rem;" />QQ群：
              <span><s>1群 68428921</s></span>
              <span style="padding-left:10px;color:red;">2群：24457628</span>
            </p>
            <p>
              <i class="fa fa-wechat"></i> 微信：<a
                href="javascript:;"
                >wizzer</a
              >
            </p>
            <p>
              <i class="fa fa-qq"></i> QQ：<a
                href="javascript:;"
                class="QQ"
                >wizzer</a
              >
            </p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card>
          <template #header>
            <div class="clearfix">
              <span>捐赠支持</span>
            </div>
          </template>
          <div class="body">
            <img
              src="~assets/images/wxpay.png"
              alt="donate"
              style="width:50%"
            />
            <img
              src="~assets/images/alipay.png"
              alt="donate"
              style="width:50%"
            />
            <span style="display: inline-block; height: 30px; line-height: 30px"
              >你可以请作者喝杯咖啡表示鼓励</span
            >
          </div>
        </el-card>
      </el-col>
      </el-row>
    </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue"

const goTarget = (url: string) => {
    window.open(url, '__blank')
}

onMounted(() => {
    //console.log('Dashboard onMounted!!!')
})
</script>
<!--定义组件名用于keep-alive页面缓存-->
<script lang="ts">
export default {
    name: 'platform-dashboard'
}
</script>
<!--定义布局-->
<route lang="yaml">
    meta:
      layout: platform/index
</route>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }
}
</style>

