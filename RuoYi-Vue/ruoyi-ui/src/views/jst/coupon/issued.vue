<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="80px">
      <el-form-item label="券名称" prop="couponName">
        <el-input v-model="queryParams.couponName" placeholder="请输入券名称" clearable @keyup.enter.native="handleQuery" />
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
            <span class="mobile-card__title">{{ row.couponName || '--' }}</span>
            <el-tag size="mini" :type="couponStatusType(row.status)">{{ row.status || '--' }}</el-tag>
          </div>
          <div class="mobile-card__meta">
            <span>用户：{{ row.userName || row.userId || '--' }}</span>
            <span>有效至：{{ parseTime(row.validEnd, '{y}-{m}-{d}') || '--' }}</span>
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
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
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
      ]
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
    resetQuery() { this.$refs.queryForm && this.$refs.queryForm.resetFields(); this.handleQuery() }
  }
}
</script>

<style scoped>
.mobile-card-list { padding: 0 4px; }
.mobile-card { background: #fff; border-radius: 8px; padding: 12px 14px; margin-bottom: 10px; box-shadow: 0 1px 4px rgba(0,0,0,.06); }
.mobile-card__head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.mobile-card__title { font-weight: 600; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 65%; }
.mobile-card__meta { font-size: 12px; color: #909399; display: flex; gap: 12px; margin-bottom: 8px; }
</style>
