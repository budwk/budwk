<template>
    <div class="app-container">
        <el-row :gutter="16">
            <el-col :span="6">
                <el-card>
                    <el-statistic :value="98500">
                        <template #title>
                            <div style="display: inline-flex; align-items: center">
                                设备数量
                                <el-tooltip effect="dark" content="设备总数量" placement="top">
                                    <el-icon style="margin-left: 4px" :size="12">
                                        <Warning />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-statistic>
                    <div class="statistic-footer">
                        <div class="footer-item">
                            <span>在线</span>
                            <span class="small-title">
                                2340
                            </span>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card>
                    <el-statistic :value="566">
                        <template #title>
                            <div style="display: inline-flex; align-items: center">
                                当前在线
                                <el-tooltip effect="dark" content="30分钟内在线设备数" placement="top">
                                    <el-icon style="margin-left: 4px" :size="12">
                                        <Warning />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-statistic>
                    <div class="statistic-footer">
                        <div class="footer-item">
                            <span>昨日在线</span>
                            <span class="small-title">322</span>
                            <span class="red">
                                12%
                                <el-icon>
                                    <CaretBottom />
                                </el-icon>
                            </span>
                        </div>
                        <div class="footer-item">
                            <el-icon :size="14">
                                <ArrowRight />
                            </el-icon>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card>
                    <el-statistic :value="72000" title="">
                        <template #title>
                            <div style="display: inline-flex; align-items: center">
                                今日设备通信量
                                <el-tooltip effect="dark" content="设备上报数据总条数" placement="top">
                                    <el-icon style="margin-left: 4px" :size="12">
                                        <Warning />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-statistic>
                    <div class="statistic-footer">
                        <div class="footer-item">
                            <span>昨日上报数</span>
                            <span class="small-title">123</span>
                            <span class="green">
                                16%
                                <el-icon>
                                    <CaretTop />
                                </el-icon>
                            </span>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card>
                    <el-statistic :value="3500" title="">
                        <template #title>
                            <div style="display: inline-flex; align-items: center">
                                今日告警数量
                                <el-tooltip effect="dark" content="设备事件数据不算在内" placement="top">
                                    <el-icon style="margin-left: 4px" :size="12">
                                        <Warning />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-statistic>
                    <div class="statistic-footer">
                        <div class="footer-item">
                            <span>原生告警</span>
                            <span class="small-title">123</span>
                            <span>规则告警</span>
                            <span class="small-title">321</span>
                        </div>
                        <div class="footer-item">
                            <el-icon :size="14">
                                <ArrowRight />
                            </el-icon>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="16" class="content">
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            厂家设备统计
                        </div>
                    </template>
                    <el-table :data="supplierData" row-key="id" style="height: 200px">
                        <el-table-column prop="name" label="厂家名称">
                        </el-table-column>
                        <el-table-column prop="deviceCount" label="设备数量">
                        </el-table-column>
                        <el-table-column prop="deviceRate" label="设备占比">
                            <template #default="{ row }">
                                <el-progress :percentage="row.deviceRate" />
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            设备类型统计
                        </div>
                    </template>
                    <div id="pieChart" style="height: 200px" />
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="16" class="content">
            <el-col :span="24">
                <el-card>
                    <template #header>
                        <div class="card-header">
                            <el-row justify="end">
                                <el-col :span="9">设备通信情况</el-col>
                                <el-col :span="15">
                                    <el-row>
                                        <el-radio-group v-model="dataTag" @change="tagChange">
                                            <el-radio-button label="today">
                                                今日
                                            </el-radio-button>
                                            <el-radio-button label="week">
                                                近一周
                                            </el-radio-button>
                                            <el-radio-button label="month">
                                                近一月
                                            </el-radio-button>
                                            <el-radio-button label="year">
                                                近一年
                                            </el-radio-button>
                                        </el-radio-group>
                                        <div class="block">
                                            <el-date-picker
                                                v-model="dataDate" type="datetimerange" range-separator="到"
                                                start-placeholder="开始日期" end-placeholder="截至日期" />
                                        </div>
                                    </el-row>
                                </el-col>
                            </el-row>
                        </div>
                    </template>
                    <div id="lineChart" style="height: 300px" />

                </el-card>
            </el-col>
        </el-row>
    </div>
</template>
<script setup lang="ts" name="platform-device-dashboard">
import { onMounted, ref } from "vue"
import * as echarts from 'echarts'

const dataTag = ref('today')

const endToday = new Date()
endToday.setHours(23, 59, 59, 59)
const startToday = new Date()
startToday.setHours(0, 0, 0, 0)
startToday.setTime(startToday.getTime() - 3600 * 1000 * 24)

