<template>
  <div class="app-container jst-cert-template-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">证书配置</p>
        <h2>证书模板</h2>
        <p class="hero-desc">管理模板底图与布局配置，支持缩略图预览和快速编辑。</p>
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
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model.trim="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属类型" prop="ownerType">
        <el-select v-model="queryParams.ownerType" placeholder="全部" clearable>
          <el-option v-for="item in ownerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="启停状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in enableStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 action-row">
      <el-col :xs="12" :sm="8" :md="4">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:cert_template:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:cert_template:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="templateList.length">
        <div v-for="row in templateList" :key="row.templateId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.templateName || '--' }}</div>
              <div class="mobile-sub">模板ID：{{ row.templateId }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-preview">
            <image-preview v-if="row.bgImage" :src="row.bgImage" :width="110" :height="62" />
            <span v-else class="preview-empty">暂无底图</span>
          </div>
          <div class="mobile-info-row">
            所属：{{ ownerTypeLabel(row.ownerType) }} /
            <el-link v-if="canGoContest(row)" type="primary" :underline="false" @click="goContest(row)">
              {{ ownerDisplayName(row) }}
            </el-link>
            <span v-else>{{ ownerDisplayName(row) }}</span>
          </div>
          <div class="mobile-info-row">
            启停状态：
            <JstStatusBadge :status="String(row.status)" :status-map="enableStatusMap" />
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" v-hasPermi="['jst:event:cert_template:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:cert_template:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无证书模板" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="templateList">
      <template slot="empty">
        <el-empty description="暂无证书模板" :image-size="96" />
      </template>
      <el-table-column label="模板ID" prop="templateId" min-width="90" />
      <el-table-column label="模板名称" prop="templateName" min-width="160" show-overflow-tooltip />
      <el-table-column label="所属类型" min-width="110">
        <template slot-scope="scope">
          {{ ownerTypeLabel(scope.row.ownerType) }}
        </template>
      </el-table-column>
      <el-table-column label="所属赛事" min-width="140" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="canGoContest(scope.row)" type="primary" :underline="false" @click="goContest(scope.row)">
            {{ ownerDisplayName(scope.row) }}
          </el-link>
          <span v-else>{{ ownerDisplayName(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="底图预览" min-width="120">
        <template slot-scope="scope">
          <image-preview v-if="scope.row.bgImage" :src="scope.row.bgImage" :width="84" :height="48" />
          <span v-else>--</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.auditStatus || '')" :status-map="auditStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="启停状态" min-width="110">
        <template slot-scope="scope">
          <JstStatusBadge :status="String(scope.row.status)" :status-map="enableStatusMap" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="210" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" v-hasPermi="['jst:event:cert_template:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:cert_template:remove']" @click="handleDelete(scope.row)">删除</el-button>
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
      title="模板详情"
      :visible.sync="detailVisible"
      :size="isMobile ? '100%' : '50%'"
      append-to-body
    >
      <div v-loading="detailLoading" class="drawer-body">
        <template v-if="detailData">
          <div class="drawer-preview">
            <image-preview v-if="detailData.bgImage" :src="detailData.bgImage" :width="240" :height="136" />
            <el-empty v-else description="暂无底图" :image-size="80" />
          </div>
          <el-descriptions :column="isMobile ? 1 : 2" border>
            <el-descriptions-item label="模板ID">{{ detailData.templateId || '--' }}</el-descriptions-item>
            <el-descriptions-item label="模板名称">{{ detailData.templateName || '--' }}</el-descriptions-item>
            <el-descriptions-item label="所属类型">{{ ownerTypeLabel(detailData.ownerType) }}</el-descriptions-item>
            <el-descriptions-item label="所属赛事">
              <el-link v-if="canGoContest(detailData)" type="primary" :underline="false" @click="goContest(detailData)">
                {{ ownerDisplayName(detailData) }}
              </el-link>
              <span v-else>{{ ownerDisplayName(detailData) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <JstStatusBadge :status="String(detailData.auditStatus || '')" :status-map="auditStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="启停状态">
              <JstStatusBadge :status="String(detailData.status)" :status-map="enableStatusMap" />
            </el-descriptions-item>
            <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
            <el-descriptions-item label="布局JSON" :span="isMobile ? 1 : 2">
              <pre class="json-preview">{{ formatJson(detailData.layoutJson) }}</pre>
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-drawer>

    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="所属类型" prop="ownerType">
          <el-select v-model="form.ownerType" placeholder="请选择">
            <el-option v-for="item in ownerTypeOptions" :key="'f-o-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属方ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入所属方ID" />
        </el-form-item>
        <el-form-item label="底图" prop="bgImage">
          <image-upload v-model="form.bgImage" />
        </el-form-item>
        <el-form-item label="布局JSON" prop="layoutJson">
          <el-input
            v-model="form.layoutJson"
            type="textarea"
            :rows="4"
            maxlength="5000"
            show-word-limit
            placeholder="请输入布局/字段配置JSON"
          />
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-select v-model="form.auditStatus" placeholder="请选择">
            <el-option v-for="item in auditStatusOptions" :key="'f-a-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="启停状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择">
            <el-option v-for="item in enableStatusOptions" :key="'f-s-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
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
  addJst_cert_template,
  delJst_cert_template,
  getJst_cert_template,
  listJst_cert_template,
  updateJst_cert_template
} from '@/api/jst/event/jst_cert_template'

export default {
  name: 'JstCertTemplate',
  data() {
    return {
      loading: false,
      total: 0,
      templateList: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: undefined,
        ownerType: undefined,
        auditStatus: undefined,
        status: undefined
      },
      form: {},
      rules: {
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
        ownerType: [{ required: true, message: '所属类型不能为空', trigger: 'change' }],
        auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }],
        status: [{ required: true, message: '启停状态不能为空', trigger: 'change' }]
      },
      ownerTypeOptions: [
        { label: '平台模板', value: 'platform' },
        { label: '赛事方模板', value: 'partner' }
      ],
      auditStatusOptions: [
        { label: '待审核', value: 'pending' },
        { label: '审核通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' }
      ],
      enableStatusOptions: [
        { label: '停用', value: 0 },
        { label: '启用', value: 1 }
      ],
      auditStatusMap: {
        pending: { label: '待审核', type: 'warning' },
        approved: { label: '审核通过', type: 'success' },
        rejected: { label: '已驳回', type: 'danger' }
      },
      enableStatusMap: {
        0: { label: '停用', type: 'info' },
        1: { label: '启用', type: 'success' }
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
        const res = await listJst_cert_template({ ...this.queryParams })
        this.templateList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        templateId: undefined,
        templateName: undefined,
        ownerType: undefined,
        ownerId: undefined,
        bgImage: undefined,
        layoutJson: undefined,
        auditStatus: undefined,
        status: undefined,
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
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加证书模板'
    },
    handleUpdate(row) {
      const templateId = row && row.templateId
      if (!templateId) return
      this.reset()
      getJst_cert_template(templateId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑证书模板'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.templateId ? updateJst_cert_template : addJst_cert_template
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.templateId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const templateId = row && row.templateId
      if (!templateId) return
      this.$modal.confirm('确认删除证书模板「' + templateId + '」吗？').then(() => {
        return delJst_cert_template(templateId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      this.download('jst/event/jst_cert_template/export', { ...this.queryParams }, `jst_cert_template_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const templateId = row && row.templateId
      this.detailData = row || null
      this.detailVisible = true
      if (!templateId) return
      this.detailLoading = true
      getJst_cert_template(templateId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    ownerTypeLabel(value) {
      const found = this.ownerTypeOptions.find(item => item.value === value)
      return found ? found.label : (value || '--')
    },
    ownerDisplayName(row) {
      if (!row) return '--'
      return row.ownerName || row.contestName || row.ownerId || '--'
    },
    canGoContest(row) {
      if (!row || !row.ownerId) return false
      return Boolean(row.ownerName || row.contestName)
    },
    goContest(row) {
      const contestId = row && row.ownerId
      if (!contestId) return
      this.$router.push({
        path: '/jst/contest',
        query: { contestId: String(contestId), autoOpen: '1' }
      }).catch(() => {})
    },
    formatJson(value) {
      if (!value) return '--'
      if (typeof value !== 'string') {
        try {
          return JSON.stringify(value, null, 2)
        } catch (e) {
          return String(value)
        }
      }
      try {
        return JSON.stringify(JSON.parse(value), null, 2)
      } catch (e) {
        return value
      }
    }
  }
}
</script>

<style scoped lang="scss">
.jst-cert-template-page {
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

.mobile-preview {
  margin-top: 10px;
}

.preview-empty {
  font-size: 12px;
  color: #9aa5b5;
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

.drawer-preview {
  margin-bottom: 12px;
}

.json-preview {
  max-height: 260px;
  margin: 0;
  padding: 8px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  background: #f7f9fc;
  border-radius: 6px;
}

@media (max-width: 768px) {
  .jst-cert-template-page {
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
