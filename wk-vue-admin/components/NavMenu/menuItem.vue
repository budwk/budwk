<template>
    <div class="c-menu-item-wrapper">
        <el-menu-item
            v-if="!menuItem.hasChildren && menuItem.showit"
            :key="menuItem.path"
            :index="menuItem.url"
            class="c-menu-item"
        >
            <i :class="`${menuItem.icon}`" /><span slot="title">
                {{ menuItem.name }}
            </span>
        </el-menu-item>

        <el-submenu
            v-else-if="menuItem.showit"
            :key="menuItem.path"
            class="c-submenu"
            :index="menuItem.url ? menuItem.url : menuItem.id"
        >
            <template slot="title">
                <i :class="`${menuItem.icon}`" /><span slot="title">
                    {{ menuItem.name }}
                </span>
            </template>

            <menu-item
                v-for="child in menuItem.children"
                :key="child.path"
                :menu-item="child"
            />
        </el-submenu>
    </div>
</template>

<script>
export default {
    name: "MenuItem",
    props: {
        menuItem: {
            type: Object,
            default() {
                return {}
            }
        },
        menuIndex: {
            type: String,
            default: ""
        }
    },
    data() {
        return {}
    },
    computed: {},
    watch: {},
    mounted() {},
    methods: {}
}
</script>
