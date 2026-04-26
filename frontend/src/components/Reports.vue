<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>每日对账报表</span>
          <div>
            <el-button type="primary" @click="generateTodayReport">生成今日报表</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" style="margin-bottom: 20px">
        <el-form-item label="查询日期">
          <el-date-picker
            v-model="queryDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="queryReport">查询</el-button>
          <el-button @click="generateReport">生成该日报表</el-button>
        </el-form-item>
      </el-form>

      <el-descriptions v-if="report" title="对账报表详情" border :column="2">
        <el-descriptions-item label="报表日期">{{ report.reportDate }}</el-descriptions-item>
        <el-descriptions-item label="会员总数">{{ report.totalMembers }}</el-descriptions-item>
        <el-descriptions-item label="活跃卡数量">{{ report.activeCards }}</el-descriptions-item>
        <el-descriptions-item label="新增会员">{{ report.newMembers }}</el-descriptions-item>
        <el-descriptions-item label="新发卡数">{{ report.newCards }}</el-descriptions-item>
        <el-descriptions-item label="消费笔数">{{ report.consumptionCount }}</el-descriptions-item>
        <el-descriptions-item label="退款笔数">{{ report.refundCount }}</el-descriptions-item>
        <el-descriptions-item label="生成时间">{{ report.createdAt }}</el-descriptions-item>
        
        <el-descriptions-item label="总本金余额" :span="2">
          <span style="color: #f56c6c; font-weight: bold; font-size: 18px">
            ¥{{ report.totalBalance }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="总赠送余额" :span="2">
          <span style="color: #409eff; font-weight: bold; font-size: 18px">
            ¥{{ report.totalGiftBalance }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="当日消费总额" :span="2">
          <span style="color: #e6a23c; font-weight: bold; font-size: 18px">
            ¥{{ report.totalConsumption }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="当日分账总额" :span="2">
          <span style="color: #67c23a; font-weight: bold; font-size: 18px">
            ¥{{ report.totalSettlement }}
          </span>
        </el-descriptions-item>
      </el-descriptions>

      <el-empty v-else-if="report !== null" description="该日期暂无报表，请先生成" />

      <el-divider />

      <div>
        <h4 style="margin-bottom: 15px">日期范围查询</h4>
        <el-form :inline="true" style="margin-bottom: 20px">
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="rangeStart"
              type="date"
              placeholder="选择开始日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker
              v-model="rangeEnd"
              type="date"
              placeholder="选择结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryRange">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table :data="rangeReports" style="width: 100%" v-if="rangeReports.length > 0">
          <el-table-column prop="reportDate" label="日期" width="120" />
          <el-table-column prop="totalMembers" label="会员数" width="80" />
          <el-table-column prop="activeCards" label="活跃卡" width="80" />
          <el-table-column prop="totalBalance" label="总本金">
            <template #default="scope">
              <span style="color: #f56c6c">¥{{ scope.row.totalBalance }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalGiftBalance" label="总赠送">
            <template #default="scope">
              <span style="color: #409eff">¥{{ scope.row.totalGiftBalance }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalConsumption" label="消费额">
            <template #default="scope">
              ¥{{ scope.row.totalConsumption }}
            </template>
          </el-table-column>
          <el-table-column prop="totalSettlement" label="分账额">
            <template #default="scope">
              <span style="color: #67c23a">¥{{ scope.row.totalSettlement }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="consumptionCount" label="消费笔数" width="80" />
          <el-table-column prop="refundCount" label="退款笔数" width="80" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { reportApi } from '../api'

const today = new Date().toISOString().split('T')[0]
const queryDate = ref(today)
const rangeStart = ref('')
const rangeEnd = ref('')
const report = ref(null)
const rangeReports = ref([])

const queryReport = async () => {
  if (!queryDate.value) {
    ElMessage.warning('请选择查询日期')
    return
  }

  try {
    const res = await reportApi.getDaily(queryDate.value)
    if (res.data.success) {
      report.value = res.data.data
    } else {
      report.value = null
      ElMessage.warning(res.data.message)
    }
  } catch (error) {
    report.value = null
    ElMessage.error('查询报表失败')
  }
}

const generateReport = async () => {
  if (!queryDate.value) {
    ElMessage.warning('请选择日期')
    return
  }

  try {
    const res = await reportApi.generateDaily(queryDate.value)
    if (res.data.success) {
      ElMessage.success('报表生成成功')
      report.value = res.data.data
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('生成报表失败')
  }
}

const generateTodayReport = async () => {
  try {
    const res = await reportApi.generateDaily(today)
    if (res.data.success) {
      ElMessage.success('今日报表生成成功')
      report.value = res.data.data
      queryDate.value = today
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('生成报表失败')
  }
}

const queryRange = async () => {
  if (!rangeStart.value || !rangeEnd.value) {
    ElMessage.warning('请选择开始和结束日期')
    return
  }

  try {
    const res = await reportApi.getRange(rangeStart.value, rangeEnd.value)
    if (res.data.success) {
      rangeReports.value = res.data.data
      if (rangeReports.value.length === 0) {
        ElMessage.warning('该日期范围内暂无报表')
      }
    } else {
      rangeReports.value = []
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    rangeReports.value = []
    ElMessage.error('查询失败')
  }
}
</script>
