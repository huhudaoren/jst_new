<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Rights Templates</p>
        <h2>权益模板</h2>
        <p class="hero-desc">统一配置权益额度、核销方式和适用角色，便于批量发放。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 先配置模板，再进入发放流程。</p>
            <p>2. 口径、数值和核销方式建议成套维护。</p>
            <p>3. 抽屉保存后自动回到列表。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="权益名称" prop="rightsName">
        <el-input v-model="queryParams.rightsName" placeholder="请输入权益名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="rightsType">
        <el-select v-model="queryParams.rightsType" placeholder="全部" clearable>
          <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['jst:marketing:rights_template:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.rightsTemplateId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.rightsName }}</span>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ typeLabel(row.rightsType) }}</span>
            <span>{{ quotaModeLabel(row.quotaMode) }}：{{ row.quotaValue }}</span>
            <span>{{ writeoffLabel(row.writeoffMode) }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:rights_template:edit']">编辑</el-button>
            <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无权益模板" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="rightsTemplateId" width="70" />
      <el-table-column label="权益名称" prop="rightsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" width="120">
        <template slot-scope="{ row }">{{ typeLabel(row.rightsType) }}</template>
      </el-table-column>
      <el-table-column label="口径" width="80">
        <template slot-scope="{ row }">{{ quotaModeLabel(row.quotaMode) }}</template>
      </el-table-column>
      <el-table-column label="数值" prop="quotaValue" width="80" />
      <el-table-column label="核销方式" width="120">
        <template slot-scope="{ row }">{{ writeoffLabel(row.writeoffMode) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="150">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleEdit(row)" v-hasPermi="['jst:marketing:rights_template:edit']">编辑</el-button>
          <el-button type="text" size="mini" @click="handleToggle(row)">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
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
        <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '88px' : '100px'">
          <div class="form-section">
            <div class="form-section__title">基本信息</div>
            <el-form-item label="权益名称" prop="rightsName">
              <el-input v-model="form.rightsName" placeholder="请输入权益名称" />
            </el-form-item>
            <el-form-item label="类型" prop="rightsType">
              <el-select v-model="form.rightsType" placeholder="请选择" class="full-width">
                <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </div>

          <el-divider />

          <div class="form-section">
            <div class="form-section__title">配额与核销</div>
            <el-row :gutter="12">
              <el-col :xs="24" :sm="12">
                <el-form-item label="口径" prop="quotaMode">
                  <el-select v-model="form.quotaMode" placeholder="请选择" class="full-width">
                    <el-option label="次数" value="times" />
                    <el-option label="金额" value="amount" />
                    <el-option label="周期" value="period" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="12">
                <el-form-item label="数值" prop="quotaValue">
                  <el-input-number v-model="form.quotaValue" :min="0" :precision="2" controls-position="right" class="full-width" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="核销方式" prop="writeoffMode">
              <el-select v-model="form.writeoffMode" placeholder="请选择" class="full-width">
                <el-option label="线上自动" value="online_auto" />
                <el-option label="人工审核" value="manual_review" />
                <el-option label="线下确认" value="offline_confirm" />
              </el-select>
            </el-form-item>
          </div>

          <el-divider />

          <div class="form-section">
            <div class="form-section__title">有效期与适用</div>
            <el-form-item label="有效天数">
              <el-input-number v-model="form.validDays" :min="0" controls-position="right" class="full-width" />
            </el-form-item>
            <el-form-item label="适用角色">
              <el-select v-model="form.applicableRole" placeholder="请选择" class="full-width">
                <el-option label="学生" value="student" />
                <el-option label="渠道" value="channel" />
                <el-option label="全部" value="both" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </div>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listRightsTemplate, getRightsTemplate, addRightsTemplate, updateRightsTemplate } from '@/api/jst/rights'

const TYPE_OPTIONS = [
  { label: '报名抵扣', value: 'enroll_deduct' },
  { label: '场馆优惠', value: 'venue_reduce' },
  { label: '专属课程', value: 'exclusive_course' },
  { label: '客服优先', value: 'cs_priority' },
  { label: '自定义', value: 'custom' }
]

const QUOTA_MODE_MAP = { times: '次数', amount: '金额', period: '周期' }
const WRITEOFF_MAP = { online_auto: '线上自动', manual_review: '人工审核', offline_confirm: '线下确认' }

export default {
  name: 'JstRightsTemplate',
  data() {
    return {
      loading: false,
      submitLoading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, rightsName: null, rightsType: null, status: null },
      typeOptions: TYPE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        rightsName: [{ required: true, message: '权益名称不能为空', trigger: 'blur' }],
        rightsType: [{ required: true, message: '请选择类型', trigger: 'change' }],
        quotaMode: [{ required: true, message: '请选择口径', trigger: 'change' }],
        quotaValue: [{ required: true, message: '数值不能为空', trigger: 'blur' }],
        writeoffMode: [{ required: true, message: '请选择核销方式', trigger: 'change' }]
      }
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  mounted() {
    // PATCH-2: Picker「查看详情」自动打开目标记录
    const autoId = this.$route.query.rightsTemplateId || this.$route.query.autoOpenId
    if (autoId) {
      this.$nextTick(() => this.handleAutoOpen(Number(autoId)))
    }
  },
  methods: {
    typeLabel(t) { const o = TYPE_OPTIONS.find(x => x.value === t); return o ? o.label : t || '--' },
    async handleAutoOpen(id) {
      if (!id || Number.isNaN(id)) return
      try {
        const res = await getRightsTemplate(id)
        const data = res && (res.data || res)
        if (data && data.rightsTemplateId) {
          this.handleEdit({ rightsTemplateId: data.rightsTemplateId })
        } else {
          this.$modal.msgWarning('未找到指定权益模板，可能已删除')
        }
      } catch (e) {
        this.$modal.msgWarning('未找到指定权益模板，可能已删除')
      }
    },
    quotaModeLabel(m) { return QUOTA_MODE_MAP[m] || m || '--' },
    writeoffLabel(w) { return WRITEOFF_MAP[w] || w || '--' },
    getList() {
      this.loading = true
      listRightsTemplate(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    initForm() {
      return { rightsName: '', rightsType: 'enroll_deduct', quotaMode: 'times', quotaValue: 1, validDays: 365, writeoffMode: 'online_auto', applicableRole: 'student', status: 1 }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增权益模板'
      this.dialogVisible = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleEdit(row) {
      getRightsTemplate(row.rightsTemplateId).then(res => {
        this.form = { ...(res.data || res) }
        this.dialogTitle = '编辑权益模板'
        this.dialogVisible = true
      })
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.form.rightsTemplateId ? updateRightsTemplate : addRightsTemplate
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.rightsTemplateId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    handleToggle(row) {
      const newStatus = row.status === 1 ? 0 : 1
      updateRightsTemplate({ ...row, status: newStatus }).then(() => {
        this.$modal.msgSuccess(newStatus === 1 ? '已启用' : '已停用')
        this.getList()
      })
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

.full-width {
  width: 100%;
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

  .form-section__title {
    font-size: 13px;
    margin: 2px 0 10px;
  }
}
</style>
