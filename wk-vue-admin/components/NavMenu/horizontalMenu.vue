<template>
    <el-menu
        ref="menu"
        :router="true"
        :default-active="menuActive"
        mode="horizontal"
        class="c-horizontal-menu"
        @select="handleSelect"
        @open="handleOpen"
        @close="handleClose"
    >
        <MenuItem
            v-for="item in menuData"
            :key="item.path"
            :menu-item="item"
            :menu-index="item.path"
        />
    </el-menu>
</template>

<script>
import MenuItem from "@/components/NavMenu/menuItem"

export default {
    name: "HorizontalMenu",
    components: {
        MenuItem
    },
    props: {
        defaultActive: {
            type: String,
            default: ""
        },
        menuData: {
            type: Object,
            default() {
                return {}
            }
        },
        isOverflow: {
            type: Boolean,
            default: false
        },
        overflowTxt: {
            type: String,
            default: "..."
        },
        overflowNum: {
            type: Number,
            default: 3
        }
    },
    data() {
        return {
            menuActive: ""
        }
    },
    computed: {},
    watch: {
        defaultActive(val) {
            this.$refs.menu.activeIndex = val
        }
    },
    mounted() {
        this.menuActive = this.defaultActive
    },
    methods: {
        handleSelect(value) {
            this.$emit("select", value)
        },
        handleOpen(value) {
            this.$emit("open", value)
        },
        handleClose(value) {
            this.$emit("close", value)
        }
    }
}
</script>
