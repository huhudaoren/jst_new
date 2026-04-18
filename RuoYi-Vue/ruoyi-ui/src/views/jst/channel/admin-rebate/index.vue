<template>
  <div class="app-container admin-rebate-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">渠道财务</p>
        <h2>返点台账</h2>
        <p class="hero-desc">查看渠道返点计提明细，按渠道、订单、状态筛选。只读查询，不可编辑。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="80px"
      class="query-panel"
    >
      <el-form-item label="渠道名称" prop="channelName">
        <el-input v-model="queryParams.channelName" placeholder="请输入渠道名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单" prop="orderId">
        <order-picker v-model="queryParams.orderId" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.ledgerId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.channelName || '--' }}</div>
              <div class="mobile-sub">订单 #{{ row.orderId || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-amount" :class="{ 'amount-negative': Number(row.rebateAmount) < 0 }">{{ formatMoney(row.rebateAmount) }}</div>
          <div class="mobile-info-row">{{ parseTime(row.createTime) || '--' }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无台账记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="台账ID" prop="ledgerId" width="80" />
      <el-table-column label="渠道名称" prop="channelName" min-width="160" show-overflow-tooltip />
      <el-table-column label="订单ID" prop="orderId" min-width="100" />
      <el-table-column label="返点金额" min-width="130" align="right">
        <template slot-scope="scope">
          <span :class="{ 'amount-negative': Number(scope.row.rebateAmount) < 0 }">{{ formatMoney(scope.row.rebateAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listJst_rebate_ledger } from '@/api/jst/channel/jst_rebate_ledger'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

const STATUS_META = {
  pending: { label: '待结算', type: 'warning' },
  settled: { label: '已结算', type: 'success' },
  rolled_back: { label: '已回退', type: 'danger' },
  negative: { label: '负向抵扣', type: 'danger' }
}

export default {
  name: 'AdminRebateLedger',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        channelName: undefined,
        orderId: undefined,
        status: undefined
      },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label }))
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateViewport)
  },
  methods: {
    updateViewport() {
      this.isMobile = window.innerWidth <= 768
    },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_rebate_ledger(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, channelName: undefined, orderId: undefined, status: undefined }
      this.getList()
    },
    statusLabel(status) {
      return (STATUS_META[status] && STATUS_META[status].label) || status || '--'
    },
    statusType(status) {
      return (STATUS_META[status] && STATUS_META[status].type) || 'info'
    },
    formatMoney(value) {
      return formatMoneyUtil(value)
    }
  }
}
</script>

<style scoped>
.admin-rebate-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.amount-negative {
  color: #f56c6c;
  font-weight: 600;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.mobile-card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.mobile-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #7a8495;
}

.mobile-amount {
  margin-top: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #172033;
}

.mobile-info-row {
  margin-top: 8px;
  font-size: 13px;
  color: #7a8495;
}

@media (max-width: 768px) {
  .admin-rebate-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }
}
</style>
