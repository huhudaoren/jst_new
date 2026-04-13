<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Form Templates</p>
        <h2>报名表单模板</h2>
        <p class="hero-desc">管理模板版本与审核状态，支持 Schema 预览校验。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 模板保存后可提审。</p>
            <p>2. 审核中可执行通过或驳回。</p>
            <p>3. JSON Schema 会实时预览字段。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:event:formTemplate:edit']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.templateId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.templateName }}</span>
            <el-tag size="mini" :type="auditMeta(row.auditStatus).type">{{ auditMeta(row.auditStatus).label }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>版本：v{{ row.templateVersion || 1 }}</span>
            <span>{{ row.status === 1 ? '启用' : '停用' }}</span>
            <span>{{ parseTime(row.createTime) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:event:formTemplate:edit']">编辑</el-button>
            <el-button v-if="row.auditStatus === 'draft'" type="text" size="mini" @click="handleSubmitAudit(row)" v-hasPermi="['jst:event:formTemplate:edit']">提审</el-button>
            <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleApprove(row)" v-hasPermi="['jst:event:formTemplate:audit']">通过</el-button>
            <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleReject(row)" v-hasPermi="['jst:event:formTemplate:audit']">驳回</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无模板" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="templateId" width="70" />
      <el-table-column label="模板名称" prop="templateName" min-width="200" show-overflow-tooltip />
      <el-table-column label="版本" width="80">
        <template slot-scope="{ row }">v{{ row.templateVersion || 1 }}</template>
      </el-table-column>
      <el-table-column label="审核状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="auditMeta(row.auditStatus).type">{{ auditMeta(row.auditStatus).label }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="250">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:event:formTemplate:edit']">编辑</el-button>
          <el-button v-if="row.auditStatus === 'draft'" type="text" size="mini" @click="handleSubmitAudit(row)" v-hasPermi="['jst:event:formTemplate:edit']">提审</el-button>
          <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleApprove(row)" v-hasPermi="['jst:event:formTemplate:audit']">通过</el-button>
          <el-button v-if="row.auditStatus === 'pending'" type="text" size="mini" @click="handleReject(row)" v-hasPermi="['jst:event:formTemplate:audit']">驳回</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      :size="isMobile ? '100%' : '55%'"
      append-to-body
      :with-header="true"
    >
      <div class="drawer-body">
        <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '90px' : '96px'">
          <el-form-item label="模板名称" prop="templateName">
            <el-input v-model="form.templateName" placeholder="请输入模板名称" />
          </el-form-item>
          <el-form-item label="所属类型">
            <el-select v-model="form.ownerType" placeholder="请选择" class="full-width">
              <el-option label="平台" value="platform" />
              <el-option label="赛事方" value="partner" />
            </el-select>
          </el-form-item>
          <el-form-item label="Schema">
            <el-row :gutter="12">
              <el-col :xs="24" :sm="12">
                <el-input v-model="schemaText" type="textarea" :rows="14" placeholder="请输入 JSON Schema" @input="onSchemaInput" />
                <div v-if="schemaError" class="schema-error">{{ schemaError }}</div>
              </el-col>
              <el-col :xs="24" :sm="12">
                <div class="schema-preview">
                  <div class="schema-preview__title">预览</div>
                  <div v-if="schemaFields.length === 0" class="schema-empty">输入有效 JSON 后显示预览</div>
                  <div v-else class="schema-preview__list">
                    <div v-for="(field, idx) in schemaFields" :key="idx" class="schema-field">
                      <span class="schema-field__label">{{ field.label || field.key }}</span>
                      <el-tag size="mini" type="info">{{ field.type || 'text' }}</el-tag>
                      <el-tag v-if="field.required" size="mini" type="danger">必填</el-tag>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleFormSubmit">确认</el-button>
      </div>
    </el-drawer>

    <el-dialog title="驳回原因" :visible.sync="rejectVisible" width="420px" append-to-body>
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
      <div slot="footer">
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="doReject">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFormTemplate, getFormTemplate, saveFormTemplate, submitFormTemplate, approveFormTemplate, rejectFormTemplate } from '@/api/jst/formTemplate'
import { parseTime } from '@/utils/ruoyi'

const AUDIT_META = {
  draft: { label: '草稿', type: 'info' },
  pending: { label: '审核中', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' }
}

export default {
  name: 'JstFormTemplate',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, templateName: null, auditStatus: null },
      auditOptions: [
        { label: '草稿', value: 'draft' },
        { label: '审核中', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' }
      ],
      dialogVisible: false,
      dialogTitle: '',
      form: { templateName: '', ownerType: 'platform', schemaJson: null },
      schemaText: '',
      schemaError: '',
      schemaFields: [],
      formRules: {
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }]
      },
      rejectVisible: false,
      rejectLoading: false,
      rejectReason: '',
      rejectTargetId: null
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    auditMeta(s) { return AUDIT_META[s] || { label: s || '--', type: 'info' } },
    getList() {
      this.loading = true
      listFormTemplate(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    onSchemaInput() {
      this.schemaError = ''
      this.schemaFields = []
      if (!this.schemaText.trim()) return
      try {
        const obj = JSON.parse(this.schemaText)
        this.form.schemaJson = obj
        const fields = obj.fields || obj.properties
        if (Array.isArray(fields)) {
          this.schemaFields = fields
        } else if (fields && typeof fields === 'object') {
          this.schemaFields = Object.entries(fields).map(([key, val]) => ({ key, ...val }))
        }
      } catch (e) {
        this.schemaError = 'JSON 格式错误：' + e.message
      }
    },
    handleAdd() {
      this.form = { templateName: '', ownerType: 'platform', schemaJson: null }
      this.schemaText = ''
      this.schemaError = ''
      this.schemaFields = []
      this.dialogTitle = '新增模板'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getFormTemplate(row.templateId).then(res => {
        const d = res.data || res
        this.form = { templateId: d.templateId, templateName: d.templateName, ownerType: d.ownerType || 'platform', schemaJson: d.schemaJson }
        this.schemaText = d.schemaJson ? JSON.stringify(d.schemaJson, null, 2) : ''
        this.onSchemaInput()
        this.dialogTitle = '编辑模板'
        this.dialogVisible = true
      })
    },
    handleFormSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.schemaText.trim()) {
          try { this.form.schemaJson = JSON.parse(this.schemaText) } catch (e) {
            this.$modal.msgWarning('JSON Schema 格式错误')
            return
          }
        }
        this.submitLoading = true
        saveFormTemplate(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleSubmitAudit(row) {
      this.$modal.confirm('确认提审模板「' + row.templateName + '」？').then(() => {
        return submitFormTemplate(row.templateId)
      }).then(() => { this.$modal.msgSuccess('提审成功'); this.getList() }).catch(() => {})
    },
    handleApprove(row) {
      this.$modal.confirm('确认通过模板「' + row.templateName + '」？').then(() => {
        return approveFormTemplate(row.templateId, {})
      }).then(() => { this.$modal.msgSuccess('审核通过'); this.getList() }).catch(() => {})
    },
    handleReject(row) {
      this.rejectTargetId = row.templateId
      this.rejectReason = ''
      this.rejectVisible = true
    },
    doReject() {
      if (!this.rejectReason.trim()) { this.$modal.msgWarning('请输入驳回原因'); return }
      this.rejectLoading = true
      rejectFormTemplate(this.rejectTargetId, { reason: this.rejectReason }).then(() => {
        this.$modal.msgSuccess('已驳回')
        this.rejectVisible = false
        this.getList()
      }).finally(() => { this.rejectLoading = false })
    }
  }
}
</script>

<style scoped>
.jst-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  margin-bottom: 16px;
  color: #fff;
  border-radius: 12px;
  background: linear-gradient(130deg, #0f766e 0%, #2563eb 58%, #1e293b 100%);
}

.hero-eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .08em;
  text-transform: uppercase;
  opacity: .82;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
}

.hero-desc {
  margin: 8px 0 0;
  color: rgba(255, 255, 255, 0.82);
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.help-lines p {
  margin: 0 0 6px;
  line-height: 1.6;
}

.help-lines p:last-child {
  margin-bottom: 0;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
}

.toolbar-row {
  margin-bottom: 12px;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border: 1px solid #e5eaf2;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 10px;
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 68%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.drawer-body {
  padding: 8px 20px 90px;
}

.drawer-footer {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #e5eaf2;
  background: #fff;
}

.schema-error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

.schema-preview {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  min-height: 200px;
  overflow: auto;
}

.schema-preview__title {
  padding: 8px 12px;
  background: #f5f7fa;
  font-size: 13px;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
}

.schema-empty {
  color: #909399;
  padding: 12px;
}

.schema-preview__list {
  padding: 8px 12px;
}

.schema-field {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;
  border-bottom: 1px dashed #ebeef5;
}

.schema-field__label {
  font-size: 13px;
  min-width: 80px;
}

.full-width {
  width: 100%;
}

@media (max-width: 768px) {
  .jst-page {
    padding: 12px;
  }

  .page-hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
    min-height: 44px;
  }

  .query-panel {
    padding-bottom: 10px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-input,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-date-editor {
    width: 100%;
  }

  .query-panel .el-button,
  .mobile-card__actions .el-button {
    min-height: 44px;
  }

  .drawer-body {
    padding: 8px 14px 96px;
  }

  .drawer-footer {
    padding: 10px 14px;
  }

  .drawer-footer .el-button {
    min-height: 44px;
  }

  .schema-preview {
    margin-top: 12px;
  }
}
</style>
