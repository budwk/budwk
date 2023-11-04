<template>
    <transition-group name="fade-transform">
      <inner-link
        v-for="(item, index) in tagsViewStore.iframeViews"
        :key="item.path"
        :iframeId="'iframe' + index"
        v-show="route.path === item.path"
        :src="item.meta.link"
      ></inner-link>
    </transition-group>
  </template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import InnerLink from '../InnerLink/index.vue'
import { useTagsView } from '/@/stores/tagsView'

const route = useRoute()
const tagsViewStore = useTagsView()
</script>
