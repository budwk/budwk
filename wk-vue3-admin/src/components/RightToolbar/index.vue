
<template>
    <div class="top-right-btn" :style="style">
        <div class="table-search">
        <el-row>
            <el-input
                v-if="quickSearchShow"
                v-model="state.keyword"
                @input="debounce(onSearchInput, 500)()"
                class="xs-hidden quick-search"
                :placeholder="quickSearchPlaceholder ? quickSearchPlaceholder : '搜索'"
            />
            <div class="table-search-button-group" >
                <el-tooltip class="item" effect="dark" content="刷新" placement="top">
                    <el-button
@click="onRefresh()" class="table-search-button-item"
                    :class="columns?.length>0||extendSearch ? 'right-border' : ''"
                    color="#dcdfe6" plain>
                        <icon size="14" name="el-icon-Refresh" />
                    </el-button>
                </el-tooltip>
                <el-dropdown v-if="columns?.length>0" :max-height="380" :hide-on-click="false">
                    <el-button
                        class="table-search-button-item"
                        :class="extendSearch ? 'right-border' : ''"
                        color="#dcdfe6"
                        plain
                    >
                        <icon size="14" name="el-icon-Grid" />
                    </el-button>
                    <template #dropdown>
                        <el-dropdown-menu ref="rightToolbarDropdownMenuRef" class="dropdown-column">
                            <el-dropdown-item v-for="(item, idx) in columns" :key="item.prop">
                                <el-row class="dropdown-column-item">
                                    <el-col :span="18">
                                        <svg-icon icon-class="drag" class="table-column-drag-icon"/>
                                        <el-checkbox
                                            @change="onChangeShowColumn($event, idx)"
                                            :model-value="item.show"
                                            size="small"
                                            :label="item.label"
                                        />
                                    </el-col>
                                    <el-col :span="6" class="dropdown-column-item-right">
                                        <span @click="onFixedLeft(idx)" class="line-left" :class="item?.fixed=='left'?'active':''" >
                                            <svg-icon icon-class="line-md" />
                                        </span>
                                        <span class="divider-vertical"></span>
                                        <span @click="onFixedRight(idx)" class="line-right"  :class="item?.fixed=='right'?'active':''">
                                            <svg-icon icon-class="line-md" />
                                        </span>
                                        </el-col>
                                    </el-row>
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <el-tooltip class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top" v-if="extendSearch">
                    <el-button @click="toggleSearch()" class="table-search-button-item" color="#dcdfe6" plain>
                        <icon size="14" name="el-icon-Search" />
                    </el-button>
                </el-tooltip>
            </div>
        </el-row>
    </div>
    </div>
</template>


<script setup lang="ts">
import { computed, onMounted, reactive, ref, nextTick, onUnmounted, unref } from "vue"
import { debounce } from '/@/utils/common'
import Sortablejs from 'sortablejs'
import { cloneDeep } from "lodash"
import { ElDropdownMenu } from "element-plus"

const rightToolbarDropdownMenuRef = ref<InstanceType<typeof ElDropdownMenu>>()

const props = defineProps({
    showSearch: {
        type: Boolean,
        default: false,
    },
    columns: {
        type: Array
    },
    gutter: {
        type: Number,
        default: 10,
    },
    extendSearch: {
        type: Boolean,
        default: false,
    },
    quickSearchShow: {
        type: Boolean,
        default: false,
    },
    quickSearchPlaceholder: {
        type: String,
        default: '',
    }
})
const emits = defineEmits(['update:showSearch', 'quickSearch'])

const state = reactive({
    keyword: ''
})

const style = computed(() => {
    const ret = { marginRight: ''}
    if (props.gutter) {
        ret.marginRight = `${props.gutter / 2}px`
    }
    return ret
})

