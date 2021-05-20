<template>
    <div class="c-drawer" :class="drawerVisible ? 'c-drawer-open' : ''">
        <div v-if="mask" class="c-drawer-mask" @click="close" />
        <div
            class="c-drawer-wrap"
            :class="placementClass"
            :style="`width:${width}px;${translateStatus}`"
        >
            <div class="c-drawer-container" :style="containerStyles">
                <div v-if="title === ''" class="c-drawer-header-notitle">
                    <i
                        class="iconfont iconguanbi1 c-drawer-close"
                        @click="close"
                    />
                </div>
                <div v-else class="c-drawer-header">
                    <span>{{ title }}</span>
                    <i class="el-icon-close c-drawer-close" @click="close" />
                </div>
                <div class="c-drawer-body">
                    <slot />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "Drawer",
    props: {
        maskClosable: {
            type: Boolean,
            default: true
        },
        mask: {
            type: Boolean,
            default: true
        },
        title: {
            type: String,
            default: ""
        },
        visible: {
            type: Boolean,
            default: false
        },
        placement: {
            type: String,
            default: "right"
        },
        width: {
            type: Number,
            default: 300
        },
        handle: {
            type: Boolean,
            default: false
        },
        handleClass: {
            type: String,
            default: ""
        }
    },
    data() {
        return {
            drawerVisible: false
        }
    },
    computed: {
        containerStyles() {
            if (this.visible) {
                return "overflow: auto; height: 100%;"
            }

            return ""
        },
        placementClass() {
            switch (this.placement) {
                case "left":
                    return "c-drawer-left"
                case "right":
                    return "c-drawer-right"
                default:
                    return "c-drawer-right"
            }
        },
        translateStatus() {
            if (this.drawerVisible) {
                return ""
            } else {
                switch (this.placement) {
                    case "left":
                        return "transform: translateX(-100%);"
                    case "right":
                        return "transform: translateX(100%);"
                    default:
                        return ""
                }
            }
        }
    },
    watch: {
        visible(val) {
            this.drawerVisible = val

            document.body.style =
                "overflow: auto; touch-action: none; position: relative; width: calc(100% - 15px);"
        }
    },
    mounted() {
        this.drawerVisible = this.visible
    },
    methods: {
        close() {
            if (this.maskClosable) {
                this.drawerVisible = false
                this.$emit("update:visible", false)
            } else {
                return false
            }
        },
        toogle() {
            this.drawerVisible = !this.drawerVisible
        }
    }
}
</script>
