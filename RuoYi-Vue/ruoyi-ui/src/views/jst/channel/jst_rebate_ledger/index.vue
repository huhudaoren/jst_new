<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">渠道财务</p>
        <h2>返点台账</h2>
        <p class="hero-desc">查看渠道返点计提明细，按渠道、订单、方向筛选。只读查询。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="渠道ID" prop="channelId">
        <el-input v-model="queryParams.channelId" placeholder="请输入渠道ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单ID" prop="orderId">
        <el-input v-model="queryParams.orderId" placeholder="请输入订单ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="方向" prop="direction">
        <el-select v-model="queryParams.direction" placeholder="全部" clearable>
          <el-option label="收入" value="positive" />
          <el-option label="扣减" value="negative" />
        </el-select>
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
              <div class="mobile-title">渠道 #{{ row.channelId || '--' }}</div>
              <div class="mobile-sub">订单 #{{ row.orderId || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-amount-row">
            <el-tag size="mini" :type="row.direction === 'positive' ? 'success' : 'danger'">{{ row.direction === 'positive' ? '收入' : '扣减' }}</el-tag>
            <span class="mobile-amount" :class="{ 'amount-negative': Number(row.rebateAmount) < 0 }">{{ formatMoney(row.rebateAmount) }}</span>
          </div>
          <div class="mobile-info-row">{{ parseTime(row.accrualTime) || parseTime(row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无台账记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="台账ID" prop="ledgerId" width="80" />
      <el-table-column label="渠道ID" prop="channelId" width="90" />
      <el-table-column label="订单ID" prop="orderId" width="90" />
      <el-table-column label="方向" width="90">
        <template slot-scope="scope">
          <el-tag size="small" :type="scope.row.direction === 'positive' ? 'success' : 'danger'">{{ scope.row.direction === 'positive' ? '收入' : '扣减' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="返点金额" min-width="120" align="right">
        <template slot-scope="scope">
          <strong :class="{ 'amount-negative': Number(scope.row.rebateAmount) < 0 }">{{ formatMoney(scope.row.rebateAmount) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="净付款" min-width="110" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.netPayAmount) }}</template>
      </el-table-column>
      <el-table-column label="服务费" min-width="100" align="right">
        <template slot-scope="scope">{{ formatMoney(scope.row.serviceFee) }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="计提时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.accrualTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" title="台账详情" append-to-body>
      <div v-if="detail" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="台账ID">{{ detail.ledgerId }}</el-descriptions-item>
          <el-descriptions-item label="渠道ID">{{ detail.channelId }}</el-descriptions-item>
          <el-descriptions-item label="订单ID">{{ detail.orderId }}</el-descriptions-item>
          <el-descriptions-item label="赛事ID">{{ detail.contestId }}</el-descriptions-item>
          <el-descriptions-item label="规则ID">{{ detail.ruleId }}</el-descriptions-item>
          <el-descriptions-item label="方向">
            <el-tag size="small" :type="detail.direction === 'positive' ? 'success' : 'danger'">{{ detail.direction === 'positive' ? '收入' : '扣减' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="挂牌金额">{{ formatMoney(detail.listAmount) }}</el-descriptions-item>
          <el-descriptions-item label="净付款">{{ formatMoney(detail.netPayAmount) }}</el-descriptions-item>
          <el-descriptions-item label="服务费">{{ formatMoney(detail.serviceFee) }}</el-descriptions-item>
          <el-descriptions-item label="返点基数">{{ formatMoney(detail.rebateBase) }}</el-descriptions-item>
          <el-descriptions-item label="返点金额"><strong :class="{ 'amount-negative': Number(detail.rebateAmount) < 0 }">{{ formatMoney(detail.rebateAmount) }}</strong></el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="计提时间">{{ parseTime(detail.accrualTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事结束时间">{{ parseTime(detail.eventEndTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="结算单ID">{{ detail.settlementId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_rebate_ledger, getJst_rebate_ledger } from '@/api/jst/channel/jst_rebate_ledger'

const STATUS_META = {
  pending_accrual: { label: '待计提', type: 'info' },
  withdrawable: { label: '可提现', type: 'success' },
  in_review: { label: '审核中', type: 'warning' },
  paid: { label: '已打款', type: 'success' },
  rolled_back: { label: '已回退', type: 'danger' },
  negative: { label: '负向抵扣', type: 'danger' }
}

export default {
  name: 'RebateLedgerManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, channelId: undefined, orderId: undefined, direction: undefined, status: undefined },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label })),
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
        const res = await listJst_rebate_ledger(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, channelId: undefined, orderId: undefined, direction: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detail = null
      try {
        const res = await getJst_rebate_ledger(row.ledgerId)
        this.detail = res.data
      } catch (_) { this.detail = row }
    },
    statusLabel(status) { return (STATUS_META[status] && STATUS_META[status].label) || status || '--' },
    statusType(status) { return (STATUS_META[status] && STATUS_META[status].type) || 'info' },
    formatMoney(v) { return '\u00a5' + Number(v || 0).toFixed(2) }
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
.amount-negative { color: #f56c6c; font-weight: 600; }
.drawer-body { padding: 20px; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-amount-row { margin-top: 12px; display: flex; align-items: center; gap: 8px; }
.mobile-amount { font-size: 22px; font-weight: 700; color: #172033; }
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