onMounted(()=>{
    nextTick(()=>{
        //console.log(document.querySelectorAll('.dropdown-column'))
        const columnListEl = unref(rightToolbarDropdownMenuRef)
        if (!columnListEl) return
        const el = columnListEl.$el as any
        if (!el) return
        Sortablejs.create(unref(el), {
            animation: 500,
            delay: 400,
            delayOnTouchOnly: true,
            handle: '.table-column-drag-icon',
            onEnd: (evt) => {
                const { oldIndex, newIndex } = evt
                if (oldIndex === undefined || newIndex === undefined || oldIndex === newIndex) {
                    return;
                }
                const cols = cloneDeep(props.columns)
                if (oldIndex > newIndex) {
                    props.columns.splice(oldIndex, 1)
                    props.columns.splice(newIndex, 0, cols[oldIndex])
                } else {
                    props.columns.splice(newIndex + 1, 0, props.columns[oldIndex])
                    props.columns.splice(oldIndex, 1)
                }
            }
        })
    })
})

// 显示高级搜索
const toggleSearch = () => {
    emits("update:showSearch", !props.showSearch)
}

// 搜索
const onSearchInput = () => {
    emits("quickSearch", { keyword: state.keyword })
}

// 刷新
const onRefresh = () => {
    emits("quickSearch", { keyword: state.keyword })
}

const onChangeShowColumn = (event: boolean, idx: number) =>{
    props.columns[idx].show = event
}

const onFixedLeft = (idx: number) => {
    if(props.columns[idx].fixed != 'left'){
        props.columns[idx].fixed = 'left'
    } else {
        props.columns[idx].fixed = false
    }
}

const onFixedRight = (idx: number) => {
    if(props.columns[idx].fixed != 'right'){
        props.columns[idx].fixed = 'right'
    } else {
        props.columns[idx].fixed = false
    }
}

</script>

<style scoped lang="scss">
.table-header {
    position: relative;
    overflow: hidden;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    width: 100%;
    max-width: 100%;
    background-color: var(--ba-bg-color-overlay);
    border: 1px solid var(--ba-border-color);
    border-bottom: none;
    padding: 13px 15px;
    font-size: 14px;
    .table-header-operate-text {
        margin-left: 6px;
    }
}

.dropdown-column {
    min-width: 240px;
}
.dropdown-column-item {
    width: 100%;
}
.dropdown-column-item-right {
    display: flex;
    text-align: right;
    align-items: center;
    right: 2px;
    position: relative;
}
.line-left {
    &.active {
        color: var(--el-color-primary);
    }
}
.line-right {
    &.active {
        color: var(--el-color-primary);
    }
    &>.svg-icon{
    transform: rotate(180deg);
    }
}
.divider-vertical {
    position: relative;
    top: -0.06em;
    display: inline-block;
    height: 0.9em;
    margin: 0 8px;
    vertical-align: middle;
    border-top: 0;
    border-left: 1px solid rgba(0,0,0,.06);
}

.table-column-drag-icon {
    margin: 0 5px;
    cursor: move;
}

.mlr-12 {
    margin-left: 12px;
}
.mlr-12 + .el-button {
    margin-left: 12px;
}
.table-search {
    display: flex;
    margin-left: auto;
    .quick-search {
        width: auto;
    }
}
.table-search-button-group {
    display: flex;
    margin-left: 12px;
    border: 1px solid var(--el-border-color);
    border-radius: var(--el-border-radius-base);
    overflow: hidden;
    button:focus,
    button:active {
        background-color: var(--ba-bg-color-overlay);
    }
    button:hover {
        background-color: var(--el-color-info-light-7);
    }
    .table-search-button-item {
        border: none;
        border-radius: 0;
    }
    .el-button + .el-button {
        margin: 0;
    }
    .right-border {
        border-right: 1px solid var(--el-border-color);
    }
}

html.dark {
    .table-search-button-group {
        button:focus,
        button:active {
            background-color: var(--el-color-info-dark-2);
        }
        button:hover {
            background-color: var(--el-color-info-light-7);
        }
        button {
            background-color: #898a8d;
            el-icon {
                color: white !important;
            }
        }
    }
}
</style>
