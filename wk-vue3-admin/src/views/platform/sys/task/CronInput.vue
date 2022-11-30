<template>
    <div>
        <el-input :value="cron" :readonly="inputReadOnly" placeholder="Cron表达式">
            <template #append>
                <el-button icon="Setting" :disabled="buttonDisabled" @click="cronExpDialog" />
            </template>
        </el-input>
        <el-dialog v-model="dialogCronExpVisible" :close-on-click-modal="false" width="45%">
            <vcrontab :expression="cron" @hide="dialogCronExpVisible=false" @fill="saveCron" />
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="saveCron">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
  
<script setup lang="ts">
import { computed, ref } from 'vue'
import vcrontab from '/@/components/CronTab/index.vue'

const dialogCronExpVisible = ref(false)
const cron = computed(()=>{ return props.modelValue})

const emits = defineEmits(['update:modelValue'])

const props = defineProps({
    // 是否禁用button
    buttonDisabled: {
        type: Boolean,
        default: false
    },
    // 是否只读input
    inputReadOnly: {
        type: Boolean,
        default: false
    },
    modelValue: {
        type: String
    }
})

const cronExpDialog = () => {
    dialogCronExpVisible.value = true
}

const saveCron = (val: string) => {
    emits('update:modelValue', val)
    dialogCronExpVisible.value = false
}

</script>
  