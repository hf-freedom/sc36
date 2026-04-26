<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #545c64">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/members">
          <el-icon><User /></el-icon>
          <span>会员管理</span>
        </el-menu-item>
        <el-menu-item index="/cards">
          <el-icon><Wallet /></el-icon>
          <span>卡管理</span>
        </el-menu-item>
        <el-menu-item index="/consumption">
          <el-icon><ShoppingCart /></el-icon>
          <span>消费核销</span>
        </el-menu-item>
        <el-menu-item index="/refunds">
          <el-icon><Refresh /></el-icon>
          <span>退款管理</span>
        </el-menu-item>
        <el-menu-item index="/reports">
          <el-icon><Document /></el-icon>
          <span>对账报表</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="text-align: right; font-size: 18px; border-bottom: 1px solid #eee; background: #fff; line-height: 60px; padding: 0 20px">
        <span style="font-weight: bold">会员储值卡系统</span>
      </el-header>
      <el-main style="background: #f0f2f5; padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, Wallet, ShoppingCart, Refresh, Document } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
}
</style>
