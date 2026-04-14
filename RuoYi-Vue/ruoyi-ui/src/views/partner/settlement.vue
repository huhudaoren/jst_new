<template>
  <div class="app-container partner-settlement-page">
    <div class="settlement-hero">
      <div>
        <p class="hero-eyebrow">结算中心</p>
        <h2>每一笔结算都有清楚的来龙去脉</h2>
        <p class="hero-desc">查看赛事结算金额、订单拆分、打款凭证与平台审核进度。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <div class="metric-grid">
      <div v-for="item in kpiCards" :key="item.key" class="metric-tile">
        <div class="metric-label">{{ item.label }}</div>
        <div class="metric-value">{{ item.value }}</div>
        <div class="metric-hint">{{ item.hint }}</div>
      </div>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="72px"
      class="query-panel"
    >
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="输入赛事ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="settlementList.length">
        <div v-for="row in settlementList" :key="row.eventSettlementId" class="mobile-item">
          <div class="mobile-top">
            <div>
              <div class="mobile-title">{{ row.contestName || '未命名赛事' }}</div>
              <div class="mobile-sub">{{ row.settlementNo }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row) }}</el-tag>
          </div>
          <div class="mobile-amount">{{ formatMoney(row.finalAmount) }}</div>
          <div class="mobile-lines">
            <span>订单 {{ row.enrollCount || 0 }}</span>
            <span>净实付 {{ formatMoney(row.totalNetPay) }}</span>
            <span>服务费 {{ formatMoney(row.totalServiceFee) }}</span>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">查看</el-button>
            <el-button v-if="canAct(row)" type="text" @click="handleConfirm(row)">确认</el-button>
            <el-button v-if="canAct(row)" type="text" class="danger-text" @click="openDispute(row)">有争议</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无结算单" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="settlementList">
      <el-table-column label="结算单号" prop="settlementNo" min-width="170" show-overflow-tooltip />
      <el-table-column label="赛事" prop="contestName" min-width="220" show-overflow-tooltip />
      <el-table-column label="订单数" prop="enrollCount" width="90" align="center" />
      <el-table-column label="净实付" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.totalNetPay) }}</template>
      </el-table-column>
      <el-table-column label="渠道返点" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.totalRebate) }}</template>
      </el-table-column>
      <el-table-column label="服务费" min-width="120" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.totalServiceFee) }}</template>
      </el-table-column>
      <el-table-column label="最终结算" min-width="130" align="right">
        <template slot-scope="scope">
          <strong>{{ formatMoney(scope.row.finalAmount) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="打款时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.payTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="190" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">查看</el-button>
          <el-button v-if="canAct(scope.row)" type="text" @click="handleConfirm(scope.row)">确认</el-button>
          <el-button v-if="canAct(scope.row)" type="text" class="danger-text" @click="openDispute(scope.row)">有争议</el-button>
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

    <el-drawer
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '760px'"
      title="结算详情"
      append-to-body
      custom-class="settlement-drawer"
    >
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detail">
          <div class="detail-head">
            <div>
              <div class="detail-title">{{ detail.contestName || '未命名赛事' }}</div>
              <div class="detail-sub">{{ detail.settlementNo }}</div>
            </div>
            <el-tag :type="statusType(detail.status)">{{ statusLabel(detail) }}</el-tag>
          </div>

          <div class="amount-band">
            <div>
              <span>最终结算金额</span>
              <strong>{{ formatMoney(detail.finalAmount) }}</strong>
            </div>
            <div>
              <span>用户净实付</span>
              <strong>{{ formatMoney(detail.totalNetPay) }}</strong>
            </div>
          </div>

          <el-descriptions :column="isMobile ? 1 : 2" border class="detail-section">
            <el-descriptions-item label="标价合计">{{ formatMoney(detail.totalListAmount) }}</el-descriptions-item>
            <el-descriptions-item label="优惠券抵扣">{{ formatMoney(detail.totalCouponAmount) }}</el-descriptions-item>
            <el-descriptions-item label="积分折抵">{{ formatMoney(detail.totalPointsAmount) }}</el-descriptions-item>
            <el-descriptions-item label="平台承担">{{ formatMoney(detail.platformBearAmount) }}</el-descriptions-item>
            <el-descriptions-item label="退款合计">{{ formatMoney(detail.totalRefund) }}</el-descriptions-item>
            <el-descriptions-item label="渠道返点">{{ formatMoney(detail.totalRebate) }}</el-descriptions-item>
            <el-descriptions-item label="平台服务费">{{ formatMoney(detail.totalServiceFee) }}</el-descriptions-item>
            <el-descriptions-item label="合同扣项">{{ formatMoney(detail.contractDeduction) }}</el-descriptions-item>
            <el-descriptions-item label="审核备注">{{ disputeText(detail.auditRemark) || detail.auditRemark || '--' }}</el-descriptions-item>
            <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
          </el-descriptions>

          <div class="section-title">订单明细</div>
          <el-table :data="detail.orderList || []" size="small" max-height="240">
            <el-table-column label="订单号" prop="orderNo" min-width="170" show-overflow-tooltip />
            <el-table-column label="净实付" min-width="100" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.netPayAmount) }}</template>
            </el-table-column>
            <el-table-column label="服务费" min-width="100" align="right">
              <template slot-scope="scope">{{ formatMoney(scope.row.serviceFee) }}</template>
            </el-table-column>
            <el-table-column label="退款" prop="refundStatus" min-width="90">
              <template slot-scope="scope">
                <dict-tag :options="dict.type.jst_refund_status" :value="scope.row.refundStatus" />
              </template>
            </el-table-column>
            <el-table-column label="支付时间" min-width="150">
              <template slot-scope="scope">{{ parseTime(scope.row.payTime) || '--' }}</template>
            </el-table-column>
          </el-table>

          <div class="section-title">打款凭证</div>
          <div v-if="detail.payoutFiles && detail.payoutFiles.length" class="file-list">
            <div v-for="file in detail.payoutFiles" :key="file.fileNo" class="file-item">
              <i class="el-icon-document-checked" />
              <span>{{ file.fileName || file.fileNo }}</span>
              <el-link v-if="file.fileUrl" type="primary" :href="file.fileUrl" target="_blank">查看凭证</el-link>
            </div>
          </div>
          <el-empty v-else description="暂无打款凭证" :image-size="80" />

          <div class="section-title">合同与发票</div>
          <el-alert
            title="合同下载与发票开具将在 F-2 阶段接入，本页仅展示已关联记录。"
            type="info"
            show-icon
            :closable="false"
          />
          <div class="file-list muted-files">
            <div v-for="file in contractInvoiceFiles" :key="file.fileType + '-' + file.fileNo" class="file-item">
              <i class="el-icon-document" />
              <span>{{ file.fileName || file.fileNo || '未命名文件' }}</span>
              <el-tag size="mini" type="info">{{ file.status || '待处理' }}</el-tag>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>

    <el-dialog title="提交结算争议" :visible.sync="disputeVisible" width="460px" append-to-body>
      <el-form ref="disputeForm" :model="disputeForm" :rules="disputeRules" label-width="88px">
        <el-form-item label="结算单">
          <span>{{ currentRow && currentRow.settlementNo }}</span>
        </el-form-item>
        <el-form-item label="争议说明" prop="disputeReason">
          <el-input
            v-model="disputeForm.disputeReason"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            placeholder="请说明金额或扣项需要复核的原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="disputeVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitDispute">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  confirmPartnerSettlement,
  disputePartnerSettlement,
  getPartnerSettlement,
  listPartnerSettlements
} from '@/api/partner/settlement'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

