<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="queryParams.templateCode" placeholder="请输入模板编码" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="发送渠道" prop="channel">
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
      <el-form-item label="发送时间" prop="sendTime">
        <el-date-picker v-model="queryParams.sendTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="精确时间" clearable style="width:190px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.logId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.templateCode }}</span>
            <el-tag size="mini" :type="statusType(row.sendStatus)">{{ statusLabel(row.sendStatus) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>{{ channelLabel(row.channel) }}</span>
            <span>用户：{{ row.targetUserId || '--' }}</span>
            <span>手机号：{{ row.targetMobile || '--' }}</span>
            <span>{{ parseTime(row.sendTime) || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无消息日志" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="日志ID" prop="logId" width="90" />
      <el-table-column label="模板编码" prop="templateCode" min-width="130" />
      <el-table-column label="发送渠道" min-width="110">
        <template slot-scope="{ row }">{{ channelLabel(row.channel) }}</template>
      </el-table-column>
      <el-table-column label="用户ID" prop="targetUserId" min-width="90" />
      <el-table-column label="手机号" prop="targetMobile" min-width="120" />
      <el-table-column label="触发来源" prop="triggerSource" min-width="140" show-overflow-tooltip />
      <el-table-column label="发送状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.sendStatus)">{{ statusLabel(row.sendStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.sendTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'消息日志详情 #' + (detailData.logId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '560px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="日志ID">{{ detailData.logId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="模板编码">{{ detailData.templateCode || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发送渠道">{{ channelLabel(detailData.channel) }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ detailData.targetUserId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailData.targetMobile || '--' }}</el-descriptions-item>
          <el-descriptions-item label="触发来源">{{ detailData.triggerSource || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发送状态">
            <el-tag size="small" :type="statusType(detailData.sendStatus)">{{ statusLabel(detailData.sendStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ parseTime(detailData.sendTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="失败原因">{{ detailData.failReason || '--' }}</el-descriptions-item>
          <el-descriptions-item label="消息内容">
            <pre class="content-block">{{ detailData.content || '--' }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
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
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateCode: null,
        channel: null,
        sendStatus: null,
        targetUserId: null,
        sendTime: null
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
  computed: {
    isMobile() {
      return this.$store.state.app.device === 'mobile'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
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
    getList() {
      this.loading = true
      listJst_message_log(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    handleDetail(row) {
      getJst_message_log(row.logId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    }
  }
}
</script>

<style scoped>
.detail-body {
  padding: 0 16px 16px;
}

.content-block {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
  line-height: 1.5;
}

.mobile-card-list {
  padding: 0 4px;
}

.mobile-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px 14px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.mobile-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.mobile-card__title {
  font-weight: 600;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 65%;
}

.mobile-card__meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.mobile-card__actions {
  display: flex;
  gap: 6px;
}
</style>
