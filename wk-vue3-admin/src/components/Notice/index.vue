<template>
        <div>
            <el-popover
            placement="bottom-end"
            :hide-after="0"
            :width="260"
            trigger="click"
            popper-class="user-info-box"
        >
            <template #reference>
                <div class="size-icon--style">
                    <svg-icon class-name="size-icon" icon-class="bell"/>
                </div>
            </template>
            <div>
                sss
                </div>
                </el-popover>
    </div>
</template>
<script setup lang="ts">

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