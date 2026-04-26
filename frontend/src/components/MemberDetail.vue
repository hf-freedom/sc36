<template>
  <div>
    <el-button @click="$router.back()" style="margin-bottom: 20px">
      <el-icon><ArrowLeft /></el-icon>
      返回
    </el-button>

    <el-card v-if="member" style="margin-bottom: 20px">
      <template #header>
        <span>会员信息</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="姓名">{{ member.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ member.phone }}</el-descriptions-item>
        <el-descriptions-item label="等级">
          <el-tag :type="getLevelTagType(member.level)">
            {{ getLevelText(member.level) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="本金余额">
          <span style="color: #f56c6c; font-weight: bold; font-size: 18px">
            ¥{{ member.balance }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="赠送余额">
          <span style="color: #409eff; font-weight: bold; font-size: 18px">
            ¥{{ member.giftedBalance }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ member.createdAt }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="储值卡" name="stored">
        <el-table :data="storedCards" style="width: 100%">
          <el-table-column prop="name" label="卡名称" />
          <el-table-column prop="rechargeAmount" label="充值金额">
            <template #default="scope">¥{{ scope.row.rechargeAmount }}</template>
          </el-table-column>
          <el-table-column prop="giftedAmount" label="赠送金额">
            <template #default="scope">¥{{ scope.row.giftedAmount }}</template>
          </el-table-column>
          <el-table-column prop="remainingPrincipal" label="剩余本金">
            <template #default="scope">¥{{ scope.row.remainingPrincipal }}</template>
          </el-table-column>
          <el-table-column prop="remainingGift" label="剩余赠送">
            <template #default="scope">¥{{ scope.row.remainingGift }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="validTo" label="有效期至" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="套餐卡" name="package">
        <el-table :data="packageCards" style="width: 100%">
          <el-table-column prop="name" label="卡名称" />
          <el-table-column prop="price" label="购买价格">
            <template #default="scope">¥{{ scope.row.price }}</template>
          </el-table-column>
          <el-table-column prop="totalTimes" label="总次数" />
          <el-table-column prop="remainingTimes" label="剩余次数">
            <template #default="scope">
              <span :style="{ color: scope.row.remainingTimes > 0 ? '#67c23a' : '#f56c6c' }">
                {{ scope.row.remainingTimes }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="validTo" label="有效期至" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { memberApi, storedValueCardApi, packageCardApi } from '../api'

const route = useRoute()
const member = ref(null)
const storedCards = ref([])
const packageCards = ref([])
const activeTab = ref('stored')

const getLevelText = (level) => {
  const map = {
    NORMAL: '普通会员',
    SILVER: '银卡会员',
    GOLD: '金卡会员',
    PLATINUM: '白金会员'
  }
  return map[level] || level
}

const getLevelTagType = (level) => {
  const map = {
    NORMAL: '',
    SILVER: 'info',
    GOLD: 'warning',
    PLATINUM: 'danger'
  }
  return map[level] || ''
}

const getStatusText = (status) => {
  const map = {
    ACTIVE: '激活',
    USED: '已用完',
    EXPIRED: '已过期',
    FROZEN: '已冻结',
    REFUNDED: '已退款'
  }
  return map[status] || status
}

const getStatusTagType = (status) => {
  const map = {
    ACTIVE: 'success',
    USED: '',
    EXPIRED: 'info',
    FROZEN: 'warning',
    REFUNDED: 'danger'
  }
  return map[status] || ''
}

const loadMember = async () => {
  try {
    const res = await memberApi.getById(route.params.id)
    if (res.data.success) {
      member.value = res.data.data
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('加载会员信息失败')
  }
}

const loadCards = async () => {
  try {
    const [storedRes, packageRes] = await Promise.all([
      storedValueCardApi.getByMember(route.params.id),
      packageCardApi.getByMember(route.params.id)
    ])
    
    if (storedRes.data.success) {
      storedCards.value = storedRes.data.data
    }
    if (packageRes.data.success) {
      packageCards.value = packageRes.data.data
    }
  } catch (error) {
    console.error('加载卡列表失败', error)
  }
}

onMounted(() => {
  loadMember()
  loadCards()
})
</script>
