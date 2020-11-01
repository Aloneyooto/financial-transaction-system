<template>
  <!-- 一个模板只能有一个主标签 -->
  <div class="wrapper">
    <!-- 引入头部导航栏 -->
    <v-header></v-header>
    <!-- 引入侧边导航栏 -->
    <v-sidebar></v-sidebar>
    <!-- 显示嵌入的子页面 -->
    <!-- <router-view></router-view> -->
    <div class="content-box" :class="{'content-collapse':collapse}">
      <div class="content">
        <!-- 一个平滑的过渡 -->
        <transition name="move" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>

  import vHeader from '../components/Header.vue'
  import vSidebar from '../components/Sidebar.vue'

  export default {
    name: 'Home',
    data() {
        return {
            collapse: false,
        }
    },
    components : {
      vHeader,
      vSidebar,
    },
    created() {
      this.$bus.on("collapse-content", msg => {
          this.collapse = msg;
      });
    },
    beforeDestroy() {
      this.$bus.off("collapse-content", msg => {
          this.collapse = msg;
      });
    }
  }
</script>
