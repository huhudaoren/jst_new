<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable>
          <el-option v-for="item in auditOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:event:formTemplate:edit']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 手机卡片 -->
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

    <!-- 桌面表格 -->
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

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '100%' : '860px'" :fullscreen="isMobile" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="所属类型">
          <el-select v-model="form.ownerType" placeholder="请选择">
            <el-option label="平台" value="platform" />
            <el-option label="赛事方" value="partner" />
          </el-select>
        </el-form-item>
        <el-form-item label="Schema">
          <el-row :gutter="12">
            <el-col :xs="24" :sm="12">
              <el-input v-model="schemaText" type="textarea" :rows="14" placeholder="请输入 JSON Schema" @input="onSchemaInput" />
              <div v-if="schemaError" style="color:#F56C6C;font-size:12px;margin-top:4px">{{ schemaError }}</div>
            </el-col>
            <el-col :xs="24" :sm="12">
              <div class="schema-preview">
                <div class="schema-preview__title">预览</div>
                <div v-if="schemaFields.length === 0" style="color:#909399;padding:12px">输入有效 JSON 后显示预览</div>
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
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleFormSubmit">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 驳回弹窗 -->
    <el-dialog title="驳回原因" :visible.sync="rejectVisible" width="420px" append-to-body>
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
      <div slot="footer">
        <el-button @click="rejectVisible = false">取 消</el-button>
        <el-button type="primary" :loading="rejectLoading" @click="doReject">确 定</el-button>
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
        // 尝试提取字段列表用于预览
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
.mobile-card-list { padding: 0 4px; }
.mobile-card { background: #fff; border-radius: 8px; padding: 12px 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,.06); }
.mobile-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.mobile-card__title { font-weight: 600; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.mobile-card__meta { font-size: 12px; color: #909399; display: flex; gap: 12px; margin-bottom: 8px; }
.mobile-card__actions { display: flex; gap: 6px; }
.schema-preview { border: 1px solid #EBEEF5; border-radius: 6px; min-height: 200px; overflow: auto; }
.schema-preview__title { padding: 8px 12px; background: #f5f7fa; font-size: 13px; font-weight: 600; border-bottom: 1px solid #EBEEF5; }
.schema-preview__list { padding: 8px 12px; }
.schema-field { display: flex; align-items: center; gap: 8px; padding: 4px 0; border-bottom: 1px dashed #EBEEF5; }
.schema-field__label { font-size: 13px; min-width: 80px; }
</style>
