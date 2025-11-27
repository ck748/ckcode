<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import sseManager from '@/utils/sseManager';

export default {
  name: 'App',
  mounted() {
    // 应用启动时初始化全局SSE连接
    sseManager.init();
  },
  beforeDestroy() {
    // 应用关闭时关闭SSE连接
    sseManager.close();
  }
}
</script>



