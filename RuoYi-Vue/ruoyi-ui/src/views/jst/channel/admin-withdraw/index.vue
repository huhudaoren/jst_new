<template>
  <div class="app-container admin-withdraw-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">渠道财务</p>
        <h2>渠道提现管理</h2>
        <p class="hero-desc">审核渠道方提现申请，驳回不合规请求，执行打款操作。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom-end" width="320" trigger="click">
          <div class="help-popover__content">
            <p>提现处理流程：待审核→审核通过→执行打款，打款后状态会更新为已打款。</p>
            <p>驳回时请填写具体原因，便于渠道方后续修正材料再次申请。</p>
            <p>打款前先核对银行信息和金额，确认无误后再执行。</p>
            <p>建议按申请时间顺序处理，优先避免历史申请堆积。</p>
          </div>
          <el-button slot="reference" circle icon="el-icon-question" class="hero-help-btn" />
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
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

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.settlementId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.channelName || '--' }}</div>
              <div class="mobile-sub">{{ row.bankInfo || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-amount">{{ formatMoney(row.withdrawAmount) }}</div>
          <div class="mobile-info-row">{{ parseTime(row.applyTime || row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button v-if="isPending(row)" type="text" v-hasPermi="['jst:channel:withdraw:audit']" @click="handleAudit(row, 'approve')">通过</el-button>
            <el-button v-if="isPending(row)" type="text" class="danger-text" v-hasPermi="['jst:channel:withdraw:audit']" @click="handleAudit(row, 'reject')">驳回</el-button>
            <el-button v-if="isApproved(row)" type="text" v-hasPermi="['jst:channel:withdraw:execute']" @click="handleExecute(row)">打款</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无提现申请" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="申请ID" prop="settlementId" width="80" />
      <el-table-column label="渠道名称" prop="channelName" min-width="160" show-overflow-tooltip />
      <el-table-column label="提现金额" min-width="130" align="right">
        <template slot-scope="scope">
          <strong>{{ formatMoney(scope.row.withdrawAmount) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="银行信息" prop="bankInfo" min-width="200" show-overflow-tooltip />
      <el-table-column label="状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.applyTime || scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button v-if="isPending(scope.row)" type="text" v-hasPermi="['jst:channel:withdraw:audit']" @click="handleAudit(scope.row, 'approve')">通过</el-button>
          <el-button v-if="isPending(scope.row)" type="text" class="danger-text" v-hasPermi="['jst:channel:withdraw:audit']" @click="handleAudit(scope.row, 'reject')">驳回</el-button>
          <el-button v-if="isApproved(scope.row)" type="text" v-hasPermi="['jst:channel:withdraw:execute']" @click="handleExecute(scope.row)">打款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :visible.sync="detailVisible" :size="isMobile ? '100%' : '600px'" title="提现详情" append-to-body>
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detail">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="申请ID">{{ detail.settlementId }}</el-descriptions-item>
            <el-descriptions-item label="渠道名称">{{ detail.channelName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="提现金额">
              <strong>{{ formatMoney(detail.withdrawAmount) }}</strong>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag size="small" :type="statusType(detail.status)">{{ statusLabel(detail.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="银行信息">{{ detail.bankInfo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="开户名">{{ detail.accountName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ parseTime(detail.applyTime || detail.createTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ parseTime(detail.auditTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="打款时间">{{ parseTime(detail.payTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="备注">{{ detail.remark || '--' }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>

    <!-- 审核弹窗 -->
    <el-dialog :title="auditDialogTitle" :visible.sync="auditDialogVisible" width="460px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="88px">
        <el-form-item label="渠道">
          <span>{{ currentRow && currentRow.channelName }}</span>
        </el-form-item>
        <el-form-item label="提现金额">
          <strong>{{ currentRow ? formatMoney(currentRow.withdrawAmount) : '' }}</strong>
        </el-form-item>
        <el-form-item label="审核备注" prop="remark">
          <el-input v-model="auditForm.remark" type="textarea" :rows="4" maxlength="255" show-word-limit :placeholder="auditAction === 'reject' ? '驳回时请填写原因' : '可选填写'" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button :type="auditAction === 'approve' ? 'primary' : 'danger'" :loading="actionLoading" @click="submitAudit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWithdraws, getWithdrawDetail, approveWithdraw, rejectWithdraw, executeWithdraw } from '@/api/jst/channel/withdraw-admin'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '审核通过', type: 'primary' },
  rejected: { label: '已驳回', type: 'danger' },
  paid: { label: '已打款', type: 'success' },
  cancelled: { label: '已取消', type: 'info' }
}

export default {
  name: 'AdminWithdrawManage',
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
      auditDialogVisible: false,
      auditAction: 'approve',
      currentRow: null,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        channelName: undefined,
        status: undefined
      },
      auditForm: { remark: '' },
      auditRules: {
        remark: [{
          validator: (rule, value, callback) => {
            if (this.auditAction === 'reject' && !String(value || '').trim()) {
              callback(new Error('驳回时请填写原因'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }]
      },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label }))
    }
  },
  computed: {
    auditDialogTitle() {
      return this.auditAction === 'approve' ? '审核通过提现' : '驳回提现'
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
          params.beginApplyTime = this.dateRange[0]
          params.endApplyTime = this.dateRange[1]
        }
        const res = await listWithdraws(params)
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
      this.dateRange = []
      this.queryParams = { pageNum: 1, pageSize: 10, channelName: undefined, status: undefined }
      this.getList()
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getWithdrawDetail(row.settlementId)
        this.detail = res.data
      } finally {
        this.detailLoading = false
      }
    },
    isPending(row) {
      return row.status === 'pending'
    },
    isApproved(row) {
      return row.status === 'approved'
    },
    handleAudit(row, action) {
      this.currentRow = row
      this.auditAction = action
      this.auditForm.remark = ''
      this.auditDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.auditForm) this.$refs.auditForm.clearValidate()
      })
    },
    submitAudit() {
      this.$refs.auditForm.validate(async valid => {
        if (!valid || !this.currentRow) return
        this.actionLoading = true
        try {
          const fn = this.auditAction === 'approve' ? approveWithdraw : rejectWithdraw
          await fn(this.currentRow.settlementId, { remark: this.auditForm.remark })
          this.$modal.msgSuccess(this.auditAction === 'approve' ? '审核通过' : '已驳回')
          this.auditDialogVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    handleExecute(row) {
      this.$confirm('确认执行打款？打款操作不可撤销。', '执行打款', { type: 'warning' }).then(async() => {
        this.actionLoading = true
        try {
          await executeWithdraw(row.settlementId)
          this.$modal.msgSuccess('打款成功')
          this.getList()
        } finally {
          this.actionLoading = false
        }
      }).catch(() => {})
    },
    statusLabel(status) {
      return (STATUS_META[status] && STATUS_META[status].label) || status || '--'
    },
    statusType(status) {
      return (STATUS_META[status] && STATUS_META[status].type) || 'info'
    },
    formatMoney(value) {
      const n = Number(value || 0)
      return '\u00a5' + n.toFixed(2)
    }
  }
}
</script>

<style scoped>
.admin-withdraw-page {
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

.hero-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.hero-help-btn {
  border: 1px solid #dbe4f2;
  color: #2f6fec;
}

.help-popover__content p {
  margin: 0 0 8px;
  color: #475569;
  line-height: 1.6;
}

.help-popover__content p:last-child {
  margin-bottom: 0;
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
  .admin-withdraw-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .hero-actions {
    width: 100%;
    margin-top: 16px;
    justify-content: flex-start;
  }

  .hero-actions .hero-help-btn {
    width: 44px;
    min-width: 44px;
    padding: 0;
    min-height: 44px;
  }

  .hero-actions .el-button:not(.hero-help-btn) {
    flex: 1;
    min-height: 44px;
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
