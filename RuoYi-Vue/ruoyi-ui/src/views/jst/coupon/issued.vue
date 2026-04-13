<template>
  <div class="app-container jst-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">Issued Coupons</p>
        <h2>已发放优惠券</h2>
        <p class="hero-desc">按用户与状态查看已发券记录，支持右侧抽屉查看详情。</p>
      </div>
      <div class="hero-actions">
        <el-popover placement="bottom" width="280" trigger="hover">
          <div class="help-lines">
            <p>1. 可按券名和状态快速筛选。</p>
            <p>2. 点击详情查看关联订单与有效期。</p>
            <p>3. 本页为只读，不改业务流程。</p>
          </div>
          <el-button slot="reference" icon="el-icon-question" plain>使用说明</el-button>
        </el-popover>
        <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
      </div>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="券名称" prop="couponName">
        <el-input v-model="queryParams.couponName" placeholder="请输入券名称" clearable @keyup.enter.native="handleQuery" />
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
        <div v-for="row in list" :key="row.userCouponId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">{{ row.couponName || '--' }}</span>
            <el-tag size="mini" :type="couponStatusType(row.status)">{{ row.status || '--' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.userName || row.userId || '--' }}</span>
            <span>有效至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无已发券" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="ID" prop="userCouponId" width="70" />
      <el-table-column label="用户" min-width="120">
        <template slot-scope="{ row }">{{ row.userName || row.userId || '--' }}</template>
      </el-table-column>
      <el-table-column label="券名称" prop="couponName" min-width="160" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="couponStatusType(row.status)">{{ row.status || '--' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期至" width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validEnd) }}</template>
      </el-table-column>
      <el-table-column label="关联订单" prop="orderId" width="100" />
      <el-table-column label="操作" width="90" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'发券详情 #' + (detailData.userCouponId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '420px'" append-to-body>
      <div class="detail-wrap">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="券名称">{{ detailData.couponName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ detailData.userName || detailData.userId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="couponStatusType(detailData.status)">{{ detailData.status || '--' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="有效开始">{{ parseTime(detailData.validStart) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="有效结束">{{ parseTime(detailData.validEnd) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="关联订单">{{ detailData.orderId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listUserCoupon } from '@/api/jst/coupon'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'JstCouponIssued',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, couponName: null, status: null },
      statusOptions: [
        { label: '可用', value: 'available' },
        { label: '已锁定', value: 'locked' },
        { label: '已使用', value: 'used' },
        { label: '已过期', value: 'expired' },
        { label: '已退回', value: 'returned' }
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
    couponStatusType(s) {
      const map = { available: 'success', locked: 'warning', used: 'info', expired: 'info', returned: 'danger' }
      return map[s] || 'info'
    },
    getList() {
      this.loading = true
      listUserCoupon(this.queryParams).then(res => {
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
