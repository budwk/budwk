<template>
  <div v-if="userInfo" class="platform-content">
    <el-row :gutter="10" style="padding-left:30px;padding-right:30px;">
      <el-col :span="12" class="bg-box-content">
        <div class="bg_box" style="position: relative;">
          <div class="item_coupon_box">
            <div class="item_coupon_bg" />
            <div class="item_coupon">通用红包</div>
            <div class="item_price">阿里云 ￥<span>2000</span></div>
            <p class="item_desc">云产品通用红包，可叠加官网常规优惠使用
            </p>
          </div>
          <div class="item_limited">
            <span class="item_limited_url"><a
              href="https://promotion.aliyun.com/ntms/yunparter/invite.html?userCode=jfp1pk7u"
              target="_blank"
            >立即领取</a></span>
          </div>
        </div>
      </el-col>
      <el-col :span="12" class="bg-box-content-bywave">
        <div class="bg_box-bywave">
          <div class="text-title">
            <span> 总是想窥探这个世界，却求而不得</span>现在有一把钥匙就在眼前
          </div>
          <div class="item_limited"><span class="item_bywave_url"><a
            href="https://bywave.art/aff.php?aff=3368"
            target="_blank"
          >现在开始</a></span></div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="10">
      <el-col :span="12">
        <el-timeline :reverse="reverse" style="margin-top:10px;">
          <el-timeline-item
            v-for="(activity, index) in activities"
            :key="index"
            :timestamp="activity.timestamp"
            :icon="activity.icon"
            :type="activity.type"
            :color="activity.color"
            :size="activity.size"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
      </el-col>
      <el-col :span="12">
        <el-alert
          style="padding:10px 0 10px 0;margin-buttom:10px;"
          title="物联网卡(三大运营商4G卡、NB卡)，全网最低价，微信号：LJL568"
          type="success"
          close-text="知道了"
        />
        <el-alert
          style="padding:10px 0 10px 0;"
          title="短信平台(通知类短信、营销类短信)，全网最低价，微信号：LJL568"
          type="warning"
          close-text="知道了"
        />
        <el-alert
          style="padding:10px 0 10px 0;"
          title="定制开发、远程培训，QQ：11624317，微信号：wizzer ，QQ交流群：24457628"
          type="error"
          close-text="知道了"
        />
        <el-row class="wk">
          <el-row style="margin-top:20px;">
            <span>BudWk v6 - v7 源码地址</span>
          </el-row>
          <el-row style="margin-top:20px;">
            <span><a href="https://github.com/budwk/budwk" target="_blank">https://github.com/budwk/budwk</a></span>
          </el-row>
          <el-row style="margin-top:20px;">
            <span><a href="https://gitee.com/budwk/budwk" target="_blank">https://gitee.com/budwk/budwk</a></span>
          </el-row>
          <el-row style="margin-top:30px;">
            <span>NutzWk v1 - v5 源码地址</span>
          </el-row>
          <el-row style="margin-top:20px;">
            <span><a href="https://github.com/Wizzercn/NutzWk" target="_blank">https://github.com/Wizzercn/NutzWk</a></span>
          </el-row>
          <el-row style="margin-top:20px;">
            <span><a href="https://gitee.com/Wizzer/NutzWk" target="_blank">https://gitee.com/Wizzer/NutzWk</a></span>
          </el-row>
        </el-row>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  middleware: ['authenticated'],
  data() {
    return {
      reverse: true,
      activities: [
        {
          content: 'NutzWk V1.0.1 - 单应用 + EasyUI 发布',
          timestamp: '2015-06'
        },
        {
          content: 'NutzWk V3.0.0 - 单应用 + Bootstrap 发布',
          timestamp: '2016-07'
        },
        {
          content: 'NutzWk V4.0.1 - 模块化 + Bootstrap 发布',
          timestamp: '2017-02'
        },
        {
          content: 'NutzWk V5.0.1 - 微服务 + Bootstrap 发布',
          timestamp: '2018-03'
        },
        {
          content: 'NutzWk V5.1.0 - 微服务 + Vue ElemnetUI js混合开发 发布',
          timestamp: '2018-11'
        },
        {
          content: 'NutzWk 更名为 BudWk',
          timestamp: '2019-12',
          color: '#0bbd87'
        },
        {
          content: 'BudWk V6.0.1 - 微服务 + Vue ElemnetUI 前后端完全分离 发布',
          timestamp: '2020-01',
          size: 'large',
          type: 'primary'
        },
        {
          content: 'BudWk V7.0.0 - 微服务 + 微网关 + 组件化 + Vue ElemnetUI 前后端完全分离 发布',
          timestamp: '2021-05',
          size: 'large',
          type: 'primary',
          icon: 'el-icon-more'
        }
      ]
    }
  },
  computed: mapState(['userInfo']),
  methods: {}
}
</script>

