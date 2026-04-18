<template>
  <div class="app-container user-coupon-page">
    <div class="page-hero">
      <div>
        <p class="hero-eyebrow">营销管理</p>
        <h2>用户优惠券</h2>
        <p class="hero-desc">查看已发放给用户的优惠券，包含状态、有效期与关联订单。</p>
      </div>
      <el-button type="primary" icon="el-icon-refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>

    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px" class="query-panel">
      <el-form-item label="用户" prop="userId">
        <user-picker v-model="queryParams.userId" clearable />
      </el-form-item>
      <el-form-item label="模板ID" prop="couponTemplateId">
        <coupon-template-picker v-model="queryParams.couponTemplateId" clearable />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
        <div v-for="row in list" :key="row.userCouponId" class="mobile-card">
          <div class="mobile-card__head">
            <span class="mobile-card__title">用户券 #{{ row.userCouponId }}</span>
            <el-tag size="mini" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>
              用户：
              <el-link v-if="row.userId && row.userName" type="primary" :underline="false" @click="goUser(row)">
                {{ row.userName }}
              </el-link>
              <span v-else>{{ row.userName || row.userId || '--' }}</span>
            </span>
            <span>模板：{{ row.couponTemplateName || row.couponTemplateId || '--' }}</span>
            <span>有效期至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
            <span>
              订单：
              <el-link v-if="row.orderId && row.orderNo" type="primary" :underline="false" @click="openOrder(row.orderId)">
                {{ row.orderNo }}
              </el-link>
              <span v-else>{{ row.orderNo || row.orderId || '--' }}</span>
            </span>
          </div>
          <div class="mobile-card__actions">
            <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.orderId" type="text" size="mini" @click="openOrder(row.orderId)">关联订单</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无用户优惠券" />
    </div>

    <el-table v-else v-loading="loading" :data="list">
      <el-table-column label="用户券ID" prop="userCouponId" width="100" />
      <el-table-column label="用户" min-width="120" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <el-link v-if="row.userId && row.userName" type="primary" :underline="false" @click="goUser(row)">
            {{ row.userName }}
          </el-link>
          <span v-else>{{ row.userName || row.userId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="模板" min-width="140" show-overflow-tooltip>
        <template slot-scope="{ row }">{{ row.couponTemplateName || row.couponTemplateId || '--' }}</template>
      </el-table-column>
      <el-table-column label="发券来源" prop="issueSource" min-width="100" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生效时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validStart) || '--' }}</template>
      </el-table-column>
      <el-table-column label="失效时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.validEnd) || '--' }}</template>
      </el-table-column>
      <el-table-column label="使用订单号" min-width="130" show-overflow-tooltip>
        <template slot-scope="{ row }">
          <el-link v-if="row.orderId && row.orderNo" type="primary" :underline="false" @click="openOrder(row.orderId)">
            {{ row.orderNo }}
          </el-link>
          <span v-else>{{ row.orderNo || row.orderId || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用时间" min-width="160">
        <template slot-scope="{ row }">{{ parseTime(row.useTime) || '--' }}</template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="90">
        <template slot-scope="{ row }">
          <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-drawer :title="'用户券详情 #' + (detailData.userCouponId || '')" :visible.sync="detailVisible" :size="isMobile ? '100%' : '500px'" append-to-body>
      <div class="detail-body">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="用户券ID">{{ detailData.userCouponId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="用户">
            <el-link v-if="detailData.userId && detailData.userName" type="primary" :underline="false" @click="goUser(detailData)">
              {{ detailData.userName }}
            </el-link>
            <span v-else>{{ detailData.userName || detailData.userId || '--' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="模板">{{ detailData.couponTemplateName || detailData.couponTemplateId || '--' }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ detailData.issueBatchNo || '--' }}</el-descriptions-item>
          <el-descriptions-item label="发券来源">{{ detailData.issueSource || '--' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag size="small" :type="statusType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="生效时间">{{ parseTime(detailData.validStart) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="失效时间">{{ parseTime(detailData.validEnd) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="使用订单">
            <el-link v-if="detailData.orderId && detailData.orderNo" type="primary" :underline="false" @click="openOrder(detailData.orderId)">
              {{ detailData.orderNo }}
            </el-link>
            <span v-else>{{ detailData.orderNo || detailData.orderId || '--' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用时间">{{ parseTime(detailData.useTime) || '--' }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '--' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { parseTime } from '@/utils/ruoyi'
import { listJst_user_coupon, getJst_user_coupon } from '@/api/jst/marketing/jst_user_coupon'
import CouponTemplatePicker from '@/components/RelationPicker/CouponTemplatePicker.vue'

const STATUS_META = {
  unused: { label: '未使用', type: 'info' },
  locked: { label: '已锁定', type: 'warning' },
  used: { label: '已使用', type: 'success' },
  expired: { label: '已过期', type: 'info' },
  refunded: { label: '已退回', type: 'danger' }
}

export default {
  name: 'JstUserCoupon',
  components: {
    CouponTemplatePicker
  },
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        couponTemplateId: null,
        status: null
      },
      detailVisible: false,
      detailData: {},
      statusOptions: Object.keys(STATUS_META).map(key => ({ value: key, label: STATUS_META[key].label }))
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
    statusType(status) {
      return (STATUS_META[status] || {}).type || 'info'
    },
    statusLabel(status) {
      return (STATUS_META[status] || {}).label || status || '--'
    },
    getList() {
      this.loading = true
      listJst_user_coupon(this.queryParams).then(res => {
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
      getJst_user_coupon(row.userCouponId).then(res => {
        this.detailData = res.data || res || row
        this.detailVisible = true
      })
    },
    goUser(row) {
      const userId = row && row.userId
      if (!userId) return
      this.$router.push({
        path: '/jst/user',
        query: { userId: String(userId), autoOpen: '1' }
      }).catch(() => {})
    },
    openOrder(orderId) {
      this.$router.push({
        path: '/jst/order/admin-order',
        query: { orderId: String(orderId), autoOpen: '1' }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.user-coupon-page {
  background: #f6f8fb;
  min-height: calc(100vh - 84px);
}

.page-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  margin-bottom: 18px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.hero-eyebrow {
  margin: 0 0 8px;
  color: #2f6fec;
  font-size: 13px;
  font-weight: 600;
}

.page-hero h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #172033;
}

.hero-desc {
  margin: 8px 0 0;
  color: #6f7b8f;
}

.query-panel {
  padding: 16px 16px 0;
  margin-bottom: 16px;
  background: #ffffff;
  border: 1px solid #e5eaf2;
  border-radius: 8px;
}

.detail-body {
  padding: 0 16px 16px;
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

@media (max-width: 768px) {
  .user-coupon-page {
    padding: 12px;
  }

  .page-hero {
    display: block;
    padding: 18px;
  }

  .page-hero .el-button {
    width: 100%;
    min-height: 44px;
    margin-top: 16px;
  }

  .page-hero h2 {
    font-size: 20px;
  }

  .query-panel {
    padding-bottom: 8px;
  }

  .query-panel ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .query-panel ::v-deep .el-form-item__content,
  .query-panel ::v-deep .el-select,
  .query-panel ::v-deep .el-input {
    width: 100%;
  }
}
</style>
