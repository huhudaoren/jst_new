<template>
  <div class="app-container jst-admin-contest-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">赛事中心</p>
        <h2>赛事管理</h2>
        <p class="hero-desc">统一处理赛事审核、上下线、详情查看与核心内容编辑。</p>
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
      <el-form-item label="分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入分类" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="业务状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in contestStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="赛事方" prop="partnerName">
        <el-input v-model="queryParams.partnerName" placeholder="请输入赛事方名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="contestList.length">
        <div v-for="row in contestList" :key="row.contestId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contestName || '--' }}</div>
              <div class="mobile-sub">ID: {{ row.contestId }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-info-row">分类：{{ row.category || '--' }}</div>
          <div class="mobile-info-row">赛事方：{{ row.partnerName || '--' }}</div>
          <div class="mobile-info-row">价格：¥{{ formatMoney(row.price) }}</div>
          <div class="mobile-info-row">报名数：{{ row.enrollCount || 0 }}</div>
          <div class="mobile-info-row">
            业务状态：
            <JstStatusBadge :status="String(row.status || '')" :status-map="contestStatusMap" />
          </div>
          <div class="mobile-info-row">创建时间：{{ parseTime(row.createTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" v-hasPermi="['jst:event:contest:edit']" @click="openEdit(row)">编辑</el-button>
            <el-button
              v-if="canApprove(row)"
              type="text"
              v-hasPermi="['jst:event:contest:audit']"
              @click="openAudit(row, 'approved')"
            >
              通过
            </el-button>
            <el-button
              v-if="canReject(row)"
              type="text"
              class="danger-text"
              v-hasPermi="['jst:event:contest:audit']"
              @click="openAudit(row, 'rejected')"
            >
              驳回
            </el-button>
            <el-button
              v-if="canOnline(row)"
              type="text"
              v-hasPermi="['jst:event:contest:online']"
              @click="handleOnline(row)"
            >
              上线
            </el-button>
            <el-button
              v-if="canOffline(row)"
              type="text"
              v-hasPermi="['jst:event:contest:offline']"
              @click="handleOffline(row)"
            >
              下线
            </el-button>
          </div>
        </div>
      </div>
      <JstEmptyState v-else description="暂无赛事数据" />
    </div>

    <el-table v-else v-loading="loading" :data="contestList">
      <el-table-column label="赛事ID" prop="contestId" min-width="92" />
      <el-table-column label="赛事名称" prop="contestName" min-width="220" show-overflow-tooltip />
      <el-table-column label="分类" prop="category" min-width="120" />
      <el-table-column label="赛事方" prop="partnerName" min-width="140" show-overflow-tooltip />
      <el-table-column label="价格" min-width="90" align="right">
        <template slot-scope="scope">¥{{ formatMoney(scope.row.price) }}</template>
      </el-table-column>
      <el-table-column label="审核状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.auditStatus || '')" :status-map="auditStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="业务状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.status || '')" :status-map="contestStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="报名人数" prop="enrollCount" min-width="90" align="right">
        <template slot-scope="scope">{{ scope.row.enrollCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.createTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="300" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" v-hasPermi="['jst:event:contest:edit']" @click="openEdit(scope.row)">编辑</el-button>
          <el-button
            v-if="canApprove(scope.row)"
            type="text"
            v-hasPermi="['jst:event:contest:audit']"
            @click="openAudit(scope.row, 'approved')"
          >
            通过
          </el-button>
          <el-button
            v-if="canReject(scope.row)"
            type="text"
            class="danger-text"
            v-hasPermi="['jst:event:contest:audit']"
            @click="openAudit(scope.row, 'rejected')"
          >
            驳回
          </el-button>
          <el-button
            v-if="canOnline(scope.row)"
            type="text"
            v-hasPermi="['jst:event:contest:online']"
            @click="handleOnline(scope.row)"
          >
            上线
          </el-button>
          <el-button
            v-if="canOffline(scope.row)"
            type="text"
            v-hasPermi="['jst:event:contest:offline']"
            @click="handleOffline(scope.row)"
          >
            下线
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

    <el-dialog :title="auditDialogTitle" :visible.sync="auditVisible" width="420px" append-to-body>
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="88px">
        <el-form-item label="赛事ID">
          <span>{{ auditForm.contestId || '--' }}</span>
        </el-form-item>
        <el-form-item label="审核备注" prop="auditRemark">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="4"
            maxlength="255"
            show-word-limit
            :placeholder="auditForm.result === 'rejected' ? '驳回时请填写原因' : '可选备注'"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="submitAudit">确定</el-button>
      </div>
    </el-dialog>

    <ContestDetail :visible.sync="detailVisible" :contest-id="currentContestId" />
    <ContestEdit :visible.sync="editVisible" :contest-id="currentContestId" @success="handleEditSuccess" />
  </div>
</template>

<script>
import {
  approveAdminContest,
  listAdminContests,
  offlineAdminContest,
  onlineAdminContest,
  rejectAdminContest
} from '@/api/jst/event/admin-contest'
import ContestDetail from './detail.vue'
import ContestEdit from './edit.vue'

export default {
  name: 'JstAdminContestIndex',
  components: {
    ContestDetail,
    ContestEdit
  },
  data() {
    return {
      loading: false,
      actionLoading: false,
      total: 0,
      contestList: [],
      detailVisible: false,
      editVisible: false,
      currentContestId: null,
      auditVisible: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: undefined,
        category: undefined,
        auditStatus: undefined,
        status: undefined,
        partnerName: undefined
      },
      auditForm: {
        contestId: null,
        result: 'approved',
        auditRemark: ''
      },
      auditRules: {
        auditRemark: [{
          validator: (rule, value, callback) => {
            if (this.auditForm.result === 'rejected' && !String(value || '').trim()) {
              callback(new Error('驳回时请填写审核备注'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }]
      },
      auditStatusMap: {
        draft: { label: '草稿', type: 'info' },
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '审核通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' },
        online: { label: '已上线', type: 'success' },
        offline: { label: '已下线', type: 'info' }
      },
      contestStatusMap: {
        not_started: { label: '未开始', type: 'info' },
        enrolling: { label: '报名中', type: 'warning' },
        closed: { label: '报名结束', type: 'info' },
        scoring: { label: '评分中', type: 'warning' },
        published: { label: '已公布', type: 'success' },
        ended: { label: '已结束', type: 'info' }
      },
      auditStatusOptions: [
        { label: '草稿', value: 'draft' },
        { label: '待审核', value: 'pending' },
        { label: '审核通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '已上线', value: 'online' },
        { label: '已下线', value: 'offline' }
      ],
      contestStatusOptions: [
        { label: '未开始', value: 'not_started' },
        { label: '报名中', value: 'enrolling' },
        { label: '报名结束', value: 'closed' },
        { label: '评分中', value: 'scoring' },
        { label: '已公布', value: 'published' },
        { label: '已结束', value: 'ended' }
      ]
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    auditDialogTitle() {
      return this.auditForm.result === 'approved' ? '审核通过' : '审核驳回'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const res = await listAdminContests({ ...this.queryParams })
        this.contestList = Array.isArray(res.rows) ? res.rows : []
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
    openDetail(row) {
      this.currentContestId = row.contestId
      this.detailVisible = true
    },
    openEdit(row) {
      this.currentContestId = row.contestId
      this.editVisible = true
    },
    handleEditSuccess() {
      this.getList()
    },
    openAudit(row, result) {
      this.auditForm = {
        contestId: row.contestId,
        result,
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
          const payload = { auditRemark: this.auditForm.auditRemark }
          if (this.auditForm.result === 'approved') {
            await approveAdminContest(this.auditForm.contestId, payload)
          } else {
            await rejectAdminContest(this.auditForm.contestId, payload)
          }
          this.$modal.msgSuccess(this.auditForm.result === 'approved' ? '审核通过成功' : '驳回成功')
          this.auditVisible = false
          this.getList()
        } finally {
          this.actionLoading = false
        }
      })
    },
    handleOnline(row) {
      this.$modal.confirm('确认将赛事「' + (row.contestName || row.contestId) + '」上线吗？').then(async() => {
        await onlineAdminContest(row.contestId)
        this.$modal.msgSuccess('上线成功')
        this.getList()
      }).catch(() => {})
    },
    handleOffline(row) {
      this.$modal.confirm('确认将赛事「' + (row.contestName || row.contestId) + '」下线吗？').then(async() => {
        await offlineAdminContest(row.contestId)
        this.$modal.msgSuccess('下线成功')
        this.getList()
      }).catch(() => {})
    },
    canApprove(row) {
      return row.auditStatus === 'pending'
    },
    canReject(row) {
      return row.auditStatus === 'pending'
    },
    canOnline(row) {
      return row.auditStatus === 'approved'
    },
    canOffline(row) {
      return row.auditStatus === 'online'
    },
    formatMoney(value) {
      const n = Number(value || 0)
      return n.toFixed(2)
    }
  }
}
</script>

<style scoped>
.jst-admin-contest-page {
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

@media (max-width: 768px) {
  .jst-admin-contest-page {
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
