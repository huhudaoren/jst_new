<template>
  <div class="app-container admin-settlement-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">财务中心</p>
        <h2>赛事结算</h2>
        <p class="hero-desc">查看赛事方结算单，审核确认结算金额，管理结算流程。</p>
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
      <el-form-item label="赛事名称" prop="contestName">
        <el-input v-model="queryParams.contestName" placeholder="请输入赛事名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="赛事方" prop="partnerName">
        <el-input v-model="queryParams.partnerName" placeholder="请输入赛事方名称" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.eventSettlementId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contestName || '--' }}</div>
              <div class="mobile-sub">{{ row.partnerName || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-amounts">
            <div class="mobile-amount-item">
              <span class="mobile-amount-label">总金额</span>
              <span class="mobile-amount-value">{{ formatMoney(row.totalAmount || row.finalAmount) }}</span>
            </div>
            <div class="mobile-amount-item">
              <span class="mobile-amount-label">已结</span>
              <span class="mobile-amount-value">{{ formatMoney(row.settledAmount) }}</span>
            </div>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button v-if="canConfirm(row)" type="text" v-hasPermi="['jst:channel:event_settlement:edit']" @click="handleConfirm(row)">确认结算</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无结算单" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="结算ID" prop="eventSettlementId" width="80" />
      <el-table-column label="赛事名称" prop="contestName" min-width="200" show-overflow-tooltip />
      <el-table-column label="赛事方" prop="partnerName" min-width="160" show-overflow-tooltip />
      <el-table-column label="总金额" min-width="130" align="right">
        <template slot-scope="scope">
          <strong>{{ formatMoney(scope.row.totalAmount || scope.row.finalAmount) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="已结金额" min-width="130" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.settledAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button v-if="canConfirm(scope.row)" type="text" v-hasPermi="['jst:channel:event_settlement:edit']" @click="handleConfirm(scope.row)">确认结算</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '680px'" title="结算详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detail">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="结算ID">{{ detail.eventSettlementId }}</el-descriptions-item>
            <el-descriptions-item label="结算单号">{{ detail.settlementNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="赛事名称">{{ detail.contestName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="赛事方">{{ detail.partnerName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="总金额">
              <strong>{{ formatMoney(detail.totalAmount || detail.finalAmount) }}</strong>
            </el-descriptions-item>
            <el-descriptions-item label="已结金额">{{ formatMoney(detail.settledAmount) }}</el-descriptions-item>
            <el-descriptions-item label="净实付">{{ formatMoney(detail.totalNetPay) }}</el-descriptions-item>
            <el-descriptions-item label="渠道返点">{{ formatMoney(detail.totalRebate) }}</el-descriptions-item>
            <el-descriptions-item label="服务费">{{ formatMoney(detail.totalServiceFee) }}</el-descriptions-item>
            <el-descriptions-item label="退款合计">
              <span :class="{ 'amount-negative': Number(detail.totalRefund) > 0 }">{{ formatMoney(detail.totalRefund) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ detail.auditRemark || detail.remark || '--' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_event_settlement, getJst_event_settlement } from '@/api/jst/channel/jst_event_settlement'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

const STATUS_META = {
  pending_confirm: { label: '待确认', type: 'warning' },
  reviewing: { label: '审核中', type: 'primary' },
  rejected: { label: '已驳回', type: 'danger' },
  pending_pay: { label: '待打款', type: 'info' },
  paid: { label: '已打款', type: 'success' }
}

export default {
  name: 'AdminSettlement',
  data() {
    return {
      loading: false,
      detailLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      detail: null,
      detailVisible: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: undefined,
        partnerName: undefined,
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
        const res = await listJst_event_settlement(this.queryParams)
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
      this.queryParams = { pageNum: 1, pageSize: 10, contestName: undefined, partnerName: undefined, status: undefined }
      this.getList()
    },
    canConfirm(row) {
      return row.status === 'pending_confirm' || row.status === 'reviewing'
    },
    handleConfirm(row) {
      this.$confirm('确认该结算单金额无误？确认后将进入打款流程。', '确认结算', { type: 'warning' }).then(async() => {
        // 使用通用编辑接口更新状态
        const { updateJst_event_settlement } = await import('@/api/jst/channel/jst_event_settlement')
        await updateJst_event_settlement({ eventSettlementId: row.eventSettlementId, status: 'pending_pay' })
        this.$modal.msgSuccess('结算已确认')
        this.getList()
      }).catch(() => {})
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_event_settlement(row.eventSettlementId)
        this.detail = res.data
      } finally {
        this.detailLoading = false
      }
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
.admin-settlement-page {
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

.drawer-body {
  padding: 0 24px 24px;
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

.mobile-amounts {
  display: flex;
  gap: 24px;
  margin-top: 14px;
}

.mobile-amount-label {
  display: block;
  font-size: 12px;
  color: #7a8495;
}

.mobile-amount-value {
  display: block;
  margin-top: 4px;
  font-size: 20px;
  font-weight: 700;
  color: #172033;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .admin-settlement-page {
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

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
