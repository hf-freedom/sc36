<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <template #header>
        <span>消费核销</span>
      </template>
      
      <el-tabs v-model="consumeType">
        <el-tab-pane label="储值消费" name="stored">
          <el-form :model="storedForm" label-width="120px" style="max-width: 500px">
            <el-form-item label="会员ID">
              <el-input v-model="storedForm.memberId" placeholder="请输入会员ID" />
              <el-button type="primary" link @click="loadMemberCards('stored')">加载可用卡</el-button>
            </el-form-item>
            <el-form-item label="选择卡">
              <el-select v-model="storedForm.cardId" placeholder="请选择储值卡" style="width: 100%" @change="onCardChange">
                <el-option
                  v-for="card in availableStoredCards"
                  :key="card.id"
                  :label="`${card.name} - 本金:¥${card.remainingPrincipal} 赠送:¥${card.remainingGift}`"
                  :value="card.id"
                />
              </el-select>
              <div v-if="selectedStoredCard" style="margin-top: 10px; padding: 10px; background: #f5f7fa; border-radius: 4px">
                <p>本金余额: <span style="color: #f56c6c; font-weight: bold">¥{{ selectedStoredCard.remainingPrincipal }}</span></p>
                <p>赠送余额: <span style="color: #409eff; font-weight: bold">¥{{ selectedStoredCard.remainingGift }}</span></p>
                <p style="color: #909399; font-size: 12px">* 消费时优先扣除赠送金额</p>
              </div>
            </el-form-item>
            <el-form-item label="门店ID">
              <el-input v-model="storedForm.storeId" placeholder="请输入门店ID" />
            </el-form-item>
            <el-form-item label="服务项目">
              <el-input v-model="storedForm.serviceName" placeholder="请输入服务名称" />
            </el-form-item>
            <el-form-item label="消费金额">
              <el-input-number v-model="storedForm.amount" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
            <el-form-item label="幂等键(可选)">
              <el-input v-model="storedForm.idempotentKey" placeholder="用于防止重复消费" />
              <el-button type="text" @click="generateIdempotentKey">自动生成</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="consumeStoredValue">确认消费</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="套餐核销" name="package">
          <el-form :model="packageForm" label-width="120px" style="max-width: 500px">
            <el-form-item label="会员ID">
              <el-input v-model="packageForm.memberId" placeholder="请输入会员ID" />
              <el-button type="primary" link @click="loadMemberCards('package')">加载可用卡</el-button>
            </el-form-item>
            <el-form-item label="服务项目ID">
              <el-input v-model="packageForm.serviceId" placeholder="请输入服务项目ID" />
              <el-button type="primary" link @click="searchCardsByService">按服务查找</el-button>
            </el-form-item>
            <el-form-item label="选择卡">
              <el-select v-model="packageForm.cardId" placeholder="请选择套餐卡" style="width: 100%" @change="onPackageCardChange">
                <el-option
                  v-for="card in availablePackageCards"
                  :key="card.id"
                  :label="`${card.name} - 剩余${card.remainingTimes}次`"
                  :value="card.id"
                />
              </el-select>
              <div v-if="selectedPackageCard" style="margin-top: 10px; padding: 10px; background: #f5f7fa; border-radius: 4px">
                <p>套餐名称: {{ selectedPackageCard.name }}</p>
                <p>总次数: {{ selectedPackageCard.totalTimes }}</p>
                <p>剩余次数: <span :style="{ color: selectedPackageCard.remainingTimes > 0 ? '#67c23a' : '#f56c6c', fontWeight: 'bold' }">
                  {{ selectedPackageCard.remainingTimes }}
                </span></p>
              </div>
            </el-form-item>
            <el-form-item label="门店ID">
              <el-input v-model="packageForm.storeId" placeholder="请输入门店ID" />
            </el-form-item>
            <el-form-item label="服务名称">
              <el-input v-model="packageForm.serviceName" placeholder="请输入服务名称" />
            </el-form-item>
            <el-form-item label="核销次数">
              <el-input-number v-model="packageForm.times" :min="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="幂等键(可选)">
              <el-input v-model="packageForm.idempotentKey" placeholder="用于防止重复核销" />
              <el-button type="text" @click="generatePackageIdempotentKey">自动生成</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="consumePackage">确认核销</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-card>
      <template #header>
        <span>消费记录查询</span>
      </template>
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="幂等键">
          <el-input v-model="searchIdempotentKey" placeholder="输入幂等键查询" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchByIdempotentKey">查询</el-button>
        </el-form-item>
      </el-form>
      
      <el-descriptions v-if="searchResult" title="查询结果" border>
        <el-descriptions-item label="消费ID">{{ searchResult.id }}</el-descriptions-item>
        <el-descriptions-item label="卡类型">{{ searchResult.cardType === 'STORED_VALUE' ? '储值卡' : '套餐卡' }}</el-descriptions-item>
        <el-descriptions-item label="服务">{{ searchResult.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ searchResult.amount }}</el-descriptions-item>
        <el-descriptions-item label="使用本金">¥{{ searchResult.principalUsed }}</el-descriptions-item>
        <el-descriptions-item label="使用赠送">¥{{ searchResult.giftUsed }}</el-descriptions-item>
        <el-descriptions-item label="使用次数">{{ searchResult.timesUsed }}</el-descriptions-item>
        <el-descriptions-item label="是否退款">
          <el-tag :type="searchResult.refunded ? 'danger' : 'success'">
            {{ searchResult.refunded ? '已退款' : '正常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消费时间">{{ searchResult.consumedAt }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else-if="searchResult !== null" description="未找到消费记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { consumptionApi, storedValueCardApi, packageCardApi } from '../api'

const consumeType = ref('stored')
const availableStoredCards = ref([])
const availablePackageCards = ref([])
const searchResult = ref(null)
const searchIdempotentKey = ref('')

const selectedStoredCard = computed(() => {
  return availableStoredCards.value.find(c => c.id === storedForm.cardId)
})

const selectedPackageCard = computed(() => {
  return availablePackageCards.value.find(c => c.id === packageForm.cardId)
})

const storedForm = reactive({
  memberId: '',
  cardId: '',
  storeId: 'store001',
  serviceId: '',
  serviceName: '',
  amount: 0,
  idempotentKey: ''
})

const packageForm = reactive({
  memberId: '',
  cardId: '',
  storeId: 'store001',
  serviceId: '',
  serviceName: '',
  times: 1,
  idempotentKey: ''
})

const generateIdempotentKey = () => {
  storedForm.idempotentKey = 'STORED_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const generatePackageIdempotentKey = () => {
  packageForm.idempotentKey = 'PKG_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

const loadMemberCards = async (type) => {
  const memberId = type === 'stored' ? storedForm.memberId : packageForm.memberId
  if (!memberId) {
    ElMessage.warning('请先输入会员ID')
    return
  }

  try {
    if (type === 'stored') {
      const res = await storedValueCardApi.getActiveByMember(memberId)
      if (res.data.success) {
        availableStoredCards.value = res.data.data
        if (availableStoredCards.value.length === 0) {
          ElMessage.warning('该会员没有可用的储值卡')
        }
      }
    } else {
      const res = await packageCardApi.getActiveByMember(memberId)
      if (res.data.success) {
        availablePackageCards.value = res.data.data
        if (availablePackageCards.value.length === 0) {
          ElMessage.warning('该会员没有可用的套餐卡')
        }
      }
    }
  } catch (error) {
    ElMessage.error('加载卡列表失败')
  }
}

const searchCardsByService = async () => {
  if (!packageForm.memberId || !packageForm.serviceId) {
    ElMessage.warning('请输入会员ID和服务项目ID')
    return
  }

  try {
    const res = await packageCardApi.getByMemberAndService(packageForm.memberId, packageForm.serviceId)
    if (res.data.success) {
      availablePackageCards.value = res.data.data
      if (availablePackageCards.value.length === 0) {
        ElMessage.warning('该会员没有可用此服务的套餐卡')
      }
    }
  } catch (error) {
    ElMessage.error('查找套餐卡失败')
  }
}

const onCardChange = () => {
  // card changed
}

const onPackageCardChange = () => {
  // card changed
}

const consumeStoredValue = async () => {
  if (!storedForm.memberId || !storedForm.cardId || !storedForm.amount || !storedForm.serviceName) {
    ElMessage.warning('请填写必要信息')
    return
  }

  try {
    const res = await consumptionApi.consumeStoredValue({
      memberId: storedForm.memberId,
      cardId: storedForm.cardId,
      storeId: storedForm.storeId,
      serviceId: storedForm.serviceId || storedForm.serviceName,
      serviceName: storedForm.serviceName,
      amount: storedForm.amount,
      idempotentKey: storedForm.idempotentKey
    })

    if (res.data.success) {
      ElMessage.success('消费成功')
      searchResult.value = res.data.data
      loadMemberCards('stored')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('消费失败')
  }
}

const consumePackage = async () => {
  if (!packageForm.memberId || !packageForm.cardId || !packageForm.serviceName || !packageForm.times) {
    ElMessage.warning('请填写必要信息')
    return
  }

  try {
    const res = await consumptionApi.consumePackage({
      memberId: packageForm.memberId,
      cardId: packageForm.cardId,
      storeId: packageForm.storeId,
      serviceId: packageForm.serviceId || packageForm.serviceName,
      serviceName: packageForm.serviceName,
      times: packageForm.times,
      idempotentKey: packageForm.idempotentKey
    })

    if (res.data.success) {
      ElMessage.success('核销成功')
      searchResult.value = res.data.data
      loadMemberCards('package')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('核销失败')
  }
}

const searchByIdempotentKey = async () => {
  if (!searchIdempotentKey.value) {
    ElMessage.warning('请输入幂等键')
    return
  }

  try {
    const res = await consumptionApi.getByIdempotentKey(searchIdempotentKey.value)
    if (res.data.success) {
      searchResult.value = res.data.data
    } else {
      searchResult.value = null
      ElMessage.warning('未找到消费记录')
    }
  } catch (error) {
    searchResult.value = null
    ElMessage.error('查询失败')
  }
}
</script>
