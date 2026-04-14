<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">消息中心</p>
        <h2>消息发送记录</h2>
        <p class="hero-desc">查看消息发送历史</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="queryParams.templateCode" placeholder="请输入模板编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送通道" prop="channel">
        <el-select v-model="queryParams.channel" placeholder="全部" clearable>
          <el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="发送状态" prop="sendStatus">
        <el-select v-model="queryParams.sendStatus" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID" prop="targetUserId">
        <el-input v-model="queryParams.targetUserId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.logId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">{{ row.templateCode || '--' }}</div>
              <div class="mobile-sub">{{ channelLabel(row.channel) }} / 用户 #{{ row.targetUserId || '--' }}</div>
            </div>
            <el-tag size="mini" :type="statusType(row.sendStatus)">{{ statusLabel(row.sendStatus) }}</el-tag>
          </div>
          <div class="mobile-info-row">{{ row.targetMobile || '--' }}</div>
          <div class="mobile-info-row">{{ parseTime(row.sendTime, '{y}-{m}-{d} {h}:{i}') || '--' }}</div>
          <div class="mobile-actions">
            <el-button type="text" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息日志" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="logId" width="70" />
      <el-table-column label="模板编码" prop="templateCode" min-width="130" show-overflow-tooltip />
      <el-table-column label="发送通道" min-width="110">
        <template slot-scope="scope">
          <el-tag size="small" type="info">{{ channelLabel(scope.row.channel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户ID" prop="targetUserId" min-width="90" />
      <el-table-column label="手机号" prop="targetMobile" min-width="120" />
      <el-table-column label="触发来源" prop="triggerSource" min-width="140" show-overflow-tooltip />
      <el-table-column label="发送状态" width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.sendStatus)">{{ statusLabel(scope.row.sendStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" min-width="150">
        <template slot-scope="scope">{{ parseTime(scope.row.sendTime, '{y}-{m}-{d} {h}:{i}') || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="80">
        <template slot-scope="scope">
          <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情抽屉 -->
    <el-drawer :title="'消息日志详情 #' + (detailData.logId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" append-to-body>
      <div v-if="detailData.logId" class="drawer-body">
        <el-descriptions :column="isMobile ? 1 : 2" border>
          <el-descriptions-item label="日志ID">{{ detailData.logId }}</el-descriptions-item>
          <el-descriptions-item label="模板编码">{{ detailData.templateCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发送通道">
            <el-tag size="small" type="info">{{ channelLabel(detailData.channel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detailData.targetUserId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailData.targetMobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="触发来源">{{ detailData.triggerSource || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发送状态">
            <el-tag size="small" :type="statusType(detailData.sendStatus)">{{ statusLabel(detailData.sendStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ parseTime(detailData.sendTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item v-if="detailData.failReason" label="失败原因" :span="2">
            <span style="color: #f56c6c">{{ detailData.failReason }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="消息内容" :span="2">
            <div class="tpl-content-preview">
              <template v-for="(seg, idx) in parseTemplateVars(detailData.content)">
                <el-tag v-if="seg.isVar" :key="'v'+idx" size="mini" type="warning" class="tpl-var-tag">{{ seg.text }}</el-tag>
                <span v-else :key="'t'+idx">{{ seg.text }}</span>
              </template>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listJst_message_log, getJst_message_log } from '@/api/jst/message/jst_message_log'

const CHANNEL_OPTIONS = [
  { label: '站内信', value: 'inner' },
  { label: '短信', value: 'sms' },
  { label: '微信消息', value: 'wechat' },
  { label: '微信模板消息', value: 'wechat_template' }
]

const STATUS_META = {
  pending: { label: '待发送', type: 'warning' },
  sent: { label: '发送成功', type: 'success' },
  success: { label: '发送成功', type: 'success' },
  failed: { label: '发送失败', type: 'danger' }
}

export default {
  name: 'JstMessageLog',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateCode: null,
        channel: null,
        sendStatus: null,
        targetUserId: null
      },
      detailVisible: false,
      detailData: {},
      channelOptions: CHANNEL_OPTIONS,
      statusOptions: [
        { label: '待发送', value: 'pending' },
        { label: '发送成功', value: 'success' },
        { label: '发送失败', value: 'failed' }
      ]
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
    statusType(status) {
      return (STATUS_META[status] || {}).type || 'info'
    },
    statusLabel(status) {
      return (STATUS_META[status] || {}).label || status || '--'
    },
    parseTemplateVars(content) {
      if (!content) return [{ text: '--', isVar: false }]
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
      return parts.length ? parts : [{ text: content, isVar: false }]
    },
    async getList() {
      this.loading = true
      try {
        const res = await listJst_message_log(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, templateCode: null, channel: null, sendStatus: null, targetUserId: null }
      this.getList()
    },
    async handleDetail(row) {
      this.detailVisible = true
      this.detailData = {}
      try {
        const res = await getJst_message_log(row.logId)
        this.detailData = res.data || res || row
      } catch (e) {
        this.$modal.msgError('加载详情失败')
        this.detailData = row
      }
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
.drawer-body { padding: 20px; }
.tpl-content-preview { line-height: 1.8; font-size: 13px; color: #606266; }
.tpl-var-tag { margin: 0 2px; vertical-align: baseline; }
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
