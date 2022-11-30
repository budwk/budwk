<template>
    <div>
      <el-dropdown trigger="click" @command="handleSetSize">
        <div class="size-icon--style">
          <svg-icon class-name="size-icon" icon-class="fontsize" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="item of sizeOptions" :key="item.value" :disabled="size === item.value" :command="item.value">
              {{ item.label }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </template>
  
<script setup lang="ts">
import { computed, getCurrentInstance, ref } from 'vue';
import { useClient } from '/@/stores/client'
import modal from '/@/utils/modal'
  
const client = useClient()
const size = computed(() => client.size)
const sizeOptions = ref([
    { label: "较大", value: "large" },
    { label: "默认", value: "default" },
    { label: "稍小", value: "small" },
]);
  
function handleSetSize(size: any) {
    modal.loading("正在设置布局大小，请稍候...");
    client.setSize(size);
    setTimeout("window.location.reload()", 1000);
}
</script>
  
  <style lang='scss' scoped>
  .size-icon--style {
    font-size: 18px;
    line-height: 50px;
    padding-right: 7px;
    &:hover{
      .svg-icon{
        animation: twinkle 0.3s ease-in-out;
      }
    }
  }
  </style>