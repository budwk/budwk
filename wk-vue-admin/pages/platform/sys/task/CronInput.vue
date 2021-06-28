<template>
  <div>
    <el-input
      :value="cron"
      :size="size"
      :readonly="inputReadOnly"
      placeholder="Cron表达式"
      @input="handleInput"
    >
      <el-button
        slot="append"
        icon="el-icon-setting"
        :disabled="buttonDisabled"
        @click="cronExpDialog"
      />
    </el-input>

    <el-dialog
      v-if="dialogCronExpVisible"
      :visible.sync="dialogCronExpVisible"
      :close-on-click-modal="false"
      append-to-body
    >
      <vcrontab :expression="cron" @hide="dialogCronExpVisible=false" @fill="saveCron" />
    </el-dialog>
  </div>
</template>

<script>
import vcrontab from 'vcrontab'

export default {
  name: 'CronInput',
  components: {
    vcrontab
  },
  model: {
    // 双向绑定
    prop: 'cron',
    event: 'change'
  },
  props: {
    // 输入域大小
    size: {
      type: String,
      default: ''
    },
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
    // 双向绑定
    cron: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      dialogCronExpVisible: false
    }
  },
  methods: {
    // 打开 cron 表达式选择窗口
    cronExpDialog() {
      this.dialogCronExpVisible = true
    },
    cancelCron() {
      this.dialogCronExpVisible = false
    },
    handleInput(value) {
      this.$emit('change', value)
    },
    // 保存cron
    saveCron(cron) {
      this.$emit('change', cron)
      this.dialogCronExpVisible = false
    }
  }
}
</script>
