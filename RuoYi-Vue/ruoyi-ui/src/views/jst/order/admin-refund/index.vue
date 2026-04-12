<template>
  <div class="app-container admin-refund-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">退款中心</p>
        <h2>退款管理</h2>
        <p class="hero-desc">审核退款申请、驳回不合理请求、执行已审核的退款操作。</p>
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
      <el-form-item label="退款单号" prop="refundNo">
        <el-input v-model="queryParams.refundNo" placeholder="请输入退款单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="退款状态" prop="refundStatus">
        <el-select v-model="queryParams.refundStatus" placeholder="全部" clearable>
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
        <div v-for="row in list" :key="row.refundId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.refundNo }}</div>
              <div class="mobile-sub">关联订单: {{ row.orderNo || row.orderId || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.refundStatus)">{{ statusLabel(row.refundStatus) }}</el-tag>
          </div>
          <div class="mobile-amount amount-negative">{{ formatMoney(row.refundAmount) }}</div>
          <div class="mobile-info-row">
            <span>{{ row.userName || '--' }}</span>
            <span>{{ parseTime(row.applyTime) || '--' }}</span>
          </div>
          <div class="mobile-actions">
            <el-button v-if="isPending(row)" type="text" v-hasPermi="['jst:order:refund:approve']" @click="handleAudit(row, 'approve')">通过</el-button>
            <el-button v-if="isPending(row)" type="text" class="danger-text" v-hasPermi="['jst:order:refund:approve']" @click="handleAudit(row, 'reject')">驳回</el-button>
            <el-button v-if="isApproved(row)" type="text" v-hasPermi="['jst:order:refund:execute']" @click="handleExecute(row)">执行退款</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无退款记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="退款单号" prop="refundNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="关联订单" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">{{ scope.row.orderNo || scope.row.orderId || '--' }}</template>
      </el-table-column>
      <el-table-column label="用户" prop="userName" min-width="120" show-overflow-tooltip />
      <el-table-column label="退款金额" min-width="120" align="right">
        <template slot-scope="scope">
          <span class="amount-negative">{{ formatMoney(scope.row.refundAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="退款状态" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.refundStatus)">{{ statusLabel(scope.row.refundStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.applyTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="审核时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.auditTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="isPending(scope.row)" type="text" v-hasPermi="['jst:order:refund:approve']" @click="handleAudit(scope.row, 'approve')">通过</el-button>
          <el-button v-if="isPending(scope.row)" type="text" class="danger-text" v-hasPermi="['jst:order:refund:approve']" @click="handleAudit(scope.row, 'reject')">驳回</el-button>
          <el-button v-if="isApproved(scope.row)" type="text" v-hasPermi="['jst:order:refund:execute']" @click="handleExecute(scope.row)">执行退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 审核弹窗 -->
    <el-dialog :title="auditDialogTitle" :visible.sync="auditVisible" width="460px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="88px">
        <el-form-item label="退款单号">
          <span>{{ currentRow && currentRow.refundNo }}</span>
        </el-form-item>
        <el-form-item label="退款金额">
          <span class="amount-negative">{{ currentRow ? formatMoney(currentRow.refundAmount) : '' }}</span>
        </el-form-item>
        <el-form-item label="审核备注" prop="remark">
          <el-input v-model="auditForm.remark" type="textarea" :rows="4" maxlength="255" show-word-limit :placeholder="auditAction === 'reject' ? '驳回时请填写原因' : '可选填写'" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button :type="auditAction === 'approve' ? 'primary' : 'danger'" :loading="actionLoading" @click="submitAudit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRefunds, approveRefund, rejectRefund, executeRefund } from '@/api/jst/order/refund-admin'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已审核', type: 'primary' },
  rejected: { label: '已驳回', type: 'danger' },
  executing: { label: '退款中', type: 'info' },
  success: { label: '退款成功', type: 'success' },
  failed: { label: '退款失败', type: 'danger' }
}

export default {
  name: 'AdminRefundManage',
  data() {
    return {
      loading: false,
      actionLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      currentRow: null,
      auditVisible: false,
      auditAction: 'approve',
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        refundNo: undefined,
        refundStatus: undefined
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
      return this.auditAction === 'approve' ? '审核通过退款' : '驳回退款'
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
        const res = await listRefunds(params)
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
      this.queryParams = { pageNum: 1, pageSize: 10, refundNo: undefined, refundStatus: undefined }
      this.getList()
    },
    isPending(row) {
      return row.refundStatus === 'pending'
    },
    isApproved(row) {
      return row.refundStatus === 'approved'
    },
    handleAudit(row, action) {
      this.currentRow = row
      this.auditAction = action
      this.auditForm.remark = ''
      this.auditVisible = true
      this.$nextTick(() => {
        if (this.$refs.auditForm) this.$refs.auditForm.clearValidate()
      })
    },
    submitAudit() {
      this.$refs.auditForm.validate(async valid => {
        if (!valid || !this.currentRow) return
        this.actionLoading = true
        try {
          const fn = this.auditAction === 'approve' ? approveRefund : rejectRefund
          await fn(this.currentRow.refundId, { remark: this.auditForm.remark })
          this.$modal.msgSuccess(this.auditAction === 'approve' ? '已审核通过' : '已驳回')
          this.auditVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    handleExecute(row) {
      this.$confirm('确认执行退款？退款将不可撤销。', '执行退款', { type: 'warning' }).then(async() => {
        this.actionLoading = true
        try {
          await executeRefund(row.refundId)
          this.$modal.msgSuccess('退款执行成功')
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
.admin-refund-page {
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
  .admin-refund-page {
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
