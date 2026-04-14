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
      <el-form-item label="分数范围" prop="scoreMin">
        <el-input-number v-model="queryParams.scoreMin" :min="0" :precision="0" size="small" controls-position="right" placeholder="最低" style="width:90px" />
        <span style="margin:0 4px">-</span>
        <el-input-number v-model="queryParams.scoreMax" :min="0" :precision="0" size="small" controls-position="right" placeholder="最高" style="width:90px" />
      </el-form-item>
      <el-form-item label="评审老师" prop="reviewerName">
        <el-input v-model="queryParams.reviewerName" placeholder="评审老师姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="证书状态" prop="certStatus">
        <el-select v-model="queryParams.certStatus" placeholder="全部" clearable size="small">
          <el-option label="已生成" value="issued" />
          <el-option label="未生成" value="none" />
        </el-select>
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
        <el-button
          type="warning"
          plain
          size="mini"
          icon="el-icon-s-operation"
          v-hasRole="['jst_partner']"
          @click="openCardReview"
        >
          卡片审核
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
      <el-table-column label="总分" prop="scoreTotal" min-width="90" align="center">
        <template slot-scope="scope">
          <span :class="{ 'score-highlight': scope.row.scoreTotal > 0 }">{{ scope.row.scoreTotal != null ? scope.row.scoreTotal : '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="证书编号" prop="certNo" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.certNo">{{ scope.row.certNo }}</span>
          <el-tag v-else size="mini" type="info">未生成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评审老师" prop="reviewerName" min-width="110" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.reviewerName || '--' }}</span>
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

    <!-- ==================== 卡片滑动审核 ==================== -->
    <el-dialog
      :visible.sync="cardReviewVisible"
      fullscreen
      custom-class="card-review-dialog"
      :show-close="false"
      :close-on-click-modal="false"
      append-to-body
      @open="onCardReviewOpen"
      @close="onCardReviewClose"
    >
      <div class="card-review-shell">
        <div class="card-review-header">
          <div class="card-review-title">
            <i class="el-icon-s-check" />
            <span>卡片审核模式</span>
            <el-tag size="small">{{ cardCurrentIndex + 1 }} / {{ cardList.length }}</el-tag>
          </div>
          <div class="card-review-shortcuts">
            <span><kbd>&larr;</kbd> 上一条</span>
            <span><kbd>&rarr;</kbd> 下一条</span>
            <span><kbd>Enter</kbd> 通过</span>
            <span><kbd>Esc</kbd> 关闭</span>
          </div>
          <el-button icon="el-icon-close" circle @click="cardReviewVisible = false" />
        </div>

        <div class="card-review-body">
          <div v-loading="cardLoading" class="card-review-left">
            <template v-if="cardDetail">
              <div class="cr-section">
                <div class="cr-section-head">学生信息</div>
                <div class="cr-info-grid">
                  <div class="cr-info-item">
                    <div class="cr-info-label">参赛人</div>
                    <div class="cr-info-value">{{ cardDetail.participantName || '--' }}</div>
                  </div>
                  <div class="cr-info-item">
                    <div class="cr-info-label">监护人</div>
                    <div class="cr-info-value">{{ cardDetail.guardianName || '--' }}</div>
                  </div>
                  <div class="cr-info-item">
                    <div class="cr-info-label">联系电话</div>
                    <div class="cr-info-value">{{ cardDetail.guardianMobileMasked || '--' }}</div>
                  </div>
                  <div class="cr-info-item">
                    <div class="cr-info-label">学校</div>
                    <div class="cr-info-value">{{ cardDetail.school || '--' }}</div>
                  </div>
                  <div class="cr-info-item">
                    <div class="cr-info-label">赛事</div>
                    <div class="cr-info-value">{{ cardDetail.contestName || '--' }}</div>
                  </div>
                  <div class="cr-info-item">
                    <div class="cr-info-label">报名编号</div>
                    <div class="cr-info-value">{{ cardDetail.enrollNo || '--' }}</div>
                  </div>
                </div>
              </div>
              <div class="cr-section">
                <div class="cr-section-head">动态表单快照</div>
                <div v-if="cardFormEntries.length" class="cr-form-grid">
                  <div v-for="entry in cardFormEntries" :key="entry.key" class="cr-info-item">
                    <div class="cr-info-label">{{ entry.label }}</div>
                    <div class="cr-info-value">
                      <pre v-if="entry.multiline" class="cr-json-block">{{ entry.value }}</pre>
                      <span v-else>{{ entry.value || '--' }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="cr-empty">无表单数据</div>
              </div>
              <div class="cr-section">
                <div class="cr-section-head">作品材料</div>
                <div v-if="cardAttachments.length" class="cr-attachment-grid">
                  <div v-for="att in cardAttachments" :key="att.key" class="cr-attachment-card">
                    <el-image v-if="att.kind === 'image'" :src="att.url" :preview-src-list="cardImageList" fit="cover" class="cr-attachment-img" />
                    <div v-else class="cr-attachment-file">
                      <i :class="att.kind === 'pdf' ? 'el-icon-document' : 'el-icon-video-camera-solid'" />
                      <span>{{ att.name }}</span>
                    </div>
                  </div>
                </div>
                <div v-else class="cr-empty">暂无附件</div>
              </div>
            </template>
          </div>

          <div class="card-review-right">
            <div class="cr-section">
              <div class="cr-section-head">成绩打分</div>
              <div v-if="cardScoreItems.length" class="cr-score-list">
                <div v-for="item in cardScoreItems" :key="item.itemId || item.id" class="cr-score-row">
                  <div class="cr-score-name">{{ item.itemName }} <span class="cr-score-max">(满{{ item.maxScore }}分)</span></div>
                  <el-input-number
                    v-model="item.score"
                    :min="0"
                    :max="item.maxScore || 999"
                    :precision="1"
                    size="small"
                    controls-position="right"
                    class="cr-score-input"
                  />
                </div>
                <div class="cr-score-total-row">
                  <span>加权总分</span>
                  <span class="cr-score-total-value">{{ cardWeightedTotal }}</span>
                </div>
              </div>
              <div v-else class="cr-empty">该赛事未配置成绩项</div>
            </div>
            <div class="cr-section">
              <div class="cr-section-head">审核备注</div>
              <el-input
                v-model="cardAuditRemark"
                type="textarea"
                :rows="3"
                maxlength="255"
                show-word-limit
                placeholder="通过可留空；驳回时请填写原因"
              />
            </div>
            <div class="cr-actions">
              <el-button type="danger" :loading="cardSubmitting" :disabled="!cardCanAudit" @click="cardAudit('rejected')">
                驳回
              </el-button>
              <el-button type="primary" :loading="cardSubmitting" :disabled="!cardCanAudit" @click="cardAudit('approved')">
                通过
              </el-button>
            </div>
          </div>
        </div>

        <div class="card-review-footer">
          <el-progress :percentage="cardList.length > 0 ? Math.round(((cardCurrentIndex + 1) / cardList.length) * 100) : 0" :stroke-width="6" class="card-progress" />
          <div class="card-nav">
            <el-button size="small" :disabled="cardCurrentIndex <= 0" @click="cardNav(-1)">
              <i class="el-icon-arrow-left" /> 上一条
            </el-button>
            <span class="card-nav-counter">{{ cardCurrentIndex + 1 }} / {{ cardList.length }}</span>
            <el-button size="small" :disabled="cardCurrentIndex >= cardList.length - 1" @click="cardNav(1)">
              下一条 <i class="el-icon-arrow-right" />
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { auditPartnerEnroll, batchAuditEnrolls, listPartnerEnrolls, listScoreItems } from '@/api/partner/enroll'
import { getPartnerEnrollDetail } from '@/api/partner/enroll'
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
      // Card review state
      cardReviewVisible: false,
      cardLoading: false,
      cardSubmitting: false,
      cardList: [],
      cardCurrentIndex: 0,
      cardDetail: null,
      cardScoreItems: [],
      cardAuditRemark: '',
      cardKeyHandler: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: null,
        participantName: null,
        guardianMobile: null,
        scoreMin: null,
        scoreMax: null,
        reviewerName: null,
        certStatus: null
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
    },
    cardCanAudit() {
      return this.cardDetail && ['pending', 'supplement'].includes(this.cardDetail.auditStatus)
    },
    cardWeightedTotal() {
      if (!this.cardScoreItems.length) return '--'
      let total = 0
      let weightSum = 0
      this.cardScoreItems.forEach(item => {
        const w = Number(item.weight) || 1
        const s = Number(item.score) || 0
        total += s * w
        weightSum += w
      })
      return weightSum > 0 ? total.toFixed(1) : '--'
    },
    cardFormEntries() {
      if (!this.cardDetail) return []
      return this.buildFormEntries(this.cardDetail.formSnapshotJson)
    },
    cardAttachments() {
      if (!this.cardDetail) return []
      return this.buildAttachments(this.cardDetail.formSnapshotJson)
    },
    cardImageList() {
      return this.cardAttachments.filter(item => item.kind === 'image').map(item => item.url)
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
    // ==================== Card Review ====================
    openCardReview() {
      const pending = this.enrollList.filter(r => ['pending', 'supplement'].includes(r.auditStatus))
      if (!pending.length) {
        this.$modal.msgWarning('当前列表无待审核记录，请切换到"待审核"标签')
        return
      }
      this.cardList = pending
      this.cardCurrentIndex = 0
      this.cardReviewVisible = true
    },
    onCardReviewOpen() {
      this.loadCardDetail()
      this.cardKeyHandler = this.handleCardKeydown.bind(this)
      document.addEventListener('keydown', this.cardKeyHandler)
    },
    onCardReviewClose() {
      if (this.cardKeyHandler) {
        document.removeEventListener('keydown', this.cardKeyHandler)
        this.cardKeyHandler = null
      }
      this.getList()
    },
    handleCardKeydown(e) {
      if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') return
      if (e.key === 'ArrowLeft') {
        e.preventDefault()
        this.cardNav(-1)
      } else if (e.key === 'ArrowRight') {
        e.preventDefault()
        this.cardNav(1)
      } else if (e.key === 'Enter') {
        e.preventDefault()
        if (this.cardCanAudit) this.cardAudit('approved')
      } else if (e.key === 'Escape') {
        e.preventDefault()
        this.cardReviewVisible = false
      }
    },
    cardNav(direction) {
      const next = this.cardCurrentIndex + direction
      if (next < 0 || next >= this.cardList.length) return
      this.cardCurrentIndex = next
      this.loadCardDetail()
    },
    loadCardDetail() {
      const row = this.cardList[this.cardCurrentIndex]
      if (!row) return
      this.cardLoading = true
      this.cardAuditRemark = ''
      this.cardScoreItems = []
      this.cardDetail = null
      getPartnerEnrollDetail(row.enrollId).then(response => {
        this.cardDetail = response.data || {}
        if (this.cardDetail.contestId) {
          return listScoreItems(this.cardDetail.contestId)
        }
      }).then(scoreResp => {
        if (scoreResp) {
          const items = Array.isArray(scoreResp.data) ? scoreResp.data : (Array.isArray(scoreResp.rows) ? scoreResp.rows : [])
          this.cardScoreItems = items.map(item => ({ ...item, score: null }))
        }
      }).catch(() => {}).finally(() => {
        this.cardLoading = false
      })
    },
    cardAudit(result) {
      const remark = (this.cardAuditRemark || '').trim()
      if (result === 'rejected' && !remark) {
        this.$modal.msgError('驳回时请填写原因')
        return
      }
      if (result === 'approved' && this.cardScoreItems.length) {
        const hasEmpty = this.cardScoreItems.some(item => item.score === null || item.score === undefined)
        if (hasEmpty) {
          this.$modal.msgWarning('请为所有成绩项打分')
          return
        }
      }
      const enrollId = this.cardList[this.cardCurrentIndex].enrollId
      const data = { result, auditRemark: remark }
      if (result === 'approved' && this.cardScoreItems.length) {
        data.scores = this.cardScoreItems.map(item => ({ itemId: item.itemId || item.id, score: item.score }))
      }
      this.cardSubmitting = true
      auditPartnerEnroll(enrollId, data).then(() => {
        this.$modal.msgSuccess(result === 'approved' ? '审核通过' : '已驳回')
        this.cardList[this.cardCurrentIndex].auditStatus = result
        // Auto-advance after 500ms
        setTimeout(() => {
          if (this.cardCurrentIndex < this.cardList.length - 1) {
            this.cardCurrentIndex++
            this.loadCardDetail()
          } else {
            this.$modal.msgSuccess('全部审核完毕')
            this.cardReviewVisible = false
          }
        }, 500)
      }).catch(() => {
        this.$modal.msgError('操作失败，请重试')
      }).finally(() => {
        this.cardSubmitting = false
      })
    },
    // ==================== Form Snapshot Helpers ====================
    buildFormEntries(snapshotRaw) {
      const snapshot = this.parseSnapshot(snapshotRaw)
      const entries = []
      const formData = snapshot && snapshot.formData
      if (Array.isArray(formData)) {
        formData.forEach((item, index) => {
          const key = item && (item.key || item.field || item.label || item.name || 'field_' + (index + 1))
          const rawValue = item && Object.prototype.hasOwnProperty.call(item, 'value') ? item.value : item
          entries.push(this.makeFormEntry(key, rawValue))
        })
      } else if (formData && typeof formData === 'object') {
        Object.keys(formData).forEach(key => {
          entries.push(this.makeFormEntry(key, formData[key]))
        })
      }
      if (!entries.length && snapshot && typeof snapshot === 'object') {
        Object.keys(snapshot).filter(k => !['template_id', 'template_version', 'attachments', 'formData'].includes(k)).forEach(key => {
          entries.push(this.makeFormEntry(key, snapshot[key]))
        })
      }
      return entries
    },
    buildAttachments(snapshotRaw) {
      const snapshot = this.parseSnapshot(snapshotRaw)
      const rawList = snapshot && snapshot.attachments
      if (!Array.isArray(rawList)) return []
      return rawList.map((item, index) => {
        const url = typeof item === 'string' ? item : (item && (item.url || item.fileUrl || item.path || item.value))
        if (!url) return null
        const lower = String(url).toLowerCase()
        let kind = 'file'
        if (/\.(png|jpe?g|gif|bmp|webp|svg)(\?.*)?$/.test(lower)) kind = 'image'
        else if (/\.pdf(\?.*)?$/.test(lower)) kind = 'pdf'
        else if (/\.(mp4|mov|m4v|avi|webm)(\?.*)?$/.test(lower)) kind = 'video'
        const name = (item && (item.name || item.fileName)) || url.split('?')[0].split('/').pop() || '附件'
        return { key: index + '-' + url, url, name, kind }
      }).filter(Boolean)
    },
    parseSnapshot(raw) {
      if (!raw) return {}
      if (typeof raw === 'string') {
        try { return JSON.parse(raw) } catch (e) { return { raw } }
      }
      return raw
    },
    makeFormEntry(key, rawValue) {
      let value = '--'
      if (rawValue !== null && rawValue !== undefined && rawValue !== '') {
        if (Array.isArray(rawValue)) value = rawValue.map(v => typeof v === 'object' ? JSON.stringify(v) : String(v)).join('\n')
        else if (typeof rawValue === 'object') value = JSON.stringify(rawValue, null, 2)
        else value = String(rawValue)
      }
      return { key, label: String(key).replace(/_/g, ' '), value, multiline: typeof value === 'string' && value.includes('\n') }
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

.score-highlight {
  font-weight: 600;
  color: #67c23a;
}

/* ==================== Card Review Dialog ==================== */
.card-review-shell {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.card-review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.card-review-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-review-title i {
  font-size: 20px;
  color: #409eff;
}

.card-review-shortcuts {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.card-review-shortcuts kbd {
  display: inline-block;
  padding: 1px 6px;
  font-size: 11px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #f5f7fa;
  font-family: inherit;
}

.card-review-body {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 16px 20px;
  overflow: hidden;
}

.card-review-left {
  flex: 3;
  overflow-y: auto;
  padding-right: 8px;
}

.card-review-right {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 14px;
  overflow-y: auto;
}

.cr-section {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 14px;
}

.cr-section-head {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.cr-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.cr-info-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 10px 12px;
}

.cr-info-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.cr-info-value {
  color: #303133;
  word-break: break-word;
  line-height: 1.5;
}

.cr-json-block {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
}

.cr-form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.cr-attachment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 10px;
}

.cr-attachment-card {
  border: 1px solid #ebeef5;
  border-radius: 10px;
  overflow: hidden;
}

.cr-attachment-img {
  display: block;
  width: 100%;
  height: 120px;
  background: #f4f6f8;
}

.cr-attachment-file {
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #f8fafc;
  color: #606266;
  padding: 10px;
  text-align: center;
}

.cr-attachment-file i {
  font-size: 24px;
}

.cr-empty {
  padding: 14px;
  border-radius: 8px;
  background: #fafafa;
  color: #909399;
  font-size: 13px;
}

.cr-score-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cr-score-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #fafafa;
}

.cr-score-name {
  font-size: 13px;
  color: #303133;
}

.cr-score-max {
  color: #909399;
  font-size: 12px;
}

.cr-score-input {
  width: 120px;
}

.cr-score-total-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f0f9eb;
  font-size: 14px;
  color: #303133;
}

.cr-score-total-value {
  font-size: 20px;
  font-weight: 700;
  color: #67c23a;
}

.cr-actions {
  display: flex;
  gap: 12px;
  padding: 0 4px;
}

.cr-actions .el-button {
  flex: 1;
  height: 44px;
  font-size: 15px;
}

.card-review-footer {
  padding: 10px 20px 14px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}

.card-progress {
  margin-bottom: 8px;
}

.card-nav {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.card-nav-counter {
  font-size: 13px;
  color: #606266;
  min-width: 60px;
  text-align: center;
}

@media (max-width: 768px) {
  .card-review-body {
    flex-direction: column;
    overflow-y: auto;
  }

  .card-review-left,
  .card-review-right {
    flex: none;
    overflow: visible;
  }

  .cr-info-grid,
  .cr-form-grid {
    grid-template-columns: 1fr;
  }

  .card-review-shortcuts {
    display: none;
  }
}
</style>
