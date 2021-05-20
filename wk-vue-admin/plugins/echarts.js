import Vue from 'vue'
import ECharts from 'vue-echarts' // 在 webpack 环境下指向 components/ECharts.vue

import 'echarts/lib/chart/line'
import 'echarts/lib/component/polar'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/axis'
import 'echarts/lib/component/axisPointer'

Vue.component('v-chart', ECharts)
