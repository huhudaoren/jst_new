<template>
  <div class="app-container partner-enroll-page">
    <div class="page-banner">
      <div>
        <div class="page-eyebrow">Partner Workspace</div>
        <div class="page-title">报名审核管理</div>
        <div class="page-desc">面向赛事方的报名审核工作台，支持按状态筛选、批量处理与抽屉式详情审核。</div>
      </div>
      <div class="page-stats">
        <div class="stat-card">
          <div class="stat-label">本页记录</div>
          <div class="stat-value">{{ enrollList.length }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-label">已选中</div>
          <div class="stat-value">{{ ids.length }}</div>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="status-tabs" @tab-click="handleTabChange">
      <el-tab-pane label="待审核" name="pending" />
      <el-tab-pane label="已通过" name="approved" />
      <el-tab-pane label="已驳回" name="rejected" />
      <el-tab-pane label="全部" name="all" />
    </el-tabs>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="76px"
      class="query-form"
    >
      <el-form-item label="赛事 ID" prop="contestId">
        <el-input
          v-model="queryParams.contestId"
          placeholder="请输入赛事 ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生姓名" prop="participantName">
        <el-input
          v-model="queryParams.participantName"
          placeholder="请输入学生姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="guardianMobile">
        <el-input
          v-model="queryParams.guardianMobile"
          placeholder="请输入监护人手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker
          v-model="submitRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar-row">
      <div class="toolbar-actions">
        <el-button
          type="primary"
          plain
          size="mini"
          icon="el-icon-check"
          :disabled="!ids.length"
          v-hasRole="['jst_partner']"
          @click="openBatchDialog('approved')"
        >
          批量通过
        </el-button>
        <el-button
          type="danger"
          plain
          size="mini"
          icon="el-icon-close"
          :disabled="!ids.length"
          v-hasRole="['jst_partner']"
          @click="openBatchDialog('rejected')"
        >
          批量驳回
        </el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-checkbox-group
      v-if="isMobile"
      v-model="ids"
      class="mobile-list"
    >
      <div v-loading="loading">
        <div v-if="enrollList.length" class="mobile-card-list">
          <div v-for="row in enrollList" :key="row.enrollId" class="mobile-card">
            <div class="mobile-card-head">
              <el-checkbox :label="row.enrollId">选择</el-checkbox>
              <div class="mobile-tag-group">
                <el-tag size="small" :type="statusType(row.auditStatus)">{{ statusLabel(row.auditStatus) }}</el-tag>
                <el-tag v-if="row.isTemporaryArchive" size="small" type="warning">临时档案</el-tag>
              </div>
            </div>
            <div class="mobile-main">{{ row.participantName || '--' }}</div>
            <div class="mobile-sub">{{ row.guardianMobileMasked || '--' }}</div>
            <div class="mobile-info">{{ row.contestName || '--' }}</div>
            <div class="mobile-info">来源：{{ sourceLabel(row) }}</div>
            <div class="mobile-info">提交：{{ formatTime(row.submitTime) }}</div>
            <div class="mobile-actions">
              <el-button type="text" size="mini" @click="openDetail(row)">查看</el-button>
              <el-button
                v-if="canAudit(row)"
                type="text"
                size="mini"
                v-hasRole="['jst_partner']"
                @click="openDetail(row)"
              >
                审核
              </el-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-wrap">暂无报名记录</div>
      </div>
    </el-checkbox-group>

    <el-table
      v-else
      ref="recordTable"
      v-loading="loading"
      :data="enrollList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报名编号" prop="enrollNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="学生信息" min-width="180">
        <template slot-scope="scope">
          <div class="student-cell">
            <div class="student-name">
              <span>{{ scope.row.participantName || '--' }}</span>
              <el-tag v-if="scope.row.isTemporaryArchive" size="mini" type="warning">临时档案</el-tag>
            </div>
            <div class="student-mobile">{{ scope.row.guardianMobileMasked || '--' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="赛事名称" prop="contestName" min-width="200" show-overflow-tooltip />
      <el-table-column label="来源" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="sourceType(scope.row)">{{ sourceLabel(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" min-width="160">
        <template slot-scope="scope">
          <span>{{ formatTime(scope.row.submitTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.auditStatus)">{{ statusLabel(scope.row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openDetail(scope.row)">查看</el-button>
          <el-button
            v-if="canAudit(scope.row)"
            type="text"
            size="mini"
            v-hasRole="['jst_partner']"
            @click="openDetail(scope.row)"
          >
            审核
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

    <el-dialog
      :title="batchDialogTitle"
      :visible.sync="batchDialogVisible"
      width="460px"
      append-to-body
    >
      <el-form ref="batchForm" :model="batchForm" :rules="batchRules" label-width="88px">
        <el-form-item label="处理结果">
          <el-tag :type="batchForm.result === 'approved' ? 'success' : 'danger'">
            {{ batchForm.result === 'approved' ? '批量通过' : '批量驳回' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="处理对象">
          <span>已选择 {{ ids.length }} 条报名记录</span>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="batchForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            :placeholder="batchForm.result === 'approved' ? '可选填写' : '驳回时必须填写原因'"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchSubmitting" @click="submitBatchAudit">确定</el-button>
      </div>
    </el-dialog>

    <EnrollDetailDrawer
      :visible.sync="detailVisible"
      :enroll-id="currentEnrollId"
      :mobile="isMobile"
      @audited="handleAuditSuccess"
    />
  </div>
</template>

<script>
import { batchAuditEnrolls, listPartnerEnrolls } from '@/api/partner/enroll'
import EnrollDetailDrawer from './components/EnrollDetailDrawer'
import { parseTime } from '@/utils/ruoyi'

const STATUS_META = {
  pending: { label: '待审核', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  supplement: { label: '补件中', type: 'info' }
}

const SOURCE_META = {
  self: { label: '个人报名', type: '' },
  channel_single: { label: '渠道代报名', type: 'info' },
  channel_batch: { label: '渠道代报名', type: 'info' }
}

export default {
  name: 'PartnerEnrollManage',
  components: {
    EnrollDetailDrawer
  },
  data() {
    return {
      loading: false,
      batchSubmitting: false,
      showSearch: true,
      activeTab: 'pending',
      total: 0,
      ids: [],
      enrollList: [],
      submitRange: [],
      detailVisible: false,
      currentEnrollId: null,
      batchDialogVisible: false,
      batchForm: {
        result: 'approved',
        auditRemark: ''
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: null,
        participantName: null,
        guardianMobile: null
      },
      batchRules: {
        auditRemark: [
          {
            validator: (rule, value, callback) => {
              if (this.batchForm.result === 'rejected' && !String(value || '').trim()) {
                callback(new Error('批量驳回时请填写原因'))
                return
              }
              callback()
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    batchDialogTitle() {
      return this.batchForm.result === 'approved' ? '批量通过报名' : '批量驳回报名'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPartnerEnrolls(this.buildQueryParams()).then(response => {
        const rows = Array.isArray(response.rows) ? response.rows : []
        this.enrollList = rows.map(row => this.normalizeRow(row))
        this.total = Number(response.total || 0)
        this.clearSelection()
      }).finally(() => {
        this.loading = false
      })
    },
    buildQueryParams() {
      const params = {
        ...this.queryParams
      }
      if (this.activeTab !== 'all') {
        params.auditStatus = this.activeTab
      }
      if (this.submitRange.length === 2) {
        params.beginSubmitTime = this.submitRange[0]
        params.endSubmitTime = this.submitRange[1]
      }
      return params
    },
    normalizeRow(row) {
      const normalized = {
        ...row
      }
      normalized.isTemporaryArchive = this.detectTemporaryArchive(row)
      return normalized
    },
    detectTemporaryArchive(row) {
      if (!row) {
        return false
      }
      if (row.isTemporaryArchive === true || row.tempArchive === true) {
        return true
      }
      if (row.participantType === 'temporary_participant') {
        return true
      }
      return row.userId === null && Object.prototype.hasOwnProperty.call(row, 'userId')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.submitRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleTabChange() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.enrollId)
    },
    clearSelection() {
      this.ids = []
      this.$nextTick(() => {
        if (this.$refs.recordTable) {
          this.$refs.recordTable.clearSelection()
        }
      })
    },
    openDetail(row) {
      this.currentEnrollId = row.enrollId
      this.detailVisible = true
    },
    canAudit(row) {
      return ['pending', 'supplement'].includes(row.auditStatus)
    },
    openBatchDialog(result) {
      if (!this.ids.length) {
        this.$modal.msgError('请先选择要处理的报名记录')
        return
      }
      this.batchForm = {
        result,
        auditRemark: ''
      }
      this.batchDialogVisible = true
      this.$nextTick(() => {
        if (this.$refs.batchForm) {
          this.$refs.batchForm.clearValidate()
        }
      })
    },
    submitBatchAudit() {
      this.$refs.batchForm.validate(valid => {
        if (!valid) {
          return
        }
        this.batchSubmitting = true
        batchAuditEnrolls({
          enrollIds: this.ids,
          result: this.batchForm.result,
          auditRemark: (this.batchForm.auditRemark || '').trim()
        }).then(() => {
          this.$modal.msgSuccess(this.batchForm.result === 'approved' ? '批量通过成功' : '批量驳回成功')
          this.batchDialogVisible = false
          this.getList()
        }).finally(() => {
          this.batchSubmitting = false
        })
      })
    },
    handleAuditSuccess() {
      this.getList()
    },
    statusLabel(status) {
      return (STATUS_META[status] && STATUS_META[status].label) || status || '未知状态'
    },
    statusType(status) {
      return (STATUS_META[status] && STATUS_META[status].type) || 'info'
    },
    sourceLabel(row) {
      const source = row && (row.enrollSource || row.source || row.sourceType)
      return (SOURCE_META[source] && SOURCE_META[source].label) || (source ? source : '待后端补充')
    },
    sourceType(row) {
      const source = row && (row.enrollSource || row.source || row.sourceType)
      return (SOURCE_META[source] && SOURCE_META[source].type) || 'info'
    },
    formatTime(value) {
      return value ? parseTime(value, '{y}-{m}-{d} {h}:{i}') : '--'
    }
  }
}
</script>

<style scoped>
.partner-enroll-page {
  background: #f5f7fa;
}

.page-banner {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  gap: 16px;
  padding: 18px 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border: 1px solid #e6edf5;
  margin-bottom: 16px;
}

.page-eyebrow {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-desc {
  margin-top: 8px;
  color: #606266;
  line-height: 1.6;
  max-width: 720px;
}

.page-stats {
  display: flex;
  gap: 12px;
}

.stat-card {
  min-width: 120px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f9fbfd;
  border: 1px solid #edf2f7;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.stat-value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
}

.status-tabs,
.query-form,
.toolbar-row,
.mobile-list {
  background: #ffffff;
}

.status-tabs {
  padding: 0 16px;
  border-radius: 14px;
  border: 1px solid #ebeef5;
  margin-bottom: 14px;
}

.query-form {
  padding: 16px 16px 0;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  margin-bottom: 14px;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  margin-bottom: 14px;
}

.toolbar-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.student-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.student-name {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  color: #303133;
  font-weight: 500;
}

.student-mobile {
  font-size: 12px;
  color: #909399;
}

.mobile-list {
  border-radius: 14px;
  border: 1px solid #ebeef5;
  padding: 14px;
  margin-bottom: 14px;
}

.mobile-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mobile-card {
  border: 1px solid #ebeef5;
  border-radius: 14px;
  padding: 14px;
  background: #fbfdff;
}

.mobile-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.mobile-tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: flex-end;
}

.mobile-main {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.mobile-sub,
.mobile-info {
  margin-top: 6px;
  color: #606266;
  font-size: 13px;
}

.mobile-actions {
  margin-top: 12px;
  display: flex;
  gap: 16px;
}

.empty-wrap {
  padding: 40px 0;
  text-align: center;
  color: #909399;
}

@media (max-width: 992px) {
  .page-banner,
  .toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }

  .page-stats {
    width: 100%;
  }

  .stat-card {
    flex: 1;
  }

  .status-tabs,
  .query-form,
  .toolbar-row,
  .mobile-list {
    padding-left: 12px;
    padding-right: 12px;
  }
}

@media (max-width: 640px) {
  .page-title {
    font-size: 20px;
  }

  .page-stats {
    flex-direction: column;
  }

  .toolbar-actions .el-button {
    flex: 1;
    min-width: 0;
  }
}
</style>