<style>
    .bg_box {
        width: 575px;
        height: 157px;
        position: relative;
        float: left;
        box-shadow: 0 0 5px #666;
        background: url(https://img.alicdn.com/tfs/TB1kd3jr3mTBuNjy1XbXXaMrVXa-250-230.png), url(https://img.alicdn.com/tfs/TB10DxPskCWBuNjy0FaXXXUlXXa-552-233.png);
        background-size: 22%;
        background-position: 100% 100%, 0 0;
        background-repeat: no-repeat, repeat;
        z-index: 9;
    }

    .bg_box .item_coupon_box .item_coupon_bg {
        width: 63px;
        height: 20px;
        transform: skew(-16deg);
        border: 1px solid #ff8a00;
        border-radius: 2px;
        position: relative;
        overflow: hidden;
        margin: 20px 0 0 20px;
    }

    .bg_box .item_coupon_box .item_price i {
        font-style: normal;
        font-size: 18px;
    }

    .bg_box .item_coupon_box .item_price span {
        font-size: 40px;
    }

    .bg-box-content {
        margin: 20px auto;
    }

    .bg_box .item_coupon_box .item_coupon {
        width: 63px;
        height: 20px;
        line-height: 20px;
        text-align: center;
        color: #ff8a00;
        position: absolute;
        top: 22px;
        left: 20px;
        overflow: hidden;
        font-size: 12px;
    }

    .bg_box .item_coupon_box .item_price {
        height: 40px;
        line-height: 50px;
        margin-left: 20px;
        color: #ff8a00;
        font-size: 14px;
        letter-spacing: 2px;
    }

    .bg_box .item_limited {
        width: 200px;
        height: 157px;
        margin-left: 350px;
        top: -80px;
        position: relative;
        overflow: hidden;
        float: left;
        z-index: 9999;
    }

    .bg_box .item_limited .item_limited_url a{
        display: block;
        width: 160px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        overflow: hidden;
        font-size: 16px;
        background: #00c1de;
        color: #fff;
        cursor: pointer;
        text-decoration: none;
    }

    .bg-box-content-bywave {
        margin: 20px auto;
    }

    .bg_box-bywave .item_limited{
        width: 200px;
        height: 157px;
        margin-left: 350px;
        position: relative;
        overflow: hidden;
        float: left;
        z-index: 9999;
    }

    .bg_box-bywave .item_limited .item_bywave_url a{
        display: block;
        width: 160px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        overflow: hidden;
        font-size: 16px;
        background: #fc68a6;
        border: #fc68a6;
        color: #fff;
        cursor: pointer;
        text-decoration: none;
    }

    .bg_box-bywave {
        width: 575px;
        height: 157px;
        position: relative;
        float: left;
        box-shadow: 0 0 5px #666;
        background-size: 22%;
        background-position: 100% 100%, 0 0;
        background-repeat: no-repeat, repeat;
        z-index: 9;
    }

    .bg_box-bywave .text-title {
        font-family: 'Poppins', sans-serif;
        text-transform: uppercase;
        font-weight: bold;
        font-size: 35px;
        letter-spacing: 1px;
        margin-bottom: 10px;
        color: #2f3538;
        position: relative;
        padding-left: 20px;
        padding-top: 20px;
    }

    .bg_box-bywave .text-title span {
        display: block;
        font-size: 14px;
        text-transform: capitalize;
        font-weight: 500;
        letter-spacing: 0;
        font-family: 'Quicksand', sans-serif;
        margin-bottom: 10px;
        color: #aebfc7;
    }

    .bg_box .item_coupon_box .item_desc {
        width: 350px;
        height: 36px;
        line-height: 40px;
        font-size: 14px;
        color: #333;
        margin-left: 20px;
        overflow: hidden;
    }
    .bg-box-content2 {
        width: 800px;
        margin: 10px auto;
    }
    .wk a {
      font-size: 12px;
      text-decoration: none;
    }
</style>