export default {
  name: 'PartnerSettlement',
  dicts: ['jst_refund_status'],
  data() {
    return {
      loading: false,
      detailLoading: false,
      actionLoading: false,
      isMobile: false,
      settlementList: [],
      total: 0,
      detail: null,
      detailVisible: false,
      disputeVisible: false,
      currentRow: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: undefined,
        contestId: undefined
      },
      disputeForm: {
        disputeReason: ''
      },
      disputeRules: {
        disputeReason: [
          { required: true, message: '请填写争议说明', trigger: 'blur' },
          { min: 2, max: 255, message: '争议说明为2到255个字符', trigger: 'blur' }
        ]
      },
      statusOptions: [
        { value: 'pending_confirm', label: '待确认' },
        { value: 'reviewing', label: '审核中' },
        { value: 'rejected', label: '已驳回' },
        { value: 'pending_pay', label: '待打款' },
        { value: 'paid', label: '已打款' }
      ]
    }
  },
  computed: {
    kpiCards() {
      const monthAmount = this.sumBy(row => this.isThisMonth(row.createTime) ? row.finalAmount : 0)
      return [
        { key: 'month', label: '本月应结', value: this.formatMoney(monthAmount), hint: '按当前筛选结果统计' },
        { key: 'paid', label: '累计已结', value: this.formatMoney(this.sumBy(row => row.status === 'paid' ? row.finalAmount : 0)), hint: '已打款结算单' },
        { key: 'reviewing', label: '审核中', value: this.countBy(row => row.status === 'reviewing'), hint: '等待平台复核' },
        { key: 'dispute', label: '争议中', value: this.countBy(row => this.isDispute(row)), hint: '赛事方已提交说明' }
      ]
    },
    contractInvoiceFiles() {
      if (!this.detail) {
        return []
      }
      return [
        ...(this.detail.contractFiles || []),
        ...(this.detail.invoiceFiles || [])
      ]
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
        const res = await listPartnerSettlements(this.queryParams)
        this.settlementList = res.rows || []
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
      this.queryParams.status = undefined
      this.queryParams.contestId = undefined
      this.handleQuery()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getPartnerSettlement(row.eventSettlementId)
        this.detail = res.data
      } finally {
        this.detailLoading = false
      }
    },
    handleConfirm(row) {
      this.$confirm('确认后结算单将进入平台复核，金额不可由赛事方修改。', '确认结算单', {
        type: 'warning'
      }).then(async() => {
        this.actionLoading = true
        try {
          await confirmPartnerSettlement(row.eventSettlementId)
          this.$modal.msgSuccess('已提交确认')
          this.getList()
          if (this.detailVisible) {
            this.openDetail(row)
          }
        } finally {
          this.actionLoading = false
        }
      }).catch(() => {})
    },
    openDispute(row) {
      this.currentRow = row
      this.disputeForm.disputeReason = ''
      this.disputeVisible = true
    },
    submitDispute() {
      this.$refs.disputeForm.validate(async valid => {
        if (!valid || !this.currentRow) {
          return
        }
        this.actionLoading = true
        try {
          await disputePartnerSettlement(this.currentRow.eventSettlementId, this.disputeForm)
          this.$modal.msgSuccess('已提交争议说明')
          this.disputeVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    canAct(row) {
      return row && row.status === 'pending_confirm'
    },
    statusLabel(row) {
      if (this.isDispute(row)) {
        return '争议中'
      }
      const hit = this.statusOptions.find(item => item.value === (row && row.status))
      return hit ? hit.label : (row && row.status) || '--'
    },
    statusType(status) {
      const map = {
        pending_confirm: 'warning',
        reviewing: 'primary',
        rejected: 'danger',
        pending_pay: 'info',
        paid: 'success'
      }
      return map[status] || 'info'
    },
    isDispute(row) {
      return !!(row && row.auditRemark && row.auditRemark.indexOf('DISPUTE:') === 0)
    },
    disputeText(text) {
      return text && text.indexOf('DISPUTE:') === 0 ? text.replace('DISPUTE:', '') : ''
    },
    sumBy(picker) {
      return this.settlementList.reduce((sum, row) => sum + Number(picker(row) || 0), 0)
    },
    countBy(predicate) {
      return this.settlementList.filter(predicate).length
    },
    isThisMonth(value) {
      if (!value) {
        return false
      }
      const date = new Date(value)
      const now = new Date()
      return date.getFullYear() === now.getFullYear() && date.getMonth() === now.getMonth()
    },
    formatMoney(value) {
      const amount = Number(value || 0)
      return '¥' + amount.toFixed(2)
    }
  }
}
</script>

<style scoped>
.partner-settlement-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.settlement-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  color: #172033;
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

.settlement-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.metric-tile,
.query-panel,
.mobile-item {
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.metric-tile {
  padding: 18px;
}

.metric-label,
.metric-hint,
.mobile-sub,
.mobile-lines {
  color: #7a8495;
}

.metric-value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.metric-hint {
  margin-top: 6px;
  font-size: 12px;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
}

.danger-text {
  color: #f56c6c;
}

.mobile-list {
  min-height: 180px;
}

.mobile-item {
  padding: 16px;
  margin-bottom: 12px;
}

.mobile-top,
.detail-head,
.file-item,
.amount-band {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.mobile-title,
.detail-title {
  font-weight: 700;
  color: #172033;
}

.mobile-sub,
.detail-sub {
  margin-top: 4px;
  font-size: 12px;
}

.mobile-amount {
  margin-top: 14px;
  font-size: 24px;
  font-weight: 700;
}

.mobile-lines {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  gap: 14px;
  margin-top: 12px;
}

.drawer-body {
  padding: 0 24px 24px;
}

.amount-band {
  margin: 18px 0;
  padding: 18px;
  background: #f6f8fb;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.amount-band span {
  display: block;
  color: #7a8495;
  font-size: 13px;
}

.amount-band strong {
  display: block;
  margin-top: 6px;
  color: #172033;
  font-size: 22px;
}

.detail-section,
.section-title {
  margin-top: 18px;
}

.section-title {
  margin-bottom: 10px;
  font-weight: 700;
  color: #172033;
}

.file-list {
  display: grid;
  gap: 10px;
  margin-top: 10px;
}

.file-item {
  min-height: 44px;
  padding: 10px 12px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.file-item span {
  flex: 1;
}

.muted-files .file-item {
  color: #6f7b8f;
  background: #f8fafc;
}

@media (max-width: 768px) {
  .partner-settlement-page {
    padding: 12px;
  }

  .settlement-hero {
    display: block;
    padding: 18px;
  }

  .settlement-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .metric-tile {
    padding: 14px;
  }

  .metric-value {
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

  .amount-band {
    display: grid;
    grid-template-columns: 1fr;
  }
}
</style>
