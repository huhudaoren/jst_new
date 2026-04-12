<template>
  <div class="app-container jst-admin-enroll-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">报名中心</p>
        <h2>报名管理</h2>
        <p class="hero-desc">查看赛事报名记录，支持审核处理、详情查看与表单快照追溯。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="78px"
      class="query-panel"
    >
      <el-form-item label="赛事名称" prop="contestName">
        <el-input v-model="queryParams.contestName" placeholder="请输入赛事名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="参赛人" prop="participantName">
        <el-input v-model="queryParams.participantName" placeholder="请输入参赛人姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="提交时间">
        <el-date-picker
          v-model="submitTimeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="enrollList.length">
        <div v-for="row in enrollList" :key="row.enrollId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contestName || '--' }}</div>
              <div class="mobile-sub">报名ID: {{ row.enrollId }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-info-row">参赛人：{{ row.participantName || '--' }}</div>
          <div class="mobile-info-row">
            材料状态：
            <JstStatusBadge :status="String(row.materialStatus || '')" :status-map="materialStatusMap" />
          </div>
          <div class="mobile-info-row">提交时间：{{ parseTime(row.submitTime || row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" @click="openSnapshot(row)">表单快照</el-button>
            <el-button
              v-if="canAudit(row)"
              type="text"
              v-hasPermi="['jst:event:enrollRecord:audit']"
              @click="openAudit(row)"
            >
              审核
            </el-button>
          </div>
        </div>
      </div>
      <JstEmptyState v-else description="暂无报名记录" />
    </div>

    <el-table v-else v-loading="loading" :data="enrollList">
      <el-table-column label="报名ID" prop="enrollId" min-width="90" />
      <el-table-column label="赛事名称" prop="contestName" min-width="220" show-overflow-tooltip />
      <el-table-column label="参赛人" prop="participantName" min-width="130" />
      <el-table-column label="审核状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.auditStatus || '')" :status-map="auditStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="材料状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.materialStatus || '')" :status-map="materialStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="提交时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.submitTime || scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="230" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" @click="openSnapshot(scope.row)">表单快照</el-button>
          <el-button
            v-if="canAudit(scope.row)"
            type="text"
            v-hasPermi="['jst:event:enrollRecord:audit']"
            @click="openAudit(scope.row)"
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

    <el-dialog title="审核报名" :visible.sync="auditVisible" width="420px" append-to-body>
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="86px">
        <el-form-item label="报名ID">
          <span>{{ auditForm.enrollId || '--' }}</span>
        </el-form-item>
        <el-form-item label="审核结果" prop="result">
          <el-radio-group v-model="auditForm.result">
            <el-radio label="approved">通过</el-radio>
            <el-radio label="rejected">驳回</el-radio>
            <el-radio label="supplement">补充材料</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            :placeholder="auditForm.result === 'approved' ? '可选备注' : '请填写审核原因'"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitAudit">确定</el-button>
      </div>
    </el-dialog>

    <el-drawer
      title="报名详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '820px'"
      append-to-body
    >
      <div v-loading="detailLoading" class="detail-wrap">
        <template v-if="detailData">
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="报名ID">{{ detailData.enrollId }}</el-descriptions-item>
            <el-descriptions-item label="报名号">{{ detailData.enrollNo || '--' }}</el-descriptions-item>
            <el-descriptions-item label="赛事">{{ detailData.contestName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="参赛人">{{ detailData.participantName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="监护人">{{ detailData.guardianName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="监护人手机号">{{ detailData.guardianMobileMasked || '--' }}</el-descriptions-item>
            <el-descriptions-item label="学校">{{ detailData.school || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <JstStatusBadge :status="String(detailData.auditStatus || '')" :status-map="auditStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="材料状态">
              <JstStatusBadge :status="String(detailData.materialStatus || '')" :status-map="materialStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ parseTime(detailData.submitTime) || '--' }}</el-descriptions-item>
            <el-descriptions-item label="审核备注" :span="isMobile ? 1 : 2">
              {{ detailData.auditRemark || '--' }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="section-title">表单快照</div>
          <div class="snapshot-box">{{ prettySnapshot(detailData.formSnapshotJson) }}</div>
        </template>
        <JstEmptyState v-else description="暂无详情数据" />
      </div>
    </el-drawer>

    <el-dialog title="表单快照" :visible.sync="snapshotVisible" width="760px" append-to-body>
      <pre class="snapshot-pre">{{ prettySnapshot(snapshotData) }}</pre>
      <div slot="footer">
        <el-button type="primary" @click="snapshotVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { auditAdminEnroll, getAdminEnrollDetail, listAdminEnrolls } from '@/api/jst/event/admin-enroll'

export default {
  name: 'JstAdminEnrollIndex',
  data() {
    return {
      loading: false,
      actionLoading: false,
      detailLoading: false,
      total: 0,
      enrollList: [],
      submitTimeRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: undefined,
        participantName: undefined,
        auditStatus: undefined
      },
      auditVisible: false,
      auditForm: {
        enrollId: null,
        result: 'approved',
        auditRemark: ''
      },
      auditRules: {
        result: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
        auditRemark: [{
          validator: (rule, value, callback) => {
            if (this.auditForm.result !== 'approved' && !String(value || '').trim()) {
              callback(new Error('请填写审核原因'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }]
      },
      detailVisible: false,
      detailData: null,
      snapshotVisible: false,
      snapshotData: null,
      auditStatusMap: {
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '已通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' },
        supplement: { label: '补充材料', type: 'info' }
      },
      materialStatusMap: {
        draft: { label: '草稿', type: 'info' },
        submitted: { label: '已提交', type: 'success' },
        supplement: { label: '待补充', type: 'warning' }
      },
      auditStatusOptions: [
        { label: '待审核', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '补充材料', value: 'supplement' }
      ]
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
        const params = { ...this.queryParams }
        if (this.submitTimeRange && this.submitTimeRange.length === 2) {
          params.beginSubmitTime = this.submitTimeRange[0]
          params.endSubmitTime = this.submitTimeRange[1]
        }
        const res = await listAdminEnrolls(params)
        this.enrollList = Array.isArray(res.rows) ? res.rows : []
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
      this.submitTimeRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    canAudit(row) {
      return row.auditStatus === 'pending'
    },
    openAudit(row) {
      this.auditForm = {
        enrollId: row.enrollId,
        result: 'approved',
        auditRemark: ''
      }
      this.auditVisible = true
      this.$nextTick(() => {
        if (this.$refs.auditFormRef) this.$refs.auditFormRef.clearValidate()
      })
    },
    submitAudit() {
      this.$refs.auditFormRef.validate(async valid => {
        if (!valid) return
        this.actionLoading = true
        try {
          await auditAdminEnroll(this.auditForm.enrollId, {
            result: this.auditForm.result,
            auditRemark: this.auditForm.auditRemark
          })
          this.$modal.msgSuccess('审核处理成功')
          this.auditVisible = false
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
        const res = await getAdminEnrollDetail(row.enrollId)
        this.detailData = res.data || null
      } catch (e) {
        this.detailData = null
      } finally {
        this.detailLoading = false
      }
    },
    async openSnapshot(row) {
      this.snapshotVisible = true
      try {
        const res = await getAdminEnrollDetail(row.enrollId)
        const data = res.data || {}
        this.snapshotData = data.formSnapshotJson
      } catch (e) {
        this.snapshotData = null
      }
    },
    prettySnapshot(value) {
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
.jst-admin-enroll-page {
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

.snapshot-box {
  margin-top: 10px;
  padding: 10px 12px;
  max-height: 300px;
  overflow: auto;
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

.snapshot-pre {
  max-height: 460px;
  overflow: auto;
  padding: 12px;
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
  .jst-admin-enroll-page {
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
