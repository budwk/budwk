<template>
  <div class="container">
    <h1 v-if="error.statusCode === 404">
      页面不存在
    </h1>
    <h1 v-else>
      应用发生错误异常
      {{ error }}
    </h1>
    <nuxt-link to="/">
      首 页
    </nuxt-link>
  </div>
</template>

<script>
export default {
  layout({ store }) {
    return store.state.auth ? 'default' : 'errorLayout'
  },
  props: {
    error: {
      type: Object,
      default() {
        return {}
      }
    }
  }
}
</script>
