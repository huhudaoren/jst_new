<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">风控中心</p>
        <h2>风控规则</h2>
        <p class="hero-desc">管理平台风控规则，配置阈值与触发动作，按维度和类型灵活启停。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="规则名" prop="ruleName">
        <el-input v-model="queryParams.ruleName" placeholder="请输入规则名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="维度" prop="dimension">
        <el-select v-model="queryParams.dimension" placeholder="全部" clearable>
          <el-option label="用户" value="user" />
          <el-option label="设备" value="device" />
          <el-option label="手机" value="mobile" />
          <el-option label="渠道" value="channel" />
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

    <el-row :gutter="10" class="mb8 action-bar">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['jst:risk:rule:add']">新增</el-button>
      </el-col>
    </el-row>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.riskRuleId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.ruleName || '--' }}</div>
              <div class="mobile-sub">{{ ruleTypeLabel(row.ruleType) }} / {{ dimensionLabel(row.dimension) }}</div>
            </div>
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" v-hasPermi="['jst:risk:rule:edit']" />
          </div>
          <div class="mobile-info-row">
            <el-tag size="mini" :type="actionType(row.action)">{{ actionLabel(row.action) }}</el-tag>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="handleEdit(row)" v-hasPermi="['jst:risk:rule:edit']">编辑</el-button>
            <el-button type="text" class="danger-text" @click="handleDelete(row)" v-hasPermi="['jst:risk:rule:remove']">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无风控规则" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="riskRuleId" width="70" />
      <el-table-column label="规则名" prop="ruleName" min-width="160" show-overflow-tooltip />
      <el-table-column label="规则类型" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ ruleTypeLabel(scope.row.ruleType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维度" min-width="90">
        <template slot-scope="scope">
          <el-tag size="small">{{ dimensionLabel(scope.row.dimension) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="触发动作" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="actionType(scope.row.action)">{{ actionLabel(scope.row.action) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" v-hasPermi="['jst:risk:rule:edit']" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)" v-hasPermi="['jst:risk:rule:edit']">编辑</el-button>
          <el-button type="text" class="danger-text" @click="handleDelete(scope.row)" v-hasPermi="['jst:risk:rule:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '95%' : '640px'" append-to-body>
      <el-form ref="editForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名" />
        </el-form-item>
        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="form.ruleType" placeholder="请选择" style="width:100%">
            <el-option v-for="t in ruleTypes" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="维度" prop="dimension">
          <el-select v-model="form.dimension" placeholder="请选择" style="width:100%">
            <el-option label="用户" value="user" />
            <el-option label="设备" value="device" />
            <el-option label="手机" value="mobile" />
            <el-option label="渠道" value="channel" />
          </el-select>
        </el-form-item>
        <el-form-item label="触发动作" prop="action">
          <el-select v-model="form.action" placeholder="请选择" style="width:100%">
            <el-option label="告警" value="warn" />
            <el-option label="拦截" value="intercept" />
            <el-option label="人工审核" value="manual" />
          </el-select>
        </el-form-item>
        <el-form-item label="阈值配置" prop="thresholdJson">
          <el-input v-model="form.thresholdJson" type="textarea" :rows="4" placeholder='JSON 格式，如 {"max_count":5,"window_minutes":60}' />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listJst_risk_rule, getJst_risk_rule, addJst_risk_rule, updateJst_risk_rule, delJst_risk_rule } from '@/api/jst/risk/jst_risk_rule'

const RULE_TYPES = [
  { value: 'bind_freq', label: '绑定频率' },
  { value: 'coupon_freq', label: '优惠券频率' },
  { value: 'refund_freq', label: '退款频率' },
  { value: 'rebate_anomaly', label: '返点异常' },
  { value: 'zero_order_freq', label: '零元订单频率' },
  { value: 'aftersale_anomaly', label: '售后异常' }
]

export default {
  name: 'RiskRuleManage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, ruleName: undefined, dimension: undefined, status: undefined },
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      ruleTypes: RULE_TYPES,
      rules: {
        ruleName: [{ required: true, message: '请输入规则名', trigger: 'blur' }],
        ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
        dimension: [{ required: true, message: '请选择维度', trigger: 'change' }],
        action: [{ required: true, message: '请选择触发动作', trigger: 'change' }]
      }
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() { window.removeEventListener('resize', this.updateViewport) },
  methods: {
    updateViewport() { this.isMobile = window.innerWidth <= 768 },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_risk_rule(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, ruleName: undefined, dimension: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.dialogTitle = '新增风控规则'
      this.form = { status: 1, action: 'warn', thresholdJson: '' }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.editForm && this.$refs.editForm.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑风控规则'
      this.form = { ...row }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.editForm && this.$refs.editForm.clearValidate())
    },
    async submitForm() {
      await this.$refs.editForm.validate()
      this.submitLoading = true
      try {
        if (this.form.riskRuleId) {
          await updateJst_risk_rule(this.form)
          this.$modal.msgSuccess('修改成功')
        } else {
          await addJst_risk_rule(this.form)
          this.$modal.msgSuccess('新增成功')
        }
        this.dialogVisible = false
        this.getList()
      } finally { this.submitLoading = false }
    },
    async handleStatusChange(row) {
      try {
        await updateJst_risk_rule({ riskRuleId: row.riskRuleId, status: row.status })
        this.$modal.msgSuccess(row.status === 1 ? '已启用' : '已停用')
      } catch (_) { row.status = row.status === 1 ? 0 : 1 }
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该风控规则？').then(() => delJst_risk_rule(row.riskRuleId)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    ruleTypeLabel(t) { const found = RULE_TYPES.find(r => r.value === t); return found ? found.label : t || '--' },
    dimensionLabel(d) { return { user: '用户', device: '设备', mobile: '手机', channel: '渠道' }[d] || d || '--' },
    actionLabel(a) { return { warn: '告警', intercept: '拦截', manual: '人工审核' }[a] || a || '--' },
    actionType(a) { return { warn: 'warning', intercept: 'danger', manual: 'info' }[a] || 'info' }
  }
}
</script>

<style scoped>
.enhanced-page { background: #f6f8fb; min-height: calc(100vh - 84px); }
.page-hero { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 24px; margin-bottom: 18px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.hero-eyebrow { margin: 0 0 8px; color: #2f6fec; font-size: 13px; font-weight: 600; }
.page-hero h2 { margin: 0; font-size: 24px; font-weight: 700; color: #172033; }
.hero-desc { margin: 8px 0 0; color: #6f7b8f; }
.query-panel { padding: 16px 16px 0; margin-bottom: 16px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.action-bar { padding: 0 4px; margin-bottom: 12px; }
.danger-text { color: #f56c6c; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; display: flex; align-items: center; gap: 8px; }
.mobile-actions { margin-top: 12px; display: flex; gap: 12px; border-top: 1px solid #f0f2f5; padding-top: 12px; }
@media (max-width: 768px) {
  .enhanced-page { padding: 12px; }
  .page-hero { display: block; padding: 18px; }
  .page-hero .el-button { width: 100%; min-height: 44px; margin-top: 16px; }
  .page-hero h2 { font-size: 20px; }
  .query-panel { padding-bottom: 8px; }
  .query-panel ::v-deep .el-form-item { display: block; margin-right: 0; }
  .query-panel ::v-deep .el-form-item__content, .query-panel ::v-deep .el-select, .query-panel ::v-deep .el-input { width: 100%; }
}
</style>
