<template>
  <!-- 根组件只放一个出口：路由匹配到哪个页面，就在这里渲染哪个 -->
  <router-view />
</template>

<style>
/* 没有 scoped：这里是全局基础样式，给整个页面铺满高度、统一字体 */
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
}
</style>
