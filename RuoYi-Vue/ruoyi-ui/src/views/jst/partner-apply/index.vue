<template>
  <div class="app-container jst-admin-partner-apply-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">入驻中心</p>
        <h2>赛事方入驻审核</h2>
        <p class="hero-desc">审核赛事方入驻申请，查看材料详情并完成通过或驳回。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="84px"
      class="query-panel"
    >
      <el-form-item label="申请关键词" prop="keyword">
        <el-input v-model="queryParams.keyword" placeholder="申请单号/机构名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="applyList.length">
        <div v-for="row in applyList" :key="row.applyId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.partnerName || '--' }}</div>
              <div class="mobile-sub">申请ID: {{ row.applyId }}</div>
            </div>
            <JstStatusBadge :status="String(row.applyStatus || '')" :status-map="statusMap" />
          </div>
          <div class="mobile-info-row">申请单号：{{ row.applyNo || '--' }}</div>
          <div class="mobile-info-row">联系人：{{ row.contactName || '--' }}</div>
          <div class="mobile-info-row">联系电话：{{ row.contactMobile || '--' }}</div>
          <div class="mobile-info-row">提交时间：{{ parseTime(row.submitTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button
              v-if="canApprove(row)"
              type="text"
              v-hasPermi="['jst:organizer:partnerApply:approve']"
              @click="openApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="canReject(row)"
              type="text"
              class="danger-text"
              v-hasPermi="['jst:organizer:partnerApply:reject']"
              @click="openReject(row)"
            >
              驳回
            </el-button>
          </div>
        </div>
      </div>
      <JstEmptyState v-else description="暂无入驻申请" />
    </div>

    <el-table v-else v-loading="loading" :data="applyList">
      <el-table-column label="申请ID" prop="applyId" min-width="90" />
      <el-table-column label="机构名称" prop="partnerName" min-width="180" show-overflow-tooltip />
      <el-table-column label="联系人" prop="contactName" min-width="120" />
      <el-table-column label="联系电话" prop="contactMobile" min-width="140" />
      <el-table-column label="审核状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.applyStatus || '')" :status-map="statusMap" />
        </template>
      </el-table-column>
      <el-table-column label="提交时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.submitTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="220" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button
            v-if="canApprove(scope.row)"
            type="text"
            v-hasPermi="['jst:organizer:partnerApply:approve']"
            @click="openApprove(scope.row)"
          >
            通过
          </el-button>
          <el-button
            v-if="canReject(scope.row)"
            type="text"
            class="danger-text"
            v-hasPermi="['jst:organizer:partnerApply:reject']"
            @click="openReject(scope.row)"
          >
            驳回
          </el-button>
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

    <el-dialog title="审核通过" :visible.sync="approveVisible" width="460px" append-to-body>
      <el-form ref="approveFormRef" :model="approveForm" :rules="approveRules" label-width="92px">
        <el-form-item label="申请ID">
          <span>{{ approveForm.applyId || '--' }}</span>
        </el-form-item>
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="approveForm.username" maxlength="30" placeholder="请输入要创建的账号名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="approveForm.email" maxlength="50" placeholder="可选：用于通知" />
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="approveForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            placeholder="可选备注"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitApprove">确定通过</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核驳回" :visible.sync="rejectVisible" width="420px" append-to-body>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="92px">
        <el-form-item label="申请ID">
          <span>{{ rejectForm.applyId || '--' }}</span>
        </el-form-item>
        <el-form-item label="驳回原因" prop="auditRemark">
          <el-input
            v-model="rejectForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            placeholder="请填写驳回原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitReject">确定驳回</el-button>
      </div>
    </el-dialog>

    <el-drawer
      title="申请详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '840px'"
      append-to-body
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <template v-if="detailData">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="申请ID">{{ detailData.applyId }}</el-descriptions-item>
            <el-descriptions-item label="申请单号">{{ detailData.applyNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="机构名称">{{ detailData.partnerName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="机构ID">{{ detailData.partnerId || '--' }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactMobile || '--' }}</el-descriptions-item>
            <el-descriptions-item label="营业执照号">{{ detailData.businessLicenseNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <JstStatusBadge :status="String(detailData.applyStatus || '')" :status-map="statusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ parseTime(detailData.submitTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核时间">{{ parseTime(detailData.auditTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核备注" :span="isMobile ? 1 : 2">
              {{ detailData.auditRemark || '--' }}
            </el-descriptions-item>
            <el-descriptions-item label="补充要求" :span="isMobile ? 1 : 2">
              {{ detailData.supplementRemark || '--' }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="section-title">资质材料</div>
          <JsonDisplay :value="detailData.qualificationJson" />

          <div class="section-title">结算信息</div>
          <JsonDisplay :value="detailData.settlementInfoJson" :label-map="settlementLabelMap" />

          <div class="section-title">开票信息</div>
          <JsonDisplay :value="detailData.invoiceInfoJson" :label-map="invoiceLabelMap" />

          <div class="section-title">合同文件</div>
          <JsonDisplay :value="detailData.contractFilesJson" />
        </template>
        <JstEmptyState v-else description="暂无申请详情" />
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {
  approveAdminPartnerApply,
  getAdminPartnerApply,
  listAdminPartnerApplies,
  rejectAdminPartnerApply
} from '@/api/jst/organizer/admin-partner-apply'

export default {
  name: 'JstAdminPartnerApplyIndex',
  data() {
    return {
      loading: false,
      actionLoading: false,
      detailLoading: false,
      total: 0,
      applyList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined,
        applyStatus: undefined
      },
      approveVisible: false,
      rejectVisible: false,
      detailVisible: false,
      approveForm: {
        applyId: null,
        username: '',
        email: '',
        auditRemark: ''
      },
      rejectForm: {
        applyId: null,
        auditRemark: ''
      },
      detailData: null,
      approveRules: {
        username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }]
      },
      rejectRules: {
        auditRemark: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
      },
      statusMap: {
        submitted: { label: '已提交', type: 'warning' },
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '已通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' },
        supplement: { label: '待补充', type: 'info' }
      },
      statusOptions: [
        { label: '已提交', value: 'submitted' },
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '待补充', value: 'supplement' }
      ],
      settlementLabelMap: {
        accountName: '户名', bankAccount: '银行账号', bankName: '开户行',
        accountType: '账户类型', bankBranch: '支行', swiftCode: 'SWIFT'
      },
      invoiceLabelMap: {
        invoiceType: '发票类型', taxNo: '税号', companyName: '公司名称',
        bankAccount: '银行账号', bankName: '开户行', address: '地址',
        phone: '电话'
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          applyNo: this.queryParams.keyword,
          partnerName: this.queryParams.keyword
        }
        delete params.keyword
        const res = await listAdminPartnerApplies(params)
        this.applyList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.pageNum = 1
      this.getList()
    },
    canApprove(row) {
      return ['pending', 'submitted', 'supplement'].includes(row.applyStatus)
    },
    canReject(row) {
      return ['pending', 'submitted', 'supplement'].includes(row.applyStatus)
    },
    openApprove(row) {
      const fallbackUsername = (row.contactMobile || '').replace(/\D/g, '').slice(-8)
      this.approveForm = {
        applyId: row.applyId,
        username: fallbackUsername ? 'partner_' + fallbackUsername : '',
        email: '',
        auditRemark: ''
      }
      this.approveVisible = true
      this.$nextTick(() => {
        if (this.$refs.approveFormRef) this.$refs.approveFormRef.clearValidate()
      })
    },
    openReject(row) {
      this.rejectForm = {
        applyId: row.applyId,
        auditRemark: ''
      }
      this.rejectVisible = true
      this.$nextTick(() => {
        if (this.$refs.rejectFormRef) this.$refs.rejectFormRef.clearValidate()
      })
    },
    submitApprove() {
      this.$refs.approveFormRef.validate(async valid => {
        if (!valid) return
        this.actionLoading = true
        try {
          await approveAdminPartnerApply(this.approveForm.applyId, {
            username: this.approveForm.username,
            email: this.approveForm.email,
            auditRemark: this.approveForm.auditRemark
          })
          this.$modal.msgSuccess('审核通过成功')
          this.approveVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    submitReject() {
      this.$refs.rejectFormRef.validate(async valid => {
        if (!valid) return
        this.actionLoading = true
        try {
          await rejectAdminPartnerApply(this.rejectForm.applyId, {
            auditRemark: this.rejectForm.auditRemark
          })
          this.$modal.msgSuccess('驳回成功')
          this.rejectVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    async openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      try {
        const res = await getAdminPartnerApply(row.applyId)
        this.detailData = res.data || null
      } catch (e) {
        this.detailData = null
      } finally {
        this.detailLoading = false
      }
    },
    prettyJson(value) {
      if (value == null || value === '') return '--'
      if (typeof value === 'string') {
        try {
          return JSON.stringify(JSON.parse(value), null, 2)
        } catch (e) {
          return value
        }
      }
      try {
        return JSON.stringify(value, null, 2)
      } catch (e) {
        return String(value)
      }
    }
  }
}
</script>

<style scoped>
.jst-admin-partner-apply-page {
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

.mobile-info-row {
  margin-top: 8px;
  color: #7a8495;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-top: 12px;
}

.detail-wrap {
  padding: 0 16px 16px;
}

.section-title {
  margin: 20px 0 10px;
  font-size: 15px;
  font-weight: 700;
  color: #172033;
}

.json-box {
  max-height: 260px;
  overflow: auto;
  padding: 10px 12px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
  background: #fafcff;
  color: #4b5565;
  font-family: Menlo, Consolas, monospace;
  font-size: 12px;
}

@media (max-width: 768px) {
  .jst-admin-partner-apply-page {
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