const dataDate = ref<[Date, Date]>([startToday, endToday])

const tagChange = (tag: string) => {
    switch(tag){
        case 'today':
            dataDate.value= [startToday, endToday]
            break
        case 'week':
            const endWeek = new Date()
            endWeek.setHours(23, 59, 59, 59)
            const startWeek = new Date()
            startWeek.setHours(0, 0, 0, 0)
            startWeek.setTime(startWeek.getTime() - 3600 * 1000 * 24 * 7)
            dataDate.value= [startWeek, endWeek]
            break
        case 'month':
            const endMonth = new Date()
            endMonth.setHours(23, 59, 59, 59)
            const startMonth = new Date(endMonth.getFullYear(), endMonth.getMonth() - 1, endMonth.getDate())
            startMonth.setHours(0, 0, 0, 0)
            dataDate.value= [startMonth, endMonth]
            break    
        case 'year':
            const endYear = new Date()
            endYear.setHours(23, 59, 59, 59)
            const startYear = new Date(endYear.getFullYear() - 1, endYear.getMonth(), endYear.getDate())
            startYear.setHours(0, 0, 0, 0)
            dataDate.value= [startYear, endYear]
            break    
    }
}

const supplierData = ref([{
    id: 1,
    name: '厂家1',
    deviceCount: 5000,
    deviceRate: 50
}, {
    id: 2,
    name: '厂家2',
    deviceCount: 2500,
    deviceRate: 25
}, {
    id: 3,
    name: '厂家3',
    deviceCount: 1500,
    deviceRate: 15
}, {
    id: 4,
    name: '厂家4',
    deviceCount: 500,
    deviceRate: 5
}, {
    id: 5,
    name: '厂家5',
    deviceCount: 500,
    deviceRate: 5
}])

const initChart = () => {
    var chartDom = document.getElementById('pieChart')!
    var myChart = echarts.init(chartDom)
    const option = {
        title: {
            text: '',
            left: 'center'
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            orient: 'vertical',
            left: 'left'
        },
        series: [
            {
                name: '设备数量',
                type: 'pie',
                radius: '50%',
                data: [
                    { value: 1048, name: '物联网表' },
                    { value: 735, name: '采集器' },
                    { value: 580, name: '报警器' },
                    { value: 484, name: '摄像头' }
                ],
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
    myChart.setOption(option)
}

const initLine = () => {
    var chartDom = document.getElementById('lineChart')!
    var lineChart = echarts.init(chartDom)
    const option = {
        color: ['#80FFA5'],
        title: {
            text: ''
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        //legend: {
        //   data: ['Line 1']
        //},
        toolbox: {
            feature: {
                saveAsImage: {}
            }
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                data: ['4月20', '4月21', '4月22', '4月23', '4月24', '4月25']
            }
        ],
        yAxis: [
            {
                type: 'value'
            }
        ],
        series: [
            {
                name: '通信量',
                type: 'line',
                stack: 'Total',
                smooth: true,
                lineStyle: {
                    width: 0
                },
                showSymbol: false,
                areaStyle: {
                    opacity: 0.8,
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        {
                            offset: 0,
                            color: 'rgb(128, 255, 165)'
                        },
                        {
                            offset: 1,
                            color: 'rgb(1, 191, 236)'
                        }
                    ])
                },
                emphasis: {
                    focus: 'series'
                },
                data: [140, 232, 101, 264, 210, 230]
            }
        ]
    }
    lineChart.setOption(option)
}

onMounted(() => {
    setTimeout(() => {
        initChart()
        initLine()
    }, 500)
})

</script>
<route lang="yaml">
    meta:
      layout: platform/index
</route>
<style scoped>
:global(h2#card-usage ~ .example .example-showcase) {
    background-color: var(--el-fill-color) !important;
}

.el-statistic {
    --el-statistic-content-font-size: 28px;
}

.statistic-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    font-size: 12px;
    color: var(--el-text-color-regular);
    margin-top: 16px;
}

.statistic-footer .footer-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.statistic-footer .footer-item span:last-child {
    display: inline-flex;
    align-items: center;
    margin-left: 4px;
}

.small-title {
    padding-left: 2px;
    padding-right: 10px;
}

.green {
    color: var(--el-color-success);
}

.red {
    color: var(--el-color-error);
}

.content {
    margin-top: 20px;
}

.block {
    padding-left: 10px;
}
</style>
<style>
.el-card__header {
    border-bottom: none;
}
</style>