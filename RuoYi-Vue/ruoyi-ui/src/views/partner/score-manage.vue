<template>
  <div class="app-container partner-score-page">
    <div class="page-banner">
      <div>
        <div class="page-eyebrow">Score Workspace</div>
        <div class="page-title">成绩管理</div>
        <div class="page-desc">导入成绩、维护草稿并提交平台审核，已发布成绩只能发起更正申请。</div>
      </div>
      <div class="banner-actions">
        <el-button icon="el-icon-download" @click="downloadTemplate">下载模板</el-button>
        <el-upload action="" :show-file-list="false" :http-request="handleImport" accept=".xls,.xlsx">
          <el-button type="primary" icon="el-icon-upload2" :disabled="!queryParams.contestId">导入成绩</el-button>
        </el-upload>
      </div>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="76px">
      <el-form-item label="赛事" prop="contestId">
        <el-select v-model="queryParams.contestId" filterable clearable placeholder="请选择赛事" @change="handleQuery">
          <el-option v-for="item in contests" :key="item.contestId" :label="item.contestName" :value="item.contestId" />
        </el-select>
      </el-form-item>
      <el-form-item label="姓名" prop="participantName">
        <el-input v-model="queryParams.participantName" clearable placeholder="参赛人姓名" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="displayStatus">
        <el-select v-model="queryParams.displayStatus" clearable placeholder="全部状态">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12" class="score-stats">
      <el-col :xs="12" :sm="6"><div class="stat-card"><span>报名数</span><strong>{{ stats.enrollCount || 0 }}</strong></div></el-col>
      <el-col :xs="12" :sm="6"><div class="stat-card"><span>已录入</span><strong>{{ stats.scoredCount || 0 }}</strong></div></el-col>
      <el-col :xs="12" :sm="6"><div class="stat-card"><span>审核中</span><strong>{{ stats.pendingReviewCount || 0 }}</strong></div></el-col>
      <el-col :xs="12" :sm="6"><div class="stat-card"><span>已发布</span><strong>{{ stats.publishedCount || 0 }}</strong></div></el-col>
    </el-row>

    <div class="toolbar-row">
      <el-button type="primary" icon="el-icon-plus" size="mini" @click="openScoreDialog()">新增成绩</el-button>
      <el-button icon="el-icon-s-promotion" size="mini" :disabled="!selectedRows.length" @click="submitSelected">提交审核</el-button>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <el-table v-loading="loading" :data="scoreList" @selection-change="selectedRows = $event">
      <el-table-column type="selection" width="46" />
      <el-table-column label="报名号" prop="enrollNo" min-width="130" />
      <el-table-column label="参赛人" prop="participantName" min-width="120" />
      <el-table-column label="手机号" prop="guardianMobileMasked" min-width="130" />
      <el-table-column label="分数" prop="scoreValue" min-width="90" />
      <el-table-column label="奖项" prop="awardLevel" min-width="120" />
      <el-table-column label="名次" prop="rankNo" min-width="80" />
      <el-table-column label="状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.displayStatus)">{{ statusLabel(scope.row.displayStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="210">
        <template slot-scope="scope">
          <el-button v-if="canEdit(scope.row)" type="text" size="mini" @click="openScoreDialog(scope.row)">编辑</el-button>
          <el-button v-if="canSubmit(scope.row)" type="text" size="mini" @click="submitRows([scope.row])">提审</el-button>
          <el-button v-if="canCorrection(scope.row)" type="text" size="mini" @click="openCorrection(scope.row)">申请更正</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="scoreForm.scoreId ? '编辑成绩' : '新增成绩'" :visible.sync="scoreDialogOpen" width="520px" append-to-body>
      <el-form ref="scoreForm" :model="scoreForm" :rules="scoreRules" label-width="86px">
        <el-form-item label="报名ID" prop="enrollId"><el-input v-model="scoreForm.enrollId" placeholder="请输入报名ID" /></el-form-item>
        <el-form-item label="分数" prop="scoreValue"><el-input-number v-model="scoreForm.scoreValue" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="奖项" prop="awardLevel"><el-input v-model="scoreForm.awardLevel" maxlength="32" /></el-form-item>
        <el-form-item label="名次" prop="rankNo"><el-input-number v-model="scoreForm.rankNo" :min="1" /></el-form-item>
        <el-form-item label="评语" prop="remark"><el-input v-model="scoreForm.remark" type="textarea" maxlength="500" show-word-limit /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="scoreDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="saveScore">保存</el-button>
      </div>
    </el-dialog>

    <el-dialog title="成绩更正申请" :visible.sync="correctionDialogOpen" width="520px" append-to-body>
      <el-form ref="correctionForm" :model="correctionForm" :rules="correctionRules" label-width="96px">
        <el-form-item label="新分数"><el-input-number v-model="correctionForm.newScoreValue" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="新奖项"><el-input v-model="correctionForm.newAwardLevel" maxlength="32" /></el-form-item>
        <el-form-item label="新名次"><el-input-number v-model="correctionForm.newRankNo" :min="1" /></el-form-item>
        <el-form-item label="更正原因" prop="reason"><el-input v-model="correctionForm.reason" type="textarea" maxlength="300" show-word-limit /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="correctionDialogOpen = false">取消</el-button>
        <el-button type="primary" @click="submitCorrection">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPartnerContests } from '@/api/partner/contest'
import {
  applyScoreCorrection,
  getPartnerScoreStats,
  importPartnerScores,
  listPartnerScores,
  savePartnerScore,
  submitPartnerScoreReview
} from '@/api/partner/score'

const STATUS_META = {
  draft: { label: '草稿', type: 'info' },
  rejected: { label: '已驳回', type: 'danger' },
  pending_review: { label: '审核中', type: 'warning' },
  pending_publish: { label: '待发布', type: 'warning' },
  published: { label: '已发布', type: 'success' },
  correction_requested: { label: '更正中', type: 'warning' }
}

export default {
  name: 'PartnerScoreManage',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      contests: [],
      scoreList: [],
      selectedRows: [],
      stats: {},
      scoreDialogOpen: false,
      correctionDialogOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: this.$route.query.contestId ? Number(this.$route.query.contestId) : null,
        participantName: null,
        displayStatus: null
      },
      scoreForm: {},
      correctionForm: {},
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label })),
      scoreRules: {
        enrollId: [{ required: true, message: '报名ID不能为空', trigger: 'blur' }]
      },
      correctionRules: {
        reason: [{ required: true, message: '更正原因不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadContests()
    this.getList()
  },
  methods: {
    loadContests() {
      listPartnerContests({ pageNum: 1, pageSize: 100 }).then(response => {
        this.contests = Array.isArray(response.rows) ? response.rows : []
      })
    },
    getList() {
      this.loading = true
      listPartnerScores(this.queryParams).then(response => {
        this.scoreList = Array.isArray(response.rows) ? response.rows : []
        this.total = Number(response.total || 0)
      }).finally(() => {
        this.loading = false
      })
      if (this.queryParams.contestId) {
        getPartnerScoreStats({ contestId: this.queryParams.contestId }).then(response => {
          this.stats = response.data || {}
        })
      } else {
        this.stats = {}
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
    handleImport(param) {
      if (!this.queryParams.contestId) {
        this.$modal.msgWarning('请先选择赛事')
        return
      }
      const formData = new FormData()
      formData.append('contestId', this.queryParams.contestId)
      formData.append('file', param.file)
      importPartnerScores(formData).then(response => {
        const data = response.data || {}
        this.$modal.msgSuccess(`导入成功 ${data.successCount || 0} 条，失败 ${data.failedCount || 0} 条`)
        this.getList()
      })
    },
    downloadTemplate() {
      const html = '<table><tr><th>报名ID</th><th>分数</th><th>奖项等级</th><th>名次</th><th>评语</th></tr><tr><td>8904</td><td>92.5</td><td>全国金奖</td><td>1</td><td>表现稳定</td></tr></table>'
      const blob = new Blob([html], { type: 'application/vnd.ms-excel;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = '成绩导入模板.xls'
      link.click()
      URL.revokeObjectURL(url)
    },
    openScoreDialog(row) {
      this.scoreForm = row ? {
        scoreId: row.scoreId,
        contestId: row.contestId,
        enrollId: row.enrollId,
        scoreValue: row.scoreValue,
        awardLevel: row.awardLevel,
        rankNo: row.rankNo,
        remark: row.remark
      } : {
        scoreId: 0,
        contestId: this.queryParams.contestId,
        enrollId: null,
        scoreValue: null,
        awardLevel: null,
        rankNo: null,
        remark: null
      }
      this.scoreDialogOpen = true
    },
    saveScore() {
      this.$refs.scoreForm.validate(valid => {
        if (!valid) return
        if (!this.scoreForm.contestId) {
          this.$modal.msgWarning('请先选择赛事')
          return
        }
        savePartnerScore(this.scoreForm.scoreId || 0, this.scoreForm).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.scoreDialogOpen = false
          this.getList()
        })
      })
    },
    submitSelected() {
      this.submitRows(this.selectedRows)
    },
    submitRows(rows) {
      const scoreIds = rows.map(item => item.scoreId)
      submitPartnerScoreReview({ scoreIds }).then(() => {
        this.$modal.msgSuccess('已提交审核')
        this.getList()
      })
    },
    openCorrection(row) {
      this.correctionForm = {
        scoreId: row.scoreId,
        newScoreValue: row.scoreValue,
        newAwardLevel: row.awardLevel,
        newRankNo: row.rankNo,
        reason: null
      }
      this.correctionDialogOpen = true
    },
    submitCorrection() {
      this.$refs.correctionForm.validate(valid => {
        if (!valid) return
        applyScoreCorrection(this.correctionForm).then(() => {
          this.$modal.msgSuccess('更正申请已提交')
          this.correctionDialogOpen = false
          this.getList()
        })
      })
    },
    canEdit(row) {
      return row.displayStatus === 'draft' || row.displayStatus === 'rejected'
    },
    canSubmit(row) {
      return row.displayStatus === 'draft' || row.displayStatus === 'rejected'
    },
    canCorrection(row) {
      return row.displayStatus === 'published'
    },
    statusLabel(status) {
      return STATUS_META[status] ? STATUS_META[status].label : (status || '--')
    },
    statusType(status) {
      return STATUS_META[status] ? STATUS_META[status].type : 'info'
    }
  }
}
</script>

<style scoped>
.partner-score-page .page-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  margin-bottom: 16px;
  background: #f6f8fb;
  border: 1px solid #e7ebf2;
  border-radius: 8px;
}
.page-eyebrow {
  color: #6b7280;
  font-size: 12px;
  letter-spacing: 0;
}
.page-title {
  margin-top: 4px;
  color: #111827;
  font-size: 22px;
  font-weight: 700;
}
.page-desc {
  margin-top: 6px;
  color: #4b5563;
}
.banner-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.score-stats {
  margin-bottom: 12px;
}
.stat-card {
  min-height: 72px;
  padding: 14px 16px;
  margin-bottom: 12px;
  background: #ffffff;
  border: 1px solid #e7ebf2;
  border-radius: 8px;
}
.stat-card span {
  display: block;
  color: #6b7280;
  font-size: 13px;
}
.stat-card strong {
  display: block;
  margin-top: 8px;
  color: #111827;
  font-size: 24px;
}
.toolbar-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 0;
}
@media (max-width: 768px) {
  .partner-score-page .page-banner,
  .toolbar-row,
  .banner-actions {
    align-items: stretch;
    flex-direction: column;
  }
}
</style>
