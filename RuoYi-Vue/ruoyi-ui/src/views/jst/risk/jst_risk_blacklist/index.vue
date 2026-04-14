<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">风控中心</p>
        <h2>黑白名单</h2>
        <p class="hero-desc">管理风控黑白名单</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="名单类型" prop="listType">
        <el-select v-model="queryParams.listType" placeholder="全部" clearable>
          <el-option v-for="item in dict.type.jst_list_type" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标类型" prop="targetType">
        <el-select v-model="queryParams.targetType" placeholder="全部" clearable>
          <el-option label="用户" value="user" />
          <el-option label="设备" value="device" />
          <el-option label="手机" value="mobile" />
          <el-option label="渠道" value="channel" />
          <el-option label="地址" value="address" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标值" prop="targetValue">
        <el-input v-model="queryParams.targetValue" placeholder="请输入目标值" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 action-bar">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['jst:risk:blacklist:add']">添加</el-button>
      </el-col>
    </el-row>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.listId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.targetValue || '--' }}</div>
              <div class="mobile-sub">{{ targetTypeLabel(row.targetType) }}</div>
            </div>
            <dict-tag :options="dict.type.jst_list_type" :value="row.listType" />
          </div>
          <div class="mobile-info-row">{{ row.reason || '--' }}</div>
          <div class="mobile-info-row">有效期：{{ parseTime(row.effectiveTime, '{y}-{m}-{d}') || '--' }} ~ {{ parseTime(row.expireTime, '{y}-{m}-{d}') || '永久' }}</div>
          <div class="mobile-actions">
            <el-popconfirm title="确认移除该记录？" @confirm="handleDelete(row)">
              <el-button slot="reference" type="text" class="danger-text" v-hasPermi="['jst:risk:blacklist:remove']">移除</el-button>
            </el-popconfirm>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无名单记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="listId" width="70" />
      <el-table-column label="名单类型" min-width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_list_type" :value="scope.row.listType" />
        </template>
      </el-table-column>
      <el-table-column label="目标类型" min-width="90">
        <template slot-scope="scope">{{ targetTypeLabel(scope.row.targetType) }}</template>
      </el-table-column>
      <el-table-column label="目标值" prop="targetValue" min-width="160" show-overflow-tooltip />
      <el-table-column label="原因" prop="reason" min-width="200" show-overflow-tooltip />
      <el-table-column label="生效时间" min-width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.effectiveTime, '{y}-{m}-{d}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="过期时间" min-width="120">
        <template slot-scope="scope">{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') || '永久' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '生效' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template slot-scope="scope">
          <el-popconfirm title="确认移除该记录？" @confirm="handleDelete(scope.row)">
            <el-button slot="reference" type="text" class="danger-text" v-hasPermi="['jst:risk:blacklist:remove']">移除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加弹窗 -->
    <el-dialog title="添加名单" :visible.sync="dialogVisible" :width="isMobile ? '95%' : '560px'" append-to-body>
      <el-form ref="addForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名单类型" prop="listType">
          <el-select v-model="form.listType" class="full-width">
            <el-option v-for="item in dict.type.jst_list_type" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标类型" prop="targetType">
          <el-select v-model="form.targetType" class="full-width">
            <el-option label="用户" value="user" />
            <el-option label="设备" value="device" />
            <el-option label="手机" value="mobile" />
            <el-option label="渠道" value="channel" />
            <el-option label="地址" value="address" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标值" prop="targetValue">
          <el-input v-model="form.targetValue" placeholder="用户ID / 设备ID / 手机号等" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" :rows="2" placeholder="请输入原因" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="form.expireTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="留空为永久" class="full-width" />
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
import { listJst_risk_blacklist, addJst_risk_blacklist, delJst_risk_blacklist } from '@/api/jst/risk/jst_risk_blacklist'

export default {
  name: 'RiskBlacklistManage',
  dicts: ['jst_list_type'],
  data() {
    return {
      loading: false,
      submitLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, listType: undefined, targetType: undefined, targetValue: undefined },
      dialogVisible: false,
      form: {},
      rules: {
        listType: [{ required: true, message: '请选择名单类型', trigger: 'change' }],
        targetType: [{ required: true, message: '请选择目标类型', trigger: 'change' }],
        targetValue: [{ required: true, message: '请输入目标值', trigger: 'blur' }]
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
        const res = await listJst_risk_blacklist(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, listType: undefined, targetType: undefined, targetValue: undefined }
      this.getList()
    },
    handleAdd() {
      this.form = { listType: 'black', targetType: 'user', status: 1 }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.addForm && this.$refs.addForm.clearValidate())
    },
    async submitForm() {
      await this.$refs.addForm.validate()
      this.submitLoading = true
      try {
        await addJst_risk_blacklist(this.form)
        this.$modal.msgSuccess('添加成功')
        this.dialogVisible = false
        this.getList()
      } finally { this.submitLoading = false }
    },
    async handleDelete(row) {
      try {
        await delJst_risk_blacklist(row.listId)
        this.$modal.msgSuccess('移除成功')
        this.getList()
      } catch (_) {}
    },
    targetTypeLabel(t) { return { user: '用户', device: '设备', mobile: '手机', channel: '渠道', address: '地址' }[t] || t || '--' }
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
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; }
.mobile-actions { margin-top: 12px; border-top: 1px solid #f0f2f5; padding-top: 12px; }
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
