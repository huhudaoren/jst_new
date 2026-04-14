<template>
  <div class="app-container partner-contest-page">
    <div class="page-banner">
      <div>
        <div class="page-eyebrow">Contest Workspace</div>
        <div class="page-title">赛事管理</div>
        <div class="page-desc">创建赛事、维护报名规则，并在草稿完成后提交平台审核。</div>
      </div>
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新建赛事</el-button>
    </div>

    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="76px"
      class="query-form"
    >
      <el-form-item label="赛事名称" prop="contestName">
        <el-input
          v-model="queryParams.contestName"
          placeholder="请输入赛事名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部状态" clearable>
          <el-option v-for="item in auditStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="全部分类" clearable filterable allow-create>
          <el-option v-for="item in categoryOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="createTimeRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar-row">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </div>

    <div v-if="isMobile" class="mobile-card-list" v-loading="loading">
      <div v-if="contestList.length">
        <div v-for="row in contestList" :key="row.contestId" class="contest-card">
          <div class="contest-card__head">
            <div>
              <div class="contest-name">{{ row.contestName || '--' }}</div>
              <div class="contest-no">赛事编号：{{ row.contestId || '--' }}</div>
            </div>
            <el-tag size="small" :type="auditStatusType(row.auditStatus)">{{ auditStatusLabel(row.auditStatus) }}</el-tag>
          </div>
          <div class="contest-meta">
            <span><dict-tag :options="dict.type.jst_contest_category" :value="row.category" /></span>
            <span>报名：{{ formatDate(row.enrollStartTime) }}</span>
            <span>报名数：{{ row.enrollCount || '--' }}</span>
          </div>
          <div class="contest-actions">
            <el-button type="text" size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" size="mini" @click="handleCopy(row)">复制</el-button>
            <el-button v-if="canSubmit(row)" type="text" size="mini" @click="handleSubmit(row)">提交审核</el-button>
            <el-button v-if="canOffline(row)" type="text" size="mini" @click="handleOffline(row)">下线</el-button>
            <el-button type="text" size="mini" @click="handleViewEnroll(row)">查看报名</el-button>
            <el-button v-if="canDelete(row)" type="text" size="mini" class="danger-text" @click="handleDelete(row)">删除</el-button>
          </div>
        </div>
      </div>
      <div v-else class="empty-wrap">暂无赛事数据</div>
    </div>

    <el-table v-else v-loading="loading" :data="contestList">
      <el-table-column label="赛事编号" prop="contestId" min-width="110" />
      <el-table-column label="赛事名称" prop="contestName" min-width="220" show-overflow-tooltip />
      <el-table-column label="类别" prop="category" min-width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.jst_contest_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="开始时间" min-width="160">
        <template slot-scope="scope">
          <span>{{ formatDate(scope.row.eventStartTime || scope.row.enrollStartTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="120">
        <template slot-scope="scope">
          <el-tag size="small" :type="auditStatusType(scope.row.auditStatus)">{{ auditStatusLabel(scope.row.auditStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报名数" min-width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.enrollCount || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="360">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="mini" @click="handleCopy(scope.row)">复制</el-button>
          <el-button v-if="canSubmit(scope.row)" type="text" size="mini" @click="handleSubmit(scope.row)">提交审核</el-button>
          <el-button v-if="canOffline(scope.row)" type="text" size="mini" @click="handleOffline(scope.row)">下线</el-button>
          <el-button type="text" size="mini" @click="handleViewEnroll(scope.row)">查看报名</el-button>
          <el-button v-if="canDelete(scope.row)" type="text" size="mini" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
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
  </div>
</template>

<script>
import { listPartnerContests, offlinePartnerContest, submitPartnerContest, copyPartnerContest } from '@/api/partner/contest'
import { parseTime } from '@/utils/ruoyi'

const AUDIT_STATUS_META = {
  draft: { label: '草稿', type: 'info' },
  pending: { label: '审核中', type: 'warning' },
  approved: { label: '已通过', type: 'success' },
  rejected: { label: '已驳回', type: 'danger' },
  online: { label: '已上线', type: 'success' },
  offline: { label: '已下线', type: 'info' }
}

export default {
  name: 'PartnerContestList',
  dicts: ['jst_contest_category'],
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      contestList: [],
      createTimeRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        contestName: null,
        auditStatus: null,
        category: null
      },
      auditStatusOptions: [
        { label: '草稿', value: 'draft' },
        { label: '审核中', value: 'pending' },
        { label: '已通过', value: 'approved' },
        { label: '已驳回', value: 'rejected' },
        { label: '已上线', value: 'online' },
        { label: '已下线', value: 'offline' }
      ],
      categoryOptions: ['艺术', '音乐', '舞蹈', '美术', '朗诵戏剧', '文化', '科技', '体育']
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
    getList() {
      this.loading = true
      listPartnerContests(this.buildQueryParams()).then(response => {
        this.contestList = Array.isArray(response.rows) ? response.rows : []
        this.total = Number(response.total || 0)
      }).finally(() => {
        this.loading = false
      })
    },
    buildQueryParams() {
      const params = { ...this.queryParams }
      if (this.createTimeRange.length === 2) {
        params.beginCreateTime = this.createTimeRange[0]
        params.endCreateTime = this.createTimeRange[1]
      }
      return params
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.createTimeRange = []
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleCreate() {
      this.$router.push('/partner/contest-edit')
    },
    handleEdit(row) {
      this.$router.push({ path: `/partner/contest-edit/${row.contestId}` })
    },
    handleViewEnroll(row) {
      this.$router.push({ path: '/partner/enroll-manage', query: { contestId: row.contestId } })
    },
    handleSubmit(row) {
      this.$modal.confirm(`确认将赛事「${row.contestName || row.contestId}」提交平台审核吗？`).then(() => {
        return submitPartnerContest(row.contestId)
      }).then(() => {
        this.$modal.msgSuccess('提交审核成功')
        this.getList()
      }).catch(() => {})
    },
    handleOffline(row) {
      this.$modal.confirm(`确认下线赛事「${row.contestName || row.contestId}」吗？下线后用户端将不可见。`).then(() => {
        return offlinePartnerContest(row.contestId)
      }).then(() => {
        this.$modal.msgSuccess('下线成功')
        this.getList()
      }).catch(() => {})
    },
    handleCopy(row) {
      this.$modal.confirm(`确认复制赛事「${row.contestName || row.contestId}」吗？`).then(() => {
        return copyPartnerContest(row.contestId)
      }).then(response => {
        const newId = (response.data && response.data.contestId) || response.contestId
        this.$modal.msgSuccess('已复制，请修改后保存')
        if (newId) {
          this.$router.push({ path: `/partner/contest-edit/${newId}` })
        } else {
          this.getList()
        }
      }).catch(() => {})
    },
    handleDelete() {
      this.$modal.msgWarning('当前后端未提供赛事方隔离删除接口，已在字段缺口反馈中记录；请先不要调用通用删除接口。')
    },
    canSubmit(row) {
      return ['draft', 'rejected'].includes(row.auditStatus)
    },
    canOffline(row) {
      return row.auditStatus === 'online'
    },
    canDelete(row) {
      return row.auditStatus === 'draft'
    },
    auditStatusLabel(status) {
      return (AUDIT_STATUS_META[status] && AUDIT_STATUS_META[status].label) || status || '--'
    },
    auditStatusType(status) {
      return (AUDIT_STATUS_META[status] && AUDIT_STATUS_META[status].type) || 'info'
    },
    formatDate(value) {
      return value ? parseTime(value, '{y}-{m}-{d}') : '--'
    }
  }
}
</script>

<style scoped>
.partner-contest-page {
  background: #f5f7fa;
}

.page-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  margin-bottom: 16px;
  border: 1px solid #e6edf5;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
}

.page-eyebrow {
  margin-bottom: 6px;
  font-size: 12px;
  color: #909399;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-desc {
  margin-top: 8px;
  color: #606266;
}

.query-form,
.toolbar-row,
.mobile-card-list {
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 14px;
}

.query-form {
  padding: 16px 16px 0;
  margin-bottom: 14px;
}

.toolbar-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 14px;
  padding: 12px 16px;
  margin-bottom: 14px;
}

.toolbar-hint {
  flex: 1;
}

.danger-text {
  color: #f56c6c;
}

.mobile-card-list {
  padding: 14px;
}

.contest-card {
  padding: 14px;
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  background: #fbfdff;
}

.contest-card__head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.contest-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.contest-no,
.contest-meta {
  margin-top: 6px;
  font-size: 13px;
  color: #606266;
}

.contest-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.contest-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
}

.empty-wrap {
  padding: 40px 0;
  text-align: center;
  color: #909399;
}

@media (max-width: 992px) {
  .page-banner,
  .toolbar-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
