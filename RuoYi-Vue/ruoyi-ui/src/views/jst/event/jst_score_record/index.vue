<template>
  <div class="app-container jst-score-record-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">赛事成绩</p>
        <h2>成绩记录</h2>
        <p class="hero-desc">支持按赛事与参赛人检索，查看审核/发布状态与成绩详情。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="88px"
      class="query-panel"
    >
      <el-form-item label="赛事" prop="contestId">
        <el-input
          v-model.trim="contestKeyword"
          placeholder="赛事ID/名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参赛人" prop="participantId">
        <el-input
          v-model.trim="participantKeyword"
          placeholder="参赛人ID/姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="奖项" prop="awardLevel">
        <el-input
          v-model.trim="queryParams.awardLevel"
          placeholder="奖项关键字"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态" prop="publishStatus">
        <el-select v-model="queryParams.publishStatus" placeholder="全部" clearable>
          <el-option v-for="item in publishStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发布时间">
        <el-date-picker
          v-model="publishRange"
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

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :xs="12" :sm="8" :md="4">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:score_record:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:score_record:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="recordList.length">
        <div v-for="row in recordList" :key="row.scoreId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.contestName || ('赛事 #' + (row.contestId || '--')) }}</div>
              <div class="mobile-sub">成绩ID：{{ row.scoreId }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-info-row">参赛人：{{ row.participantName || row.participantId || '--' }}</div>
          <div class="mobile-info-row">分数：{{ formatScore(row.scoreValue) }}</div>
          <div class="mobile-info-row">奖项：{{ row.awardLevel || '--' }}</div>
          <div class="mobile-info-row">
            发布状态：
            <JstStatusBadge :status="String(row.publishStatus || '')" :status-map="publishStatusMap" />
          </div>
          <div class="mobile-info-row">发布时间：{{ parseTime(row.publishTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" v-hasPermi="['jst:event:score_record:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:score_record:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无成绩记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="recordList">
      <template slot="empty">
        <el-empty description="暂无成绩记录" :image-size="96" />
      </template>
      <el-table-column label="成绩ID" prop="scoreId" min-width="90" />
      <el-table-column label="赛事" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.contestName || ('赛事 #' + (scope.row.contestId || '--')) }}
        </template>
      </el-table-column>
      <el-table-column label="参赛人" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.participantName || scope.row.participantId || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="分数" prop="scoreValue" min-width="90" align="right">
        <template slot-scope="scope">{{ formatScore(scope.row.scoreValue) }}</template>
      </el-table-column>
      <el-table-column label="奖项" prop="awardLevel" min-width="130" show-overflow-tooltip />
      <el-table-column label="审核状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.auditStatus || '')" :status-map="auditStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="发布状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.publishStatus || '')" :status-map="publishStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="发布时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.publishTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" v-hasPermi="['jst:event:score_record:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:score_record:remove']" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-drawer
      title="成绩详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '50%'"
      append-to-body
    >
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detailData" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="成绩ID">{{ detailData.scoreId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事">{{ detailData.contestName || detailData.contestId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="报名ID">{{ detailData.enrollId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detailData.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="参赛人">{{ detailData.participantName || detailData.participantId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="分数">{{ formatScore(detailData.scoreValue) }}</el-descriptions-item>
          <el-descriptions-item label="奖项">{{ detailData.awardLevel || '--' }}</el-descriptions-item>
          <el-descriptions-item label="名次">{{ detailData.rankNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="导入批次">{{ detailData.importBatchNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <JstStatusBadge :status="String(detailData.auditStatus || '')" :status-map="auditStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="发布状态">
            <JstStatusBadge :status="String(detailData.publishStatus || '')" :status-map="publishStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ parseTime(detailData.publishTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog :title="title" :visible.sync="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="赛事ID" prop="contestId">
          <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
        </el-form-item>
        <el-form-item label="报名ID" prop="enrollId">
          <el-input v-model="form.enrollId" placeholder="请输入报名ID" />
        </el-form-item>
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="参赛人ID" prop="participantId">
          <el-input v-model="form.participantId" placeholder="请输入参赛人ID" />
        </el-form-item>
        <el-form-item label="分数" prop="scoreValue">
          <el-input v-model="form.scoreValue" placeholder="请输入分数" />
        </el-form-item>
        <el-form-item label="奖项" prop="awardLevel">
          <el-input v-model="form.awardLevel" placeholder="请输入奖项等级" />
        </el-form-item>
        <el-form-item label="名次" prop="rankNo">
          <el-input v-model="form.rankNo" placeholder="请输入名次" />
        </el-form-item>
        <el-form-item label="导入批次" prop="importBatchNo">
          <el-input v-model="form.importBatchNo" placeholder="请输入导入批次号" />
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-select v-model="form.auditStatus" placeholder="请选择">
            <el-option v-for="item in auditStatusOptions" :key="'f-a-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" prop="publishStatus">
          <el-select v-model="form.publishStatus" placeholder="请选择">
            <el-option v-for="item in publishStatusOptions" :key="'f-p-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker
            v-model="form.publishTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发布时间"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addJst_score_record,
  delJst_score_record,
  getJst_score_record,
  listJst_score_record,
  updateJst_score_record
} from '@/api/jst/event/jst_score_record'

export default {
  name: 'JstScoreRecord',
  data() {
    return {
      loading: false,
      total: 0,
      rawList: [],
      contestKeyword: '',
      participantKeyword: '',
      publishRange: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestId: undefined,
        participantId: undefined,
        awardLevel: undefined,
        auditStatus: undefined,
        publishStatus: undefined
      },
      form: {},
      rules: {
        contestId: [{ required: true, message: '赛事ID不能为空', trigger: 'blur' }],
        enrollId: [{ required: true, message: '报名ID不能为空', trigger: 'blur' }],
        participantId: [{ required: true, message: '参赛人ID不能为空', trigger: 'blur' }],
        auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }],
        publishStatus: [{ required: true, message: '发布状态不能为空', trigger: 'change' }]
      },
      auditStatusOptions: [
        { label: '待审核', value: 'pending' },
        { label: '审核通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' }
      ],
      publishStatusOptions: [
        { label: '未发布', value: 'unpublished' },
        { label: '已发布', value: 'published' },
        { label: '已撤回', value: 'withdrawn' }
      ],
      auditStatusMap: {
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '审核通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' }
      },
      publishStatusMap: {
        unpublished: { label: '未发布', type: 'info' },
        published: { label: '已发布', type: 'success' },
        withdrawn: { label: '已撤回', type: 'warning' }
      }
    }
  },
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    },
    recordList() {
      const contestKeyword = String(this.contestKeyword || '').toLowerCase()
      const participantKeyword = String(this.participantKeyword || '').toLowerCase()
      const hasRange = this.publishRange && this.publishRange.length === 2
      let list = Array.isArray(this.rawList) ? this.rawList.slice() : []

      if (contestKeyword) {
        list = list.filter(row => this.containsText(row.contestName, contestKeyword) || this.containsText(row.contestId, contestKeyword))
      }
      if (participantKeyword) {
        list = list.filter(row => this.containsText(row.participantName, participantKeyword) || this.containsText(row.participantId, participantKeyword))
      }
      if (hasRange) {
        const begin = new Date(this.publishRange[0] + ' 00:00:00').getTime()
        const end = new Date(this.publishRange[1] + ' 23:59:59').getTime()
        list = list.filter(row => {
          const time = this.toTime(row.publishTime)
          return time && time >= begin && time <= end
        })
      }
      return list
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
        if (!params.contestId && /^\d+$/.test(this.contestKeyword)) {
          params.contestId = Number(this.contestKeyword)
        }
        if (!params.participantId && /^\d+$/.test(this.participantKeyword)) {
          params.participantId = Number(this.participantKeyword)
        }
        const res = await listJst_score_record(params)
        this.rawList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        scoreId: undefined,
        contestId: undefined,
        enrollId: undefined,
        userId: undefined,
        participantId: undefined,
        scoreValue: undefined,
        awardLevel: undefined,
        rankNo: undefined,
        importBatchNo: undefined,
        auditStatus: undefined,
        publishStatus: undefined,
        publishTime: undefined,
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.contestKeyword = ''
      this.participantKeyword = ''
      this.publishRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加成绩记录'
    },
    handleUpdate(row) {
      const scoreId = row && row.scoreId
      if (!scoreId) return
      this.reset()
      getJst_score_record(scoreId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑成绩记录'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.scoreId ? updateJst_score_record : addJst_score_record
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.scoreId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const scoreId = row && row.scoreId
      if (!scoreId) return
      this.$modal.confirm('确认删除成绩记录「' + scoreId + '」吗？').then(() => {
        return delJst_score_record(scoreId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      const params = { ...this.queryParams }
      if (!params.contestId && /^\d+$/.test(this.contestKeyword)) {
        params.contestId = Number(this.contestKeyword)
      }
      if (!params.participantId && /^\d+$/.test(this.participantKeyword)) {
        params.participantId = Number(this.participantKeyword)
      }
      this.download('jst/event/jst_score_record/export', params, `jst_score_record_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const scoreId = row && row.scoreId
      this.detailData = row || null
      this.detailVisible = true
      if (!scoreId) return
      this.detailLoading = true
      getJst_score_record(scoreId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    formatScore(value) {
      if (value === undefined || value === null || value === '') {
        return '--'
      }
      return value
    },
    containsText(value, keyword) {
      return String(value === undefined || value === null ? '' : value).toLowerCase().indexOf(keyword) > -1
    },
    toTime(value) {
      if (!value) return 0
      const text = String(value).replace('T', ' ')
      const time = new Date(text).getTime()
      return Number.isNaN(time) ? 0 : time
    }
  }
}
</script>

<style scoped lang="scss">
.jst-score-record-page {
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
  background: #fff;
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
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.action-row .el-button {
  width: 100%;
}

.mobile-list {
  min-height: 180px;
}

.mobile-card {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
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
  color: #6f7b8f;
  font-size: 13px;
}

.mobile-actions {
  display: flex;
  gap: 16px;
  margin-top: 12px;
}

.danger-text {
  color: #f56c6c;
}

.drawer-body {
  padding: 8px 16px 16px;
}

@media (max-width: 768px) {
  .jst-score-record-page {
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
