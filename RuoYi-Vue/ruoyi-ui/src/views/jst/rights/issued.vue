<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Issued Rights</p>
        <h2>已发放权益</h2>
        <p class="hero-desc">查看用户权益余额与状态，详情抽屉只读展示关键字段。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 支持按权益名与状态快速过滤。</p>
            <p>2. 点击详情查看剩余额度与有效期。</p>
            <p>3. 本页不改业务流程，仅做查看。</p>
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
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 toolbar-row">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <div v-if="isMobile" v-loading="loading">
      <div v-if="list.length" class="mobile-card-list">
        <div v-for="row in list" :key="row.userRightsId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.rightsName || '--' }}</span>
            <el-tag size="mini" :type="rightsStatusType(row.status)">{{ row.status || '--' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.userName || row.userId || '--' }}</span>
            <span>剩余：{{ row.remainQuota != null ? row.remainQuota : '--' }}</span>
            <span>有效至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)" v-hasPermi="['jst:marketing:user_rights:list']">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无已发权益" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="userRightsId" width="70" />
      <el-table-column label="用户" min-width="120">
        <template slot-scope="{ row }">{{ row.userName || row.userId || '--' }}</template>
      </el-table-column>
      <el-table-column label="权益名称" prop="rightsName" min-width="160" show-overflow-tooltip />
      <el-table-column label="剩余额度" prop="remainQuota" width="90" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="rightsStatusType(row.status)">{{ row.status || '--' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期至" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validEnd) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)" v-hasPermi="['jst:marketing:user_rights:list']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'权益详情 #' + (detailData.userRightsId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '420px'" append-to-body>
      <div class="detail-wrap">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="权益名称">{{ detailData.rightsName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ detailData.userName || detailData.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="rightsStatusType(detailData.status)">{{ detailData.status || '--' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="剩余额度">{{ detailData.remainQuota != null ? detailData.remainQuota : '--' }}</el-descriptions-item>
          <el-descriptions-item label="有效开始">{{ parseTime(detailData.validStart) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="有效结束">{{ parseTime(detailData.validEnd) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listUserRights } from '@/api/jst/rights'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'JstRightsIssued',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, rightsName: null, status: null },
      statusOptions: [
        { label: '有效', value: 'active' },
        { label: '已用完', value: 'exhausted' },
        { label: '已过期', value: 'expired' }
      ],
      detailVisible: false,
      detailData: {}
    }
  },
  computed: {
    isMobile() { return this.$store.state.app.device === 'mobile' }
  },
  created() { this.getList() },
  methods: {
    parseTime,
    rightsStatusType(s) {
      const map = { active: 'success', exhausted: 'info', expired: 'info' }
      return map[s] || 'info'
    },
    getList() {
      this.loading = true
      listUserRights(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() },
    handleDetail(row) {
      this.detailData = { ...row }
      this.detailVisible = true
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

.detail-wrap {
  padding: 0 20px 20px;
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

  .detail-wrap {
    padding: 0 14px 14px;
  }
}
</style>
