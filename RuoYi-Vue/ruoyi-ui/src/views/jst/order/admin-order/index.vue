<template>
  <div class="app-container admin-order-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">订单中心</p>
        <h2>订单管理</h2>
        <p class="hero-desc">支持按订单号、状态筛选，查看订单详情，并可发起特批退款。</p>
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
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="全部" clearable>
          <el-option v-for="s in orderStatusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="退款状态" prop="refundStatus">
        <el-select v-model="queryParams.refundStatus" placeholder="全部" clearable>
          <el-option v-for="s in refundStatusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间">
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
        <div v-for="row in list" :key="row.orderId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.orderNo || '--' }}</div>
              <div class="mobile-sub">{{ row.contestName || '--' }}</div>
            </div>
            <el-tag size="small" :type="orderStatusType(row.orderStatus)">{{ orderStatusLabel(row.orderStatus) }}</el-tag>
          </div>
          <div class="mobile-amount">{{ formatMoney(row.payAmount) }}</div>
          <div class="mobile-info-row">
            <span>标价 {{ formatMoney(row.listAmount) }}</span>
            <span v-if="row.refundStatus && row.refundStatus !== 'none'">
              退款 <el-tag size="mini" :type="refundStatusType(row.refundStatus)">{{ refundStatusLabel(row.refundStatus) }}</el-tag>
            </span>
          </div>
          <div class="mobile-info-row">{{ parseTime(row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看详情</el-button>
            <el-button
              v-if="canSpecialRefund(row)"
              type="text"
              class="danger-text"
              v-hasPermi="['jst:order:refund:special']"
              @click="openSpecialRefund(row)"
            >特批退款</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无订单" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="订单号" prop="orderNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="用户" prop="userName" min-width="120" show-overflow-tooltip />
      <el-table-column label="赛事名称" prop="contestName" min-width="200" show-overflow-tooltip />
      <el-table-column label="标价" min-width="110" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.listAmount) }}</template>
      </el-table-column>
      <el-table-column label="实付" min-width="110" align="right">
        <template slot-scope="scope">
          <span :class="{ 'amount-negative': Number(scope.row.payAmount) < 0 }">{{ formatMoney(scope.row.payAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="orderStatusType(scope.row.orderStatus)">{{ orderStatusLabel(scope.row.orderStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退款状态" min-width="110">
        <template slot-scope="scope">
          <el-tag
            v-if="scope.row.refundStatus && scope.row.refundStatus !== 'none'"
            size="small"
            :type="refundStatusType(scope.row.refundStatus)"
          >{{ refundStatusLabel(scope.row.refundStatus) }}</el-tag>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            v-if="canSpecialRefund(scope.row)"
            type="text"
            class="danger-text"
            v-hasPermi="['jst:order:refund:special']"
            @click="openSpecialRefund(scope.row)"
          >特批退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '720px'" title="订单详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detail">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="订单号">{{ detail.orderNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="用户">{{ detail.userName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="赛事">{{ detail.contestName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag size="small" :type="orderStatusType(detail.orderStatus)">{{ orderStatusLabel(detail.orderStatus) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="标价金额">{{ formatMoney(detail.listAmount) }}</el-descriptions-item>
            <el-descriptions-item label="优惠券抵扣">{{ formatMoney(detail.couponAmount) }}</el-descriptions-item>
            <el-descriptions-item label="积分抵扣">{{ formatMoney(detail.pointsDeductAmount) }}</el-descriptions-item>
            <el-descriptions-item label="实付金额">
              <strong>{{ formatMoney(detail.payAmount) }}</strong>
            </el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ detail.payMethod || '--' }}</el-descriptions-item>
            <el-descriptions-item label="退款状态">{{ refundStatusLabel(detail.refundStatus) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
          </el-descriptions>

          <div class="section-title">订单项明细</div>
          <el-table :data="detail.items || []" size="small" max-height="200">
            <el-table-column label="项目" prop="itemName" min-width="160" show-overflow-tooltip />
            <el-table-column label="金额" min-width="100" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.itemAmount) }}</template>
            </el-table-column>
          </el-table>

          <div class="section-title">支付记录</div>
          <el-table :data="detail.payments || []" size="small" max-height="200">
            <el-table-column label="支付流水号" prop="transactionNo" min-width="180" show-overflow-tooltip />
            <el-table-column label="金额" min-width="100" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.amount) }}</template>
            </el-table-column>
            <el-table-column label="方式" prop="payMethod" min-width="90" />
            <el-table-column label="状态" prop="status" min-width="90" />
            <el-table-column label="时间" min-width="150">
              <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
            </el-table-column>
          </el-table>

          <div class="section-title">退款记录</div>
          <el-table :data="detail.refunds || []" size="small" max-height="200">
            <el-table-column label="退款单号" prop="refundNo" min-width="180" show-overflow-tooltip />
            <el-table-column label="退款金额" min-width="100" align="right">
              <template slot-scope="scope">
                <span class="amount-negative">{{ formatMoney(scope.row.refundAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" min-width="100">
              <template slot-scope="scope">
                <el-tag size="small" :type="refundStatusType(scope.row.refundStatus)">{{ refundStatusLabel(scope.row.refundStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" min-width="150">
              <template slot-scope="scope">{{ parseTime(scope.row.applyTime) || '--' }}</template>
            </el-table-column>
          </el-table>
        </template>
      </div>
    </el-drawer>

    <el-dialog title="特批退款" :visible.sync="specialRefundVisible" width="460px" append-to-body>
      <el-alert
        title="特批退款将直接发起全额退款，无需赛事方审核。请谨慎操作。"
        type="warning"
        show-icon
        :closable="false"
        class="mb12"
      />
      <el-form ref="specialRefundForm" :model="specialRefundForm" :rules="specialRefundRules" label-width="88px">
        <el-form-item label="订单号">
          <span>{{ currentRow && currentRow.orderNo }}</span>
        </el-form-item>
        <el-form-item label="退款原因" prop="reason">
          <el-input
            v-model="specialRefundForm.reason"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            placeholder="请填写特批退款原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="specialRefundVisible = false">取消</el-button>
        <el-button type="danger" :loading="actionLoading" @click="submitSpecialRefund">确认退款</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrderMains, getOrderDetail } from '@/api/jst/order/order-admin'
import { specialRefund } from '@/api/jst/order/refund-admin'

const ORDER_STATUS = {
  pending_pay: { label: '待支付', type: 'warning' },
  paid: { label: '已支付', type: 'success' },
  cancelled: { label: '已取消', type: 'info' },
  closed: { label: '已关闭', type: 'info' }
}

const REFUND_STATUS = {
  none: { label: '无退款', type: 'info' },
  pending: { label: '退款中', type: 'warning' },
  partial: { label: '部分退款', type: 'warning' },
  full: { label: '全额已退', type: 'danger' }
}

export default {
  name: 'AdminOrderManage',
  data() {
    return {
      loading: false,
      detailLoading: false,
      actionLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      detail: null,
      detailVisible: false,
      specialRefundVisible: false,
      currentRow: null,
      dateRange: [],
      lastAutoOpenKey: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: undefined,
        orderStatus: undefined,
        refundStatus: undefined
      },
      specialRefundForm: { reason: '' },
      specialRefundRules: {
        reason: [{ required: true, message: '请填写退款原因', trigger: 'blur' }]
      },
      orderStatusOptions: Object.entries(ORDER_STATUS).map(([value, { label }]) => ({ value, label })),
      refundStatusOptions: Object.entries(REFUND_STATUS).map(([value, { label }]) => ({ value, label }))
    }
  },
  watch: {
    '$route.query': {
      handler() {
        this.tryAutoOpenFromRoute()
      },
      deep: true
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
        const params = { ...this.queryParams }
        if (this.dateRange && this.dateRange.length === 2) {
          params.beginCreateTime = this.dateRange[0]
          params.endCreateTime = this.dateRange[1]
        }
        const res = await listOrderMains(params)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally {
        this.loading = false
        this.tryAutoOpenFromRoute()
      }
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
        orderNo: undefined,
        orderStatus: undefined,
        refundStatus: undefined
      }
      this.getList()
    },
    async openDetail(row) {
      const orderId = Number(row && row.orderId)
      if (!orderId) return
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getOrderDetail(orderId)
        this.detail = res.data || null
      } finally {
        this.detailLoading = false
      }
    },
    tryAutoOpenFromRoute() {
      const query = this.$route.query || {}
      const autoOpen = query.autoOpen
      const orderId = Number(query.orderId)
      if (autoOpen !== '1' || !orderId) return
      const key = autoOpen + '-' + orderId
      if (this.lastAutoOpenKey === key) return
      this.lastAutoOpenKey = key
      this.openDetail({ orderId })
    },
    canSpecialRefund(row) {
      return row.orderStatus === 'paid' && (!row.refundStatus || row.refundStatus === 'none')
    },
    openSpecialRefund(row) {
      this.currentRow = row
      this.specialRefundForm.reason = ''
      this.specialRefundVisible = true
    },
    submitSpecialRefund() {
      this.$refs.specialRefundForm.validate(async valid => {
        if (!valid || !this.currentRow) return
        this.actionLoading = true
        try {
          await specialRefund(this.currentRow.orderId, { reason: this.specialRefundForm.reason })
          this.$modal.msgSuccess('特批退款已提交')
          this.specialRefundVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    orderStatusLabel(status) {
      return (ORDER_STATUS[status] && ORDER_STATUS[status].label) || status || '--'
    },
    orderStatusType(status) {
      return (ORDER_STATUS[status] && ORDER_STATUS[status].type) || 'info'
    },
    refundStatusLabel(status) {
      return (REFUND_STATUS[status] && REFUND_STATUS[status].label) || status || '--'
    },
    refundStatusType(status) {
      return (REFUND_STATUS[status] && REFUND_STATUS[status].type) || 'info'
    },
    formatMoney(value) {
      const n = Number(value || 0)
      return '\u00A5' + (n / 100).toFixed(2)
    }
  }
}
</script>

<style scoped>
.admin-order-page {
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

.danger-text {
  color: #f56c6c;
}

.mb12 {
  margin-bottom: 12px;
}

.drawer-body {
  padding: 0 24px 24px;
}

.section-title {
  margin: 18px 0 10px;
  font-weight: 700;
  color: #172033;
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
  .admin-order-page {
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
