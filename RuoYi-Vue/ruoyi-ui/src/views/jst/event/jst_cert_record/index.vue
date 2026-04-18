<template>
  <div class="app-container jst-cert-record-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">证书中心</p>
        <h2>证书记录</h2>
        <p class="hero-desc">按赛事、参赛人、证书编号查询发放进度，并支持在线预览证书。</p>
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
      <el-form-item label="证书编号" prop="certNo">
        <el-input
          v-model.trim="queryParams.certNo"
          placeholder="输入证书编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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
      <el-form-item label="发放状态" prop="issueStatus">
        <el-select v-model="queryParams.issueStatus" placeholder="全部" clearable>
          <el-option v-for="item in issueStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发放时间">
        <el-date-picker
          v-model="issueRange"
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
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:cert_record:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:cert_record:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="recordList.length">
        <div v-for="row in recordList" :key="row.certId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.certNo || ('证书 #' + (row.certId || '--')) }}</div>
              <div class="mobile-sub">{{ row.contestName || ('赛事ID：' + (row.contestId || '--')) }}</div>
            </div>
            <JstStatusBadge :status="String(row.issueStatus || '')" :status-map="issueStatusMap" />
          </div>
          <div class="mobile-info-row">参赛人：{{ row.participantName || row.participantId || '--' }}</div>
          <div class="mobile-info-row">证书模板：{{ row.templateId || '--' }}</div>
          <div class="mobile-info-row">发放时间：{{ parseTime(row.issueTime) || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" @click="openPreview(row)">预览证书</el-button>
            <el-button type="text" v-hasPermi="['jst:event:cert_record:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:cert_record:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无证书记录" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="recordList">
      <template slot="empty">
        <el-empty description="暂无证书记录" :image-size="96" />
      </template>
      <el-table-column label="证书ID" prop="certId" min-width="90" />
      <el-table-column label="证书编号" prop="certNo" min-width="210" show-overflow-tooltip />
      <el-table-column label="赛事" min-width="170" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.contestName || ('赛事 #' + (scope.row.contestId || '--')) }}
        </template>
      </el-table-column>
      <el-table-column label="参赛人" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.row.participantName || scope.row.participantId || '--' }}
        </template>
      </el-table-column>
      <el-table-column label="发放状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.issueStatus || '')" :status-map="issueStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="发放时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.issueTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="证书文件" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="scope.row.certFileUrl" type="primary" @click="openPreview(scope.row)">点击预览</el-link>
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" @click="openPreview(scope.row)">预览</el-button>
          <el-button type="text" v-hasPermi="['jst:event:cert_record:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:cert_record:remove']" @click="handleDelete(scope.row)">删除</el-button>
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
      title="证书详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '50%'"
      append-to-body
    >
      <div v-loading="detailLoading" class="drawer-body">
        <el-descriptions v-if="detailData" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="证书ID">{{ detailData.certId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="证书编号">{{ detailData.certNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="赛事">{{ detailData.contestName || detailData.contestId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="成绩ID">{{ detailData.scoreId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="报名ID">{{ detailData.enrollId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="参赛人">{{ detailData.participantName || detailData.participantId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="模板ID">{{ detailData.templateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发放状态">
            <JstStatusBadge :status="String(detailData.issueStatus || '')" :status-map="issueStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="发放时间">{{ parseTime(detailData.issueTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="校验码">{{ detailData.verifyCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="作废原因" :span="isMobile ? 1 : 2">{{ detailData.voidReason || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog title="证书预览" :visible.sync="previewVisible" width="900px" append-to-body class="preview-dialog">
      <div class="preview-wrap">
        <iframe v-if="previewUrl" :src="previewUrl" frameborder="0" />
        <el-empty v-else description="暂无证书文件" :image-size="96" />
      </div>
      <div slot="footer">
        <el-link v-if="previewUrl" type="primary" :href="previewUrl" target="_blank">新窗口打开</el-link>
        <el-button @click="previewVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" :width="isMobile ? '100%' : '640px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" :label-width="isMobile ? '88px' : '100px'">
        <div class="form-section">
          <div class="form-section__title">证书基础信息</div>
          <el-form-item label="证书编号" prop="certNo">
            <el-input v-model="form.certNo" placeholder="请输入证书编号" />
          </el-form-item>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="模板ID" prop="templateId">
                <el-input v-model="form.templateId" placeholder="请输入模板ID" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="证书文件URL" prop="certFileUrl">
                <el-input v-model="form.certFileUrl" placeholder="请输入证书文件URL" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">关联赛事 / 成绩 / 报名</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="赛事" prop="contestId">
                <contest-picker v-model="form.contestId" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="成绩ID" prop="scoreId">
                <el-input v-model="form.scoreId" placeholder="请输入关联成绩ID" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="报名ID" prop="enrollId">
                <el-input v-model="form.enrollId" placeholder="请输入报名ID" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="参赛人" prop="participantId">
                <participant-picker v-model="form.participantId" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">发放信息</div>
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-form-item label="发放状态" prop="issueStatus">
                <el-select v-model="form.issueStatus" placeholder="请选择" class="full-width">
                  <el-option v-for="item in issueStatusOptions" :key="'f-i-' + item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="发放时间" prop="issueTime">
                <el-date-picker
                  v-model="form.issueTime"
                  type="date"
                  value-format="yyyy-MM-dd"
                  placeholder="请选择发放时间"
                  class="full-width"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="作废原因" prop="voidReason">
            <el-input v-model="form.voidReason" placeholder="请输入作废原因" />
          </el-form-item>
        </div>

        <el-divider />

        <div class="form-section">
          <div class="form-section__title">校验与备注</div>
          <el-form-item label="校验码" prop="verifyCode">
            <el-input v-model="form.verifyCode" placeholder="请输入公开校验码" />
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="255" show-word-limit placeholder="请输入备注" />
          </el-form-item>
        </div>
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
  addJst_cert_record,
  delJst_cert_record,
  getJst_cert_record,
  listJst_cert_record,
  updateJst_cert_record
} from '@/api/jst/event/jst_cert_record'

export default {
  name: 'JstCertRecord',
  data() {
    return {
      loading: false,
      total: 0,
      rawList: [],
      contestKeyword: '',
      participantKeyword: '',
      issueRange: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      previewVisible: false,
      previewUrl: '',
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        certNo: undefined,
        contestId: undefined,
        participantId: undefined,
        issueStatus: undefined
      },
      form: {},
      rules: {
        certNo: [{ required: true, message: '证书编号不能为空', trigger: 'blur' }],
        contestId: [{ required: true, message: '赛事ID不能为空', trigger: 'blur' }],
        enrollId: [{ required: true, message: '报名ID不能为空', trigger: 'blur' }],
        participantId: [{ required: true, message: '参赛人ID不能为空', trigger: 'blur' }],
        issueStatus: [{ required: true, message: '发放状态不能为空', trigger: 'change' }]
      },
      issueStatusOptions: [
        { label: '待发放', value: 'pending' },
        { label: '已发放', value: 'issued' },
        { label: '已作废', value: 'voided' }
      ],
      issueStatusMap: {
        pending: { label: '待发放', type: 'warning' },
        issued: { label: '已发放', type: 'success' },
        voided: { label: '已作废', type: 'danger' }
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
      const hasRange = this.issueRange && this.issueRange.length === 2
      let list = Array.isArray(this.rawList) ? this.rawList.slice() : []

      if (contestKeyword) {
        list = list.filter(row => this.containsText(row.contestName, contestKeyword) || this.containsText(row.contestId, contestKeyword))
      }
      if (participantKeyword) {
        list = list.filter(row => this.containsText(row.participantName, participantKeyword) || this.containsText(row.participantId, participantKeyword))
      }
      if (hasRange) {
        const begin = new Date(this.issueRange[0] + ' 00:00:00').getTime()
        const end = new Date(this.issueRange[1] + ' 23:59:59').getTime()
        list = list.filter(row => {
          const time = this.toTime(row.issueTime)
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
        const res = await listJst_cert_record(params)
        this.rawList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        certId: undefined,
        certNo: undefined,
        contestId: undefined,
        scoreId: undefined,
        enrollId: undefined,
        userId: undefined,
        participantId: undefined,
        templateId: undefined,
        certFileUrl: undefined,
        issueStatus: undefined,
        issueTime: undefined,
        voidReason: undefined,
        verifyCode: undefined,
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
      this.issueRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加证书记录'
    },
    handleUpdate(row) {
      const certId = row && row.certId
      if (!certId) return
      this.reset()
      getJst_cert_record(certId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑证书记录'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.certId ? updateJst_cert_record : addJst_cert_record
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.certId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const certId = row && row.certId
      if (!certId) return
      this.$modal.confirm('确认删除证书记录「' + certId + '」吗？').then(() => {
        return delJst_cert_record(certId)
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
      this.download('jst/event/jst_cert_record/export', params, `jst_cert_record_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const certId = row && row.certId
      this.detailData = row || null
      this.detailVisible = true
      if (!certId) return
      this.detailLoading = true
      getJst_cert_record(certId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    openPreview(row) {
      const url = row && row.certFileUrl
      if (!url) {
        this.$modal.msgWarning('该记录暂无证书文件')
        return
      }
      this.previewUrl = url
      this.previewVisible = true
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
.jst-cert-record-page {
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
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 12px;
}

.danger-text {
  color: #f56c6c;
}

.drawer-body {
  padding: 8px 16px 16px;
}

.preview-wrap {
  height: 62vh;
  border: 1px solid #e6ebf4;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.preview-wrap iframe {
  width: 100%;
  height: 100%;
}

.form-section {
  padding: 0 4px;
}

.form-section + .form-section {
  margin-top: 4px;
}

.form-section__title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 4px 0 14px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
  line-height: 1.2;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .jst-cert-record-page {
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

  .preview-dialog ::v-deep .el-dialog {
    width: 96% !important;
  }

  .form-section__title {
    font-size: 13px;
    margin: 2px 0 10px;
  }
}
</style>
