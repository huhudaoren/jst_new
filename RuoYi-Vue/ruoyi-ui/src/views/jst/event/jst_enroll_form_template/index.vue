<template>
  <div class="app-container jst-enroll-form-template-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">报名配置</p>
        <h2>报名表单模板</h2>
        <p class="hero-desc">管理表单模板版本，支持 JSON 预览和一键复制模板。</p>
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
      <el-form-item label="赛事" prop="ownerId">
        <el-input
          v-model.trim="contestKeyword"
          placeholder="赛事ID/名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="版本号" prop="templateVersion">
        <el-input
          v-model.trim="queryParams.templateVersion"
          placeholder="输入版本号"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
      <el-form-item label="生效时间">
        <el-date-picker
          v-model="effectiveRange"
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
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-hasPermi="['jst:event:enroll_form_template:add']">新增</el-button>
      </el-col>
      <el-col :xs="12" :sm="8" :md="4">
        <el-button icon="el-icon-download" @click="handleExport" v-hasPermi="['jst:event:enroll_form_template:export']">导出</el-button>
      </el-col>
    </el-row>

    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="templateList.length">
        <div v-for="row in templateList" :key="row.templateId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.templateName || '--' }}</div>
              <div class="mobile-sub">模板ID：{{ row.templateId }} · 版本 {{ row.templateVersion || '--' }}</div>
            </div>
            <JstStatusBadge :status="String(row.auditStatus || '')" :status-map="auditStatusMap" />
          </div>
          <div class="mobile-info-row">所属：{{ ownerTypeLabel(row.ownerType) }} / {{ row.ownerId || '--' }}</div>
          <div class="mobile-info-row">生效时间：{{ parseTime(row.effectiveTime) || '--' }}</div>
          <div class="mobile-info-row">
            启停状态：
            <JstStatusBadge :status="String(row.status)" :status-map="enableStatusMap" />
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="openDetail(row)">详情</el-button>
            <el-button type="text" @click="openSchemaPreview(row)">JSON预览</el-button>
            <el-button type="text" v-hasPermi="['jst:event:enroll_form_template:add']" @click="handleCopy(row)">复制</el-button>
            <el-button type="text" v-hasPermi="['jst:event:enroll_form_template:edit']" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" class="danger-text" v-hasPermi="['jst:event:enroll_form_template:remove']" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无报名模板" :image-size="96" />
    </div>

    <el-table v-else v-loading="loading" :data="templateList">
      <template slot="empty">
        <el-empty description="暂无报名模板" :image-size="96" />
      </template>
      <el-table-column label="模板ID" prop="templateId" min-width="90" />
      <el-table-column label="模板名称" prop="templateName" min-width="180" show-overflow-tooltip />
      <el-table-column label="版本号" prop="templateVersion" min-width="90" align="center" />
      <el-table-column label="所属类型" min-width="110">
        <template slot-scope="scope">{{ ownerTypeLabel(scope.row.ownerType) }}</template>
      </el-table-column>
      <el-table-column label="所属方ID" prop="ownerId" min-width="110" />
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
      <el-table-column label="生效时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.effectiveTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" min-width="280" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="openDetail(scope.row)">详情</el-button>
          <el-button type="text" @click="openSchemaPreview(scope.row)">JSON预览</el-button>
          <el-button type="text" v-hasPermi="['jst:event:enroll_form_template:add']" @click="handleCopy(scope.row)">复制</el-button>
          <el-button type="text" v-hasPermi="['jst:event:enroll_form_template:edit']" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button type="text" class="danger-text" v-hasPermi="['jst:event:enroll_form_template:remove']" @click="handleDelete(scope.row)">删除</el-button>
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
        <el-descriptions v-if="detailData" :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="模板ID">{{ detailData.templateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="模板名称">{{ detailData.templateName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="版本号">{{ detailData.templateVersion || '--' }}</el-descriptions-item>
          <el-descriptions-item label="所属类型">{{ ownerTypeLabel(detailData.ownerType) }}</el-descriptions-item>
          <el-descriptions-item label="所属方ID">{{ detailData.ownerId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <JstStatusBadge :status="String(detailData.auditStatus || '')" :status-map="auditStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="启停状态">
            <JstStatusBadge :status="String(detailData.status)" :status-map="enableStatusMap" />
          </el-descriptions-item>
          <el-descriptions-item label="生效时间">{{ parseTime(detailData.effectiveTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="isMobile ? 1 : 2">{{ detailData.remark || '--' }}</el-descriptions-item>
          <el-descriptions-item label="字段定义JSON" :span="isMobile ? 1 : 2">
            <pre class="json-preview">{{ formatJson(detailData.schemaJson) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog title="字段定义 JSON 预览" :visible.sync="schemaPreviewVisible" width="760px" append-to-body>
      <pre class="json-preview large">{{ schemaPreviewContent }}</pre>
      <div slot="footer">
        <el-button @click="schemaPreviewVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="104px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="版本号" prop="templateVersion">
          <el-input v-model="form.templateVersion" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="所属类型" prop="ownerType">
          <el-select v-model="form.ownerType" placeholder="请选择">
            <el-option v-for="item in ownerTypeOptions" :key="'f-o-' + item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属方ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入所属方ID" />
        </el-form-item>
        <el-form-item label="字段JSON" prop="schemaJson">
          <el-input
            v-model="form.schemaJson"
            type="textarea"
            :rows="6"
            maxlength="6000"
            show-word-limit
            placeholder="请输入字段定义JSON"
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
        <el-form-item label="生效时间" prop="effectiveTime">
          <el-date-picker
            v-model="form.effectiveTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择生效时间"
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
  addJst_enroll_form_template,
  delJst_enroll_form_template,
  getJst_enroll_form_template,
  listJst_enroll_form_template,
  updateJst_enroll_form_template
} from '@/api/jst/event/jst_enroll_form_template'

export default {
  name: 'JstEnrollFormTemplate',
  data() {
    return {
      loading: false,
      total: 0,
      rawList: [],
      contestKeyword: '',
      effectiveRange: [],
      detailVisible: false,
      detailLoading: false,
      detailData: null,
      schemaPreviewVisible: false,
      schemaPreviewContent: '--',
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: undefined,
        templateVersion: undefined,
        ownerId: undefined,
        auditStatus: undefined,
        status: undefined
      },
      form: {},
      rules: {
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
        templateVersion: [{ required: true, message: '版本号不能为空', trigger: 'blur' }],
        ownerType: [{ required: true, message: '所属类型不能为空', trigger: 'change' }],
        schemaJson: [{ required: true, message: '字段JSON不能为空', trigger: 'blur' }],
        auditStatus: [{ required: true, message: '审核状态不能为空', trigger: 'change' }],
        status: [{ required: true, message: '启停状态不能为空', trigger: 'change' }]
      },
      ownerTypeOptions: [
        { label: '平台模板', value: 'platform' },
        { label: '赛事方模板', value: 'partner' }
      ],
      auditStatusOptions: [
        { label: '草稿', value: 'draft' },
        { label: '待审核', value: 'pending' },
        { label: '审核通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' }
      ],
      enableStatusOptions: [
        { label: '停用', value: 0 },
        { label: '启用', value: 1 }
      ],
      auditStatusMap: {
        draft: { label: '草稿', type: 'info' },
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
    },
    templateList() {
      const keyword = String(this.contestKeyword || '').toLowerCase()
      const hasRange = this.effectiveRange && this.effectiveRange.length === 2
      let list = Array.isArray(this.rawList) ? this.rawList.slice() : []

      if (keyword) {
        list = list.filter(row => this.containsText(row.contestName, keyword) || this.containsText(row.ownerId, keyword))
      }
      if (hasRange) {
        const begin = new Date(this.effectiveRange[0] + ' 00:00:00').getTime()
        const end = new Date(this.effectiveRange[1] + ' 23:59:59').getTime()
        list = list.filter(row => {
          const time = this.toTime(row.effectiveTime)
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
        if (!params.ownerId && /^\d+$/.test(this.contestKeyword)) {
          params.ownerId = Number(this.contestKeyword)
        }
        const res = await listJst_enroll_form_template(params)
        this.rawList = Array.isArray(res.rows) ? res.rows : []
        this.total = Number(res.total || 0)
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = {
        templateId: undefined,
        templateName: undefined,
        templateVersion: undefined,
        ownerType: undefined,
        ownerId: undefined,
        schemaJson: undefined,
        auditStatus: undefined,
        status: undefined,
        effectiveTime: undefined,
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
      this.effectiveRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加报名模板'
    },
    handleUpdate(row) {
      const templateId = row && row.templateId
      if (!templateId) return
      this.reset()
      getJst_enroll_form_template(templateId).then(res => {
        this.form = { ...this.form, ...(res.data || {}) }
        this.open = true
        this.title = '编辑报名模板'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.templateId ? updateJst_enroll_form_template : addJst_enroll_form_template
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
      this.$modal.confirm('确认删除报名模板「' + templateId + '」吗？').then(() => {
        return delJst_enroll_form_template(templateId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleCopy(row) {
      const templateId = row && row.templateId
      if (!templateId) return
      getJst_enroll_form_template(templateId).then(res => {
        const src = res.data || {}
        const payload = { ...src }
        delete payload.templateId
        delete payload.createBy
        delete payload.createTime
        delete payload.updateBy
        delete payload.updateTime
        delete payload.delFlag
        payload.templateName = (src.templateName || '未命名模板') + '-副本'
        payload.templateVersion = Number(src.templateVersion || 0) + 1
        return addJst_enroll_form_template(payload)
      }).then(() => {
        this.$modal.msgSuccess('复制成功')
        this.getList()
      }).catch(() => {})
    },
    handleExport() {
      const params = { ...this.queryParams }
      if (!params.ownerId && /^\d+$/.test(this.contestKeyword)) {
        params.ownerId = Number(this.contestKeyword)
      }
      this.download('jst/event/jst_enroll_form_template/export', params, `jst_enroll_form_template_${Date.now()}.xlsx`)
    },
    openDetail(row) {
      const templateId = row && row.templateId
      this.detailData = row || null
      this.detailVisible = true
      if (!templateId) return
      this.detailLoading = true
      getJst_enroll_form_template(templateId).then(res => {
        this.detailData = res.data || row
      }).finally(() => {
        this.detailLoading = false
      })
    },
    openSchemaPreview(row) {
      this.schemaPreviewContent = this.formatJson(row && row.schemaJson)
      this.schemaPreviewVisible = true
    },
    ownerTypeLabel(value) {
      const found = this.ownerTypeOptions.find(item => item.value === value)
      return found ? found.label : (value || '--')
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
.jst-enroll-form-template-page {
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

.json-preview {
  max-height: 240px;
  margin: 0;
  padding: 10px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  background: #f7f9fc;
  border-radius: 6px;
}

.json-preview.large {
  max-height: 60vh;
}

@media (max-width: 768px) {
  .jst-enroll-form-template-page {
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
