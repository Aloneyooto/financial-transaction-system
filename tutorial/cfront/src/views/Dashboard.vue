<template>
  <div>
    <div class="crumbs">
        <!-- 提示文字 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>
              <i class="el-icon-pie-chart"></i>资金股份
          </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
    <!-- 资金持仓 -->
    <el-card class="container">
        <posi-list/>
    </el-card>
  </div>
</template>

<script>
  import PosiList from "../components/PosiList"

  export default {
    name: 'Dashboard',
    components: {
        PosiList,
    }
  }
</script>
