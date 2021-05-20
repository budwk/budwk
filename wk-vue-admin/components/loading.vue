<template lang="html">
    <div>loading...</div>
</template>

<script>
export default {
    data: () => ({
        pageLoading: null
    }),
    methods: {
        start() {
            this.pageLoading = this.$loading({
                lock: true,
                text: "Loading",
                spinner: "el-icon-loading",
                background: "rgba(0, 0, 0, 0.7)"
            })
        },
        finish() {
            this.pageLoading.close()
        }
    }
}
</script>

<style scoped lang="scss">
.loading-page {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.8);
    text-align: center;
    font-size: 30px;
    font-family: sans-serif;
    z-index: 100;
    p {
        margin-top: 200px;
    }
}
</style>
