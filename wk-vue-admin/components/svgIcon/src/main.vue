<template>
    <svg class="icon-svg"><use :xlink:href="icon" /></svg>
</template>

<script>
export default {
    name: "SvgIcon",
    props: {
        isIconfont: {
            type: Boolean,
            default: true
        },
        iconName: {
            type: String,
            default: ""
        }
    },
    computed: {
        icon() {
            return this.isIconfont
                ? `#${this.iconName}`
                : `icons.svg#${this.iconName}`
        }
    }
}
</script>
