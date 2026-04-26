<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>会员列表</span>
          <el-button type="primary" @click="showCreateDialog = true">
            新增会员
          </el-button>
        </div>
      </template>
      
      <el-table :data="members" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="level" label="等级">
          <template #default="scope">
            <el-tag :type="getLevelTagType(scope.row.level)">
              {{ getLevelText(scope.row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="本金余额">
          <template #default="scope">
            ¥{{ scope.row.balance }}
          </template>
        </el-table-column>
        <el-table-column prop="giftedBalance" label="赠送余额">
          <template #default="scope">
            ¥{{ scope.row.giftedBalance }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" link @click="viewDetail(scope.row.id)">
              查看详情
            </el-button>
            <el-button type="primary" link @click="openLevelDialog(scope.row)">
              修改等级
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreateDialog" title="新增会员" width="400px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="createForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="createForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createMember">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showLevelDialog" title="修改会员等级" width="400px">
      <el-form :model="levelForm" label-width="80px">
        <el-form-item label="当前等级">
          <el-tag :type="getLevelTagType(levelForm.currentLevel)">
            {{ getLevelText(levelForm.currentLevel) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新等级">
          <el-select v-model="levelForm.newLevel" placeholder="请选择等级" style="width: 100%">
            <el-option label="普通会员" value="NORMAL" />
            <el-option label="银卡会员" value="SILVER" />
            <el-option label="金卡会员" value="GOLD" />
            <el-option label="白金会员" value="PLATINUM" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLevelDialog = false">取消</el-button>
        <el-button type="primary" @click="updateLevel">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { memberApi } from '../api'

const router = useRouter()
const loading = ref(false)
const members = ref([])
const showCreateDialog = ref(false)
const showLevelDialog = ref(false)

const createForm = ref({
  name: '',
  phone: ''
})

const levelForm = ref({
  memberId: '',
  currentLevel: '',
  newLevel: ''
})

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

const loadMembers = async () => {
  loading.value = true
  try {
    const res = await memberApi.getAll()
    if (res.data.success) {
      members.value = res.data.data
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('加载会员列表失败')
  } finally {
    loading.value = false
  }
}

const createMember = async () => {
  if (!createForm.value.name || !createForm.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const res = await memberApi.create(createForm.value)
    if (res.data.success) {
      ElMessage.success('会员创建成功')
      showCreateDialog.value = false
      createForm.value = { name: '', phone: '' }
      loadMembers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('创建会员失败')
  }
}

const viewDetail = (id) => {
  router.push(`/members/${id}`)
}

const openLevelDialog = (member) => {
  levelForm.value = {
    memberId: member.id,
    currentLevel: member.level,
    newLevel: member.level
  }
  showLevelDialog.value = true
}

const updateLevel = async () => {
  try {
    const res = await memberApi.updateLevel(levelForm.value.memberId, levelForm.value.newLevel)
    if (res.data.success) {
      ElMessage.success('会员等级更新成功')
      showLevelDialog.value = false
      loadMembers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('更新会员等级失败')
  }
}

onMounted(() => {
  loadMembers()
})
</script>
