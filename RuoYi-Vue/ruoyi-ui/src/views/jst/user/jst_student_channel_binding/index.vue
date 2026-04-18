<template>
  <div class="app-container enhanced-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">用户管理</p>
        <h2>学生绑定关系</h2>
        <p class="hero-desc">查看学生与渠道方的绑定关系，按学生、渠道、状态筛选。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="学生" prop="userId">
        <user-picker v-model="queryParams.userId" clearable />
      </el-form-item>
      <el-form-item label="渠道" prop="channelId">
        <channel-picker v-model="queryParams.channelId" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 手机端卡片 -->
    <div v-if="isMobile" v-loading="loading" class="mobile-list">
      <div v-if="list.length">
        <div v-for="row in list" :key="row.bindingId" class="mobile-card">
          <div class="mobile-card-top">
            <div>
              <div class="mobile-title">
                学生：
                <el-link v-if="row.userId && row.studentName" type="primary" :underline="false" @click="goUser(row)">
                  {{ row.studentName }}
                </el-link>
                <span v-else>{{ row.studentName || row.userId || '--' }}</span>
                -
                渠道：
                <el-link v-if="row.channelId && row.channelName" type="primary" :underline="false" @click="goChannel(row)">
                  {{ row.channelName }}
                </el-link>
                <span v-else>{{ row.channelName || row.channelId || '--' }}</span>
              </div>
              <div class="mobile-sub">绑定：{{ parseTime(row.bindTime, '{y}-{m}-{d}') || '--' }}</div>
            </div>
            <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div v-if="row.unbindTime" class="mobile-info-row">解绑：{{ parseTime(row.unbindTime, '{y}-{m}-{d}') }}</div>
          <div v-if="row.unbindReason" class="mobile-info-row">原因：{{ row.unbindReason }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无绑定记录" :image-size="96" />
    </div>

    <!-- PC 端表格 -->
    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="bindingId" width="70" />
      <el-table-column label="学生" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="scope.row.userId && scope.row.studentName" type="primary" :underline="false" @click="goUser(scope.row)">
            {{ scope.row.studentName }}
          </el-link>
          <span v-else>{{ scope.row.studentName || scope.row.userId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="渠道" min-width="130" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-link v-if="scope.row.channelId && scope.row.channelName" type="primary" :underline="false" @click="goChannel(scope.row)">
            {{ scope.row.channelName }}
          </el-link>
          <span v-else>{{ scope.row.channelName || scope.row.channelId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="绑定时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.bindTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="解绑时间" min-width="160">
        <template slot-scope="scope">{{ parseTime(scope.row.unbindTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template slot-scope="scope">
          <el-tag size="small" :type="statusType(scope.row.status)">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="解绑原因" prop="unbindReason" min-width="200" show-overflow-tooltip />
      <el-table-column label="操作人" prop="operatorId" width="80">
        <template slot-scope="scope">{{ scope.row.operatorId || '--' }}</template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script>
import { listJst_student_channel_binding } from '@/api/jst/user/jst_student_channel_binding'

const STATUS_META = {
  active: { label: '生效中', type: 'success' },
  expired: { label: '已过期', type: 'info' },
  replaced: { label: '已替换', type: 'warning' },
  manual_unbound: { label: '手动解绑', type: 'danger' }
}

export default {
  name: 'StudentChannelBindingManage',
  data() {
    return {
      loading: false,
      isMobile: false,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, userId: undefined, channelId: undefined, status: undefined },
      statusOptions: Object.entries(STATUS_META).map(([value, { label }]) => ({ value, label }))
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
        const res = await listJst_student_channel_binding(this.queryParams)
        this.list = res.rows || []
        this.total = res.total || 0
      } finally { this.loading = false }
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, userId: undefined, channelId: undefined, status: undefined }
      this.getList()
    },
    goUser(row) {
      const userId = row && row.userId
      if (!userId) return
      this.$router.push({
        path: '/jst/user',
        query: { userId: String(userId), autoOpen: '1' }
      }).catch(() => {})
    },
    goChannel(row) {
      const channelId = row && row.channelId
      if (!channelId) return
      this.$router.push({
        path: '/jst/channel',
        query: { channelId: String(channelId), autoOpen: '1' }
      }).catch(() => {})
    },
    statusLabel(s) { return (STATUS_META[s] && STATUS_META[s].label) || s || '--' },
    statusType(s) { return (STATUS_META[s] && STATUS_META[s].type) || 'info' }
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
.mobile-list { min-height: 180px; }
.mobile-card { padding: 16px; margin-bottom: 12px; background: #fff; border: 1px solid #e5eaf2; border-radius: 8px; }
.mobile-card-top { display: flex; justify-content: space-between; align-items: flex-start; gap: 12px; }
.mobile-title { font-weight: 700; color: #172033; }
.mobile-sub { margin-top: 4px; font-size: 12px; color: #7a8495; }
.mobile-info-row { margin-top: 8px; font-size: 13px; color: #7a8495; }
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
