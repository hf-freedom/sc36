<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>开卡管理</span>
          <el-radio-group v-model="cardType" size="small">
            <el-radio-button value="stored">储值卡</el-radio-button>
            <el-radio-button value="package">套餐卡</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-form :model="form" label-width="120px" style="max-width: 600px">
        <el-form-item label="会员ID">
          <el-input v-model="form.memberId" placeholder="请输入会员ID" />
          <el-button type="text" @click="searchMember">根据手机号查找</el-button>
        </el-form-item>
        
        <el-form-item label="卡名称">
          <el-input v-model="form.name" placeholder="请输入卡名称" />
        </el-form-item>

        <template v-if="cardType === 'stored'">
          <el-form-item label="充值金额">
            <el-input-number v-model="form.rechargeAmount" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="赠送金额">
            <el-input-number v-model="form.giftedAmount" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item label="套餐ID">
            <el-input v-model="form.packageId" placeholder="请输入套餐ID" />
          </el-form-item>
          <el-form-item label="购买价格">
            <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="总次数">
            <el-input-number v-model="form.totalTimes" :min="1" style="width: 100%" />
          </el-form-item>
          <el-form-item label="服务项目ID">
            <el-input v-model="form.serviceIds" placeholder="多个用逗号分隔" />
          </el-form-item>
        </template>

        <el-form-item label="有效期开始">
          <el-date-picker
            v-model="form.validFrom"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="有效期结束">
          <el-date-picker
            v-model="form.validTo"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="createCard">创建卡</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog v-model="showSearchDialog" title="根据手机号查找会员" width="400px">
      <el-form label-width="80px">
        <el-form-item label="手机号">
          <el-input v-model="searchPhone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSearchDialog = false">取消</el-button>
        <el-button type="primary" @click="searchMemberByPhone">查找</el-button>
      </template>
    </el-dialog>

    <el-card>
      <template #header>
        <span>已开卡列表</span>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="储值卡" name="stored">
          <el-table :data="storedCards" style="width: 100%">
            <el-table-column prop="memberId" label="会员ID" width="180" />
            <el-table-column prop="name" label="卡名称" />
            <el-table-column prop="rechargeAmount" label="充值">
              <template #default="scope">¥{{ scope.row.rechargeAmount }}</template>
            </el-table-column>
            <el-table-column prop="remainingPrincipal" label="剩余本金">
              <template #default="scope">¥{{ scope.row.remainingPrincipal }}</template>
            </el-table-column>
            <el-table-column prop="remainingGift" label="剩余赠送">
              <template #default="scope">¥{{ scope.row.remainingGift }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="danger" link @click="refundStoredCard(scope.row)" :disabled="scope.row.status !== 'ACTIVE' && scope.row.status !== 'EXPIRED'">
                  退卡
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="套餐卡" name="package">
          <el-table :data="packageCards" style="width: 100%">
            <el-table-column prop="memberId" label="会员ID" width="180" />
            <el-table-column prop="name" label="卡名称" />
            <el-table-column prop="price" label="价格">
              <template #default="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="totalTimes" label="总次数" />
            <el-table-column prop="remainingTimes" label="剩余" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="danger" link @click="refundPackageCard(scope.row)" :disabled="scope.row.status !== 'ACTIVE' && scope.row.status !== 'EXPIRED'">
                  退卡
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { storedValueCardApi, packageCardApi, memberApi } from '../api'

const cardType = ref('stored')
const activeTab = ref('stored')
const showSearchDialog = ref(false)
const searchPhone = ref('')

const storedCards = ref([])
const packageCards = ref([])

const form = reactive({
  memberId: '',
  name: '',
  rechargeAmount: 0,
  giftedAmount: 0,
  packageId: '',
  price: 0,
  totalTimes: 1,
  serviceIds: '',
  validFrom: null,
  validTo: null
})

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

const searchMember = () => {
  showSearchDialog.value = true
}

const searchMemberByPhone = async () => {
  if (!searchPhone.value) {
    ElMessage.warning('请输入手机号')
    return
  }
  try {
    const res = await memberApi.getByPhone(searchPhone.value)
    if (res.data.success) {
      form.memberId = res.data.data.id
      ElMessage.success(`找到会员: ${res.data.data.name}`)
      showSearchDialog.value = false
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('未找到该会员')
  }
}

const createCard = async () => {
  if (!form.memberId || !form.name) {
    ElMessage.warning('请填写必要信息')
    return
  }

  try {
    if (cardType.value === 'stored') {
      const data = {
        memberId: form.memberId,
        name: form.name,
        rechargeAmount: form.rechargeAmount,
        giftedAmount: form.giftedAmount,
        validFrom: form.validFrom,
        validTo: form.validTo
      }
      const res = await storedValueCardApi.create(data)
      if (res.data.success) {
        ElMessage.success('储值卡创建成功')
        loadCards()
      } else {
        ElMessage.error(res.data.message)
      }
    } else {
      const data = {
        memberId: form.memberId,
        name: form.name,
        packageId: form.packageId,
        price: form.price,
        totalTimes: form.totalTimes,
        serviceIds: form.serviceIds ? form.serviceIds.split(',').map(s => s.trim()) : [],
        validFrom: form.validFrom,
        validTo: form.validTo
      }
      const res = await packageCardApi.create(data)
      if (res.data.success) {
        ElMessage.success('套餐卡创建成功')
        loadCards()
      } else {
        ElMessage.error(res.data.message)
      }
    }
  } catch (error) {
    ElMessage.error('创建卡失败')
  }
}

const loadCards = async () => {
  try {
    const members = (await memberApi.getAll()).data.data || []
    const memberIds = members.map(m => m.id)
    
    let allStored = []
    let allPackage = []
    
    for (const id of memberIds) {
      try {
        const [storedRes, packageRes] = await Promise.all([
          storedValueCardApi.getByMember(id),
          packageCardApi.getByMember(id)
        ])
        if (storedRes.data.success) allStored = allStored.concat(storedRes.data.data)
        if (packageRes.data.success) allPackage = allPackage.concat(packageRes.data.data)
      } catch (e) {}
    }
    
    storedCards.value = allStored
    packageCards.value = allPackage
  } catch (error) {
    console.error('加载卡列表失败', error)
  }
}

const refundStoredCard = async (card) => {
  try {
    await ElMessageBox.confirm('是否确认退卡？本金可退还吗？', '退卡确认', {
      confirmButtonText: '确认退卡(本金可退)',
      cancelButtonText: '取消'
    })
    
    const res = await storedValueCardApi.refund(card.id, true)
    if (res.data.success) {
      ElMessage.success(`退卡成功，退还本金: ¥${res.data.data}`)
      loadCards()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (action) {
    if (action === 'confirm') {
    }
  }
}

const refundPackageCard = async (card) => {
  try {
    await ElMessageBox.confirm('是否确认退卡？套餐卡退卡后不可恢复使用。', '退卡确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await packageCardApi.refund(card.id)
    if (res.data.success) {
      ElMessage.success('退卡成功')
      loadCards()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (action) {}
}

onMounted(() => {
  loadCards()
})
</script>
