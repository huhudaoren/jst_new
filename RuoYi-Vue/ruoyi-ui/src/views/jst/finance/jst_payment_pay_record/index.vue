<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">财务中心</p>
        <h2>打款记录</h2>
        <p class="hero-desc">查看平台对渠道方和赛事方的打款记录，支持按单号、类型、时间筛选。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="打款单号" prop="payNo">
        <el-input v-model="queryParams.payNo" placeholder="请输入打款单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="全部" clearable>
          <el-option label="渠道提现" value="rebate_withdraw" />
          <el-option label="赛事结算" value="event_settlement" />
        </el-select>
      </el-form-item>
      <el-form-item label="打款时间" prop="payTime">
        <el-date-picker v-model="queryParams.payTime" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.payRecordId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.payNo || '--' }}</div>
              <div class="mobile-sub">{{ bizTypeLabel(row.businessType) }} / {{ targetTypeLabel(row.targetType) }} #{{ row.targetId }}</div>
            </div>
          </div>
          <div class="mobile-amount amount-brand" style="margin-top:12px;font-size:22px;font-weight:700">{{ formatMoney(row.amount) }}</div>
          <div class="mobile-info-row">{{ parseTime(row.payTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无打款记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="payRecordId" width="70" />
      <el-table-column label="打款单号" prop="payNo" min-width="160" show-overflow-tooltip />
      <el-table-column label="业务类型" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="scope.row.businessType === 'rebate_withdraw' ? '' : 'warning'">{{ bizTypeLabel(scope.row.businessType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标类型" min-width="90">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ targetTypeLabel(scope.row.targetType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标ID" prop="targetId" width="80" />
      <el-table-column label="打款金额" min-width="130" align="right">
        <template slot-scope="scope"><strong class="amount-brand">{{ formatMoney(scope.row.amount) }}</strong></template>
      </el-table-column>
      <el-table-column label="打款账户" prop="payAccount" min-width="160" show-overflow-tooltip />
      <el-table-column label="打款时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.payTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" title="打款详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="打款ID">{{ detail.payRecordId }}</el-descriptions-item>
          <el-descriptions-item label="打款单号">{{ detail.payNo }}</el-descriptions-item>
          <el-descriptions-item label="业务类型"><el-tag size="small">{{ bizTypeLabel(detail.businessType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="业务单ID">{{ detail.businessId }}</el-descriptions-item>
          <el-descriptions-item label="目标类型"><el-tag size="small" type="info">{{ targetTypeLabel(detail.targetType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="目标ID">{{ detail.targetId }}</el-descriptions-item>
          <el-descriptions-item label="打款金额"><strong class="amount-brand" style="font-size:18px">{{ formatMoney(detail.amount) }}</strong></el-descriptions-item>
          <el-descriptions-item label="打款账户">{{ detail.payAccount || '--' }}</el-descriptions-item>
          <el-descriptions-item label="操作人ID">{{ detail.operatorId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.voucherUrl" label="打款凭证" :span="2">
            <el-image :src="detail.voucherUrl" style="max-width: 200px" :preview-src-list="[detail.voucherUrl]" />
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_payment_pay_record, getJst_payment_pay_record } from '@/api/jst/finance/jst_payment_pay_record'
import { formatMoney as formatMoneyUtil } from '@/utils/format'

export default {
  name: 'PaymentPayRecordManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, payNo: undefined, businessType: undefined, payTime: undefined },
      detailVisible: false,
      detail: null
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() { window.removeEventListener('resize', this.updateViewport) },
  methods: {
    updateViewport() { this.isMobile = window.innerWidth <= 768 },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_payment_pay_record(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, payNo: undefined, businessType: undefined, payTime: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_payment_pay_record(row.payRecordId)
        this.detail = res.data
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detail = row
      }
    },
    bizTypeLabel(t) { return { rebate_withdraw: '渠道提现', event_settlement: '赛事结算' }[t] || t || '--' },
    targetTypeLabel(t) { return { channel: '渠道方', partner: '赛事方' }[t] || t || '--' },
    formatMoney(v) { return formatMoneyUtil(v) }
  }
}
</script>

<style scoped>
.enhanced-page { background: #f6f8fb; min-height: calc(100vh - 84px); }
.page-hero { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 24px; margin-bottom: 18px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.hero-eyebrow { margin: 0 0 8px; color: #2f6fec; font-size: 13px; font-weight: 600; }
.page-hero h2 { margin: 0; font-size: 24px; font-weight: 700; color: #172033; }
.hero-desc { margin: 8px 0 0; color: #6f7b8f; }
.query-panel { padding: 16px 16px 0; margin-bottom: 16px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.amount-brand { color: #2f6fec; }
.drawer-body { padding: 20px; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; }
.mobile-actions { margin-top: 12px; border-top: 1px solid #f0f2f5; padding-top: 12px; }
@media (max-width: 768px) {
  .enhanced-page { padding: 12px; }
  .page-hero { display: block; padding: 18px; }
  .page-hero .el-button { width: 100%; min-height: 44px; margin-top: 16px; }
  .page-hero h2 { font-size: 20px; }
  .query-panel { padding-bottom: 8px; }
  .query-panel ::v-deep .el-form-item { display: block; margin-right: 0; }
  .query-panel ::v-deep .el-form-item__content, .query-panel ::v-deep .el-select, .query-panel ::v-deep .el-input { width: 100%; }
}
</style>
