<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">消息中心</p>
        <h2>消息模板</h2>
        <p class="hero-desc">管理系统消息模板</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="queryParams.templateCode" placeholder="请输入模板编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送通道" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="全部" clearable>
          <el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd" v-hasPermi="['jst:message:message_template:add']">新增</el-button>
      </el-col>
    </el-row>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.templateId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.templateName }}</div>
              <div class="mobile-sub">{{ row.templateCode }} / {{ channelLabel(row.channel) }} / {{ sceneLabel(row.scene) }}</div>
            </div>
            <el-tag size="mini" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </div>
          <div v-if="row.content" class="mobile-info-row tpl-content-preview">
            <template v-for="(seg, idx) in parseTemplateVars(row.content)">
              <el-tag v-if="seg.isVar" :key="'v'+idx" size="mini" type="warning" class="tpl-var-tag">{{ seg.text }}</el-tag>
              <span v-else :key="'t'+idx">{{ seg.text }}</span>
            </template>
          </div>
          <div class="mobile-actions">
            <el-button type="text" @click="handleEdit(row)" v-hasPermi="['jst:message:message_template:edit']">编辑</el-button>
            <el-button type="text" @click="handleToggle(row)" v-hasPermi="['jst:message:message_template:edit']">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息模板" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="templateId" width="70" />
      <el-table-column label="模板编码" prop="templateCode" min-width="140" show-overflow-tooltip />
      <el-table-column label="模板名称" prop="templateName" min-width="140" show-overflow-tooltip />
      <el-table-column label="发送通道" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ channelLabel(scope.row.channel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="业务场景" min-width="120">
        <template slot-scope="scope">{{ sceneLabel(scope.row.scene) }}</template>
      </el-table-column>
      <el-table-column label="模板内容" min-width="280">
        <template slot-scope="scope">
          <div class="tpl-content-preview">
            <template v-for="(seg, idx) in parseTemplateVars(scope.row.content)">
              <el-tag v-if="seg.isVar" :key="'v'+idx" size="mini" type="warning" class="tpl-var-tag">{{ seg.text }}</el-tag>
              <span v-else :key="'t'+idx">{{ seg.text }}</span>
            </template>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(scope.row)"
            v-hasPermi="['jst:message:message_template:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="scope">
          <el-button type="text" @click="handleEdit(scope.row)" v-hasPermi="['jst:message:message_template:edit']">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" :width="isMobile ? '95%' : '680px'" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" :label-width="isMobile ? '84px' : '100px'">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="模板编码" prop="templateCode">
              <el-input v-model="form.templateCode" placeholder="例如：POINTS_CHANGE" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12">
            <el-form-item label="发送通道" prop="channel">
              <el-select v-model="form.channel" placeholder="请选择" class="full-width">
                <el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="业务场景" prop="scene">
              <el-select v-model="form.scene" placeholder="请选择" class="full-width">
                <el-option v-for="item in sceneOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="模板内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入模板内容，支持 ${变量名} 占位符" />
        </el-form-item>
        <div v-if="form.content" class="tpl-preview-box">
          <div class="tpl-preview-label">内容预览</div>
          <div class="tpl-content-preview">
            <template v-for="(seg, idx) in parseTemplateVars(form.content)">
              <el-tag v-if="seg.isVar" :key="'v'+idx" size="mini" type="warning" class="tpl-var-tag">{{ seg.text }}</el-tag>
              <span v-else :key="'t'+idx">{{ seg.text }}</span>
            </template>
          </div>
        </div>
        <el-alert title="变量占位符示例：${userName}、${pointsChange}、${orderNo}" type="info" :closable="false" style="margin-bottom: 12px" />
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listJst_message_template, getJst_message_template, addJst_message_template, updateJst_message_template } from '@/api/jst/message/jst_message_template'

const CHANNEL_OPTIONS = [
  { label: '站内信', value: 'inner' },
  { label: '短信', value: 'sms' },
  { label: '微信模板消息', value: 'wechat_template' },
  { label: '微信订阅消息', value: 'wechat' }
]

