<template>
  <div class="app-container" v-loading="loading">
    <!-- 页头 -->
    <el-page-header @back="$router.go(-1)" content="月结单详情" />

    <div v-if="settlement">
      <!-- 基本信息 -->
      <el-card class="detail-card" style="margin-top: 16px;">
        <div slot="header" class="card-header">
          <span>结算信息</span>
          <el-tag size="small" :type="statusTagType(settlement.status)">{{ statusLabel(settlement.status) }}</el-tag>
        </div>
        <el-descriptions :column="descColumn" border size="medium">
          <el-descriptions-item label="结算单ID">{{ settlement.settlementId }}</el-descriptions-item>
          <el-descriptions-item label="销售姓名">{{ settlement.salesName || settlement.salesId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结算月份">{{ settlement.period || '-' }}</el-descriptions-item>
          <el-descriptions-item label="成交笔数">{{ settlement.orderCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="提成金额">¥{{ formatAmount(settlement.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ settlement.auditorName || settlement.auditBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="打款流水">{{ settlement.payVoucher || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(settlement.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <template v-if="settlement.status === 'pending_review'">
          <el-button type="success" icon="el-icon-check" :loading="submitting" @click="handleApprove">审核通过</el-button>
          <el-button type="danger" icon="el-icon-close" :loading="submitting" @click="handleReject">驳回</el-button>
        </template>
        <el-button v-if="settlement.status === 'approved'" type="primary" icon="el-icon-bank-card" :loading="submitting" @click="handlePay">录入打款</el-button>
      </div>

      <!-- 提成明细 -->
      <el-card class="detail-card" style="margin-top: 16px;" v-if="ledgers.length">
        <div slot="header">提成明细（共 {{ ledgers.length }} 笔）</div>
        <el-table :data="ledgers" border stripe size="small">
          <el-table-column label="订单号" prop="orderNo" min-width="160" show-overflow-tooltip />
          <el-table-column label="渠道" min-width="140">
            <template slot-scope="scope">
              <entity-link v-if="scope.row.channelId" entity="channel" :id="scope.row.channelId" :name="scope.row.channelName" />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="业务类型" prop="businessType" width="110" align="center">
            <template slot-scope="scope">{{ scope.row.businessType || '-' }}</template>
          </el-table-column>
          <el-table-column label="基础金额" prop="baseAmount" width="110" align="right">
            <template slot-scope="scope">¥{{ formatAmount(scope.row.baseAmount) }}</template>
          </el-table-column>
          <el-table-column label="费率" prop="appliedRate" width="80" align="right">
            <template slot-scope="scope">
              {{ scope.row.appliedRate != null ? (scope.row.appliedRate * 100).toFixed(2) + '%' : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="提成金额" prop="amount" width="110" align="right">
            <template slot-scope="scope">
              <span style="color: #67C23A; font-weight: 600;">¥{{ formatAmount(scope.row.amount) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <empty-state-cta v-if="!loading && !ledgers.length" title="暂无提成明细" description="本月结单暂无关联提成台账" :image-size="60" />
    </div>
    <el-empty v-if="!loading && !settlement" description="结算单不存在" :image-size="80" />
  </div>
</template>

<script>
import { getSettlementDetail, approveSettlement, rejectSettlement, recordPayment } from '@/api/admin/sales/settlement'

export default {
  name: 'SalesSettlementDetail',
  data() {
    return {
      loading: false,
      settlement: null,
      ledgers: [],
      submitting: false
    }
  },
  computed: {
    descColumn() {
      return window.innerWidth <= 768 ? 1 : 2
    }
  },
  created() {
    const id = this.$route.query.settlementId
    if (id) this.loadDetail(id)
  },
  methods: {
    loadDetail(id) {
      this.loading = true
      getSettlementDetail(id).then(res => {
        const data = (res && res.data) || {}
        this.settlement = data.settlement || data
        this.ledgers = data.ledgers || []
      }).catch(() => {}).finally(() => { this.loading = false })
    },
    statusTagType(s) {
      const m = { pending_review: 'warning', approved: 'primary', paid: 'success', rejected: 'danger' }
      return m[s] || 'info'
    },
    statusLabel(s) {
      const m = { pending_review: '待审核', approved: '已审核', paid: '已打款', rejected: '已驳回' }
      return m[s] || s || '-'
    },
    formatAmount(v) {
      if (v == null) return '0.00'
      return Number(v).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },
    handleApprove() {
      this.$confirm('确认审核通过本月结单？', '审核确认', { type: 'warning' }).then(() => {
        this.submitting = true
        return approveSettlement(this.settlement.settlementId)
      }).then(() => {
        this.$modal.msgSuccess('审核通过')
        this.loadDetail(this.settlement.settlementId)
      }).catch(() => {}).finally(() => { this.submitting = false })
    },
    handleReject() {
      this.$prompt('请输入驳回原因', '驳回月结单', {
        inputPlaceholder: '请输入原因',
        confirmButtonText: '确认驳回',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (!value) return
        this.submitting = true
        return rejectSettlement(this.settlement.settlementId, value)
      }).then(() => {
        this.$modal.msgSuccess('已驳回')
        this.loadDetail(this.settlement.settlementId)
      }).catch(() => {}).finally(() => { this.submitting = false })
    },
    handlePay() {
      this.$prompt('请输入打款凭证号', '录入打款', {
        inputPlaceholder: '如银行流水号、转账编号',
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (!value) return
        this.submitting = true
        return recordPayment(this.settlement.settlementId, value)
      }).then(() => {
        this.$modal.msgSuccess('打款录入成功')
        this.loadDetail(this.settlement.settlementId)
      }).catch(() => {}).finally(() => { this.submitting = false })
    }
  }
}
</script>

<style scoped>
.detail-card .card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  font-weight: 600;
}
.action-bar {
  margin: 16px 0 0;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
@media (max-width: 768px) {
  .action-bar { justify-content: center; }
}
</style>
