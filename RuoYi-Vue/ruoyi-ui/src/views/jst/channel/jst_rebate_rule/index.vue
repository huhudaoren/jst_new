<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">渠道财务</p>
        <h2>返点规则</h2>
        <p class="hero-desc">管理渠道返点规则，支持按比例和固定金额两种模式，可按赛事/渠道灵活配置。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="赛事ID" prop="contestId">
        <el-input v-model="queryParams.contestId" placeholder="请输入赛事ID" clearable @keyup.enter.native="handleQuery" />
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
        <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['jst:channel:rebate_rule:add']">新增</el-button>
      </el-col>
    </el-row>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.ruleId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">规则 #{{ row.ruleId }}</div>
              <div class="mobile-sub">赛事 #{{ row.contestId || '--' }}{{ row.channelId ? ' / 渠道 #' + row.channelId : '' }}</div>
            </div>
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" v-hasPermi="['jst:channel:rebate_rule:edit']" />
          </div>
          <div class="mobile-info-row">
            <el-tag size="small" :type="row.rebateMode === 'rate' ? '' : 'warning'">{{ row.rebateMode === 'rate' ? '按比例' : '固定金额' }}</el-tag>
            <span class="mobile-amount">{{ row.rebateMode === 'rate' ? (Number(row.rebateValue) * 100).toFixed(1) + '%' : formatMoney(row.rebateValue) }}</span>
          </div>
          <div class="mobile-info-row">有效期：{{ parseTime(row.effectiveTime, '{y}-{m}-{d}') || '--' }} ~ {{ parseTime(row.expireTime, '{y}-{m}-{d}') || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="handleEdit(row)" v-hasPermi="['jst:channel:rebate_rule:edit']">编辑</el-button>
            <el-button type="text" class="danger-text" @click="handleDelete(row)" v-hasPermi="['jst:channel:rebate_rule:remove']">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无返点规则" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="规则ID" prop="ruleId" width="80" />
      <el-table-column label="赛事ID" prop="contestId" width="90" />
      <el-table-column label="渠道ID" prop="channelId" width="90">
        <template slot-scope="scope">{{ scope.row.channelId || '默认' }}</template>
      </el-table-column>
      <el-table-column label="返点模式" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="scope.row.rebateMode === 'rate' ? '' : 'warning'">{{ scope.row.rebateMode === 'rate' ? '按比例' : '固定金额' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="返点值" min-width="100" align="right">
        <template slot-scope="scope">
          <strong>{{ scope.row.rebateMode === 'rate' ? (Number(scope.row.rebateValue) * 100).toFixed(1) + '%' : formatMoney(scope.row.rebateValue) }}</strong>
        </template>
      </el-table-column>
      <el-table-column label="服务费模式" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ serviceFeeLabel(scope.row.serviceFeeMode) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="200">
        <template slot-scope="scope">{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') || '--' }} ~ {{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" v-hasPermi="['jst:channel:rebate_rule:edit']" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)" v-hasPermi="['jst:channel:rebate_rule:edit']">编辑</el-button>
          <el-button type="text" class="danger-text" @click="handleDelete(scope.row)" v-hasPermi="['jst:channel:rebate_rule:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '95%' : '600px'" append-to-body>
      <el-form ref="editForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="赛事ID" prop="contestId">
          <el-input v-model="form.contestId" placeholder="请输入赛事ID" />
        </el-form-item>
        <el-form-item label="渠道ID" prop="channelId">
          <el-input v-model="form.channelId" placeholder="留空为默认规则" />
        </el-form-item>
        <el-form-item label="返点模式" prop="rebateMode">
          <el-select v-model="form.rebateMode" placeholder="请选择" style="width: 100%">
            <el-option label="按比例" value="rate" />
            <el-option label="固定金额" value="fixed" />
          </el-select>
        </el-form-item>
        <el-form-item label="返点值" prop="rebateValue">
          <el-input v-model="form.rebateValue" :placeholder="form.rebateMode === 'rate' ? '如 0.1 表示 10%' : '固定金额（元）'" />
        </el-form-item>
        <el-form-item label="服务费模式" prop="serviceFeeMode">
          <el-select v-model="form.serviceFeeMode" placeholder="请选择" style="width: 100%">
            <el-option label="无" value="none" />
            <el-option label="固定金额" value="fixed" />
            <el-option label="按比例" value="rate" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.serviceFeeMode !== 'none'" label="服务费值" prop="serviceFeeValue">
          <el-input v-model="form.serviceFeeValue" placeholder="请输入服务费值" />
        </el-form-item>
        <el-form-item label="有效期">
          <el-date-picker v-model="dateRange" type="daterange" value-format="yyyy-MM-dd" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 100%" />
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
import { listJst_rebate_rule, getJst_rebate_rule, addJst_rebate_rule, updateJst_rebate_rule, delJst_rebate_rule } from '@/api/jst/channel/jst_rebate_rule'

export default {
  name: 'RebateRuleManage',
  data() {
    return {
      loading: false,
      submitLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, contestId: undefined, status: undefined },
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      dateRange: [],
      rules: {
        contestId: [{ required: true, message: '请输入赛事ID', trigger: 'blur' }],
        rebateMode: [{ required: true, message: '请选择返点模式', trigger: 'change' }],
        rebateValue: [{ required: true, message: '请输入返点值', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.updateViewport()
    window.addEventListener('resize', this.updateViewport)
    this.getList()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateViewport)
  },
  methods: {
    updateViewport() { this.isMobile = window.innerWidth <= 768 },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_rebate_rule(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, contestId: undefined, status: undefined }
      this.getList()
    },
    handleAdd() {
      this.dialogTitle = '新增返点规则'
      this.form = { rebateMode: 'rate', serviceFeeMode: 'none', status: 1 }
      this.dateRange = []
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.editForm && this.$refs.editForm.clearValidate())
    },
    handleEdit(row) {
      this.dialogTitle = '编辑返点规则'
      this.form = { ...row }
      this.dateRange = row.effectiveTime && row.expireTime ? [row.effectiveTime, row.expireTime] : []
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.editForm && this.$refs.editForm.clearValidate())
    },
    async submitForm() {
      await this.$refs.editForm.validate()
      this.submitLoading = true
      try {
        if (this.dateRange && this.dateRange.length === 2) {
          this.form.effectiveTime = this.dateRange[0]
          this.form.expireTime = this.dateRange[1]
        }
        if (this.form.ruleId) {
          await updateJst_rebate_rule(this.form)
          this.$modal.msgSuccess('修改成功')
        } else {
          await addJst_rebate_rule(this.form)
          this.$modal.msgSuccess('新增成功')
        }
        this.dialogVisible = false
        this.getList()
      } finally { this.submitLoading = false }
    },
    async handleStatusChange(row) {
      try {
        await updateJst_rebate_rule({ ruleId: row.ruleId, status: row.status })
        this.$modal.msgSuccess(row.status === 1 ? '已启用' : '已停用')
      } catch (_) {
        row.status = row.status === 1 ? 0 : 1
      }
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除该返点规则？').then(() => {
        return delJst_rebate_rule(row.ruleId)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    formatMoney(v) { return '\u00a5' + Number(v || 0).toFixed(2) },
    serviceFeeLabel(mode) {
      return { fixed: '固定', rate: '按比例', none: '无' }[mode] || mode || '--'
    }
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
.mobile-amount { font-size: 18px; font-weight: 700; color: #172033; margin-left: 8px; }
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