const SCENE_OPTIONS = [
  { label: '认证结果', value: 'auth_result' },
  { label: '提现结果', value: 'withdraw_result' },
  { label: '结算结果', value: 'settle_result' },
  { label: '积分变动', value: 'points_change' },
  { label: '发货通知', value: 'ship' },
  { label: '售后通知', value: 'aftersale' }
]

export default {
  name: 'JstMessageTemplate',
  data() {
    return {
      loading: false,
      submitLoading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateCode: null,
        templateName: null,
        channel: null,
        status: null
      },
      channelOptions: CHANNEL_OPTIONS,
      sceneOptions: SCENE_OPTIONS,
      dialogVisible: false,
      dialogTitle: '',
      form: {},
      formRules: {
        templateCode: [{ required: true, message: '模板编码不能为空', trigger: 'blur' }],
        templateName: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
        channel: [{ required: true, message: '请选择发送通道', trigger: 'change' }],
        scene: [{ required: true, message: '请选择业务场景', trigger: 'change' }],
        content: [{ required: true, message: '模板内容不能为空', trigger: 'blur' }]
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
    channelLabel(channel) {
      const match = CHANNEL_OPTIONS.find(item => item.value === channel)
      return match ? match.label : channel || '--'
    },
    sceneLabel(scene) {
      const match = SCENE_OPTIONS.find(item => item.value === scene)
      return match ? match.label : scene || '--'
    },
    parseTemplateVars(content) {
      if (!content) return []
      const parts = []
      const regex = /\$\{(\w+)\}/g
      let lastIndex = 0
      let match
      while ((match = regex.exec(content)) !== null) {
        if (match.index > lastIndex) {
          parts.push({ text: content.substring(lastIndex, match.index), isVar: false })
        }
        parts.push({ text: '${' + match[1] + '}', isVar: true })
        lastIndex = regex.lastIndex
      }
      if (lastIndex < content.length) {
        parts.push({ text: content.substring(lastIndex), isVar: false })
      }
      return parts
    },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_message_template(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, templateCode: null, templateName: null, channel: null, status: null }
      this.getList()
    },
    initForm() {
      return {
        templateId: null,
        templateCode: '',
        templateName: '',
        channel: 'inner',
        scene: 'points_change',
        content: '',
        status: 1,
        remark: ''
      }
    },
    handleAdd() {
      this.form = this.initForm()
      this.dialogTitle = '新增消息模板'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    async handleEdit(row) {
      try {
        const res = await getJst_message_template(row.templateId)
        this.form = { ...this.initForm(), ...(res.data || res || row) }
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.form = { ...this.initForm(), ...row }
      }
      this.dialogTitle = '编辑消息模板'
      this.dialogVisible = true
    },
    handleToggle(row) {
      const status = row.status === 1 ? 0 : 1
      this.updateStatus({ ...row, status })
    },
    handleStatusChange(row) {
      this.updateStatus(row)
    },
    async updateStatus(payload) {
      try {
        await updateJst_message_template(payload)
        this.$modal.msgSuccess('状态更新成功')
        this.getList()
      } catch (e) {
        this.$modal.msgError('状态更新失败')
        this.getList()
      }
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const api = this.form.templateId ? updateJst_message_template : addJst_message_template
        api(this.form).then(() => {
          this.$modal.msgSuccess(this.form.templateId ? '修改成功' : '新增成功')
          this.dialogVisible = false
          this.getList()
        }).catch(() => {
          this.$modal.msgError('操作失败')
        }).finally(() => {
          this.submitLoading = false
        })
      })
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
.full-width { width: 100%; }
.tpl-content-preview { line-height: 1.8; font-size: 13px; color: #606266; }
.tpl-var-tag { margin: 0 2px; vertical-align: baseline; }
.tpl-preview-box { background: #f6f8fb; border: 1px solid #e5eaf2; border-radius: 6px; padding: 12px; margin-bottom: 12px; }
.tpl-preview-label { font-size: 12px; color: #909399; margin-bottom: 6px; }
.drawer-body { padding: 20px; }
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; }
.mobile-actions { margin-top: 12px; border-top: 1px solid #f0f2f5; padding-top: 12px; display: flex; gap: 12px; }
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
