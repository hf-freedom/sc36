<template>
  <div>
    <el-card>
      <template #header>
        <span>退款管理 - 消费记录列表</span>
      </template>

      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="手机号">
          <el-input v-model="searchPhone" placeholder="请输入会员手机号" style="width: 200px" />
        </el-form-item>
        <el-form-item label="卡号ID">
          <el-input v-model="searchCardId" placeholder="请输入卡号ID" style="width: 250px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchByPhone">按手机号查询</el-button>
          <el-button type="primary" @click="searchByCardId">按卡号查询</el-button>
          <el-button @click="loadAllConsumptions">显示全部</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="consumptionList" style="width: 100%" border>
        <el-table-column prop="id" label="消费记录ID" width="280" />
        <el-table-column prop="memberId" label="会员ID" width="250" />
        <el-table-column prop="serviceName" label="服务名称" width="120" />
        <el-table-column label="卡类型" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.cardType === 'STORED_VALUE' ? 'primary' : 'success'">
              {{ scope.row.cardType === 'STORED_VALUE' ? '储值卡' : '套餐卡' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="消费金额" width="100">
          <template #default="scope">
            <span style="color: #f56c6c; font-weight: bold" v-if="scope.row.cardType === 'STORED_VALUE'">
              ¥{{ scope.row.amount }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="principalUsed" label="使用本金" width="90">
          <template #default="scope">
            <span v-if="scope.row.cardType === 'STORED_VALUE'">¥{{ scope.row.principalUsed }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="giftUsed" label="使用赠送" width="90">
          <template #default="scope">
            <span v-if="scope.row.cardType === 'STORED_VALUE'">¥{{ scope.row.giftUsed }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="timesUsed" label="使用次数" width="80">
          <template #default="scope">
            <span v-if="scope.row.cardType === 'PACKAGE'">{{ scope.row.timesUsed }} 次</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="consumedAt" label="消费时间" width="180" />
        <el-table-column prop="refunded" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.refunded ? 'danger' : 'success'">
              {{ scope.row.refunded ? '已退款' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              size="small"
              @click="selectConsumption(scope.row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="!scope.row.refunded"
              type="danger"
              link
              size="small"
              @click="showRefundDialogForRow(scope.row)"
            >
              申请退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="consumptionList.length === 0" description="暂无消费记录" />
    </el-card>

    <el-dialog v-model="showDetailDialog" title="消费记录详情" width="650px">
      <el-descriptions v-if="selectedConsumption" title="消费记录" border>
        <el-descriptions-item label="消费ID">{{ selectedConsumption.id }}</el-descriptions-item>
        <el-descriptions-item label="会员ID">{{ selectedConsumption.memberId }}</el-descriptions-item>
        <el-descriptions-item label="卡类型">
          {{ selectedConsumption.cardType === 'STORED_VALUE' ? '储值卡' : '套餐卡' }}
        </el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ selectedConsumption.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="门店ID">{{ selectedConsumption.storeId }}</el-descriptions-item>
        <el-descriptions-item label="消费金额">¥{{ selectedConsumption.amount }}</el-descriptions-item>
        <el-descriptions-item label="使用本金">¥{{ selectedConsumption.principalUsed }}</el-descriptions-item>
        <el-descriptions-item label="使用赠送">¥{{ selectedConsumption.giftUsed }}</el-descriptions-item>
        <el-descriptions-item label="使用次数">{{ selectedConsumption.timesUsed }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedConsumption.refunded ? 'danger' : 'success'">
            {{ selectedConsumption.refunded ? '已退款' : '正常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消费时间">{{ selectedConsumption.consumedAt }}</el-descriptions-item>
        <el-descriptions-item label="幂等键">{{ selectedConsumption.idempotentKey || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px; text-align: right">
        <el-button
          v-if="selectedConsumption && !selectedConsumption.refunded"
          type="danger"
          @click="showRefundDialogForSelected"
        >
          申请退款
        </el-button>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="showRefundDialog" title="退款确认" width="500px">
      <el-alert
        title="退款将恢复以下金额/次数"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <el-descriptions :column="1" border v-if="selectedConsumption">
        <el-descriptions-item label="恢复本金">
          <span style="color: #f56c6c; font-weight: bold">¥{{ selectedConsumption.principalUsed }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="恢复赠送">
          <span style="color: #409eff; font-weight: bold">¥{{ selectedConsumption.giftUsed }}</span>
        </el-descriptions-item>
        <el-descriptions-item v-if="selectedConsumption.timesUsed > 0" label="恢复次数">
          <span style="color: #67c23a; font-weight: bold">{{ selectedConsumption.timesUsed }} 次</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-form label-width="80px" style="margin-top: 20px">
        <el-form-item label="退款原因">
          <el-input
            v-model="refundReason"
            type="textarea"
            placeholder="请输入退款原因"
            :rows="3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="danger" @click="confirmRefund">确认退款</el-button>
      </template>
    </el-dialog>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>退款记录查询</span>
      </template>
      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="消费记录ID">
          <el-input v-model="queryConsumptionId" placeholder="请输入消费记录ID" style="width: 350px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRefund">查询退款记录</el-button>
        </el-form-item>
      </el-form>

      <el-descriptions v-if="refundRecord" title="退款记录" border>
        <el-descriptions-item label="退款ID">{{ refundRecord.id }}</el-descriptions-item>
        <el-descriptions-item label="关联消费ID">{{ refundRecord.relatedConsumptionId }}</el-descriptions-item>
        <el-descriptions-item label="卡类型">
          {{ refundRecord.cardType === 'STORED_VALUE' ? '储值卡' : '套餐卡' }}
        </el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ refundRecord.refundAmount }}</el-descriptions-item>
        <el-descriptions-item label="退还本金">¥{{ refundRecord.principalRefunded }}</el-descriptions-item>
        <el-descriptions-item label="退还赠送">¥{{ refundRecord.giftRefunded }}</el-descriptions-item>
        <el-descriptions-item v-if="refundRecord.timesRestored > 0" label="恢复次数">
          {{ refundRecord.timesRestored }} 次
        </el-descriptions-item>
        <el-descriptions-item label="退款原因">{{ refundRecord.reason }}</el-descriptions-item>
        <el-descriptions-item label="退款时间">{{ refundRecord.refundedAt }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else-if="refundRecord !== null" description="未找到退款记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { consumptionApi, refundApi, memberApi } from '../api'

const searchPhone = ref('')
const searchCardId = ref('')
const queryConsumptionId = ref('')
const consumptionList = ref([])
const selectedConsumption = ref(null)
const refundRecord = ref(null)
const showDetailDialog = ref(false)
const showRefundDialog = ref(false)
const refundReason = ref('')

const loadAllConsumptions = async () => {
  try {
    const res = await consumptionApi.getAll({})
    if (res.data.success) {
      consumptionList.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('加载消费记录失败')
  }
}

const searchByPhone = async () => {
  if (!searchPhone.value.trim()) {
    ElMessage.warning('请输入会员手机号')
    return
  }

  try {
    const memberRes = await memberApi.getByPhone(searchPhone.value.trim())
    if (memberRes.data.success) {
      const memberId = memberRes.data.data.id
      const res = await consumptionApi.getAll({ memberId })
      if (res.data.success) {
        consumptionList.value = res.data.data
        ElMessage.success(`找到 ${res.data.data.length} 条消费记录`)
      }
    } else {
      ElMessage.warning('未找到该会员')
      consumptionList.value = []
    }
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const searchByCardId = async () => {
  if (!searchCardId.value.trim()) {
    ElMessage.warning('请输入卡号ID')
    return
  }

  try {
    const res = await consumptionApi.getAll({ cardId: searchCardId.value.trim() })
    if (res.data.success) {
      consumptionList.value = res.data.data
      ElMessage.success(`找到 ${res.data.data.length} 条消费记录`)
    }
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const selectConsumption = (row) => {
  selectedConsumption.value = { ...row }
  showDetailDialog.value = true
}

const showRefundDialogForRow = (row) => {
  selectedConsumption.value = { ...row }
  refundReason.value = ''
  showRefundDialog.value = true
}

const showRefundDialogForSelected = () => {
  showDetailDialog.value = false
  refundReason.value = ''
  showRefundDialog.value = true
}

const confirmRefund = async () => {
  if (!refundReason.value.trim()) {
    ElMessage.warning('请输入退款原因')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确认执行退款操作？此操作将恢复会员的余额或次数。',
      '确认退款',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await refundApi.create({
      consumptionId: selectedConsumption.value.id,
      reason: refundReason.value
    })

    if (res.data.success) {
      ElMessage.success('退款成功')
      showRefundDialog.value = false
      loadAllConsumptions()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (action) {
    if (action !== 'cancel') {
      console.error(action)
    }
  }
}

const searchRefund = async () => {
  if (!queryConsumptionId.value) {
    ElMessage.warning('请输入消费记录ID')
    return
  }

  try {
    const res = await refundApi.getByConsumption(queryConsumptionId.value)
    if (res.data.success) {
      refundRecord.value = res.data.data
    } else {
      refundRecord.value = null
      ElMessage.warning(res.data.message)
    }
  } catch (error) {
    refundRecord.value = null
    ElMessage.error('查询失败')
  }
}

onMounted(() => {
  loadAllConsumptions()
})
</script>
