<template>
    <div
        :ref="scrollKey"
        class="scroll-block"
        :scroll-key="scrollKey"
        :scroll-distance="distance"
    >
        <slot />
    </div>
</template>

<script>
export default {
    name: "ScrollBlock",
    props: {
        scrollKey: {
            type: String,
            default: "",
            required: true
        },
        distance: {
            type: Number,
            default: 0,
            required: false
        }
    }
}
</script>
