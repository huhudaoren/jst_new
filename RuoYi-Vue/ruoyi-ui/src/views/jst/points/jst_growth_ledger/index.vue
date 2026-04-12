<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="账户ID" prop="accountId">
        <el-input v-model="queryParams.accountId" placeholder="请输入账户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="变更类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="全部" clearable>
          <el-option label="获取" value="earn" />
          <el-option label="调整" value="adjust" />
          <el-option label="升级" value="level_up" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源行为" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="全部" clearable>
          <el-option v-for="item in sourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.ledgerId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">成长流水 #{{ row.ledgerId }}</span>
            <el-tag size="mini" :type="Number(row.growthChange) >= 0 ? 'success' : 'danger'">
              {{ Number(row.growthChange) >= 0 ? '增加' : '减少' }}
            </el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>账户：{{ row.accountId }}</span>
            <span>来源：{{ sourceTypeLabel(row.sourceType) }}</span>
            <span :class="Number(row.growthChange) >= 0 ? 'points-positive' : 'points-negative'">
              {{ Number(row.growthChange) >= 0 ? '+' : '' }}{{ row.growthChange || 0 }}
            </span>
            <span>余额：{{ row.balanceAfter || 0 }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无成长值流水" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="流水ID" prop="ledgerId" width="90" />
      <el-table-column label="账户ID" prop="accountId" min-width="100" />
      <el-table-column label="变更类型" prop="changeType" min-width="100" />
      <el-table-column label="来源行为" min-width="120">
        <template slot-scope="{ row }">{{ sourceTypeLabel(row.sourceType) }}</template>
      </el-table-column>
      <el-table-column label="来源ID" prop="sourceRefId" min-width="100" />
      <el-table-column label="变化值" min-width="100" align="right">
        <template slot-scope="{ row }">
          <span :class="Number(row.growthChange) >= 0 ? 'points-positive' : 'points-negative'">
            {{ Number(row.growthChange) >= 0 ? '+' : '' }}{{ row.growthChange || 0 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="变更后余额" prop="balanceAfter" min-width="100" align="right" />
      <el-table-column label="等级变更" min-width="130">
        <template slot-scope="{ row }">{{ row.levelBefore || '--' }} → {{ row.levelAfter || '--' }}</template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'成长值流水详情 #' + (detailData.ledgerId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '500px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="流水ID">{{ detailData.ledgerId }}</el-descriptions-item>
          <el-descriptions-item label="账户ID">{{ detailData.accountId }}</el-descriptions-item>
          <el-descriptions-item label="变更类型">{{ detailData.changeType || '--' }}</el-descriptions-item>
          <el-descriptions-item label="来源行为">{{ sourceTypeLabel(detailData.sourceType) }}</el-descriptions-item>
          <el-descriptions-item label="来源ID">{{ detailData.sourceRefId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="变化值">
            <span :class="Number(detailData.growthChange) >= 0 ? 'points-positive' : 'points-negative'">
              {{ Number(detailData.growthChange) >= 0 ? '+' : '' }}{{ detailData.growthChange || 0 }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="变更后余额">{{ detailData.balanceAfter || 0 }}</el-descriptions-item>
          <el-descriptions-item label="等级变化">{{ detailData.levelBefore || '--' }} → {{ detailData.levelAfter || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_growth_ledger, getJst_growth_ledger } from '@/api/jst/points/jst_growth_ledger'

const SOURCE_TYPE_OPTIONS = [
  { label: '报名', value: 'enroll' },
  { label: '课程', value: 'course' },
  { label: '签到', value: 'sign' },
  { label: '邀请', value: 'invite' },
  { label: '学习', value: 'learn' },
  { label: '获奖', value: 'award' },
  { label: '手工调整', value: 'manual' }
]

export default {
  name: 'JstGrowthLedger',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountId: null,
        changeType: null,
        sourceType: null
      },
      sourceTypeOptions: SOURCE_TYPE_OPTIONS,
      detailVisible: false,
      detailData: {}
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    sourceTypeLabel(sourceType) {
      const match = SOURCE_TYPE_OPTIONS.find(item => item.value === sourceType)
      return match ? match.label : sourceType || '--'
    },
    getList() {
      this.loading = true
      listJst_growth_ledger(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    handleDetail(row) {
      getJst_growth_ledger(row.ledgerId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    }
  }
}
</script>

<style scoped>
.points-positive {
  color: #67c23a;
  font-weight: 600;
}

.points-negative {
  color: #f56c6c;
  font-weight: 600;
}

.detail-body {
  padding: 0 16px 16px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 65%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 6px;
}
</style>
