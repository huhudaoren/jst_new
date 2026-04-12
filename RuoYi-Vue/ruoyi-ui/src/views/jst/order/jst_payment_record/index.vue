<template>
  <div class="app-container payment-record-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">订单中心</p>
        <h2>支付记录管理</h2>
        <p class="hero-desc">支持支付单号与订单号检索、支付方式/状态筛选，金额统一按分转元展示。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="88px" class="query-panel">
      <el-form-item label="支付单号">
        <el-input
          v-model="queryParams.paymentNo"
          placeholder="请输入支付单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号/ID">
        <el-input
          v-model="queryParams.orderKeyword"
          placeholder="请输入订单号或订单ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式">
        <el-select v-model="queryParams.payMethod" placeholder="全部" clearable>
          <el-option v-for="item in payMethodOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付状态">
        <el-select v-model="queryParams.payStatus" placeholder="全部" clearable>
          <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始"
          end-placeholder="结束"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.paymentId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.paymentNo || '--' }}</div>
              <div class="mobile-sub">订单 {{ row.orderNo || row.orderId || '--' }}</div>
            </div>
            <el-tag size="small" :type="payStatusType(row.payStatus)">{{ payStatusLabel(row.payStatus) }}</el-tag>
          </div>
          <div class="mobile-amount">{{ formatMoney(totalAmount(row)) }}</div>
          <div class="mobile-info-row">
            <span>现金 {{ formatMoney(row.cashAmount) }}</span>
            <span>积分折现 {{ formatMoney(row.pointsAmount) }}</span>
          </div>
          <div class="mobile-info-row">
            <span>{{ payMethodLabel(row.payMethod) }}</span>
            <span>{{ parseTime(row.payTime) || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无支付记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="支付单号" prop="paymentNo" min-width="170" show-overflow-tooltip />
      <el-table-column label="订单号/ID" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.orderNo || scope.row.orderId || '--' }}</template>
      </el-table-column>
      <el-table-column label="支付方式" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ payMethodLabel(scope.row.payMethod) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="payStatusType(scope.row.payStatus)">{{ payStatusLabel(scope.row.payStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="现金金额" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.cashAmount) }}</template>
      </el-table-column>
      <el-table-column label="积分折现" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.pointsAmount) }}</template>
      </el-table-column>
      <el-table-column label="合计金额" min-width="120" align="right">
        <template slot-scope="scope">
          <span class="amount-strong">{{ formatMoney(totalAmount(scope.row)) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="消耗积分" prop="pointsUsed" min-width="100" align="right" />
      <el-table-column label="完成时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.payTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '680px'" title="支付记录详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detail" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="支付ID">{{ detail.paymentId }}</el-descriptions-item>
          <el-descriptions-item label="支付单号">{{ detail.paymentNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="订单号/ID">{{ detail.orderNo || detail.orderId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ payMethodLabel(detail.payMethod) }}</el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag size="small" :type="payStatusType(detail.payStatus)">{{ payStatusLabel(detail.payStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="凭证审核状态">{{ voucherAuditLabel(detail.voucherAuditStatus) }}</el-descriptions-item>
          <el-descriptions-item label="现金金额">{{ formatMoney(detail.cashAmount) }}</el-descriptions-item>
          <el-descriptions-item label="积分折现金额">{{ formatMoney(detail.pointsAmount) }}</el-descriptions-item>
          <el-descriptions-item label="合计金额">
            <span class="amount-strong">{{ formatMoney(totalAmount(detail)) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="消耗积分">{{ detail.pointsUsed || 0 }}</el-descriptions-item>
          <el-descriptions-item label="第三方流水号">{{ detail.thirdPartyNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="对公凭证URL">{{ detail.voucherUrl || '--' }}</el-descriptions-item>
          <el-descriptions-item label="操作人ID">{{ detail.operatorId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
        <el-empty v-else description="暂无详情数据" :image-size="96" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_payment_record, getJst_payment_record } from '@/api/jst/order/jst_payment_record'

const PAY_METHOD_MAP = {
  wechat: '微信支付',
  bank_transfer: '对公转账',
  points: '纯积分',
  mix: '混合支付',
  points_cash_mix: '积分+现金'
}

const PAY_STATUS_MAP = {
  pending: { label: '待支付', type: 'warning' },
  success: { label: '支付成功', type: 'success' },
  failed: { label: '支付失败', type: 'danger' },
  refunding: { label: '退款中', type: 'warning' },
  refunded: { label: '已退款', type: 'info' }
}

const VOUCHER_AUDIT_MAP = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已驳回'
}

export default {
  name: 'JstPaymentRecordManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      dateRange: [],
      detailVisible: false,
      detail: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paymentNo: '',
        orderKeyword: '',
        payMethod: undefined,
        payStatus: undefined
      },
      payMethodOptions: Object.keys(PAY_METHOD_MAP).map(key => ({ value: key, label: PAY_METHOD_MAP[key] })),
      payStatusOptions: Object.keys(PAY_STATUS_MAP).map(key => ({ value: key, label: PAY_STATUS_MAP[key].label }))
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
    isNumericKeyword(value) {
      return /^\d+$/.test(String(value || '').trim())
    },
    async getList() {
      this.loading = true
      try {
        const keyword = String(this.queryParams.orderKeyword || '').trim()
        const params = {
          pageNum: this.queryParams.pageNum,
          pageSize: this.queryParams.pageSize,
          paymentNo: this.queryParams.paymentNo || undefined,
          payMethod: this.queryParams.payMethod || undefined,
          payStatus: this.queryParams.payStatus || undefined
        }
        if (this.isNumericKeyword(keyword)) {
          params.orderId = Number(keyword)
        }
        if (this.dateRange && this.dateRange.length === 2) {
          params.beginPayTime = this.dateRange[0]
          params.endPayTime = this.dateRange[1]
        }

        const res = await listJst_payment_record(params)
        const rows = res.rows || []
        const shouldLocalFilter = (keyword && !this.isNumericKeyword(keyword)) || (this.dateRange && this.dateRange.length === 2)

        if (shouldLocalFilter) {
          const localKeyword = keyword.toLowerCase()
          this.list = rows.filter(row => {
            const orderText = String(row.orderNo || row.orderId || '').toLowerCase()
            const orderMatch = !localKeyword || orderText.indexOf(localKeyword) > -1
            const timeMatch = this.matchRange(row.payTime, this.dateRange)
            return orderMatch && timeMatch
          })
          this.total = this.list.length
        } else {
          this.list = rows
          this.total = res.total || 0
        }
      } finally {
        this.loading = false
      }
    },
    matchRange(value, range) {
      if (!range || range.length !== 2) return true
      if (!value) return false
      const begin = new Date(range[0] + ' 00:00:00').getTime()
      const end = new Date(range[1] + ' 23:59:59').getTime()
      const current = new Date(value).getTime()
      if (Number.isNaN(current)) return false
      return current >= begin && current <= end
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        paymentNo: '',
        orderKeyword: '',
        payMethod: undefined,
        payStatus: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getJst_payment_record(row.paymentId)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    totalAmount(row) {
      return Number(row.cashAmount || 0) + Number(row.pointsAmount || 0)
    },
    payMethodLabel(value) {
      return PAY_METHOD_MAP[value] || value || '--'
    },
    payStatusLabel(value) {
      return (PAY_STATUS_MAP[value] && PAY_STATUS_MAP[value].label) || value || '--'
    },
    payStatusType(value) {
      return (PAY_STATUS_MAP[value] && PAY_STATUS_MAP[value].type) || 'info'
    },
    voucherAuditLabel(value) {
      return VOUCHER_AUDIT_MAP[value] || value || '--'
    },
    formatMoney(value) {
      const n = Number(value || 0)
      return '\u00A5' + (n / 100).toFixed(2)
    }
  }
}
</script>

<style scoped>
.payment-record-page {
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

.amount-strong {
  font-weight: 700;
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

.mobile-amount {
  margin-top: 12px;
  font-size: 22px;
  font-weight: 700;
  color: #172033;
}

.mobile-info-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
  font-size: 13px;
  color: #7a8495;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

@media (max-width: 768px) {
  .payment-record-page {
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
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .mobile-actions .el-button {
    min-height: 44px;
  }
}
</style>
