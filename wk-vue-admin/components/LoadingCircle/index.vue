<template>
    <div class="c-loading-circle">
        <el-dialog
            :title="title"
            :visible.sync="dialogVisible"
            width="26%"
            @closed="handleExportDialogClose"
        >
            <div class="dialog-content" style="text-align: center">
                <div class="circle-loader" :class="circleClass">
                    <div class="draw" :class="symbolClass" />
                </div>

                <p v-if="dialogStatus === 'loading'">
                    {{ loadingTxt }}
                </p>
                <p v-else-if="dialogStatus === 'success'">
                    {{ loadingSuccessTxt }}
                </p>
                <p v-else-if="dialogStatus === 'failed'">
                    {{ loadingFailedTxt }}
                </p>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "LoadingCircle",
    props: {
        title: {
            type: String,
            default: "导出订单"
        },
        visible: {
            type: Boolean,
            default: false
        },
        loading: {
            type: String,
            default: "loading"
        },
        loadingTxt: {
            type: String,
            default: "订单导出中..."
        },
        loadingSuccessTxt: {
            type: String,
            default: "导出成功"
        },
        loadingFailedTxt: {
            type: String,
            default: "导出失败"
        }
    },
    data() {
        return {
            dialogVisible: null,
            dialogStatus: null
        }
    },
    computed: {
        circleClass() {
            if (this.dialogStatus === "loading") {
                return ""
            } else if (this.dialogStatus === "success") {
                return "success load-complete"
            } else if (this.dialogStatus === "failed") {
                return "failed load-complete"
            } else {
                return ""
            }
        },
        symbolClass() {
            if (this.dialogStatus === "success") {
                return "checkmark"
            } else if (this.dialogStatus === "failed") {
                return "errormark"
            } else {
                return ""
            }
        }
    },
    watch: {
        visible(val) {
            this.dialogVisible = val
        },
        loading(val) {
            this.dialogStatus = val
        }
    },
    mounted() {
        this.dialogVisible = this.visible
        this.dialogStatus = this.loading
    },
    methods: {
        handleExportDialogClose() {
            this.$emit("update:visible", false)
            this.$emit("update:loading", "loading")
        }
    }
}
</script>
